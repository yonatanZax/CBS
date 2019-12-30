package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.Move;

public class Constraint_Robust extends Constraint {

    int upperBound;
    int lowerBound;

    public Constraint_Robust(Agent agent, int time, I_Location prevLocation, I_Location location) {
        super(agent, time, prevLocation, location);
        this.upperBound = time;
        this.lowerBound = Math.max(0, time - ((RobustAgent)agent).k);
    }


    public Constraint_Robust(Agent agent, int time, I_Location location) {
        super(agent, time, location);
        this.upperBound = time;
        this.lowerBound = Math.max(0, time - ((RobustAgent)agent).k);
    }


    public boolean accepts(Move move){
        if(move == null) throw new IllegalArgumentException();
        // todo - changed to intersects with
        return ! this.location.intersectsWith(move.currLocation) || !this.inRange(move.timeNow)
                /*the constraint is limited to a specific agent, and that agent is different*/
                || (this.agent != null && !this.agent.equals(move.agent)
                /*the previous location is not null, and different*/
                || (this.prevLocation != null &&  !move.prevLocation.intersectsWith(this.prevLocation) ));
    }


    private boolean inRange(int time){
        return this.lowerBound <= time && time <= this.upperBound;
    }


}
