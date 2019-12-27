package BasicCBS.Solvers.ConstraintsAndConflicts.Constraint;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.Move;

public class TimeAgent implements I_ConstraintGroupingKey {
    public final Agent agent;
    public final int time;

    public TimeAgent(Constraint constraint) {
        this.agent = constraint.agent;
        this.time = constraint.time;
    }

    public TimeAgent(Move move){
        this.agent = move.agent;
        this.time = move.timeNow;
    }

    public TimeAgent(TimeAgent toCopy){
        this.agent = toCopy.agent;
        this.time = toCopy.time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeAgent)) return false;

        TimeAgent that = (TimeAgent) o;

        if (time != that.time) return false;
        return agent.equals(that.agent);

    }

    @Override
    public int hashCode() {
        int result = agent.hashCode();
        result = 31 * result + time;
        return result;
    }

    @Override
    public int getTime() {
        return this.time;
    }

    @Override
    public boolean relevantInTheFuture(Move finalMove) {
        return this.time > finalMove.timeNow && this.agent.equals(finalMove.agent);
    }
}
