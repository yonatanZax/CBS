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


    public void add(Constraint constraint){

        Constraint_Robust constraintRobust = (Constraint_Robust) constraint;

        for (int time = constraintRobust.lowerBound; time <= constraintRobust.upperBound; time++) {
            this.addConstraint(constraintRobust.getConstraint(time));
        }

    }

    private void addConstraint(Constraint constraint){
        I_ConstraintGroupingKey dummy = createDummy(constraint);
        this.constraints.computeIfAbsent(dummy, k -> new HashSet<>());
        add(this.constraints.get(dummy), constraint);
    }


    public boolean rejects(Move move){
        I_ConstraintGroupingKey dummy = createDummy(move);
        if(!constraints.containsKey(dummy)) {
            return false;
        }
        else {
            return rejects(constraints.get(dummy), move);
        }
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
