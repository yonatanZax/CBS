package LargeAgents_CBS.Instances.Maps;

import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Instances.Maps.I_Map;
import GraphMapPackage.GraphMapVertex_LargeAgents;

import java.util.*;

public class GraphLocationGroup implements I_Location {

    private GraphMapVertex_LargeAgents[][] mapCells; // 2D representation of LargeAgent
//    private GraphMapCell minNeighbors; // A cell in the group having minimal neighbors

    private List<GraphMapVertex_LargeAgents> innerCells = new ArrayList<>();
    private Queue<GraphMapVertex_LargeAgents> outerCells = new PriorityQueue<>(new Comparator<GraphMapVertex_LargeAgents>() {
        @Override
        public int compare(GraphMapVertex_LargeAgents cell_1, GraphMapVertex_LargeAgents cell_2) {
            if( cell_1.getNeighbors().size() <=  cell_2.getNeighbors().size()){
                return -1; // One has less neighbors than Two
            }
            return 1; // Two has less neighbors that One
        }
    });


    public GraphLocationGroup(Coordinate_2D[][] coordinate_2D, I_Map map) {

        GraphMapVertex_LargeAgents[][] mapCells = new GraphMapVertex_LargeAgents[coordinate_2D.length][coordinate_2D[0].length];

        for (int i = 0; i < coordinate_2D.length; i++) {
            for (int j = 0; j < coordinate_2D[i].length; j++) {
                I_Location location = map.getMapCell(coordinate_2D[i][j]);
                mapCells[i][j] = (GraphMapVertex_LargeAgents) location;
            }
        }
        this.mapCells = mapCells;
        this.addCellsToInnerOuter();
    }

    public GraphLocationGroup(I_Coordinate coordinate_2D_largeAgents, I_Map map){
        this( ((Coordinate_2D_LargeAgent)coordinate_2D_largeAgents).getCoordinates(), map);
    }

    public GraphLocationGroup(GraphMapVertex_LargeAgents[][] mapCells) {
        this.mapCells = mapCells;
        this.addCellsToInnerOuter();
    }


    public GraphLocationGroup(GraphLocationGroup other, Enum_direction direction){

        this.mapCells = new GraphMapVertex_LargeAgents[other.mapCells.length][other.mapCells[0].length];

        // Change group to it's neighbor at direction
        for (int i = 0; i < this.mapCells.length; i++) {
            for (int j = 0; j < this.mapCells[i].length; j++) {
                I_Location toAdd = other.mapCells[i][j].getLocationByDirection(direction);
                this.mapCells[i][j] = (GraphMapVertex_LargeAgents) toAdd;
            }
        }
        this.addCellsToInnerOuter(); // Set Inner Outer lists
    }

    /**
     * Help method to Constructor
     * Filters cells in {@link #mapCells} to {@link #innerCells}, {@link #outerCells}
     */
    private void addCellsToInnerOuter(){

        // Filter cells to inner/outer
        for (int i = 0; i < this.mapCells.length; i++) {
            for (int j = 0; j < this.mapCells[i].length; j++) {
                if( this.mapCells[i][j] == null){ continue; } // missing cells as null
                if( i > 0 && j > 0 && i < this.mapCells.length - 1 && j < this.mapCells[i].length - 1){
                    this.innerCells.add(this.mapCells[i][j]); // inner cell
                }else{
                    this.outerCells.add(this.mapCells[i][j]); // outer cell
                }
            }
        }
    }

    @Override
    public Enum_MapCellType getType() {
        return null;
    }


    @Override
    public List<I_Location> getNeighbors() {

        List<GraphMapVertex_LargeAgents> outerCellsList = new ArrayList<>(this.outerCells);
        GraphMapVertex_LargeAgents minNeighborsCell = outerCellsList.remove(0); // the cell with the minNeighbors
        List<I_Location> validNeighborsWithAllCells = new ArrayList<>(); // Init GraphLocationGroup neighbors

        // Iterate over the minNeighborsCell directions
        for (Enum_direction direction : minNeighborsCell.getDirectionCollection()) {

            Set<GraphMapVertex_LargeAgents> innerCellsOfNeighbor = new HashSet<>(outerCellsList.size());

            // Check direction this all outer cells
            for (GraphMapVertex_LargeAgents outerCell : outerCellsList) {
                if ( outerCell.getLocationByDirection(direction) != null){
                    innerCellsOfNeighbor.add(outerCell); // if cell has a neighbor in direction
                }else {
                    // go to next direction, at least one of the outer cells doesn't have a neighbor in direction
                    break ;
                }
            }

            if( innerCellsOfNeighbor.size() == outerCellsList.size()){ // all outer cells have neighbors in direction
                validNeighborsWithAllCells.add(new GraphLocationGroup(this, direction));
            }
        }
        return validNeighborsWithAllCells;
    }


    public I_Location getCellWithMinimumNeighbors(){
        return this.outerCells.peek();
    }



