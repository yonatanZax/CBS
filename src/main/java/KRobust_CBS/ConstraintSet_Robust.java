package KRobust_CBS;

import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.I_ConstraintGroupingKey;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.TimeAgent;
import BasicCBS.Solvers.Move;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConstraintSet_Robust extends ConstraintSet {


    public boolean rejects(Move move){
        I_ConstraintGroupingKey dummy = createDummy(move);

        for (Map.Entry entry : this.constraints.entrySet()) {
            Set<Constraint_Robust> set = (Set<Constraint_Robust>) entry.getValue();
            for (Constraint_Robust constraintRobust: set) {
                if(constraintRobust.agent == move.agent && constraintRobust.inRange(move.timeNow)){
                    return constraintRobust.rejects(move);
                }
            }

        }
//        if(!constraints.containsKey(dummy)) {
//            return false;
//        }
//        else {
//            return rejects(constraints.get(dummy), move);
//        }
        return false;
    }


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
