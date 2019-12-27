package LargeAgents_CBS.Solvers.LowLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.AStar.DistanceTableAStarHeuristic;
import LargeAgents_CBS.Instances.Maps.Coordinate_2D_LargeAgent;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DistanceTableHeuristic_LargeAgents extends DistanceTableAStarHeuristic {


    public DistanceTableHeuristic_LargeAgents(List<? extends Agent> agents, I_Map map) {

        super.distanceDictionaries = new HashMap<>();
        for (int i = 0; i < agents.size(); i++) {

            //this map will entered to distanceDictionaries for every agent
            Map<I_Location, Integer> mapForAgent = new HashMap<>();
            this.distanceDictionaries.put(agents.get(i), mapForAgent);
            LinkedList<I_Location> queue = new LinkedList<>();
            Coordinate_2D_LargeAgent coordinate_2D_largeAgent = (Coordinate_2D_LargeAgent) agents.get(i).target;

            GraphLocationGroup graphLocationGroup = new GraphLocationGroup(coordinate_2D_largeAgent.getCoordinates(), map);
            // imp - change to LargeAgents
            I_Location locationGroup = graphLocationGroup;

            //distance of a graphMapCell from itself
            this.distanceDictionaries.get(agents.get(i)).put(locationGroup, 0);

            //all the neighbors of a graphMapCell
            List<I_Location> neighbors = locationGroup.getNeighbors();
            if (neighbors == null){
                System.out.println();
            }
            queue.addAll(neighbors);


            int distance = 1;
            int count = queue.size();

            while (!(queue.isEmpty())) {
                I_Location i_location = queue.remove(0);

                //if a graphMapCell didn't got a distance yet
                if (!(this.distanceDictionaries.get(agents.get(i)).containsKey(i_location))) {
                    this.distanceDictionaries.get(agents.get(i)).put(i_location, distance);

                    //add all the neighbors of the current graphMapCell to  the queue
                    List<I_Location> neighborsCell = i_location.getNeighbors();
                    queue.addAll(neighborsCell);
                }

                count--;
                if (count == 0) { //full level/round of neighbors is finish
                    distance++;
                    count = queue.size(); //start new level with distance plus one
                }
            }
        }
    }



}
