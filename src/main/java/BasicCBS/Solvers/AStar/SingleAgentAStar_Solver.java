package BasicCBS.Solvers.AStar;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Instances.Maps.I_Location;
import Environment.Metrics.InstanceReport;
import BasicCBS.Solvers.*;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;

import java.util.*;

/**
 * An A* solver that only solves single agent problems. It assumes the first {@link Agent} from {@link MAPF_Instance instances}
 * that it is given is the agent to solve for.
 * By default, it uses {@link I_Coordinate#distance(I_Coordinate)} as a heuristic.
 */
public class SingleAgentAStar_Solver extends A_Solver {

    /**
     * Since A* should solve even very large single agent problems very quickly, set default timeout to 3 seconds.
     * A timeout would therefore very likely mean the problem is unsolvable.
     * Without a timeout, unsolvable problems would not resolve, since new states with higher time ({@link Move#timeNow},
     * would continue to be generated ad infinitum. This would eventually result in a heap overflow.
     */
    protected static final long DEFAULT_TIMEOUT = 3 * 1000;
    protected static final int DEFAULT_PROBLEM_START_TIME = 0;
//    protected static final Comparator<AStarState> stateFComparator = new TieBreakingForHigherGComparator();
    protected Comparator<AStarState> stateFComparator;
    protected static final Comparator<AStarState> stateGComparator = Comparator.comparing(AStarState::getG);

    public boolean agentsStayAtGoal = true;

    protected ConstraintSet constraints;
    protected AStarHeuristic heuristicFunction;
    protected I_OpenList<AStarState> openList;
    protected Set<AStarState> closed;
    protected Agent agent;
    protected I_Map map;
    protected SingleAgentPlan existingPlan;
    protected Solution existingSolution;
    /**
     * Not real-world time. The problem's start time.
     */
    protected int problemStartTime;
    protected int expandedNodes;
    protected int generatedNodes;

    public SingleAgentAStar_Solver() {
        super.DEFAULT_TIMEOUT = SingleAgentAStar_Solver.DEFAULT_TIMEOUT;
    }

    public SingleAgentAStar_Solver(boolean agentsStayAtGoal) {
        this.agentsStayAtGoal = agentsStayAtGoal;
    }
    /*  = set up =  */

    protected void init(MAPF_Instance instance, RunParameters runParameters){
        super.init(instance, runParameters);

        this.agent = instance.agents.get(0);
        this.map = instance.map;

        this.initConstraintSet(runParameters);

        if(runParameters.existingSolution != null){
            this.existingSolution = runParameters.existingSolution;
            if(runParameters.existingSolution.getPlanFor(this.agent) != null){
                this.existingPlan = runParameters.existingSolution.getPlanFor(this.agent);
                this.problemStartTime = this.existingPlan.getEndTime();
            }
            else {
                this.existingPlan = new SingleAgentPlan(this.agent);
                this.existingSolution.putPlan(this.existingPlan);
                this.problemStartTime = DEFAULT_PROBLEM_START_TIME;
            }
        }
        else{
            // make a new, empty solution, with a new, empty, plan
            this.existingSolution = new Solution();
            this.existingPlan = new SingleAgentPlan(this.agent);
            this.existingSolution.putPlan(this.existingPlan);
        }

        if(runParameters instanceof  RunParameters_SAAStar
                && ((RunParameters_SAAStar) runParameters).heuristicFunction != null){
            RunParameters_SAAStar parameters = ((RunParameters_SAAStar) runParameters);
            this.heuristicFunction = parameters.heuristicFunction;
        }
        else{
            this.heuristicFunction = new defaultHeuristic();
        }
        if(runParameters instanceof  RunParameters_SAAStar
                && ((RunParameters_SAAStar) runParameters).problemStartTime >= 0){
            RunParameters_SAAStar parameters = ((RunParameters_SAAStar) runParameters);
            this.problemStartTime = parameters.problemStartTime;
        }
        // else keep the value that it has already been initialised with (above)

        this.openList = new OpenList<>(stateFComparator);
        this.expandedNodes = 0;
        this.closed = new HashSet<>();
        this.generatedNodes = 0;
        this.stateFComparator = new TieBreakingForHigherGComparator();
    }
    // todo - add this
    protected void initConstraintSet(RunParameters runParameters){
        this.constraints = runParameters.constraints == null ? new ConstraintSet(): runParameters.constraints;
    }

    /*  = A* algorithm =  */

