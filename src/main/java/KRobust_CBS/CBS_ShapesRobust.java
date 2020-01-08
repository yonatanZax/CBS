package KRobust_CBS;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.AStar.DistanceTableAStarHeuristic;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.I_ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.MinTimeConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.SingleAgentPlan;
import LargeAgents_CBS.Solvers.HighLevel.CBS_Shapes;
import LargeAgents_CBS.Solvers.HighLevel.ConflictManager_Shapes;
import LargeAgents_CBS.Solvers.LowLevel.AStar_Shapes;
import LargeAgents_CBS.Solvers.LowLevel.DistanceTableHeuristic_LargeAgents;

import java.util.ArrayList;
import java.util.Objects;

public class CBS_ShapesRobust extends CBS_Solver {

    public CBS_ShapesRobust(){
        super(new AStar_RobustShape(),null,null,null,null);
    }

    protected CBS_ShapesRobust(I_Solver lowLevel){
        super(lowLevel,null,null,null,null);
    }

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
                new DistanceTableHeuristic_RobustShapes(new ArrayList<>(this.instance.agents), this.instance.map) :
                null;
    }

    @Override
    protected I_ConflictManager getConflictAvoidanceTableFor(CBS_Solver.CBS_Node node) {

        I_ConflictManager conflictManager = new ConflictManager_RobustShape(new MinTimeConflictSelectionStrategy());
        for (SingleAgentPlan plan : node.getSolution()) {
            conflictManager.addPlan(plan);
        }
        return conflictManager;
    }

}
