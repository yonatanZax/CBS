package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.VertexConflict;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.SingleAgentPlan;

public class ConflictManager_KRobust extends ConflictManager {


    public ConflictManager_KRobust(ConflictSelectionStrategy conflictSelectionStrategy){
        super(conflictSelectionStrategy);
    }

    protected void addAgentNewPlan(SingleAgentPlan singleAgentPlan) {

        if ( singleAgentPlan == null ){ return; }

        int k = 0;
        if( singleAgentPlan.agent instanceof  RobustAgent){
            k = ((RobustAgent)singleAgentPlan.agent).k;
        }


        int agentFirstMoveTime = singleAgentPlan.getFirstMoveTime();
        int goalTime = singleAgentPlan.getEndTime();
        I_Location goalLocation = singleAgentPlan.moveAt(goalTime).currLocation;

        /* Add tail */
        for (int kTime = goalTime + 1; kTime <= goalTime + k; kTime++) {
            singleAgentPlan.addMove(new Move(singleAgentPlan.agent, kTime, goalLocation, goalLocation));
        }


        int kGoalTime = singleAgentPlan.getEndTime();

        /*  Check for conflicts and Add timeLocations */
        for (int time = agentFirstMoveTime; time <= kGoalTime; time++) {

            I_Location location = singleAgentPlan.moveAt(time).prevLocation;
            for (int kTime = time; kTime <= time + k; kTime++) {
                // Move's from location is 'prevLocation' , therefor timeLocation is time - 1
                TimeLocation timeLocation = new TimeLocation(kTime - 1, location);

                this.checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
                this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
            }

        }

//         Check final move to goalLocation
        for (kGoalTime = goalTime; kGoalTime <= goalTime + k ; kGoalTime++) {
            TimeLocation timeLocation = new TimeLocation(kGoalTime, goalLocation);
            this.checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
            this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
        }






//        /*  Check for conflicts and Add timeLocations */
//        for (int time = agentFirstMoveTime; time < kGoalTime; time++) {
//
//            I_Location location = singleAgentPlan.moveAt(time).currLocation;
////            I_Location location = singleAgentPlan.moveAt(time).prevLocation;
//            for (int kTime = time; kTime < time + k; kTime++) {
//                // Move's from location is 'prevLocation' , therefor timeLocation is time - 1
//                TimeLocation timeLocation = new TimeLocation(kTime - 1, location);
////                TimeLocation timeLocation = new TimeLocation(kTime - 1, location);
//
//                this.checkAddConflictsByTimeLocation(timeLocation, singleAgentPlan); // Checks for conflicts
//                this.timeLocationTables.addTimeLocation(timeLocation, singleAgentPlan);
//            }
//
//        }




        // Checks for conflicts and add if exists. Adds the goal's timeLocation
        this.manageGoalLocationFromPlan(goalTime, singleAgentPlan);
    }


    protected void manageGoalLocationFromPlan(int goalTime, SingleAgentPlan singleAgentPlan) {

        int k = 0;
        if( singleAgentPlan.agent instanceof  RobustAgent){
            k = ((RobustAgent)singleAgentPlan.agent).k;
        }
        singleAgentPlan.removeLastKMoves(k);


        I_Location goalLocation = singleAgentPlan.moveAt(goalTime).currLocation;
        TimeLocation goalTimeLocation = new TimeLocation(goalTime, goalLocation);

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        this.checkAddSwappingConflicts(goalTime, singleAgentPlan);
        this.checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);


        /*  = Add goal timeLocation =  */
        this.timeLocationTables.addGoalTimeLocation(goalTimeLocation, singleAgentPlan);
    }


    protected VertexConflict createVertexConflict(Agent agent1, Agent agent2, TimeLocation timeLocation){
        return new VertexConflict_KRobust(agent1, agent2, timeLocation);
    }


}