    @Override
    protected Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters) {
        return solveAStar();
    }

    /**
     * Solves AStar for a single agent.
     * Assumes just 1 goal {@link I_Location location} - otherwise there may be problems when accounting for constraints
     * at goal, that come after reaching goal.
     * @return a solution that contains a plan for the {@link #agent} to its goal.
     */
    protected Solution solveAStar() {
        // if failed to init OPEN then the problem cannot be solved as defined (bad constraints? bad existing plan?)
        if (!initOpen()) return null;

        AStarState currentState;
        int firstRejectionAtGoalTime = -1;

        while ((currentState = openList.poll()) != null){ //dequeu in the if
            if(checkTimeout()) {return null;}
            closed.add(currentState);

            // nicetohave -  change to early goal test
            if (isGoalState(currentState)){
                // check to see if a rejecting constraint on the goal exists at some point in the future.

                // smaller means we have passed the current rejection (if one existed) and should check if another exists.
                // shouldn't be equal because such a state would not be generated
                if(agentsStayAtGoal && firstRejectionAtGoalTime < currentState.move.timeNow) {
                    // do the expensive update/check
                    firstRejectionAtGoalTime = constraints.rejectsEventually(currentState.move);
                }

                if(firstRejectionAtGoalTime == -1){ // no rejections
                    currentState.backTracePlan(); // updates this.existingPlan which is contained in this.existingSolution
                    return this.existingSolution; // the goal is good and we can return the plan.
                }
                else{ // we are rejected from the goal at some point in the future.
                    // We clear OPEN because we have already found an optimal plan to the goal.
//                    openList.clear();
                    /*
                    We then solve a smaller search problem where we make a plan from the goal at time t[x], to the goal
                    at time t[y+1], where y is the time of the future constraint.
                     */
                    currentState.expand();
                }
            }
            else{ //expand
                currentState.expand(); //doesn't generate closed or duplicate states
            }
        }
        return null; //no goal state found (problem unsolvable)
    }

    /**
     * Initialises {@link #openList OPEN}.
     *
     * OPEN is not initialised with a single root state as is common. This is because states in this solver represent
     * {@link Move moves} (classically - operators) rather than {@link I_Location map cells} (classically - states).
     * Instead, OPEN is initialised with all possible moves from the starting position.
     * @return true if OPEN was successfully initialised, else false.
     */
    protected boolean initOpen() {
        // if the existing plan isn't empty, we start from the last move of the existing plan.
        if(existingPlan.size() > 0){
            Move lastExistingMove = existingPlan.moveAt(existingPlan.getEndTime());
            // We assume that we cannot change the existing plan, so if it is rejected by constraints, we can't initialise OPEN.
            if(constraints.rejects(lastExistingMove)) {
                return false;
            }

            openList.add(new AStarState(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size()));
        }
        else { // the existing plan is empty (no existing plan)

            I_Location sourceCell = map.getMapCell(agent.source);
            // can move to neighboring cells or stay put
            List<I_Location> neighborCellsIncludingCurrent = new ArrayList<>(sourceCell.getNeighbors());
            neighborCellsIncludingCurrent.add(sourceCell);

            for (I_Location destination: neighborCellsIncludingCurrent) {
                Move possibleMove = new Move(agent, problemStartTime + 1, sourceCell, destination);
                if (constraints.accepts(possibleMove)) { //move not prohibited by existing constraint
                    AStarState rootState = new AStarState(possibleMove, null, 1);
                    openList.add(rootState);
                }
            }

        }

        // if none of the root nodes was valid, OPEN will be empty, and thus uninitialised.
        boolean result = !openList.isEmpty();
        return result;
    }

    protected boolean isGoalState(AStarState state) {
        return state.move.currLocation.getCoordinate().equals(agent.target);
    }

    /*  = wind down =  */

    protected void writeMetricsToReport(Solution solution) {
        // skips super's writeMetricsToReport(Solution solution).
        super.endTime = System.currentTimeMillis();
        if(instanceReport != null){
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel, this.expandedNodes);
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel, this.generatedNodes);
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.elapsedTimeMS, (int)(super.endTime-super.startTime));
        }
    }

    protected void releaseMemory() {
        super.releaseMemory();
        this.constraints = null;
        this.instanceReport = null;
        this.openList = null;
        this.closed = null;
        this.agent = null;
        this.map = null;
        this.existingSolution = null;
        this.existingPlan = null;
    }

    /*  = inner classes =  */

    public class AStarState implements Comparable<AStarState>{
        // todo - protected
        protected final int serialID = SingleAgentAStar_Solver.this.generatedNodes++; // take and increment
        protected final Move move;
        protected final AStarState prev;
        protected final int g;
        protected final float h;


        public AStarState(Move move, AStarState prevState, int g) {
            this.move = move;
            this.prev = prevState;
            this.g = g;

            // must call this last, since it needs the other fields to be initialized already.
            this.h = calcH();
        }

        /*  = getters =  */

        public Move getMove() {
            return move;
        }

        public AStarState getPrev() {
            return prev;
        }

        public int getG() {
            return g;
        }

        /*  = other methods =  */

        public float getF(){
            return g + h;
        }

        // todo - protected
        protected float calcH() {
            return SingleAgentAStar_Solver.this.heuristicFunction.getH(this);
        }

        public void expand() {
            expandedNodes++;
            // can move to neighboring cells or stay put
            List<I_Location> neighborCellsIncludingCurrent = new ArrayList<>(this.move.currLocation.getNeighbors());
            neighborCellsIncludingCurrent.add(this.move.currLocation);

            for (I_Location destination: neighborCellsIncludingCurrent){
                Move possibleMove = new Move(this.move.agent, this.move.timeNow+1, this.move.currLocation, destination);
                if(constraints.accepts(possibleMove)){ //move not prohibited by existing constraint
                    AStarState child = new AStarState(possibleMove, this, this.g + 1);

                    AStarState existingState;
                    if(closed.contains(child)){ // state visited already
                        // for non consistent heuristics - if the new one has a lower f, remove the old one from closed
                        // and add the new one to open
                    }
                    else if(null != (existingState = openList.get(child)) ){ //an equal state is waiting in open
                        //keep the one with min G
                        keepTheStateWithMinG(child, existingState); //O(LOGn)
                    }
                    else{ // it's a new state
                        openList.add(child);
                    }
                }
            }
        }

        // todo - protected
        protected void keepTheStateWithMinG(AStarState newState, AStarState existingState) {
            openList.keepOne(existingState, newState, stateGComparator);
//            boolean shouldSwap = newState.g < existingState.g;
//            if(shouldSwap){
//                openList.replace(existingState, newState);
//            }
        }

        public SingleAgentPlan backTracePlan() {
            List<Move> moves = new LinkedList<>();
            AStarState currentState = this;
            while (currentState != null){
                moves.add(currentState.move);
                currentState = currentState.prev;
            }
            Collections.reverse(moves); //reorder moves because they were reversed

            //if there was an existing plan before solving, then we started from its last move, and don't want to duplicate it.
            if(existingPlan.size() > 0) {moves.remove(0);}
            /*containing class.*/ existingPlan.addMoves(moves);
            return existingPlan;
        }

        /**
         * equality is determined by location (current), and time.
         * @param o {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AStarState that = (AStarState) o;
            return move.currLocation.equals(that.move.currLocation) && move.timeNow == that.move.timeNow;
        }

        @Override
        public int hashCode() {
            return Objects.hash(move.currLocation.hashCode(), move.timeNow);
        }

        @Override
        public int compareTo(AStarState o) {
            return stateFComparator.compare(this, o);
        }
    }

    private class defaultHeuristic implements AStarHeuristic{

        @Override
        public float getH(AStarState state) {
            return state.move.currLocation.getCoordinate().distance(state.move.agent.target);
        }
    }

    protected static class TieBreakingForHigherGComparator implements Comparator<AStarState>{

        private static Comparator<AStarState> fComparator = Comparator.comparing(AStarState::getF);

        @Override
        public int compare(AStarState o1, AStarState o2) {
            if(Math.abs(o1.getF() - o2.getF()) < 0.1){ // floats are equal
                // if f() value is equal, we consider the state with higher g() to be better (smaller). Therefore, we
                // want to return a negative integer if o1.g is bigger than o2.g
                if (o2.g == o1.g){
                    // If still equal, we tie break for smaller ID (older nodes) (arbitrary) to force a total ordering and remain deterministic
                    return o1.serialID - o2.serialID;
                }
                else {
                    return o2.g - o1.g;//higher g is better
                }
            }
            else {
                return fComparator.compare(o1, o2);
            }
        }

    }

    protected static class TargetConflictFixingComparator implements Comparator<AStarState>{

        private static Comparator<AStarState> fComparator = Comparator.comparing(AStarState::getF);
        private final int maxConstraintAtTargetTime;

        private TargetConflictFixingComparator(ConstraintSet constraintSet, I_Location target, Agent agent) {
            this.maxConstraintAtTargetTime = constraintSet.lastRejectAt(target, agent);
        }

        @Override
        public int compare(AStarState o1, AStarState o2) {
            //  the cost of all nodes is at least the time of the last constraint at target + 1
            float f1Adjusted = Math.max(o1.getF(), maxConstraintAtTargetTime+1);
            float f2Adjusted = Math.max(o2.getF(), maxConstraintAtTargetTime+1);

            if(Math.abs(f1Adjusted - f2Adjusted) < 0.1){ // floats are equal
                // if equal (will be equal for all states until we get to states that pass the time of the constraint),
                // we want to prefer lower h to direct the search towards the target

                if (Math.abs(o2.h - o1.h) < 0.1){
                    // If still equal, we tie break for smaller ID (older nodes) (arbitrary) to force a total ordering and remain deterministic
                    return o1.serialID - o2.serialID;
                }
                else {
                    return (int)Math.floor(o1.h - o2.h); //lower h is better
                }
            }
            else {
                return fComparator.compare(o1, o2);
            }
        }
    }
}
