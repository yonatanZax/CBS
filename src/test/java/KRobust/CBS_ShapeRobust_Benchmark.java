package KRobust;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.*;
import BasicCBS.Solvers.CBS.CBS_SolverTest;
import Environment.IO_Package.IO_Manager;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;
import GraphMapPackage.MapFactory;
import KRobust_CBS.CBS_ShapesRobust;
import KRobust_CBS.InstanceBuilder_Robust;
import KRobust_CBS.RobustAgent;
import KRobust_CBS.RobustShape;
import LargeAgents_CBS.Solvers.HighLevel.CBS_Shapes;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CBS_ShapeRobust_Benchmark {
    I_Solver solver = new CBS_ShapesRobust();

    @Test
    public void runRobust_k0(){
        this.TestingBenchmark(0);
    }

    @Test
    public void runRobust_k1(){
        this.TestingBenchmark(1);
    }

    @Test
    public void runRobust_k2(){
        this.TestingBenchmark(2);
    }



    private void TestingBenchmark(int k){
        boolean useAsserts = false;

        I_Solver solver = this.solver;
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                                                            "RobustBenchmark"});
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_Robust(k));

        MAPF_Instance instance = null;
        // load the pre-made benchmark
        try {
            long timeout = 300 /*seconds*/
                    *1000L;
            Map<String, Map<String, String>> benchmarks = CBS_SolverTest.readResultsCSV(path + "\\RobustCSV_k" + k + ".csv");
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
                report.putStringValue(InstanceReport.StandardFields.experimentName, "RobustBenchmark");
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
                    Solution basicSolution = this.getBasicMoveSolutions(solution);
                    boolean valid = basicSolution.isValidSolution();
//                    boolean valid = solution.isValidSolution();
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
                            report.getIntegerValue(InstanceReport.StandardFields.elapsedTimeMS) - (int)Float.parseFloat(benchmarkForInstance.get("Planning time (ms)")));

                    if(valid) numValid++;
                    if(optimal) numOptimal++;
                    if(valid && !optimal) numValidSuboptimal++;
                    if(!valid && optimal) numInvalidOptimal++;


                    System.out.println(solution.readableToString());
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



    private Solution getBasicMoveSolutions(Solution solution){
        Solution result = new Solution();
        for (SingleAgentPlan plan : solution) {
            SingleAgentPlan basicPlan = new SingleAgentPlan(plan.agent);
            for (Move move : plan) {
                basicPlan.addMove(new Move(move.agent, move.timeNow, ((RobustShape)move.prevLocation).getHeadLocation(),((RobustShape)move.currLocation).getHeadLocation()));
            }
            result.putPlan(basicPlan);
        }

        return result;
    }
}
