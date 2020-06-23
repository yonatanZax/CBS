package Environment;

import Environment.IO_Package.Enum_IO;
import Environment.IO_Package.IO_Manager;
import Environment.IO_Package.Writer;
import GraphMapPackage.I_InstanceBuilder;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.MAPF_Instance;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import KRobust_CBS.RobustAgent;
import LargeAgents_CBS.Instances.LargeAgent;

/**
 * Experiment class lets the user to specify the instances it needs for the experiment.
 * A_RunManager holds a list of Experiments, each Experiment receives Name and {@link InstanceManager}
 * {@link InstanceManager} receives a path to the instances' folder and a relevant parser {@link I_InstanceBuilder}
 * Optional, users can filter the instances in the folder by simply adding {@link BasicCBS.Instances.InstanceProperties} to {@link InstanceManager}
 * Make sure that {@link #setReport(MAPF_Instance, I_Solver)} is defined as you want
 */
public  class Experiment {

  public final String experimentName;
  public final int numOfInstances;
  protected InstanceManager instanceManager;

  public Experiment(String experimentName, InstanceManager instanceManager){
    this.experimentName = experimentName;
    this.instanceManager = instanceManager;
    this.numOfInstances = Integer.MAX_VALUE;
  }

  public Experiment(String experimentName, InstanceManager instanceManager, int numOfInstances) {
    this.experimentName = experimentName;
    this.instanceManager = instanceManager;
    this.numOfInstances = numOfInstances;
  }


  public InstanceReport setReport(MAPF_Instance instance, I_Solver solver){
    InstanceReport instanceReport = S_Metrics.newInstanceReport();
    /*  = Put values in report =  */
    instanceReport.putStringValue(InstanceReport.StandardFields.experimentName, this.experimentName);
    instanceReport.putStringValue(InstanceReport.StandardFields.mapName, instance.name);
    if( instance.agents.get(0) instanceof LargeAgent){
      instanceReport.putIntegerValue(InstanceReport.StandardFields.agentSize, ((LargeAgent)instance.agents.get(0)).getHeight());
    }
    if( instance.agents.get(0) instanceof RobustAgent){
      instanceReport.putIntegerValue(InstanceReport.StandardFields.kRobust, ((RobustAgent)instance.agents.get(0)).k);
    }
    instanceReport.putIntegerValue(InstanceReport.StandardFields.numAgents, instance.agents.size());
    instanceReport.putIntegerValue(InstanceReport.StandardFields.obstacleRate, instance.getObstaclePercentage());
    instanceReport.putStringValue(InstanceReport.StandardFields.solver, solver.getClass().getSimpleName());

    return instanceReport;
  }


  public void runExperiment(I_Solver solver) {

    if( solver == null ){ return; }

    instanceManager.resetPathIndex();

    for (int i = 0; i < this.numOfInstances; i++) {

      MAPF_Instance instance = instanceManager.getNextInstance();

      if (instance == null) { break; }

      InstanceReport instanceReport = this.setReport(instance, solver);
      RunParameters runParameters = new RunParameters(instanceReport);

      System.out.println("---------- solving "  + instance.name + " ---------- with solver " + solver.getClass().getSimpleName() );
      Solution solution = null;
      try{
        solution = solver.solve(instance, runParameters);
      }catch (Exception e){
        System.out.println("Exception while solving.. :(");
      }
      System.out.println("Solved?: " + (solution != null ? "yes" : "no"));
      if(solution != null){
        boolean validSolution = solution.solves(instance);
        System.out.println("Solution is " + (validSolution ? "valid" : "invalid!!!"));
        instanceReport.putIntegerValue(InstanceReport.StandardFields.valid, validSolution ? 1 : 0);
        System.out.println("Sum of Individual Costs: " + solution.sumIndividualCosts());
      }
      Integer elapsedTime = instanceReport.getIntegerValue(InstanceReport.StandardFields.elapsedTimeMS);
      if(elapsedTime != null){
        System.out.println("Elapsed time (ms): " + elapsedTime);
      }


      boolean printSolution = false;
      if( solution != null && printSolution ){
        System.out.println(solution.readableToString());
      }

      boolean saveSolution = false;
      if( solution != null && saveSolution ){
        String sol = solution.visualization_solution();
        Writer writer = new Writer();
        Enum_IO enum_open = writer.openFileToAppend(IO_Manager.buildPath(new String[]{IO_Manager.resources_Directory,"Solutions"}),instance.name + "_" + solver.getClass().getSimpleName() + i + ".sol");
        if(enum_open == Enum_IO.OPENED){
          writer.writeText("Agents: " + String.valueOf(instance.agents.size()));
          writer.writeText("\n");
          writer.writeText(sol);
          writer.closeFile();
        }
      }

    }


  }


}