package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.VertexConflict;
import GraphMapPackage.GraphMapVertex_LargeAgents;
import LargeAgents_CBS.Instances.LargeAgent;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

public class VertexConflict_KRobust extends VertexConflict {

    public VertexConflict_KRobust(Agent agent1, Agent agent2, int time, I_Location location) {
        super(agent1, agent2, time, location);
    }

    public VertexConflict_KRobust(Agent agent1, Agent agent2, TimeLocation timeLocation) {
        super(agent1, agent2, timeLocation);
    }


    @Override
    public Constraint[] getPreventingConstraints() {

        return new Constraint[]{
                new Constraint_Robust(agent1, time, location),
                new Constraint_Robust(agent2, time, location)
        };
    }

}
