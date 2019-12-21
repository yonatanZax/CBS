package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import LargeAgents_CBS.Instances.LargeAgent;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

public class SwappingConflict_LargeAgents extends SwappingConflict {

    public SwappingConflict_LargeAgents(Agent agent1, Agent agent2, int time, I_Location agent1_destination, I_Location agent2_destination) {
        super(agent1, agent2, time, agent1_destination, agent2_destination);
    }


    @Override
    public Constraint[] getPreventingConstraints() {

        GraphLocationGroup expandedGroups_prev1 = GraphLocationGroup.expendByReferencePoint((GraphLocationGroup) agent2_destination, ((LargeAgent)agent1).getHeight());
        GraphLocationGroup expandedGroups_dest1 = GraphLocationGroup.expendByReferencePoint((GraphLocationGroup) location, ((LargeAgent)agent1).getHeight());

        GraphLocationGroup expandedGroups_dest2 = GraphLocationGroup.expendByReferencePoint((GraphLocationGroup) agent2_destination, ((LargeAgent)agent2).getHeight());
        GraphLocationGroup expandedGroups_prev2 = GraphLocationGroup.expendByReferencePoint((GraphLocationGroup) location, ((LargeAgent)agent2).getHeight());



        return new Constraint[]{
                /*
                 the order of locations:
                 agent1 will be prevented from moving from its previous location (agent2's destination) to its destination.
                 */
                new Constraint(agent1, time, expandedGroups_prev1, expandedGroups_dest1),
                /*
                 the order of locations:
                 agent2 will be prevented from moving from its previous location (agent1's destination) to its destination.
                 */
                new Constraint(agent2, time, expandedGroups_prev2, expandedGroups_dest2)};
    }


}
