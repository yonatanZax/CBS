package LargeAgents;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.CBS.CBS_SolverTest;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import Environment.IO_Package.IO_Manager;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;
import GraphMapPackage.MapFactory;
import LargeAgents_CBS.Instances.InstanceBuilder_BGU_LA;
import LargeAgents_CBS.Instances.LargeAgent;
import LargeAgents_CBS.Solvers.HighLevel.CBS_Shapes;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CBS_ShapesTest {

    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;
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
    private I_Map mapEmpty = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(map_2D_empty);

    Enum_MapCellType[][] map_2D_withPocket = {
            {e, w, e, w, e, w},
            {e, w, e, e, e, e},
            {w, w, e, w, w, e},
            {e, e, e, e, e, e},
            {e, e, w, e, w, w},
            {w, e, w, e, e, e},
    };
    private I_Map mapWithPocket = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(map_2D_withPocket);

    Enum_MapCellType[][] map_2D_smallMaze = {
            {e, e, e, w, e, w},
            {e, w, e, e, e, e},
            {e, w, e, w, w, e},
            {e, e, e, e, e, e},
            {e, e, w, e, w, w},
            {w, w, w, e, e, e},
    };
    private I_Map mapSmallMaze = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(map_2D_smallMaze);

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

    private LargeAgent agent33to12 = new LargeAgent(new Agent(0, coor33, coor12));
    private LargeAgent agent12to33 = new LargeAgent(new Agent(1, coor12, coor33));
    private LargeAgent agent53to05 = new LargeAgent(new Agent(2, coor53, coor05));
    private LargeAgent agent43to11 = new LargeAgent(new Agent(3, coor43, coor11));
    private LargeAgent agent04to54 = new LargeAgent(new Agent(4, coor04, coor54));
    private LargeAgent agent00to10 = new LargeAgent(new Agent(5, coor00, coor10));
    private LargeAgent agent10to00 = new LargeAgent(new Agent(6, coor10, coor00));

    private MAPF_Instance instanceEmpty1 = new MAPF_Instance("instanceEmpty", mapEmpty,
            new Agent[]{agent33to12, agent12to33, agent53to05, agent43to11, agent04to54, agent00to10, agent10to00});
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12, agent12to33});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent12to33, agent33to12});
    private MAPF_Instance instanceUnsolvable = new MAPF_Instance("instanceUnsolvable", mapWithPocket, new Agent[]{agent00to10, agent10to00});

    I_Solver cbsSolver_lageAgents = new CBS_Shapes();


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
        Solution solved = cbsSolver_lageAgents.solve(testInstance, new RunParameters(instanceReport));
        S_Metrics.removeReport(instanceReport);

        System.out.println(solved.readableToString());
        validate(solved, 7, solved.sumIndividualCosts(),solved.makespan()); //need to find actual optimal costs
    }

    @Test
    void circleMapValidityTest1() {
        MAPF_Instance testInstance = instanceCircle1;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        Solution solved = cbsSolver_lageAgents.solve(testInstance, new RunParameters(System.currentTimeMillis() + (60*60*1000), null, instanceReport, null));
        S_Metrics.removeReport(instanceReport);

        System.out.println(solved.readableToString());
        validate(solved, 2, 8, 5);

    }

    @Test
    void circleMapValidityTest2() {
        MAPF_Instance testInstance = instanceCircle2;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        Solution solved = cbsSolver_lageAgents.solve(testInstance, new RunParameters(instanceReport));
        S_Metrics.removeReport(instanceReport);

        System.out.println(solved.readableToString());
        validate(solved, 2, 8, 5);
    }

    @Test
    void unsolvableShouldBeInvalid() {
        MAPF_Instance testInstance = instanceUnsolvable;
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        Solution solved = cbsSolver_lageAgents.solve(testInstance, new RunParameters(instanceReport));
        S_Metrics.removeReport(instanceReport);

        assertNull(solved);
    }








    @Test
    void TestingBenchmark(){
        boolean useAsserts = true;

        I_Solver solver = new CBS_Shapes();
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                "TestingBenchmark"});
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU_LA());

        MAPF_Instance instance = null;
        // load the pre-made benchmark
        try {
            long timeout = 300 /*seconds*/
                    *1000L;
            Map<String, Map<String, String>> benchmarks = CBS_SolverTest.readResultsCSV(path + "\\Results.csv");
            int numSolved = 0;
            int numFailed = 0;
            int numValid = 0;
            int numOptimal = 0;
            int numValidSuboptimal = 0;
            int numInvalidOptimal = 0;
            // run all benchmark instances. this code is mostly copied from Environment.Experiment.
            while ((instance = instanceManager.getNextInstance()) != null) {

                //build report
                InstanceReport report = S_Metrics.newInstanceReport();
                report.putStringValue(InstanceReport.StandardFields.experimentName, "TestingBenchmark");
                report.putStringValue(InstanceReport.StandardFields.mapName, instance.name);
                report.putIntegerValue(InstanceReport.StandardFields.numAgents, instance.agents.size());
                report.putStringValue(InstanceReport.StandardFields.solver, solver.getClass().getSimpleName());

                RunParameters runParameters = new RunParameters(timeout, null, report, null);

                //solve
                System.out.println("---------- solving "  + instance.name + " ----------");
                Solution solution = solver.solve(instance, runParameters);

                // validate
                Map<String, String> benchmarkForInstance = benchmarks.get(instance.name);
                if(benchmarkForInstance == null){
                    System.out.println("can't find benchmark for " + instance.name);
                    continue;
                }

                boolean solved = solution != null;
                System.out.println("Solved?: " + (solved ? "yes" : "no"));
                if (useAsserts) assertNotNull(solution);
                if (solved) numSolved++;
                else numFailed++;

                if(solution != null){
                    boolean valid = solution.isValidSolution();
                    System.out.println("Valid?: " + (valid ? "yes" : "no"));
                    if (useAsserts) assertTrue(valid);

                    int optimalCost = Integer.parseInt(benchmarkForInstance.get("Plan Cost"));
                    int costWeGot = solution.sumIndividualCosts();
                    boolean optimal = optimalCost==costWeGot;
                    System.out.println("cost is " + (optimal ? "optimal (" + costWeGot +")" :
                            ("not optimal (" + costWeGot + " instead of " + optimalCost + ")")));
                    report.putIntegerValue("Cost Delta", costWeGot - optimalCost);
                    if (useAsserts) assertEquals(optimalCost, costWeGot);

                    report.putIntegerValue("Runtime Delta",
                            report.getIntegerValue(InstanceReport.StandardFields.elapsedTimeMS) - (int)Float.parseFloat(benchmarkForInstance.get("Plan time")));

                    if(valid) numValid++;
                    if(optimal) numOptimal++;
                    if(valid && !optimal) numValidSuboptimal++;
                    if(!valid && optimal) numInvalidOptimal++;
                }
            }

            System.out.println("--- TOTALS: ---");
            System.out.println("timeout for each (seconds): " + (timeout/1000));
            System.out.println("solved: " + numSolved);
            System.out.println("failed: " + numFailed);
            System.out.println("valid: " + numValid);
            System.out.println("optimal: " + numOptimal);
            System.out.println("valid but not optimal: " + numValidSuboptimal);
            System.out.println("not valid but optimal: " + numInvalidOptimal);

            //save results
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
            String resultsOutputDir = IO_Manager.buildPath(new String[]{   System.getProperty("user.home"), "CBS_Tests"});
            File directory = new File(resultsOutputDir);
            if (! directory.exists()){
                directory.mkdir();
            }
            String updatedPath = resultsOutputDir + "\\results " + dateFormat.format(System.currentTimeMillis()) + ".csv";
            try {
                S_Metrics.exportCSV(new FileOutputStream(updatedPath),
                        new String[]{
                                InstanceReport.StandardFields.mapName,
                                InstanceReport.StandardFields.numAgents,
                                InstanceReport.StandardFields.timeoutThresholdMS,
                                InstanceReport.StandardFields.solved,
                                InstanceReport.StandardFields.elapsedTimeMS,
                                "Runtime Delta",
                                InstanceReport.StandardFields.solutionCost,
                                "Cost Delta",
                                InstanceReport.StandardFields.totalLowLevelTimeMS,
                                InstanceReport.StandardFields.generatedNodes,
                                InstanceReport.StandardFields.expandedNodes});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}