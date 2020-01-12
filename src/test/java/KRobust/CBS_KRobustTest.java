package KRobust;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;
import GraphMapPackage.MapFactory;
import KRobust_CBS.CBS_KRobust;
import KRobust_CBS.CBS_ShapesRobust;
import KRobust_CBS.RobustAgent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CBS_KRobustTest {

    I_Solver solver = new CBS_KRobust();


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

        int k = 0;

        RobustAgent agent1 = new RobustAgent(1, new Coordinate_2D(0,0),
                new Coordinate_2D(0,3),
                k);

        RobustAgent agent2 = new RobustAgent(2, new Coordinate_2D(1,0),
                new Coordinate_2D(2,3),
                k);

        Agent[] agents = new Agent[]{agent1, agent2};

        MAPF_Instance instance = new MAPF_Instance("Small Instance", map_h, agents);
        Solution solution = solver.solve(instance, new RunParameters());
        System.out.println("Valid: " + solution.isValidSolution());

    }



    private Enum_MapCellType[][] map_2D_circle = {
            {w, w, w, w, w, w},
            {w, w, e, e, e, w},
            {w, w, e, w, e, w},
            {w, w, e, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };
    private I_Map mapCircle = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(map_2D_circle);

    Enum_MapCellType[][] map_2D_empty = {
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
    };
    private I_Map mapEmpty = MapFactory.newSimple4Connected2D_GraphMap(map_2D_empty);

    Enum_MapCellType[][] map_2D_withPocket = {
            {e, w, e, w, e, w},
            {e, w, e, e, e, e},
            {w, w, e, w, w, e},
            {e, e, e, e, e, e},
            {e, e, w, e, w, w},
            {w, e, w, e, e, e},
    };
    private I_Map mapWithPocket = MapFactory.newSimple4Connected2D_GraphMap(map_2D_withPocket);

    Enum_MapCellType[][] map_2D_smallMaze = {
            {e, e, e, w, e, w},
            {e, w, e, e, e, e},
            {e, w, e, w, w, e},
            {e, e, e, e, e, e},
            {e, e, w, e, w, w},
            {w, w, w, e, e, e},
    };
    private I_Map mapSmallMaze = MapFactory.newSimple4Connected2D_GraphMap(map_2D_smallMaze);

    private I_Coordinate coor12 = new Coordinate_2D(1,2);
    private I_Coordinate coor13 = new Coordinate_2D(1,3);
    private I_Coordinate coor14 = new Coordinate_2D(1,4);
    private I_Coordinate coor22 = new Coordinate_2D(2,2);
    private I_Coordinate coor24 = new Coordinate_2D(2,4);
    private I_Coordinate coor32 = new Coordinate_2D(3,2);
    private I_Coordinate coor33 = new Coordinate_2D(3,3);
    private I_Coordinate coor34 = new Coordinate_2D(3,4);

    private I_Coordinate coor11 = new Coordinate_2D(1,1);
    private I_Coordinate coor43 = new Coordinate_2D(4,3);
    private I_Coordinate coor53 = new Coordinate_2D(5,3);
    private I_Coordinate coor54 = new Coordinate_2D(5,4);
    private I_Coordinate coor05 = new Coordinate_2D(0,5);

    private I_Coordinate coor04 = new Coordinate_2D(0,4);
    private I_Coordinate coor00 = new Coordinate_2D(0,0);
    private I_Coordinate coor01 = new Coordinate_2D(0,1);
    private I_Coordinate coor10 = new Coordinate_2D(1,0);

    private RobustAgent agent33to12 = new RobustAgent(new Agent(0, coor33, coor12));
    private RobustAgent agent12to33 = new RobustAgent(new Agent(1, coor12, coor33));
    private RobustAgent agent53to05 = new RobustAgent(new Agent(2, coor53, coor05));
    private RobustAgent agent43to11 = new RobustAgent(new Agent(3, coor43, coor11));
    private RobustAgent agent04to54 = new RobustAgent(new Agent(4, coor04, coor54));
    private RobustAgent agent00to10 = new RobustAgent(new Agent(5, coor00, coor10));
    private RobustAgent agent10to00 = new RobustAgent(new Agent(6, coor10, coor00));

    private MAPF_Instance instanceEmpty1 = new MAPF_Instance("instanceEmpty", mapEmpty,
            new Agent[]{agent33to12, agent12to33, agent53to05, agent43to11, agent04to54, agent00to10, agent10to00});
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12, agent12to33});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle2", mapCircle, new Agent[]{agent12to33, agent33to12});
    private MAPF_Instance instanceUnsolvable = new MAPF_Instance("instanceUnsolvable", mapWithPocket, new Agent[]{agent00to10, agent10to00});

    I_Solver cbsSolver_robust = new CBS_ShapesRobust();


    void validate(Solution solution, int numAgents, int optimalSOC, int optimalMakespan){
        assertTrue(solution.isValidSolution()); //is valid (no conflicts)

        assertEquals(numAgents, solution.size()); // solution includes all agents
        assertEquals(optimalSOC, solution.sumIndividualCosts()); // SOC is optimal
        assertEquals(optimalMakespan, solution.makespan()); // makespan is optimal
    }

    @Test
    void emptyMapValidityTest1() {
        MAPF_Instance testInstance = instanceEmpty1;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        Solution solved = cbsSolver_robust.solve(testInstance, new RunParameters(instanceReport));
        S_Metrics.removeReport(instanceReport);

        System.out.println(solved.readableToString());
        validate(solved, 7, solved.sumIndividualCosts(),solved.makespan()); //need to find actual optimal costs
    }

    @Test
    void circleMapValidityTest1() {
        MAPF_Instance testInstance = instanceCircle1;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        Solution solved = cbsSolver_robust.solve(testInstance, new RunParameters(System.currentTimeMillis() + (60*60*1000), null, instanceReport, null));
        S_Metrics.removeReport(instanceReport);

        System.out.println(solved.readableToString());
        validate(solved, 2, 8, 5);

    }

    @Test
    void circleMapValidityTest2() {
        MAPF_Instance testInstance = instanceCircle2;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        Solution solved = cbsSolver_robust.solve(testInstance, new RunParameters(instanceReport));
        S_Metrics.removeReport(instanceReport);

        System.out.println(solved.readableToString());
        validate(solved, 2, 8, 5);
    }

    @Test
    void unsolvableShouldBeInvalid() {
        MAPF_Instance testInstance = instanceUnsolvable;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        long defaultTimeout = 1000*10 /* 10 sec*/;
        Solution solved = cbsSolver_robust.solve(testInstance, new RunParameters(defaultTimeout));
        S_Metrics.removeReport(instanceReport);

        assertNull(solved);
    }


}