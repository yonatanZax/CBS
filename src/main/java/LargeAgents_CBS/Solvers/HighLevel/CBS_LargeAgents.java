package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.I_ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.SingleAgentPlan;
import Environment.Metrics.S_Metrics;
import LargeAgents_CBS.Solvers.LowLevel.AStar_LargeAgents;
import LargeAgents_CBS.Solvers.LowLevel.DistanceTableHeuristic_LargeAgents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CBS_LargeAgents extends CBS_Solver {


    public CBS_LargeAgents(){
        super(new AStar_LargeAgents(),null,null,null,null);
    }


    @Override
    protected void init(MAPF_Instance instance, RunParameters runParameters) {
        this.initLargeAgents(instance, runParameters);
        this.initialConstraints = Objects.requireNonNullElseGet(runParameters.constraints, ConstraintSet::new);
        this.currentConstraints = new ConstraintSet();
        this.generatedNodes = 0;
        this.expandedNodes = 0;
        this.instance = instance;
        this.aStarHeuristic = this.lowLevelSolver instanceof SingleAgentAStar_Solver ?
                new DistanceTableHeuristic_LargeAgents(new ArrayList<>(this.instance.agents), this.instance.map) :
                null;
    }


    private void initLargeAgents(MAPF_Instance instance, RunParameters parameters){
        if(instance == null || parameters == null){throw new IllegalArgumentException();}

        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.abortedForTimeout = false;
        this.totalLowLevelStatesGenerated = 0;
        this.totalLowLevelStatesExpanded = 0;
        this.maximumRuntime = (parameters.timeout >= 0) ? parameters.timeout : this.DEFAULT_TIMEOUT;
        this.instanceReport = parameters.instanceReport == null ? S_Metrics.newInstanceReport()
                : parameters.instanceReport;
        // if we were given a report, we should leave it be. If we created our report locally, then it is unreachable
        // outside the class, and should therefore be committed.
        this.commitReport = parameters.instanceReport == null;
    }



    @Override
    protected I_ConflictManager getConflictAvoidanceTableFor(CBS_Solver.CBS_Node node) {

        I_ConflictManager conflictManager = new ConflictManager_LargeAgents();
        for (SingleAgentPlan plan :
                node.getSolution()) {
            conflictManager.addPlan(plan);
        }
        return conflictManager;
    }


}
