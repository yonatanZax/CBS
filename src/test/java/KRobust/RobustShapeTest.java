package KRobust;

import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import GraphMapPackage.GraphMapVertex;
import GraphMapPackage.MapFactory;
import KRobust_CBS.RobustShape;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RobustShapeTest {

    final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    final Enum_MapCellType w = Enum_MapCellType.WALL;
    Enum_MapCellType[][] map_2D_H = {
            { e, w, w, e},
            { e, e, e, e},
            { e, w, w, e},
    };

    I_Map map_h = MapFactory.newSimple4Connected2D_GraphMap(map_2D_H);



    @Test
    public void addingNewLocations(){

        int k = 3;

        GraphMapVertex cell0_0 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(0, 0));
        GraphMapVertex cell1_0 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(1, 0));
        GraphMapVertex cell2_0 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(2, 0));
        GraphMapVertex cell1_1 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(1, 1));
        GraphMapVertex cell1_2 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(1, 2));
        GraphMapVertex cell1_3 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(1, 3));
        GraphMapVertex cell0_3 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(0, 3));
        GraphMapVertex cell2_3 = (GraphMapVertex) map_h.getMapCell(new Coordinate_2D(2, 3));


        Set<GraphMapVertex> expected_robustShape0_0_t0_locations = new HashSet<>(){{add(cell0_0);}};
        Set<GraphMapVertex> expected_robustShape1_0_t1_locations = new HashSet<>(){{
            add(cell0_0);
            add(cell1_0);
        }};
        Set<GraphMapVertex> expected_robustShape1_0_t2_locations = new HashSet<>(){{
            add(cell0_0);
            add(cell1_0);
            add(cell1_0);
        }};
        Set<GraphMapVertex> expected_robustShape1_1_t3_locations = new HashSet<>(){{
            add(cell1_0);
            add(cell1_0);
            add(cell1_1);
        }};
        Set<GraphMapVertex> expected_robustShape1_2_t4_locations = new HashSet<>(){{
            add(cell1_0);
            add(cell1_1);
            add(cell1_2);
        }};




        RobustShape robustShape0_0_t0 = new RobustShape(new TimeLocation(0,cell0_0),k);
        RobustShape robustShape1_0_t1 = new RobustShape(new TimeLocation(1, cell1_0), robustShape0_0_t0);
        RobustShape robustShape1_0_t2 = new RobustShape(new TimeLocation(2, cell1_0), robustShape1_0_t1);
        RobustShape robustShape1_1_t3 = new RobustShape(new TimeLocation(3, cell1_1), robustShape1_0_t2);
        RobustShape robustShape1_2_t4 = new RobustShape(new TimeLocation(4, cell1_2), robustShape1_1_t3);


        assertEquals(expected_robustShape0_0_t0_locations, robustShape0_0_t0.getAllLocations());
        assertEquals(cell0_0, robustShape0_0_t0.getHead().location);
        assertEquals(expected_robustShape1_0_t1_locations, robustShape1_0_t1.getAllLocations());
        assertEquals(cell1_0, robustShape1_0_t1.getHead().location);
        assertEquals(expected_robustShape1_0_t2_locations, robustShape1_0_t2.getAllLocations());
        assertEquals(cell1_0, robustShape1_0_t2.getHead().location);
        assertEquals(expected_robustShape1_1_t3_locations, robustShape1_1_t3.getAllLocations());
        assertEquals(cell1_1, robustShape1_1_t3.getHead().location);
        assertEquals(expected_robustShape1_2_t4_locations, robustShape1_2_t4.getAllLocations());
        assertEquals(cell1_2, robustShape1_2_t4.getHead().location);



    }

}