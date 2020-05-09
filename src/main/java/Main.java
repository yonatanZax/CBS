import BasicCBS.Solvers.I_Solver;
import Environment.A_RunManager;
import Environment.IO_Package.IO_Manager;
import Environment.RunManagerSimpleExample;
import Environment.TestingBenchmarkRunManager;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.MAPF_Instance;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import KRobust_CBS.CBS_KRobust;
import KRobust_CBS.CBS_ShapesRobust;
import KRobust_CBS.RunManager_ExperimentsKRobust;
import KRobust_CBS.RunManager_KRobust;
import LargeAgents_CBS.Environment_LargeAgents.RunManager_ExperimentsLA;
import LargeAgents_CBS.Environment_LargeAgents.RunManager_LargeAgents;
import LargeAgents_CBS.Solvers.HighLevel.CBS_LargeAgents;
import LargeAgents_CBS.Solvers.HighLevel.CBS_Shapes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * We wanted to keep {@link #main(String[])} short and simple as possible
 * Things to consider before running:
 * 1. Check that the {@link #resultsOutputDir} is correct
 * 2. Check that {@link #outputResults()} is as you need
 * 3. Running an experiment should be done through {@link A_RunManager},
 * Solving a single Instance is also possible by giving a path.
 * <p>
 * For more information, view the examples below
 */
public class Main {
    /*  Testing stage commit    */

    // where to put generated reports. The default is a new folder called CBS_Results, under the user's home directory.
    public static final String resultsOutputDir = IO_Manager.buildPath(new String[]{System.getProperty("user.home"), "CBS_Results"});
//    public static final String resultsOutputDir = IO_Manager.buildPath(new String[]{   IO_Manager.testResources_Directory +
//                                                                                        "\\Reports default directory"});

    public static void main(String[] args) {
        if (verifyOutputPath()) {
            // will solve a single instance and print the solution
//            solveOneInstanceExample();
            // will solve multiple instances and print a simple report for each instance
//            runMultipleExperimentsExample();
            // will solve a set of instances. These instances have known optimal solution costs (found at
            // src\test\resources\TestingBenchmark\Results.csv), and so can be used as a benchmark.
//            runTestingBenchmarkExperiment();
            // all examples will also produce a report in CSV format, and save it to resultsOutputDir (see above)

            /*  Auto Generate Instances */
//            runLargeAgentInstances();
//            runRobustInstances();


            runExperiments_LargeAgents();
//            runExperiments_Robust();

        }
    }

    private static boolean verifyOutputPath() {
        File directory = new File(resultsOutputDir);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (!created) {
                String errString = "Could not locate or create output directory.";
                System.out.println(errString);
                return false;
            }
        }
        return true;
    }



    /*      Experiment RunManagers      */


    public static void runExperiments_LargeAgents() {


        I_Solver[] solvers = new I_Solver[]{
                new CBS_Shapes(),
                new CBS_LargeAgents()
        };
        String problemType = "LargeAgents";


        String[] folders = new String[]{
                "Instances20x20_2x2_obs0",
                "Instances20x20_2x2_obs0.1",
                "Instances30x30_3x3_obs0",
                "Instances30x30_3x3_obs0.1",
        };

        String[] folders_lak503d = new String[]{
                "Instances_lak2x2",
                "Instances_lak3x3",
                "Instances_lak4x4",
        };


        A_RunManager largeAgentRunManager = null;


        for (String folder : folders) {
            for (I_Solver solver : solvers) {
                largeAgentRunManager = new RunManager_ExperimentsLA(folder, solver, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10});
                largeAgentRunManager.runAllExperiments();
            }
            String experimentName = problemType + " Exp - " + folder + " Date - ";
            outputResults(experimentName);
        }

        for (String folder : folders_lak503d) {
            for (I_Solver solver : solvers) {
                largeAgentRunManager = new RunManager_ExperimentsLA(folder, solver, new int[]{5, 10, 15, 20, 25});
                largeAgentRunManager.runAllExperiments();
            }
            String experimentName = problemType + " Exp - " + folder + " Date - ";
            outputResults(experimentName);
        }


    }


    public static void runExperiments_Robust() {


        I_Solver[] solvers = new I_Solver[]{
//                new CBS_KRobust(),
                new CBS_ShapesRobust()
        };
        String problemType = "K-Robust";

        String[] folders = new String[]{
//                "Instances20x20_1x1_obs0",
//                "Instances20x20_1x1_obs0.1",
        };

        String[] folders_lak503d = new String[]{
//                "Instances_lak1x1",
//                "Instances_den502d_1x1",
                "Instances_den502d_1x1_x",
        };


        A_RunManager robustRunManager = null;


        for (int k = 2; k < 3; k++) {
            for (String folder : folders) {
                for (I_Solver solver : solvers) {
                    robustRunManager = new RunManager_ExperimentsKRobust(folder, solver, k, new int[]{5, 10, 15});
                    robustRunManager.runAllExperiments();
                }
                String experimentName = problemType + " Exp - " + folder + " K=" + k + " Date - ";
                outputResults(experimentName);
            }

            for (String folder : folders_lak503d) {
                for (I_Solver solver : solvers) {
                    robustRunManager = new RunManager_ExperimentsKRobust(folder, solver, k, new int[]{10});
//                    robustRunManager = new RunManager_ExperimentsKRobust(folder, solver, k, new int[]{5, 10, 15, 20/*, 25, 30, 35, 40, 45, 50*/});
                    robustRunManager.runAllExperiments();
                }
                String experimentName = problemType + " Exp - " + folder + " K=" + k + " Date - ";
                outputResults(experimentName);
            }
        }

    }







    /*      RunManagers Instances       */


    public static void solveOneInstanceExample() {

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath(new String[]{IO_Manager.resources_Directory,
                "Instances\\\\BGU_Instances\\\\den520d-10-0"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(null, new InstanceBuilder_BGU());

        MAPF_Instance instance = RunManagerSimpleExample.getInstanceFromPath(instanceManager, instancePath);

        // Solve
        CBS_Solver solver = new CBS_Solver();
        RunParameters runParameters = new RunParameters();
        Solution solution = solver.solve(instance, runParameters);

        //output results
        System.out.println(solution.readableToString());
        outputResults();
    }

    public static void runMultipleExperimentsExample() {
        RunManagerSimpleExample runManagerSimpleExample = new RunManagerSimpleExample();
        runManagerSimpleExample.runAllExperiments();

        outputResults();
    }

    public static void runLargeAgentInstances() {
        A_RunManager largeAgentRunManager = new RunManager_LargeAgents();
        largeAgentRunManager.runAllExperiments();
        outputResults();
    }

    public static void runRobustInstances() {
        A_RunManager robustManager = new RunManager_KRobust();
        robustManager.runAllExperiments();
        outputResults();
    }

    public static void runTestingBenchmarkExperiment() {
        TestingBenchmarkRunManager testingBenchmarkRunManager = new TestingBenchmarkRunManager();
        testingBenchmarkRunManager.runAllExperiments();

        outputResults();
    }


    /**
     * An example of a simple output of results to a file. It is best to handle this inside your custom
     * {@link A_RunManager run managers} instead.
     * Note that you can add more fields here, if you want metrics that are collected and not exported.
     * Note that you can easily add other metrics which are not currently collected. see {@link S_Metrics}.
     */
    private static void outputResults() {
        outputResults("");
    }

    private static void outputResults(String experimentName) {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
        String updatedPath = resultsOutputDir + "\\" + experimentName + dateFormat.format(System.currentTimeMillis()) + " .csv";
        try {
            S_Metrics.exportCSV(new FileOutputStream(updatedPath),
                    new String[]{InstanceReport.StandardFields.experimentName,
                            InstanceReport.StandardFields.mapName,
                            InstanceReport.StandardFields.agentSize,
                            InstanceReport.StandardFields.kRobust,
                            InstanceReport.StandardFields.numAgents,
                            InstanceReport.StandardFields.obstacleRate,
                            InstanceReport.StandardFields.solver,
                            InstanceReport.StandardFields.solved,
                            InstanceReport.StandardFields.elapsedTimeMS,
                            InstanceReport.StandardFields.solutionCost,
                            InstanceReport.StandardFields.expandedNodes,
                            InstanceReport.StandardFields.expandedNodesLowLevel,
                            InstanceReport.StandardFields.solution});
        } catch (IOException e) {
            e.printStackTrace();
        }

        S_Metrics.clearReports();
    }

}