    @Override
    public I_Coordinate getCoordinate() {

        Coordinate_2D[][] coordinates = new Coordinate_2D[this.mapCells.length][this.mapCells[0].length];

        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates[i].length; j++) {
                coordinates[i][j] = (Coordinate_2D) this.mapCells[i][j].getCoordinate();
            }
        }
        Coordinate_2D_LargeAgent coordinate_2D_largeAgent = new Coordinate_2D_LargeAgent(coordinates);
        return coordinate_2D_largeAgent;
    }



    @Override
    public boolean isNeighbor(I_Location other) {

        if (!(other instanceof GraphLocationGroup)){ return false; /* checks that I_Location is GraphLocationGroup */ }
        List<I_Location> myNeighbors = this.getNeighbors();
        if( myNeighbors == null ){ return false; }

        return myNeighbors.contains(other);
    }



    public Enum_direction getNeighborDirection(I_Location other){
        if(!(other instanceof GraphLocationGroup) || ! isNeighbor(other)){ return null; }

        Set<GraphMapVertex_LargeAgents> groupCells = this.getAllCells();
        Set<GraphMapVertex_LargeAgents> otherCells = ((GraphLocationGroup) other).getAllCells();
        GraphMapVertex_LargeAgents hasMinNeighbors = this.outerCells.peek();
        int expectedNeighborCount = groupCells.size();

        for (I_Location neighbor : hasMinNeighbors.getNeighbors()) {

            Enum_direction direction = hasMinNeighbors.getDirection(neighbor);
            for (GraphMapVertex_LargeAgents cellInGroup : groupCells) {
                GraphMapVertex_LargeAgents neighborAtDirection = (GraphMapVertex_LargeAgents) cellInGroup.getLocationByDirection(direction);
                if(! otherCells.contains(neighborAtDirection)){
                    expectedNeighborCount = groupCells.size();
                    break;
                }
                expectedNeighborCount--;
            }

            if( expectedNeighborCount == 0 ){ return direction; }
        }
        return null;
    }


    public Set<GraphMapVertex_LargeAgents> getAllCells(){
        Set<GraphMapVertex_LargeAgents> allCells = new HashSet<>();
        allCells.addAll(this.innerCells);
        allCells.addAll(this.outerCells);
        return allCells;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof GraphLocationGroup)){ return false; }

        GraphMapVertex_LargeAgents[][] myCells = this.mapCells;
        GraphMapVertex_LargeAgents[][] otherCells = ((GraphLocationGroup) other).mapCells;

        if( myCells == null || otherCells == null || myCells.length != otherCells.length){ return false; }

        for (int i = 0; i < myCells.length; i++)
            for (int j = 0; j < myCells[i].length; j++)
                if( myCells[i][j] != otherCells[i][j] )
                    return false;

        // all cells are equals
        return true;
    }



    @Override
    public boolean intersectsWith(I_Location other) {

        if( !(other instanceof GraphLocationGroup)){ return true; }

        GraphMapVertex_LargeAgents[][] myCells = this.mapCells;
        GraphMapVertex_LargeAgents[][] otherCells = ((GraphLocationGroup) other).mapCells;

        if( myCells == null || otherCells == null){ return true; }

        for (int xValue = 0; xValue < Math.min(myCells.length, otherCells.length); xValue++)
            for (int yValue = 0; yValue < Math.min(myCells[xValue].length, otherCells[xValue].length); yValue++)
                if( myCells[xValue][yValue] == otherCells[xValue][yValue] )
                    return true;

        return false; // No intersection
    }



    @Override
    public int hashCode() {
        int hashCode = 1;
        for (GraphMapVertex_LargeAgents e : this.getAllCells())
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }


    @Override
    public String toString() {
        return "GraphLocationGroup{" +
                this.getCoordinate().toString() + '}';
    }



    /*  = Large agents =  */

    public static GraphLocationGroup expendByReferencePoint(GraphLocationGroup intersect, int size){

        if( intersect == null || size <= 0){ return null; /* Invalid input   */ }
        if( size == 1 ){ return intersect;} /*   Agent size is one, returns it's only cell */
        GraphMapVertex_LargeAgents westEdgeCell = intersect.mapCells[0][intersect.mapCells.length - 1];

        List<GraphMapVertex_LargeAgents> westEdge = new ArrayList<>();

        while (  westEdgeCell != null ){
            westEdge.add(westEdgeCell);
            westEdgeCell = (GraphMapVertex_LargeAgents) westEdgeCell.getLocationByDirection(Enum_direction.NORTH);
        }

        GraphMapVertex_LargeAgents[][] expendedGroupLarge = new GraphMapVertex_LargeAgents[intersect.mapCells.length + size - 1][intersect.mapCells[0].length + size - 1];

        return new GraphLocationGroup(expendedGroupLarge);
    }

