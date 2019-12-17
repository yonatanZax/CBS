package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import BasicCBS.Solvers.SingleAgentPlan;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;
import GraphMapPackage.GraphMapVertex_LargeAgents;

import java.util.Set;

public class ConflictManager_Shapes extends ConflictManager {



    public ConflictManager_Shapes(ConflictSelectionStrategy conflictSelectionStrategy){
        super(conflictSelectionStrategy);
    }

    public ConflictManager_Shapes(ConflictManager_Shapes other){
        super(other);
    }


    @Override
    protected void addAgentNewPlan(SingleAgentPlan singleAgentPlan) {

        if ( singleAgentPlan == null ){
            return;
        }

        int agentFirstMoveTime = singleAgentPlan.getFirstMoveTime();
        int goalTime = singleAgentPlan.getEndTime();

        /*  Check for conflicts and Add timeLocations */
        for (int time = agentFirstMoveTime; time <= goalTime; time++) {

            // Imp - change location to Group

            // Move's from location is 'prevLocation' , therefor timeLocation is time - 1
            GraphLocationGroup locationGroup = (GraphLocationGroup) singleAgentPlan.moveAt(time).prevLocation;
            for (GraphMapVertex_LargeAgents mapCellLocation: locationGroup.getAllCells()) {
                TimeLocation timeLocation = new TimeLocation(time - 1, mapCellLocation);
                super.checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
                this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
            }
        }

        // Check final move to goalLocation
        GraphLocationGroup locationGroup = (GraphLocationGroup) singleAgentPlan.moveAt(goalTime).currLocation;
        for (GraphMapVertex_LargeAgents mapCellLocation: locationGroup.getAllCells()) {
            TimeLocation goalTimeLocation = new TimeLocation(goalTime, mapCellLocation);
            super.checkAddConflictsByTimeLocation(goalTimeLocation, singleAgentPlan); // Checks for conflicts
            this.timeLocationTables.addTimeLocation(goalTimeLocation, singleAgentPlan);
        }



        // Checks for conflicts and add if exists. Adds the goal's timeLocation
        this.manageGoalLocationFromPlan(goalTime, singleAgentPlan);
    }


    @Override
    protected void manageGoalLocationFromPlan(int goalTime, SingleAgentPlan singleAgentPlan) {

        // Imp - change location to Group
        GraphLocationGroup goalGroupLocation = (GraphLocationGroup) singleAgentPlan.moveAt(goalTime).currLocation;
        for (GraphMapVertex_LargeAgents mapCellLocation: goalGroupLocation.getAllCells()) {
            TimeLocation goalCellTimeLocation = new TimeLocation(goalTime, mapCellLocation);

            /*  = Check if this agentAtGoal conflicts with other agents =   */
            super.checkAddSwappingConflicts(goalTime, singleAgentPlan);
            super.checkAddVertexConflictsWithGoal(goalCellTimeLocation, singleAgentPlan);

            /*  = Add goal timeLocation =  */
            this.timeLocationTables.addGoalTimeLocation(goalCellTimeLocation, singleAgentPlan);
        }
    }

    @Override
    protected void checkAddSwappingConflicts(int time, SingleAgentPlan singleAgentPlan) {
        if (time < 1) {
            return;
        }
        GraphLocationGroup previousGroupLocation = (GraphLocationGroup) singleAgentPlan.moveAt(time).prevLocation;

        for (I_Location previousCellLocation : previousGroupLocation.getAllCells()) {
            TimeLocation timeLocation = new TimeLocation(time, previousCellLocation);
            Set<Agent> agentsMovingToPrevLocations = this.timeLocationTables.timeLocation_Agents.get(timeLocation);
            if (agentsMovingToPrevLocations == null) {
                return;
            }

            /* Add conflict with all the agents that:
                1. Coming from agent's moveAt(time).currLocation
                2. Going to agent's moveAt(time).prevLocation
            */
            for (Agent agentMovingToPrevPosition : agentsMovingToPrevLocations) {
                if (agentMovingToPrevPosition.equals(singleAgentPlan.agent)) {
                    continue; /* Self Conflict */
                }

                GraphLocationGroup nextGroupLocation = (GraphLocationGroup) singleAgentPlan.moveAt(time).currLocation;
                for (I_Location nextCellLocation : nextGroupLocation.getAllCells()) {
                    if (this.agent_plan.get(agentMovingToPrevPosition).moveAt(time).prevLocation.equals(nextGroupLocation)) {

                        // Create two conflicts
                        SwappingConflict swappingConflict_addedAgentFirst = new SwappingConflict(   singleAgentPlan.agent,
                                                                                                    agentMovingToPrevPosition,
                                                                                                    time,
                                                                                                    nextCellLocation,
                                                                                                    previousCellLocation);

                        SwappingConflict swappingConflict_addedAgentSecond = new SwappingConflict(  agentMovingToPrevPosition,
                                                                                                    singleAgentPlan.agent,
                                                                                                    time,
                                                                                                    previousCellLocation,
                                                                                                    nextCellLocation);


                        // Add conflicts to both of the agents
                        this.allConflicts.add(swappingConflict_addedAgentFirst);
                        this.allConflicts.add(swappingConflict_addedAgentSecond);
                    }
                }

            }


        }
    }

}
