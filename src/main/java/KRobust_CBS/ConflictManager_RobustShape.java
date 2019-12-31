package KRobust_CBS;

import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.SingleAgentPlan;

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



        /*  Check for conflicts and Add timeLocations */
        for (int time = agentFirstMoveTime; time <= goalTime; time++) {

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

        RobustShape goalRobustLocation = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;
        TimeLocation goalTimeLocation = goalRobustLocation.getHead();

        /*  = Add goal timeLocation =  */
        this.timeLocationTables.addGoalTimeLocation(goalTimeLocation, singleAgentPlan);

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        checkAddSwappingConflicts(goalTime, singleAgentPlan);
        checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);
    }

}
