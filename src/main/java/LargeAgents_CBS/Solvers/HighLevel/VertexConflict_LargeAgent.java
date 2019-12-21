package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.VertexConflict;
import LargeAgents_CBS.Instances.LargeAgent;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

public class VertexConflict_LargeAgent extends VertexConflict {

    public VertexConflict_LargeAgent(Agent agent1, Agent agent2, int time, I_Location location) {
        super(agent1, agent2, time, location);
    }

    public VertexConflict_LargeAgent(Agent agent1, Agent agent2, TimeLocation timeLocation) {
        super(agent1, agent2, timeLocation);
    }


    @Override
    public Constraint[] getPreventingConstraints() {

        GraphLocationGroup expandedGroups_location1 = GraphLocationGroup.expendByReferencePoint((GraphLocationGroup) location, ((LargeAgent) agent1).getHeight());
        GraphLocationGroup expandedGroups_location2 = GraphLocationGroup.expendByReferencePoint((GraphLocationGroup) location, ((LargeAgent) agent1).getHeight());


        return new Constraint[]{
                                    new Constraint(agent1, time, expandedGroups_location1),
                                    new Constraint(agent2, time, expandedGroups_location2)
        };
    }


}
