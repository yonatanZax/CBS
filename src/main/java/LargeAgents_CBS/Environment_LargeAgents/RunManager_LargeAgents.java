package LargeAgents_CBS.Environment_LargeAgents;

import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Instances.Maps.MapDimensions;
import Environment.A_RunManager;
import Environment.Experiment;
import Environment.IO_Package.IO_Manager;
import GraphMapPackage.I_InstanceBuilder;
import LargeAgents_CBS.Instances.InstanceBuilder_BGU_LA;
import LargeAgents_CBS.Instances.InstanceBuilder_Shapes;
import LargeAgents_CBS.Solvers.HighLevel.CBS_LargeAgents;
import LargeAgents_CBS.Solvers.HighLevel.CBS_Shapes;

public class RunManager_LargeAgents extends A_RunManager {

    /*  =   Set Path   =*/
    private String pathToBenchmark = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
                                                                            "TestingBenchmark"});



    /*  = Set BasicCBS.Solvers =  */
    @Override
    protected void setSolvers() {
        super.solvers.add(new CBS_Shapes());
        super.solvers.add(new CBS_LargeAgents());
    }

    /*  = Set Experiments =  */
    @Override
    protected void setExperiments() {
//        this.allLargeAgentInstances();
        this.autoGenerateExperiment();
//        this.addBenchmarkExperiment();
    }


    /* = Experiments =  */

    private void autoGenerateExperiment(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\AutoGenerateMaps"});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_Shapes();

        InstanceProperties instanceProperties = new InstanceProperties(null, -1, new int[]{10,10,10,10});

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder, instanceProperties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment AutoGenerate", instanceManager);
        this.experiments.add(gridExperiment);

    }


    private void allLargeAgentInstances(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\LargeAgents_Instances"});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_Shapes();


        InstanceProperties properties = null;
//        InstanceProperties properties = new InstanceProperties(new MapDimensions(new int[]{8,8}, instanceBuilder), -1, new int[]{3});

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder, properties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment All LA Instances", instanceManager);
        this.experiments.add(gridExperiment);

    }




    private void addBenchmarkExperiment(){
        InstanceManager instanceManager = new InstanceManager( this.pathToBenchmark, new InstanceBuilder_BGU_LA());
        this.experiments.add(new Experiment("All Instances in Test Benchmark", instanceManager));
    }





}
