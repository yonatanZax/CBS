package KRobust_CBS;

import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import Environment.A_RunManager;
import Environment.Experiment;
import Environment.IO_Package.IO_Manager;
import GraphMapPackage.I_InstanceBuilder;

public class RunManager_KRobust extends A_RunManager {

    /*  = Set BasicCBS.Solvers =  */
    @Override
    protected void setSolvers() {
        super.solvers.add(new CBS_ShapesRobust());
        super.solvers.add(new CBS_KRobust());
    }

    /*  = Set Experiments =  */
    @Override
    protected void setExperiments() {

        this.runRobustExperimentWithDifferentKValues("Instances20x20_1x1_obs0");
        this.runRobustExperimentWithDifferentKValues("Instances20x20_1x1_obs0.1");

        this.runRobustExperimentWithDifferentKValues("Instances_lak1x1");
    }


    /* = Experiments =  */


    private void runRobustExperimentWithDifferentKValues(String folderName){
        for (int i = 0; i < 4; i++) {
//            this.addRobustExperiment(i);

            this.addAutoGenerateExperimentByFolderName(folderName ,i);
        }
    }

    private void addRobustExperiment(int k){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\KRobust_Instances"});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_RobustShapes(k);


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment All Robust Instances with k = " + k, instanceManager);
        this.experiments.add(gridExperiment);
    }


    private void addAutoGenerateExperimentByFolderName(String folderName, int k){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\AutoGenerateMaps",
                                                            folderName});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_RobustShapes(k);

        InstanceProperties instanceProperties = new InstanceProperties(null, -1, new int[]{5,5});

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder, instanceProperties);

        /*  =   Add new experiment   =  */
        int numOfInstances = 10;
        Experiment gridExperiment = new Experiment("Experiment AutoGenerate", instanceManager, numOfInstances);
        this.experiments.add(gridExperiment);

    }

}
