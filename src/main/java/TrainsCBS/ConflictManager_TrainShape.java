package TrainsCBS;

import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.SingleAgentPlan;
import KRobust_CBS.ConflictManager_RobustShape;
import KRobust_CBS.RobustAgent;
import KRobust_CBS.RobustShape;

public class ConflictManager_TrainShape extends ConflictManager_RobustShape {

    public ConflictManager_TrainShape(ConflictSelectionStrategy conflictSelectionStrategy) {
        super(conflictSelectionStrategy);
    }

    public ConflictManager_TrainShape(ConflictManager_RobustShape other) {
        super(other);
    }


    @Override
    protected void manageGoalLocationFromPlan(int goalTime, SingleAgentPlan singleAgentPlan) {


        /*  Remove Last k moves from Plan   */
//        int extendedGoalTime = singleAgentPlan.getEndTime();
        singleAgentPlan.removeLastKMoves( 1);
        goalTime = singleAgentPlan.getEndTime();
        RobustShape goalLocation = (RobustShape) singleAgentPlan.moveAt(goalTime).currLocation;
        TimeLocation goalTimeLocation = new TimeLocation (goalTime, goalLocation.getHeadLocation());


        /*  = Add goal timeLocation =  */
        this.timeLocationTables.addGoalTimeLocation(goalTimeLocation, singleAgentPlan);

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        checkAddSwappingConflicts(goalTime, singleAgentPlan);
        checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);
    }
}