//    public static GraphLocationGroup[] expendByReferencePoint(GraphLocationGroup intersect, int size1, int size2){
//
//        int size = Math.max(size1, size2);
//        if( intersect == null || size <= 0){ return null; /* Invalid input   */ }
//        if( size == 1 ){ return new GraphLocationGroup[]{intersect, intersect}; /*   Agent size is one, returns it's only cell */}
//        GraphMapVertex_LargeAgents westEdgeCell = intersect.mapCells[0][intersect.mapCells.length - 1];
//
//        List<GraphMapVertex_LargeAgents> westEdge = new ArrayList<>();
//
//        while (  westEdgeCell != null ){
//            westEdge.add(westEdgeCell);
//            westEdgeCell = (GraphMapVertex_LargeAgents) westEdgeCell.getLocationByDirection(Enum_direction.NORTH);
//        }
//
//        GraphMapVertex_LargeAgents[][] expendedGroupLarge = new GraphMapVertex_LargeAgents[intersect.mapCells.length + size - 1][intersect.mapCells[0].length + size - 1];
//
//        for (int i = 0; i < westEdge.size(); i++) {
//            GraphMapVertex_LargeAgents currentWest = westEdge.get(i);
//            for (int j = size - 1; j >= 0 && currentWest != null; j--) {
//                expendedGroupLarge[j][westEdge.size() - i - 1] = currentWest;
//                currentWest = (GraphMapVertex_LargeAgents) currentWest.getLocationByDirection(Enum_direction.WEST);
//                if( currentWest == null ){continue;}
//            }
//
//            GraphMapVertex_LargeAgents currentEast = westEdge.get(i);
//            for (int j = size - 1; j < expendedGroupLarge[i].length && currentEast != null; j++) {
//                expendedGroupLarge[j][westEdge.size() - i - 1] = currentEast;
//                currentEast = (GraphMapVertex_LargeAgents) currentEast.getLocationByDirection(Enum_direction.EAST);
//            }
//        }
//
//        GraphLocationGroup expendedLarge = new GraphLocationGroup(expendedGroupLarge);
//        if(size1 == size2){
//            return new GraphLocationGroup[]{expendedLarge, expendedLarge};
//        }
//
//        int diff = Math.abs(size1 - size2);
//        int smallSize = expendedGroupLarge.length - diff;
//        GraphMapVertex_LargeAgents[][] expendedSmallGroup = new GraphMapVertex_LargeAgents[smallSize][smallSize];
//        for (int i = 0; i < expendedSmallGroup.length; i++) {
//            for (int j = 0; j < expendedSmallGroup[i].length; j++) {
//                expendedSmallGroup[i][j] = expendedGroupLarge[i + diff][j + diff];
//            }
//        }
//
//        if( size1 > size2 ){
//
//        }
//        return new GraphLocationGroup[]{expendedLarge, new GraphLocationGroup(expendedSmallGroup)};
//    }



    public static GraphLocationGroup findIntersection(I_Location location1, I_Location location2){

        GraphLocationGroup group1 = (GraphLocationGroup) location1;
        GraphLocationGroup group2 = (GraphLocationGroup) location2;
        Map<Integer,List<GraphMapVertex_LargeAgents>> intersectedMap = new HashMap<>();
        GraphMapVertex_LargeAgents[][] smallerGroup =   group1.mapCells.length <= group2.mapCells.length ?
                                                        group1.mapCells : group2.mapCells;
        Set<GraphMapVertex_LargeAgents> largerGroup = smallerGroup == group1.mapCells ? group2.getAllCells() : group1.getAllCells();
        int firstInsertion_xValue = -1;
        int firstInsertion_yValue = -1;

        for (int xValue = 0; xValue < smallerGroup.length; xValue++) {
            for (int yValue = 0; yValue < smallerGroup[xValue].length; yValue++) {
                if(largerGroup.contains(smallerGroup[xValue][yValue])){
                    firstInsertion_xValue = firstInsertion_xValue == -1 ? xValue : firstInsertion_xValue;
                    firstInsertion_yValue = firstInsertion_yValue == -1 ? yValue : firstInsertion_yValue;
                    intersectedMap.computeIfAbsent(xValue - firstInsertion_xValue, k-> new ArrayList<>());
                    intersectedMap.get(xValue - firstInsertion_xValue).add(smallerGroup[xValue][yValue]);
                }
            }
        }

        GraphMapVertex_LargeAgents[][] intersectionAsArray = new GraphMapVertex_LargeAgents[intersectedMap.size()][intersectedMap.size()];
        for (int i = 0; i < intersectionAsArray.length; i++) {
            for (int j = 0; j < intersectionAsArray[i].length; j++) {
                intersectionAsArray[i][j] = intersectedMap.get(i).get(j);
            }
        }

        return new GraphLocationGroup(intersectionAsArray);
    }


    public GraphMapVertex_LargeAgents getReferencePoint(){
        return this.mapCells[0][0];
    }




}
