package TrainsCBS;

import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.I_ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.MinTimeConflictSelectionStrategy;
import BasicCBS.Solvers.SingleAgentPlan;
import KRobust_CBS.AStar_RobustShape;
import KRobust_CBS.CBS_ShapesRobust;
import KRobust_CBS.ConflictManager_RobustShape;

public class CBS_TrainShape extends CBS_ShapesRobust {

    public CBS_TrainShape(){
        super(new AStar_TrainShape());
    }

    @Override
    protected I_ConflictManager getConflictAvoidanceTableFor(CBS_Solver.CBS_Node node) {

        I_ConflictManager conflictManager = new ConflictManager_TrainShape(new MinTimeConflictSelectionStrategy());
        for (SingleAgentPlan plan : node.getSolution()) {
            conflictManager.addPlan(plan);
        }
        return conflictManager;
    }

}
