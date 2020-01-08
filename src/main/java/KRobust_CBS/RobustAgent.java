package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;

import java.util.Objects;

public class RobustAgent extends Agent {

    public int k = 0;

    public RobustAgent(int iD, I_Coordinate source, I_Coordinate target, int k) {
        super(iD, source, target);
        this.k = k;
    }

    public RobustAgent(Agent agent){
        super(agent.iD, agent.source, agent.target);
        this.k = 0;
    }

    public RobustAgent(Agent agent, int k) {
        super(agent.iD, agent.source, agent.target);
        this.k = k;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RobustAgent)) return false;
        if (!super.equals(o)) return false;
        RobustAgent that = (RobustAgent) o;
        return k == that.k;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), k);
    }
}
