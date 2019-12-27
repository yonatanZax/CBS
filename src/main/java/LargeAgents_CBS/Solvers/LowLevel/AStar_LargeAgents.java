package LargeAgents_CBS.Solvers.LowLevel;

import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.RunParameters;
import GraphMapPackage.GraphMapVertex_LargeAgents;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;
import LargeAgents_CBS.Solvers.Constraints.ConstraintSet_LargeAgents;

import java.util.ArrayList;
import java.util.List;

public class AStar_LargeAgents extends SingleAgentAStar_Solver {


    @Override
    protected boolean initOpen() {
        // if the existing plan isn't empty, we start from the last move of the existing plan.
        if(existingPlan.size() > 0){
            Move lastExistingMove = existingPlan.moveAt(existingPlan.getEndTime());
            GraphLocationGroup refPointGroup_cur = ((GraphLocationGroup)lastExistingMove.currLocation).getReferencePointAsGroup();
            GraphLocationGroup refPointGroup_prev = ((GraphLocationGroup)lastExistingMove.prevLocation).getReferencePointAsGroup();
            Move refPointMove = new Move(lastExistingMove.agent, lastExistingMove.timeNow, refPointGroup_prev, refPointGroup_cur);
            // We assume that we cannot change the existing plan, so if it is rejected by constraints, we can't initialise OPEN.

            if(constraints.rejects(refPointMove)) {return false;}

            AStarState state = new AStarState(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size());
            openList.add(state);
        }
        else { // the existing plan is empty (no existing plan)

            I_Location sourceCell = new GraphLocationGroup(agent.source, map);
            // can move to neighboring cells or stay put
            List<I_Location> neighborCellsIncludingCurrent = new ArrayList<>(sourceCell.getNeighbors());
            neighborCellsIncludingCurrent.add(sourceCell);

            for (I_Location destination: neighborCellsIncludingCurrent) {
                Move possibleMove = new Move(agent, problemStartTime + 1, sourceCell, destination);

                GraphLocationGroup referencePoint_cur = ((GraphLocationGroup)possibleMove.currLocation).getReferencePointAsGroup();
                GraphLocationGroup referencePoint_prev = ((GraphLocationGroup)possibleMove.prevLocation).getReferencePointAsGroup();
                Move refPointMove = new Move(possibleMove.agent, possibleMove.timeNow,  referencePoint_prev, referencePoint_cur);
                if (constraints.accepts(refPointMove)) { //move not prohibited by existing constraint
                    AStarState rootState = new AStarState(possibleMove, null, 1);
                    openList.add(rootState);
                    generatedNodes++;
                }
            }
        }

        // if none of the root nodes was valid, OPEN will be empty, and thus uninitialised.
        return !openList.isEmpty();
    }


    protected void initConstraintSet(RunParameters runParameters){
        this.constraints = runParameters.constraints == null ? new ConstraintSet_LargeAgents(): runParameters.constraints;
    }

}
