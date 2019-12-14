package LargeAgents_CBS.Environment_LargeAgents;

import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Instances.Maps.MapDimensions;
import BasicCBS.Solvers.CBS.CBS_Solver;
import Environment.A_RunManager;
import Environment.Experiment;
import Environment.IO_Package.IO_Manager;
import LargeAgents_CBS.Instances.InstanceBuilder_BGU_LA;
import LargeAgents_CBS.Instances.InstanceBuilder_Shapes;
import LargeAgents_CBS.Solvers.HighLevel.CBS_LargeAgents;

public class RunManager_LargeAgents extends A_RunManager {

    /*  =   Set Path   =*/
    private String pathToBenchmark = IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,
            "TestingBenchmark"});




    /*  = Set BasicCBS.Solvers =  */
    @Override
    protected void setSolvers() {
//        super.solvers.add(new CBS_Solver());
        super.solvers.add(new CBS_LargeAgents());
    }

    /*  = Set Experiments =  */
    @Override
    protected void setExperiments() {
        this.addExperiment_CleanMap_20_20();
        this.addAllInstancesExperiment();
    }


    /* = Experiments =  */

    private void addExperiment_CleanMap_20_20(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\LargeAgents_Instances"});

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new MapDimensions(20,20), 0, new int[]{7});
        int numOfInstances = 1;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_Shapes(),properties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment_CleanMap_20_20", instanceManager,numOfInstances);
        this.experiments.add(gridExperiment);
    }


    private void addAllInstancesExperiment(){
        InstanceManager instanceManager = new InstanceManager( this.pathToBenchmark, new InstanceBuilder_BGU_LA());
        this.experiments.add(new Experiment("All Instances in Test Benchmark", instanceManager));
    }



}
