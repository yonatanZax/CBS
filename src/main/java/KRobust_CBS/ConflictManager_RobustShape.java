package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import BasicCBS.Solvers.SingleAgentPlan;
import GraphMapPackage.GraphMapVertex_LargeAgents;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;
import LargeAgents_CBS.Solvers.HighLevel.ConflictManager_Shapes;

import java.util.HashSet;
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


        Set<TimeLocation> timeLocationSet = new HashSet<>();

        /*  Check for conflicts and Add timeLocations */
        for (int time = agentFirstMoveTime; time <= goalTime; time++) {

            // Move's from location is 'prevLocation' , therefor timeLocation is time - 1
            RobustShape robustShape = (RobustShape) singleAgentPlan.moveAt(time).prevLocation;
            timeLocationSet.addAll(robustShape.getAllTimeLocations());
        }
        // final move to goalLocation
        RobustShape robustShapeAtGoal = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;
        timeLocationSet.addAll(robustShapeAtGoal.getAllTimeLocations());

        for (TimeLocation timeLocation : timeLocationSet) {
            checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
            this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
        }


        // Checks for conflicts and add if exists. Adds the goal's timeLocation
        this.manageGoalLocationFromPlan(goalTime, singleAgentPlan);
    }


    @Override
    protected void manageGoalLocationFromPlan(int goalTime, SingleAgentPlan singleAgentPlan) {

        RobustShape goalRobustLocation = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;
        for (TimeLocation goalTimeLocation: goalRobustLocation.getAllTimeLocations()) {

            /*  = Add goal timeLocation =  */
            this.timeLocationTables.addGoalTimeLocation(goalTimeLocation, singleAgentPlan);

            /*  = Check if this agentAtGoal conflicts with other agents =   */
            checkAddSwappingConflicts(goalTime, singleAgentPlan);
            checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);

        }
    }


}
