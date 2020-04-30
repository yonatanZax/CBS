package KRobust_CBS;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Solvers.AStar.DistanceTableAStarHeuristic;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.I_ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.MinTimeConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.SingleAgentPlan;

import java.util.ArrayList;
import java.util.Objects;

public class CBS_KRobust extends CBS_Solver {


    @Override
    protected void init(MAPF_Instance instance, RunParameters runParameters) {
        super.init(instance, runParameters);
    }

    protected void initCBS(MAPF_Instance instance, RunParameters runParameters){
        this.initialConstraints = Objects.requireNonNullElseGet(runParameters.constraints, ConstraintSet::new);
        this.currentConstraints = new ConstraintSet_Robust();
        this.generatedNodes = 0;
        this.expandedNodes = 0;
        this.instance = instance;
        this.aStarHeuristic = this.lowLevelSolver instanceof SingleAgentAStar_Solver ?
                new DistanceTableAStarHeuristic(new ArrayList<>(this.instance.agents), this.instance.map) :
                null;
    }

    // todo - add method
    @Override
    protected boolean isConstraintOnStartPosition(Constraint constraint){

        if( !(constraint instanceof Constraint_Robust )){
            return super.isConstraintOnStartPosition(constraint);
        }
        Constraint_Robust constraintRobust = (Constraint_Robust) constraint;

        I_Coordinate sourceCoordinate = constraint.agent.source;
        I_Coordinate constraintCoordinate = constraint.location.getCoordinate();

        boolean result = sourceCoordinate.equals(constraintCoordinate) ;
        result = result && constraintRobust.lowerBound == 0;
        return result;
    }



    @Override
    protected I_ConflictManager getConflictAvoidanceTableFor(CBS_Solver.CBS_Node node) {

        I_ConflictManager conflictManager = new ConflictManager_KRobust(new MinTimeConflictSelectionStrategy());
        for (SingleAgentPlan plan : node.getSolution()) {
            conflictManager.addPlan(plan);
        }
        return conflictManager;
    }


    protected ConstraintSet buildConstraintSet(CBS_Node parentNode, Constraint newConstraint) {
        // clear currentConstraints. we reuse this object every time.
        super.currentConstraints.clear();
        ConstraintSet constraintSet = this.currentConstraints;
        // start by adding all the constraints that we were asked to start the solver with (and are therefore not in the CT)
        constraintSet.addAll(initialConstraints);

        CBS_Node currentNode = parentNode;
        while (currentNode.getAddedConstraint() != null){ // will skip the root (it has no constraints)

            constraintSet.add(currentNode.getAddedConstraint());
            currentNode = currentNode.getParent();
        }

        constraintSet.add(newConstraint);
        return constraintSet;
    }
}
