package KRobust_CBS;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.OpenList;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import GraphMapPackage.GraphMapVertex;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AStar_RobustShape extends SingleAgentAStar_Solver {


    protected void init(MAPF_Instance instance, RunParameters runParameters){
        super.init(instance, runParameters);
        this.constraints = runParameters.constraints == null ? new ConstraintSet_Robust(): runParameters.constraints;
        this.openList = new OpenList<>(stateFComparator);
    }




    @Override
    protected boolean initOpen() {
        // if the existing plan isn't empty, we start from the last move of the existing plan.
        if(existingPlan.size() > 0){

            Move lastExistingMove = existingPlan.moveAt(existingPlan.getEndTime());
            // We assume that we cannot change the existing plan, so if it is rejected by constraints, we can't initialise OPEN.

            if(constraints.rejects(lastExistingMove)) {
                return false;
            }

            AStarState_RobustShape state = new AStarState_RobustShape(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size());
            openList.add(state);
        }
        else { // the existing plan is empty (no existing plan)

            int k = ((RobustAgent)agent).k;
            RobustShape sourceCell = new RobustShape(map.getMapCell(agent.source), k);

            // can move to neighboring cells or stay put
            List<I_Location> neighborCellsIncludingCurrent = new ArrayList<>(sourceCell.getNeighbors());
            neighborCellsIncludingCurrent.add(this.stayInPlace(sourceCell));


            for (I_Location destination: neighborCellsIncludingCurrent) {
                Move possibleMove = new Move(agent, problemStartTime + 1, sourceCell, destination);

                if (constraints.accepts(possibleMove)) { //move not prohibited by existing constraint
                    AStarState_RobustShape rootState = new AStarState_RobustShape(possibleMove, null, 1);
                    openList.add(rootState);
                    generatedNodes++;
                }
            }

        }

        // if none of the root nodes was valid, OPEN will be empty, and thus uninitialised.
        return !openList.isEmpty();
    }


    @Override
    protected Solution solveAStar() {
        // if failed to init OPEN then the problem cannot be solved as defined (bad constraints? bad existing plan?)
        if (!initOpen())
            return null;

        AStarState_RobustShape currentState;
        int firstRejectionAtGoalTime = -1;

        while ((currentState = (AStarState_RobustShape) openList.poll()) != null){ //dequeu in the if
            if(checkTimeout()) {
                return null;
            }
            closed.add(currentState);

            // nicetohave -  change to early goal test
            if (isGoalState(currentState)){
                // check to see if a rejecting constraint on the goal exists at some point in the future.

                // smaller means we have passed the current rejection (if one existed) and should check if another exists.
                // shouldn't be equal because such a state would not be generated
                Move currentMove = currentState.getMove();
                if(agentsStayAtGoal && firstRejectionAtGoalTime < currentMove.timeNow) {
                    // do the expensive update/check
                    firstRejectionAtGoalTime = constraints.rejectsEventually(currentMove);
                }

                if(firstRejectionAtGoalTime == -1){ // no rejections
                    currentState.backTracePlan(); // updates this.existingPlan which is contained in this.existingSolution
                    return this.existingSolution; // the goal is good and we can return the plan.
                }
                else{ // we are rejected from the goal at some point in the future.
                    // We clear OPEN because we have already found an optimal plan to the goal.
                    openList.clear();
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

    protected boolean isGoalState(AStarState_RobustShape state) {
        RobustShape robustShape = (RobustShape) state.getMove().currLocation;
        if( isGoalLocation(robustShape.getHead(), agent.target)){
            return true;
        }
        return false;
    }

    private boolean isGoalLocation(I_Location location, I_Coordinate goalCoordinate){
        return location.getCoordinate().equals(goalCoordinate);
    }



    protected RobustShape stayInPlace(RobustShape shape){
        return RobustShape.stayInPlace(shape);
    }




    protected class AStarState_RobustShape extends AStarState{

        public AStarState_RobustShape(Move move, AStarState prevState, int g) {
            super(move, prevState, g);
        }


        @Override
        public void expand() {
            expandedNodes++;
            // can move to neighboring cells or stay put
            RobustShape location = (RobustShape) this.move.currLocation;
            List<I_Location> neighborCellsIncludingCurrent = new ArrayList<>(location.getNeighbors());

            neighborCellsIncludingCurrent.add(stayInPlace(location));

            for (I_Location destination: neighborCellsIncludingCurrent){
                Move possibleMove = new Move(this.move.agent, this.move.timeNow+1, this.move.currLocation, destination);
                if(constraints.accepts(possibleMove)){ //move not prohibited by existing constraint
                    AStarState_RobustShape child = new AStarState_RobustShape(possibleMove, this, this.g + 1);
                    generatedNodes++; //field in containing class

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
    }


}
