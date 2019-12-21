package LargeAgents;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.ConstraintsAndConflicts.A_Conflict;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.RemovableConflictAvoidance;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocationTables;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.MinTimeConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagerTest;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import BasicCBS.Solvers.ConstraintsAndConflicts.VertexConflict;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.SingleAgentPlan;
import GraphMapPackage.MapFactory;
import LargeAgents_CBS.Instances.LargeAgent;
import LargeAgents_CBS.Instances.Maps.Coordinate_2D_LargeAgent;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;
import LargeAgents_CBS.Solvers.HighLevel.ConflictManager_LargeAgent;
import LargeAgents_CBS.Solvers.HighLevel.ConflictManager_Shapes;
import LargeAgents_CBS.Solvers.HighLevel.SwappingConflict_LargeAgents;
import LargeAgents_CBS.Solvers.HighLevel.VertexConflict_LargeAgent;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConflictManager_LargeAgentTest {

    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;

    private Enum_MapCellType[][] map_Empty = {
            { e, e, e, e},
            { e, e, e, e},
            { e, e, e, e},
    };

    private Enum_MapCellType[][] map_2D_H = {
            { e, w, w, e},
            { e, e, e, e},
            { e, w, w, e},
    };
    private I_Map mapH = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(map_2D_H);

    private Enum_MapCellType[][] twoCellMap = new Enum_MapCellType[][]{{e,e}};
    private I_Map mapTwoCells = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(twoCellMap);


    private I_Map mapEmpty = MapFactory.newSimple4Connected2D_GraphMap_LargeAgents(map_Empty);



    @Test
    public void goalConflict(){

        ConflictManager conflictAvoidanceTable = new ConflictManager_LargeAgent(new MinTimeConflictSelectionStrategy());



        /*  = Add a1 Plan =
            { S1 , G1 }
            S = Start
            G = Goal
        */
        Agent a1 = new LargeAgent(new Agent(1,new Coordinate_2D(0,0),new Coordinate_2D(0,1)));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();
        a1_moves.add(new Move(a1,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells)));
        a1_moves.add(new Move(a1,2, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells)));
        a1_moves.add(new Move(a1,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells)));
        a1_moves.add(new Move(a1,4, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells)));
        a1_moves.add(new Move(a1,5, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,1)),mapTwoCells)));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.addPlan(a1_plan);




        /*  = Add a2 Plan =
            { EE , S2 & G2 } // (0,1) is Start and Goal
            EE = Empty
            S = Start
            G = Goal
        */
        Agent a2 = new LargeAgent(new Agent(2,new Coordinate_2D(0,1),new Coordinate_2D(0,1)));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();
        a2_moves.add(new Move(a2,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,1)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,1)),mapTwoCells)));


        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.addPlan(a2_plan);


        /*      == Expected conflicts ==     */

        VertexConflict expectedGoalConflict = new VertexConflict(a1, a2, 5, this.mapTwoCells.getMapCell(new Coordinate_2D(0,1)));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedGoalConflict);


        /*      = Test actual values =  */
        Assert.assertTrue(ConflictManagerTest.equalsAllConflicts(expectedSet, conflictAvoidanceTable.getAllConflicts()));

    }









    @Test
    public void swappingConflict2CellMap(){

        ConflictManager conflictAvoidanceTable = new ConflictManager_LargeAgent(new MinTimeConflictSelectionStrategy());

        /*  = Add a1 Plan =
            { S1 , G1 }
            S = Start
            G = Goal
        */
        Agent a1 = new LargeAgent(new Agent(1,new Coordinate_2D(0,0), new Coordinate_2D(0,1)));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();
        a1_moves.add(new Move(a1,1,  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells),  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,1)),mapTwoCells)));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.addPlan(a1_plan);




        /*  = Add a2 Plan =
            { G2 , S2 }
            S = Start
            G = Goal
        */
        Agent a2 = new LargeAgent(new Agent(2, new Coordinate_2D(0,1), new Coordinate_2D(0,0)));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();
        a2_moves.add(new Move(a2,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,1)),mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapTwoCells)));


        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.addPlan(a2_plan);


        /*      == Expected conflicts ==     */

        SwappingConflict expectedConflict_time1 = new SwappingConflict_LargeAgents(a1,a2,1,  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,1)), this.mapTwoCells), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)), this.mapTwoCells));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedConflict_time1);


        /*      = Test actual values =  */
        Assert.assertTrue(ConflictManagerTest.equalsAllConflicts(expectedSet, conflictAvoidanceTable.getAllConflicts()));


    }


    @Test
    public void TwoAgentsWith4VertexConflicts_graphH() {

        ConflictManager_Shapes conflictAvoidanceTable = new ConflictManager_LargeAgent(new MinTimeConflictSelectionStrategy());


        /*  = Add a1 Plan =
            { S1, WW, WW, G1},
            { T1, T2, T3, T4},
            { EE, WW, WW, EE},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a1 = new LargeAgent(new Agent(1,new Coordinate_2D(0,0),new Coordinate_2D(0,3)));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();

        a1_moves.add(new Move(a1,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapH),   new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH)));
        a1_moves.add(new Move(a1,2, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH),   new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH)));
        a1_moves.add(new Move(a1,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH),   new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH)));
        a1_moves.add(new Move(a1,4, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH),   new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH)));
        a1_moves.add(new Move(a1,5, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH),   new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,3)),mapH)));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.addPlan(a1_plan);

        /*  = Add a2 Plan =
            { EE, WW, WW, EE},
            { T1, T2, T3, T4},
            { S2, WW, WW, G2},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a2 = new LargeAgent(new Agent(2,new Coordinate_2D(2,0),new Coordinate_2D(2,3)));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();

        a2_moves.add(new Move(a2,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(2,0)),mapH),  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH)));
        a2_moves.add(new Move(a2,2, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH),  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH)));
        a2_moves.add(new Move(a2,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH),  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH)));
        a2_moves.add(new Move(a2,4, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH),  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH)));
        a2_moves.add(new Move(a2,5, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH),  new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(2,3)),mapH)));

        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.addPlan(a2_plan);


        System.out.println("TwoAgentsWith4VertexConflicts_graphH: Done - Initialized two plans");


        /*      = Copy constructor =      */
        ConflictManager_LargeAgent copiedTable = new ConflictManager_LargeAgent(conflictAvoidanceTable);
        Assert.assertTrue(ConflictManagerTest.equalsAllConflicts(conflictAvoidanceTable.getAllConflicts(), copiedTable.getAllConflicts()));
        Assert.assertTrue(TimeLocationTables.equalsTimeLocations(conflictAvoidanceTable.timeLocationTables.timeLocation_Agents,copiedTable.timeLocationTables.timeLocation_Agents));
        System.out.println("TwoAgentsWith4VertexConflicts_graphH: Done - Copy Constructor");



        /*      = Expected values =     */

        /*      == Expected locations ==     */

        Map<TimeLocation, Set<Agent>> expected_timeLocationAgents = new HashMap<>();
        // Agent 1
        TimeLocation time0_a1 = new TimeLocation(0, mapH.getMapCell(new Coordinate_2D(0,0)));
        expected_timeLocationAgents.computeIfAbsent(time0_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a1).add(a1);
        TimeLocation time1_a1 = new TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time1_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a1).add(a1);
        TimeLocation time2_a1 = new TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time2_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a1).add(a1);
        TimeLocation time3_a1 = new TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time3_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a1).add(a1);
        TimeLocation time4_a1 = new TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time4_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a1).add(a1);
        TimeLocation time5_a1 = new TimeLocation(5, mapH.getMapCell(new Coordinate_2D(0,3)));
        expected_timeLocationAgents.computeIfAbsent(time5_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a1).add(a1);

        // Agent 2
        TimeLocation time0_a2 = new TimeLocation(0, mapH.getMapCell(new Coordinate_2D(2,0)));
        expected_timeLocationAgents.computeIfAbsent(time0_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a2).add(a2);
        TimeLocation time1_a2 = new TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time1_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a2).add(a2);
        TimeLocation time2_a2 = new TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time2_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a2).add(a2);
        TimeLocation time3_a2 = new TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time3_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a2).add(a2);
        TimeLocation time4_a2 = new TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time4_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a2).add(a2);
        TimeLocation time5_a2 = new TimeLocation(5, mapH.getMapCell(new Coordinate_2D(2,3)));
        expected_timeLocationAgents.computeIfAbsent(time5_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a2).add(a2);


        /*      == Expected conflicts ==     */

        VertexConflict expectedConflict_time1 = new VertexConflict_LargeAgent(a1,a2,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)), this.mapH));
        VertexConflict expectedConflict_time2 = new VertexConflict_LargeAgent(a1,a2,2, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)), this.mapH));
        VertexConflict expectedConflict_time3 = new VertexConflict_LargeAgent(a1,a2,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)), this.mapH));
        VertexConflict expectedConflict_time4 = new VertexConflict_LargeAgent(a1,a2,4, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)), this.mapH));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedConflict_time1);
        expectedSet.add(expectedConflict_time2);
        expectedSet.add(expectedConflict_time3);
        expectedSet.add(expectedConflict_time4);


        /*  = Test actual values =  */

        Assert.assertTrue(ConflictManagerTest.equalsAllConflicts(expectedSet, copiedTable.getAllConflicts()));
        Assert.assertTrue(TimeLocationTables.equalsTimeLocations(expected_timeLocationAgents,copiedTable.timeLocationTables.timeLocation_Agents));




        /*      = Test Select conflict =     */
        A_Conflict actualConflict_time1 = copiedTable.selectConflict();
        Assert.assertEquals(expectedConflict_time1,actualConflict_time1);


    }





    @Test
    public void TwoAgentsWith1SwappingConflict_graphH() {

        ConflictManager_LargeAgent conflictAvoidanceTable = new ConflictManager_LargeAgent(new MinTimeConflictSelectionStrategy());


        /*  = Add a1 Plan =
            { S1, WW, WW, G1},
            { T1, T2, T3, T4},
            { EE, WW, WW, EE},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a1 = new LargeAgent(new Agent(1,new Coordinate_2D(0,0),new Coordinate_2D(0,3)));
        SingleAgentPlan a1_plan;
        ArrayList<Move> a1_moves = new ArrayList<>();

        a1_moves.add(new Move(a1,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,0)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)), mapH)));
        a1_moves.add(new Move(a1,2, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)), mapH)));
        a1_moves.add(new Move(a1,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)), mapH)));
        a1_moves.add(new Move(a1,4, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)), mapH)));
        a1_moves.add(new Move(a1,5, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(0,3)), mapH)));

        a1_plan = new SingleAgentPlan(a1,a1_moves);
        conflictAvoidanceTable.addPlan(a1_plan);


        /*  = Add a2 Plan =
            { EE, WW, WW, EE},
            { T4, T3, T2, T1},
            { G1, WW, WW, S2},
            T = Time
            S = Start
            G = Goal
            EE = Empty cell
            WW = Wall
        */
        Agent a2 = new Agent(2,new Coordinate_2D(2,3),new Coordinate_2D(2,0));
        SingleAgentPlan a2_plan;
        ArrayList<Move> a2_moves = new ArrayList<>();

        a2_moves.add(new Move(a2,1, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(2,3)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH)));
        a2_moves.add(new Move(a2,2, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,3)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH)));
        a2_moves.add(new Move(a2,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH)));
        a2_moves.add(new Move(a2,4, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH)));
        a2_moves.add(new Move(a2,5, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,0)),mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(2,0)),mapH)));

        a2_plan = new SingleAgentPlan(a2,a2_moves);
        conflictAvoidanceTable.addPlan(a2_plan);


        System.out.println("TwoAgentsWith1SwappingConflict_graphH: Done - Initialized two plans");


        /*      = Copy constructor =      */
        ConflictManager_LargeAgent copiedTable = new ConflictManager_LargeAgent(conflictAvoidanceTable);
        Assert.assertTrue(ConflictManagerTest.equalsAllConflicts(conflictAvoidanceTable.getAllConflicts(), copiedTable.getAllConflicts()));
        Assert.assertTrue(TimeLocationTables.equalsTimeLocations(conflictAvoidanceTable.timeLocationTables.timeLocation_Agents,copiedTable.timeLocationTables.timeLocation_Agents));
        System.out.println("TwoAgentsWith1SwappingConflict_graphH: Done - Copy Constructor");



        /*      = Expected values =     */

        /*      == Expected locations ==     */

        Map<TimeLocation,Set<Agent>> expected_timeLocationAgents = new HashMap<>();
        // Agent 1
        TimeLocation time0_a1 = new TimeLocation(0, mapH.getMapCell(new Coordinate_2D(0,0)));
        expected_timeLocationAgents.computeIfAbsent(time0_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a1).add(a1);
        TimeLocation time1_a1 = new TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time1_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a1).add(a1);
        TimeLocation time2_a1 = new TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time2_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a1).add(a1);
        TimeLocation time3_a1 = new TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time3_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a1).add(a1);
        TimeLocation time4_a1 = new TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time4_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a1).add(a1);
        TimeLocation time5_a1 = new TimeLocation(5, mapH.getMapCell(new Coordinate_2D(0,3)));
        expected_timeLocationAgents.computeIfAbsent(time5_a1,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a1).add(a1);

        // Agent 2
        TimeLocation time0_a2 = new TimeLocation(0, mapH.getMapCell(new Coordinate_2D(2,3)));
        expected_timeLocationAgents.computeIfAbsent(time0_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time0_a2).add(a2);
        TimeLocation time1_a2 = new TimeLocation(1, mapH.getMapCell(new Coordinate_2D(1,3)));
        expected_timeLocationAgents.computeIfAbsent(time1_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time1_a2).add(a2);
        TimeLocation time2_a2 = new TimeLocation(2, mapH.getMapCell(new Coordinate_2D(1,2)));
        expected_timeLocationAgents.computeIfAbsent(time2_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time2_a2).add(a2);
        TimeLocation time3_a2 = new TimeLocation(3, mapH.getMapCell(new Coordinate_2D(1,1)));
        expected_timeLocationAgents.computeIfAbsent(time3_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time3_a2).add(a2);
        TimeLocation time4_a2 = new TimeLocation(4, mapH.getMapCell(new Coordinate_2D(1,0)));
        expected_timeLocationAgents.computeIfAbsent(time4_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time4_a2).add(a2);
        TimeLocation time5_a2 = new TimeLocation(5, mapH.getMapCell(new Coordinate_2D(2,0)));
        expected_timeLocationAgents.computeIfAbsent(time5_a2,k -> new HashSet<Agent>());
        expected_timeLocationAgents.get(time5_a2).add(a2);


        /*      == Expected conflicts ==     */

        SwappingConflict expectedConflict_time3 = new SwappingConflict_LargeAgents(a1,a2,3, new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,2)), mapH), new GraphLocationGroup(new Coordinate_2D_LargeAgent(new Coordinate_2D(1,1)), mapH));

        HashSet<A_Conflict> expectedSet = new HashSet<>();
        expectedSet.add(expectedConflict_time3);


        /*  = Test actual values =  */

        Assert.assertTrue(ConflictManagerTest.equalsAllConflicts(expectedSet, copiedTable.getAllConflicts()));
        Assert.assertTrue(TimeLocationTables.equalsTimeLocations(expected_timeLocationAgents,copiedTable.timeLocationTables.timeLocation_Agents));



        /*      = Test Select conflict =     */
        A_Conflict actualConflict_time1 = copiedTable.selectConflict();
        Assert.assertEquals(expectedConflict_time3,actualConflict_time1);

    }




}