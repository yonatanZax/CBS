package LargeAgents_CBS.Environment_LargeAgents;

import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Solvers.I_Solver;
import Environment.A_RunManager;
import Environment.Experiment;
import Environment.IO_Package.IO_Manager;
import GraphMapPackage.I_InstanceBuilder;
import LargeAgents_CBS.Instances.InstanceBuilder_Shapes;

public class RunManager_ExperimentsLA extends A_RunManager {


    private String folderName;
    private I_Solver CBS_Solver;
    private int[] numOfAgents;

    public RunManager_ExperimentsLA(String folderName, I_Solver solver, int[] numOfAgents){
        this.folderName = folderName;
        this.CBS_Solver = solver;
        this.numOfAgents = numOfAgents;
    }


    @Override
    protected void setSolvers() {
        this.solvers.add(this.CBS_Solver);
    }

    @Override
    protected void setExperiments() {
        this.addAutoGenerateExpWithFolderPath();
    }


    private void addAutoGenerateExpWithFolderPath(){

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\AutoGenerateMaps", folderName});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_Shapes();

        InstanceProperties instanceProperties = new InstanceProperties(null, -1, this.numOfAgents);

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder, instanceProperties);

        /*  =   Add new experiment   =  */
        int numOfInstances = 1;
        Experiment gridExperiment = new Experiment("Experiment AutoGenerate LargeAgents", instanceManager);
//        Experiment gridExperiment = new Experiment("Experiment AutoGenerate LargeAgents", instanceManager, numOfInstances);
        this.experiments.add(gridExperiment);
    }


}
