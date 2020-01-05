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
    }

    /*  = Set Experiments =  */
    @Override
    protected void setExperiments() {
        this.allRobustAgentInstances();
    }


    /* = Experiments =  */


    private void allRobustAgentInstances(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                "Instances\\\\KRobust_Instances"});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_Robust();


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment All Robust Instances", instanceManager);
        this.experiments.add(gridExperiment);

    }

}
