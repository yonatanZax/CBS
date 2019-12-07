package LargeAgents_CBS.Solvers.LowLevel;

import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.Move;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.ArrayList;
import java.util.List;

public class AStar_LargeAgents extends SingleAgentAStar_Solver {

    // imp - LargeAgent

    @Override
    protected boolean initOpen() {
        // if the existing plan isn't empty, we start from the last move of the existing plan.
        if(existingPlan.size() > 0){
            Move lastExistingMove = existingPlan.moveAt(existingPlan.getEndTime());
            // We assume that we cannot change the existing plan, so if it is rejected by constraints, we can't initialise OPEN.
            if(constraints.rejects(lastExistingMove)) {return false;}

            openList.add(new SingleAgentAStar_Solver.AStarState(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size()));
        }
        else { // the existing plan is empty (no existing plan)

            I_Location sourceCell = new GraphLocationGroup(agent.source, map);
            // can move to neighboring cells or stay put
            List<I_Location> neighborCellsIncludingCurrent = new ArrayList<>(sourceCell.getNeighbors());
            neighborCellsIncludingCurrent.add(sourceCell);

            for (I_Location destination: neighborCellsIncludingCurrent) {
                Move possibleMove = new Move(agent, problemStartTime + 1, sourceCell, destination);
                if (constraints.accepts(possibleMove)) { //move not prohibited by existing constraint
                    SingleAgentAStar_Solver.AStarState rootState = new SingleAgentAStar_Solver.AStarState(possibleMove, null, 1);
                    openList.add(rootState);
                    generatedNodes++;
                }
            }

        }

        // if none of the root nodes was valid, OPEN will be empty, and thus uninitialised.
        return !openList.isEmpty();
    }

}
