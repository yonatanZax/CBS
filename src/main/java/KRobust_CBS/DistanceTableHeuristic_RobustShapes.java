package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.AStar.DistanceTableAStarHeuristic;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import LargeAgents_CBS.Instances.Maps.Coordinate_2D_LargeAgent;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.*;

public class DistanceTableHeuristic_RobustShapes extends DistanceTableAStarHeuristic {


    public DistanceTableHeuristic_RobustShapes(ArrayList<Agent> agents, I_Map map) {
        super(agents, map);
    }

    @Override
    public float getH(SingleAgentAStar_Solver.AStarState state) {
        Map<I_Location, Integer> relevantDictionary = this.distanceDictionaries.get(state.getMove().agent);
        RobustShape robustShape = (RobustShape) state.getMove().currLocation;
        I_Location location = robustShape.getHead().location;
        return relevantDictionary.get(location);
    }
}
