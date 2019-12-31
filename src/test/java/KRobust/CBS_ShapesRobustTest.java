package KRobust;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import GraphMapPackage.MapFactory;
import KRobust_CBS.CBS_ShapesRobust;
import KRobust_CBS.RobustAgent;
import org.junit.jupiter.api.Test;

public class CBS_ShapesRobustTest {

    I_Solver solver = new CBS_ShapesRobust();


    final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    final Enum_MapCellType w = Enum_MapCellType.WALL;
    Enum_MapCellType[][] map_2D_H = {
            { e, w, w, e},
            { e, e, e, e},
            { e, w, w, e},
    };

    I_Map map_h = MapFactory.newSimple4Connected2D_GraphMap(map_2D_H);

    @Test
    public void smallInstance(){

        RobustAgent agent1 = new RobustAgent(1, new Coordinate_2D(0,0),
                                                   new Coordinate_2D(0,3),
                                            2);

        RobustAgent agent2 = new RobustAgent(2, new Coordinate_2D(2,0),
                                                   new Coordinate_2D(2,3),
                                            2);

        Agent[] agents = new Agent[]{agent1, agent2};

        MAPF_Instance instance = new MAPF_Instance("Small Instance", map_h, agents);
        Solution solution = solver.solve(instance, new RunParameters());
        System.out.println("Valid: " + solution.isValidSolution());

    }
}