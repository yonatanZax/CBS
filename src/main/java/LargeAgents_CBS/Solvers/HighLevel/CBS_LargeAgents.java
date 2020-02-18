package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.I_ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.MinTimeConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.SingleAgentPlan;
import Environment.Metrics.S_Metrics;
import LargeAgents_CBS.Solvers.Constraints.ConstraintSet_LargeAgents;
import LargeAgents_CBS.Solvers.LowLevel.AStar_LargeAgents;
import LargeAgents_CBS.Solvers.LowLevel.DistanceTableHeuristic_LargeAgents;

import java.util.ArrayList;
import java.util.Objects;

public class CBS_LargeAgents extends CBS_Solver {

    public CBS_LargeAgents(){
        super(new AStar_LargeAgents(),null,null,null,null);
    }


    @Override
    protected void init(MAPF_Instance instance, RunParameters runParameters) {
        super.init(instance, runParameters);
    }


    protected void initCBS(MAPF_Instance instance, RunParameters parameters){
        if(instance == null || parameters == null){throw new IllegalArgumentException();}

        this.initialConstraints = Objects.requireNonNullElseGet(parameters.constraints, ConstraintSet::new);
        this.currentConstraints = new ConstraintSet_LargeAgents();
        this.generatedNodes = 0;
        this.expandedNodes = 0;
        this.instance = instance;
        this.aStarHeuristic = this.lowLevelSolver instanceof SingleAgentAStar_Solver ?
                new DistanceTableHeuristic_LargeAgents(new ArrayList<>(this.instance.agents), this.instance.map) :
                null;
    }


    // todo - add method
    protected boolean isConstraintOnStartPosition(Constraint constraint){
        return false;
    }


    @Override
    protected I_ConflictManager getConflictAvoidanceTableFor(CBS_Solver.CBS_Node node) {

        I_ConflictManager conflictManager = new ConflictManager_LargeAgent(new MinTimeConflictSelectionStrategy());
        for (SingleAgentPlan plan :
                node.getSolution()) {
            conflictManager.addPlan(plan);
        }
        return conflictManager;
    }


}
