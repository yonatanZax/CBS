package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.AgentAtGoal;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import BasicCBS.Solvers.ConstraintsAndConflicts.VertexConflict;

public class ConflictManager_LargeAgent extends ConflictManager_Shapes {


    public ConflictManager_LargeAgent(ConflictSelectionStrategy conflictSelectionStrategy) {
        super(conflictSelectionStrategy); }

    public ConflictManager_LargeAgent(ConflictManager_Shapes other) { super(other); }


    @Override
    protected VertexConflict createVertexConflict(Agent agent1, Agent agent2, TimeLocation timeLocation) {
        return new VertexConflict_LargeAgent(agent1, agent2, timeLocation);
    }

    @Override
    protected SwappingConflict createSwappingConflict(Agent agent1, Agent agent2, int time, I_Location agent1_destination, I_Location agent2_destination) {
        return new SwappingConflict_LargeAgents(agent1, agent2, time, agent1_destination, agent2_destination);
    }



}
