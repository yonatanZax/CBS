package Environment;

import Environment.IO_Package.IO_Manager;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_MovingAI;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Instances.Maps.MapDimensions;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.PrioritisedPlanning.PrioritisedPlanning_Solver;
import GraphMapPackage.I_InstanceBuilder;


public class RunManagerSimpleExample extends A_RunManager {

    /*  = Set Solvers =  */
    @Override
    protected void setSolvers() {
        this.solvers.add(new PrioritisedPlanning_Solver(new SingleAgentAStar_Solver()));
        this.solvers.add(new CBS_Solver());
    }

    /*  = Set Experiments =  */
    @Override
    protected void setExperiments() {
//        addExperiment_16_7();
        addExperimentMovingAI();
    }


    /* = Experiments =  */

    private void addExperiment_16_7(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\BGU_Instances"});

        I_InstanceBuilder instanceBuilder = new InstanceBuilder_BGU();

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new MapDimensions(new int[]{16,16}, instanceBuilder), 0, new int[]{7});
        int numOfInstances = 1;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, instanceBuilder, properties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment_16_7", instanceManager,numOfInstances);
        this.experiments.add(gridExperiment);
    }

    private void addExperimentMovingAI(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\MovingAI_Instances"});


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_MovingAI());

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment_8_Room", instanceManager);
        this.experiments.add(gridExperiment);
    }



}
