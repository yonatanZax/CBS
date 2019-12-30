package KRobust_CBS;

import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.I_ConstraintGroupingKey;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.TimeAgent;
import BasicCBS.Solvers.Move;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.Set;

public class ConstraintSet_Robust extends ConstraintSet {



    public void trimToTimeRange(int minTime, int maxTime){
        this.constraints.keySet().removeIf(cw -> ((TimeAgent)cw).time < minTime || ((TimeAgent)cw).time >= maxTime);
    }


    protected I_ConstraintGroupingKey createDummy(Constraint constraint){
        return new TimeAgent(constraint);
    }

    protected I_ConstraintGroupingKey createDummy(Move move){
        return new TimeAgent(move);
    }


}
