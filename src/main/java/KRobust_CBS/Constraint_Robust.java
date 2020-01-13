package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.Move;

import java.util.Objects;

public class Constraint_Robust extends Constraint {

    int upperBound;
    int lowerBound;

    public Constraint_Robust(Agent agent, int time, I_Location prevLocation, I_Location location) {
        super(agent, time, prevLocation, location);
        this.setBounds();
    }


    public Constraint_Robust(Agent agent, int time, I_Location location) {
        super(agent, time, location);
        this.setBounds();
    }

    private void setBounds(){
        this.upperBound = this.time;
//        this.upperBound = this.time + ((RobustAgent)this.agent).k;
//        this.lowerBound = this.time;
        this.lowerBound = Math.max(0,this.time - ((RobustAgent)this.agent).k);
    }


    public boolean accepts(Move move){
        if(move == null) throw new IllegalArgumentException();
        // todo - changed to intersects with
        if( this.location.intersectsWith(move.prevLocation) && this.time == 1){
            return false;
        }
//        boolean intersectsWithPrev = this.location.intersectsWith(move.prevLocation);
        boolean intersectsWithCur = this.location.intersectsWith(move.currLocation);
        boolean timeAgent = this.inRange(move.timeNow) && this.agent.equals(move.agent);
        boolean accepts = !((intersectsWithCur) && timeAgent);

        return accepts;
    }


    public boolean inRange(int time){
        boolean inRange =  this.lowerBound <= time && time <= this.upperBound;
        return inRange;
    }


    public static ConstraintSet getConstraints(Constraint constraint){
        if( !(constraint instanceof Constraint_Robust)){
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.add(constraint);
            return constraintSet;
        }

        Constraint_Robust constraintRobust = (Constraint_Robust) constraint;
        ConstraintSet_Robust newConstraintSet = new ConstraintSet_Robust();
        int upperBound = constraintRobust.upperBound;
        int lowerBound = constraintRobust.lowerBound;

        for (int time = lowerBound; time <= upperBound; time++) {
            newConstraintSet.add(new Constraint(constraintRobust.agent,time,constraintRobust.location));
        }

        return newConstraintSet;
    }


    public Constraint getConstraint(int time){
        return new Constraint_Robust(this.agent, time, this.prevLocation, this.location);
//        return new Constraint(this.agent, time, this.prevLocation, this.location);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constraint_Robust)) return false;
        if (!super.equals(o)) return false;
        Constraint_Robust that = (Constraint_Robust) o;
        return upperBound == that.upperBound &&
                lowerBound == that.lowerBound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), upperBound, lowerBound);
    }
}
