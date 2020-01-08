package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.SingleAgentPlan;
import GraphMapPackage.GraphMapVertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConflictManager_RobustShape extends ConflictManager {

    public ConflictManager_RobustShape(ConflictSelectionStrategy conflictSelectionStrategy){
        super(conflictSelectionStrategy);
    }

    public ConflictManager_RobustShape(ConflictManager_RobustShape other){
        super(other);
    }


    @Override
    protected void addAgentNewPlan(SingleAgentPlan singleAgentPlan) {

        if ( singleAgentPlan == null ){
            return;
        }


        int agentFirstMoveTime = singleAgentPlan.getFirstMoveTime();
        int goalTime = singleAgentPlan.getEndTime();
        RobustShape goalRobustLocation = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;


        /*  Add tail locations  */
        goalRobustLocation = RobustShape.stayInGoal(goalRobustLocation);
        int extendedGoalTime = goalTime;

        SingleAgentPlan extendedPlan = singleAgentPlan;
        while (goalRobustLocation != null){
            extendedGoalTime++;
            extendedPlan.addMove(new Move(singleAgentPlan.agent, extendedGoalTime, singleAgentPlan.moveAt(extendedGoalTime - 1).currLocation, goalRobustLocation));
            goalRobustLocation = RobustShape.stayInGoal(goalRobustLocation);
        }





        /*  Check for conflicts and Add timeLocations */
        for (int time = agentFirstMoveTime; time <= extendedGoalTime; time++) {

            // Move's from location is 'prevLocation' , therefor timeLocation is time - 1
            RobustShape robustShape = (RobustShape) singleAgentPlan.moveAt(time).prevLocation;
            Set<I_Location> locationSet = robustShape.getAllLocations();

            for (I_Location location : locationSet) {
                TimeLocation timeLocation = new TimeLocation(time-1,location);
                checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
                this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
            }
        }
        // final move to goalLocation
        RobustShape robustShapeAtGoal = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;
        Set<I_Location> locationSet = robustShapeAtGoal.getAllLocations();

        for (I_Location location : locationSet) {
            TimeLocation timeLocation = new TimeLocation(goalTime,location);
            checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
            this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
        }


        // Checks for conflicts and add if exists. Adds the goal's timeLocation
        this.manageGoalLocationFromPlan(goalTime, singleAgentPlan);
    }


    @Override
    protected void manageGoalLocationFromPlan(int goalTime, SingleAgentPlan singleAgentPlan) {


        /*  Remove Last k moves from Plan   */
        int extendedGoalTime = singleAgentPlan.getEndTime();
        singleAgentPlan.removeLastKMoves( extendedGoalTime - goalTime);
        goalTime = singleAgentPlan.getEndTime();
        RobustShape goalLocation = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;
        TimeLocation goalTimeLocation = new TimeLocation (goalTime, goalLocation.getHeadLocation());


        /*  = Add goal timeLocation =  */
        this.timeLocationTables.addGoalTimeLocation(goalTimeLocation, singleAgentPlan);

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        checkAddSwappingConflicts(goalTime, singleAgentPlan);
        checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);
    }


    @Override
    protected void checkAddSwappingConflicts(int time, SingleAgentPlan singleAgentPlan) {
        if( time < 1 ){ return;}
        I_Location previousLocation = ((RobustShape)singleAgentPlan.moveAt(time).prevLocation).getHeadLocation();
        I_Location nextLocation = ((RobustShape)singleAgentPlan.moveAt(time).currLocation).getHeadLocation();
        Set<Agent> agentsMovingToPrevLocations = this.timeLocationTables.timeLocation_Agents.get(new TimeLocation(time,previousLocation));
        if ( agentsMovingToPrevLocations == null ){ return; }

        /* Add conflict with all the agents that:
            1. Coming from agent's moveAt(time).currLocation
            2. Going to agent's moveAt(time).prevLocation
        */
        for (Agent agentMovingToPrevPosition : agentsMovingToPrevLocations) {
            if( agentMovingToPrevPosition.equals(singleAgentPlan.agent) ){ continue; /* Self Conflict */ }
            // todo - change to intersectWith
            SingleAgentPlan agentMovingToPrevLocationPlan = this.agent_plan.get(agentMovingToPrevPosition);
            if( time <= agentMovingToPrevLocationPlan.getEndTime() ){
                I_Location agentMovingToPrevPosition_previousLocation = ((RobustShape)this.agent_plan.get(agentMovingToPrevPosition).moveAt(time).prevLocation).getHeadLocation();
                if ( agentMovingToPrevPosition_previousLocation.intersectsWith(nextLocation)){

                    // Create two conflicts
                    SwappingConflict swappingConflict_addedAgentFirst = createSwappingConflict( singleAgentPlan.agent,
                                                                                                agentMovingToPrevPosition,
                                                                                                time,
                                                                                                nextLocation,
                                                                                                previousLocation);

                    SwappingConflict swappingConflict_addedAgentSecond = createSwappingConflict(agentMovingToPrevPosition,
                                                                                                singleAgentPlan.agent,
                                                                                                time,
                                                                                                previousLocation,
                                                                                                nextLocation);


                    // Add conflicts to both of the agents
                    this.allConflicts.add(swappingConflict_addedAgentFirst);
                    this.allConflicts.add(swappingConflict_addedAgentSecond);
                }
            }

        }
    }

}
