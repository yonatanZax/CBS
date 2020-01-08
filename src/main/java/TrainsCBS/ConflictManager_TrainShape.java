package TrainsCBS;

import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.SingleAgentPlan;
import KRobust_CBS.ConflictManager_RobustShape;
import KRobust_CBS.RobustAgent;
import KRobust_CBS.RobustShape;

import java.util.Set;

public class ConflictManager_TrainShape extends ConflictManager_RobustShape {



    public ConflictManager_TrainShape(ConflictSelectionStrategy conflictSelectionStrategy) {
        super(conflictSelectionStrategy);
    }

    public ConflictManager_TrainShape(ConflictManager_RobustShape other) {
        super(other);
    }






    @Override
    protected void addAgentNewPlan(SingleAgentPlan singleAgentPlan) {

        if ( singleAgentPlan == null ){
            return;
        }


        int agentFirstMoveTime = singleAgentPlan.getFirstMoveTime();
        int goalTime = singleAgentPlan.getEndTime();
        TrainShape goalRobustLocation = (TrainShape) singleAgentPlan.moveAt(goalTime).currLocation;


        /*  Add tail locations  */
        goalRobustLocation = TrainShape.stayInGoal(goalRobustLocation);
        int extendedGoalTime = goalTime;

        SingleAgentPlan extendedPlan = singleAgentPlan;
        while (goalRobustLocation != null){
            extendedGoalTime++;
            extendedPlan.addMove(new Move(singleAgentPlan.agent, extendedGoalTime, singleAgentPlan.moveAt(extendedGoalTime - 1).currLocation, goalRobustLocation));
            goalRobustLocation = TrainShape.stayInGoal(goalRobustLocation);
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
        int k = ((RobustAgent)singleAgentPlan.agent).k;
        int extendedGoalTime = singleAgentPlan.getEndTime();
//        singleAgentPlan.removeLastKMoves( extendedGoalTime - goalTime);
        goalTime = singleAgentPlan.getEndTime();
        TrainShape goalLocation = (TrainShape) singleAgentPlan.moveAt(goalTime).currLocation;
        TimeLocation goalTimeLocation = new TimeLocation (goalTime, goalLocation.getHeadLocation());


        /*  = Add goal timeLocation =  */
        this.timeLocationTables.addGoalTimeLocation(goalTimeLocation, singleAgentPlan);

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        checkAddSwappingConflicts(goalTime, singleAgentPlan);
        checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);
    }
}
