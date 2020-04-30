package KRobust;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.Constraint;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.SingleAgentPlan;
import BasicCBS.Solvers.Solution;
import Environment.IO_Package.IO_Manager;
import KRobust_CBS.*;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class AStar_RobustShapeTest {

    private I_Solver solver = new AStar_RobustShape();


    private String folderPath = IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory, "Instances", "Robust"});

    private MAPF_Instance getInstance(String instancePath, int k){
        InstanceManager instanceManager = new InstanceManager(new InstanceBuilder_Robust(k));
        return instanceManager.getSpecificInstance(new InstanceManager.InstancePath(instancePath));
    }


    @Test
    public void testLargeMaps(){
        largeMaps(0);
        System.out.println("K=0 Done");
        largeMaps(1);
        System.out.println("K=1 Done");
        largeMaps(2);
        System.out.println("K=2 Done");
    }

    private void largeMaps(int k){
        InstanceManager manager = new InstanceManager(folderPath,new InstanceBuilder_RobustShapes(k));
        MAPF_Instance instance = manager.getNextInstance();

        while (instance != null){

            Solution solution = solver.solve(instance, new RunParameters());
            assertNotNull(solution);
            instance = manager.getNextInstance();
        }
    }


    @Test
    public void bigEmptyMap(){

        int k = 2;
        String instancePath = IO_Manager.buildPath(new String[]{folderPath,"Instance-200-0-2-1000"});
        MAPF_Instance instance = getInstance(instancePath,k);

        Solution solution = solver.solve(instance, new RunParameters());
        assertNotNull(solution);
    }



    /*
                    @.@@@@@@
                    @.@@@@@@
                    @.@@@@@@
                    @.@@@@@@
                    .....@@@
                    @.@@@@@@
                    @.@@@@@@
                    @@@@@@@@
                    Agents:
                    2
                    0,4,0,4,4
                    1,6,1,0,1
    */

    @Test
    public void checkTailConstraint(){

        assertEquals(4,getCostTailConstraint(0));
        assertEquals(6,getCostTailConstraint(1));
        assertEquals(7,getCostTailConstraint(2));
        assertEquals(8,getCostTailConstraint(3));
    }

    public int getCostTailConstraint(int k){
        String instancePath = IO_Manager.buildPath(new String[]{folderPath,"Instance-8-0-2-100"});
        MAPF_Instance instance = getInstance(instancePath,k);

        I_Map map = instance.map;
        Agent agent0 = instance.agents.get(0);

        //constraint
        ConstraintSet constraints = new ConstraintSet_Robust();
        for (int time = 4; time < 4 + k; time++) {
            constraints.add(new Constraint(agent0, time, null, map.getMapCell(new Coordinate_2D(4,1))));
        }
        RunParameters parameters = new RunParameters(constraints);

        Solution solved = solver.solve(instance, parameters);

        SingleAgentPlan planAgent0 = solved.getPlanFor(agent0);
        this.removeStayInGoal(planAgent0);
        return planAgent0.getCost();

    }


    private void removeStayInGoal(SingleAgentPlan plan){
        I_Coordinate target = plan.agent.target;
        I_Location prevLocation = plan.moveAt(plan.getEndTime()).prevLocation;
        while ( prevLocation != null && prevLocation.getCoordinate().equals(target)){
            if(plan.getEndTime() == 1){ break;}
            plan.removeLastKMoves(1);
            prevLocation = plan.moveAt(plan.getEndTime()).prevLocation;
        }

    }

}