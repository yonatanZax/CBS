package LargeAgents_CBS.Solvers.Constraints;

import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.*;
import BasicCBS.Solvers.Move;
import GraphMapPackage.GraphMapVertex_LargeAgents;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.Set;

public class ConstraintSet_LargeAgents extends ConstraintSet {



    protected boolean rejects(Set<Constraint> constraints, Move move){
        GraphLocationGroup prev = ((GraphLocationGroup)move.prevLocation).getReferencePointAsGroup();
        GraphLocationGroup current = ((GraphLocationGroup)move.currLocation).getReferencePointAsGroup();
        Move refPointMove = new Move(move.agent, move.timeNow,  prev, current);
        for (Constraint constraint : constraints){
            if(constraint.rejects(refPointMove)) return true;
        }
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
