package KRobust_CBS;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.AStar.DistanceTableAStarHeuristic;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.RunParameters;
import LargeAgents_CBS.Solvers.HighLevel.CBS_Shapes;
import LargeAgents_CBS.Solvers.LowLevel.AStar_Shapes;
import LargeAgents_CBS.Solvers.LowLevel.DistanceTableHeuristic_LargeAgents;

import java.util.ArrayList;
import java.util.Objects;

public class CBS_ShapesRobust extends CBS_Shapes {


    public CBS_ShapesRobust(){
        super(new AStar_RobustShape());
    }


    @Override
    protected void init(MAPF_Instance instance, RunParameters runParameters) {
        this.init(instance, runParameters);
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
}
