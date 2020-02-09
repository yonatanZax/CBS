package KRobust_CBS;

import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import GraphMapPackage.GraphMapVertex;

import java.util.*;

public class RobustShape implements I_Location {

    protected I_Location head;
    protected RobustQueue locations;


    public RobustShape(I_Location head, int capacity){
        this.head = head;
        this.locations = new RobustQueue(this, capacity);
    }

    public RobustShape(I_Location head, RobustShape prevRobustShape) {
        this.head = head;
        this.locations = new RobustQueue(this,prevRobustShape.locations);
        this.locations.addAll(prevRobustShape.locations);
        this.locations.addHead(this.getHeadLocation());
//        this.locations = new RobustQueue<I_Location>(this, prevRobustShape.locations, head.location);
    }

    public RobustShape(I_Location head, RobustQueue<I_Location> locations) {
        this.head = head;
        this.locations = locations;
    }

    public RobustShape(RobustShape other){
        this.head = other.getHead();
        this.locations.addAll(other.locations);
    }

    public static RobustShape stayInPlace(RobustShape prevShape){
        RobustShape robustShape = new RobustShape(prevShape.getHead(), prevShape);
        return robustShape;
    }

    public static RobustShape stayInGoal(RobustShape prevShape){
        RobustQueue locations = new RobustQueue<I_Location>(prevShape.locations);
        if(locations.size() == 0){ return null; }
        locations.remove(0);
        RobustShape robustShape = new RobustShape(prevShape.getHead(), locations);
        return robustShape;
    }

    @Override
    public Enum_MapCellType getType() {
        return null;
    }

    @Override
    public List<I_Location> getNeighbors() {
        List<I_Location> headNeighbors = this.head.getNeighbors();
        List<I_Location> neighbors = new ArrayList<>();

        for (I_Location neighbor : headNeighbors) {
            neighbors.add( new RobustShape(neighbor,this));
        }
        return neighbors;
    }

    @Override
    public I_Coordinate getCoordinate() {
        return this.head.getCoordinate();
    }

    @Override
    public boolean isNeighbor(I_Location other) {
        return this.head.isNeighbor(((RobustShape)other).getHead());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RobustShape)) return false;
        RobustShape that = (RobustShape) o;

//        ArrayList thisLocations = this.locations.getLocationsByOrder();
//        ArrayList thatLocations = that.locations.getLocationsByOrder();
//
//
//        if( thisLocations.size() != thatLocations.size()){return false;}
//
//        for (int i = 0; i < thisLocations.size(); i++) {
//            if(thisLocations.get(i).equals(thatLocations.get(i))){
//                return false;
//            }
//        }

//        return this.getHead().equals(that.getHead());
//
        Set<I_Location> locationsThis = this.getAllLocations();
        Set<I_Location> locationThat = that.getAllLocations();
//        return locationsThis.equals(locationThat);
        return locationsThis.equals(locationThat) && this.getHead().equals(that.getHead());
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, locations);
    }

    @Override
    public boolean intersectsWith(I_Location other) {

        Set<I_Location> myLocations = this.getAllLocations();
        if( other instanceof GraphMapVertex){
            return myLocations.contains(other);
        }
        Set<I_Location> otherLocations = ((RobustShape) other).getAllLocations();

        Set<I_Location> minSizedList =    myLocations.size() <= otherLocations.size() ?
                                          myLocations : otherLocations;

        Set<I_Location> maxSizedList =    otherLocations.equals(minSizedList) ?
                                          myLocations : otherLocations;


        for (I_Location timeLocation : minSizedList) {
            return maxSizedList.contains(timeLocation);
        }

        return false;
    }


    public void setHead(I_Location head) {
        this.head = head;
    }

    public I_Location getHead() {
        return head;
    }


    public I_Location getHeadLocation(){
        return this.head;
    }

    public int getSize(){
        int size = this.locations.getSize();
        return size;
    }




    public Set<I_Location> getAllLocations(){
        Set<I_Location> locations = this.locations.getAllLocations();
        locations.add(this.getHead());
        return locations;
    }


    @Override
    public String toString() {
        return "RobustShape{" +
                "head=" + head +
                ", locations=" + locations +
                '}';
    }

    protected static class RobustQueue<I_Location> extends ArrayList<I_Location> {
        private final int capacity;
        private final RobustShape robustShape;

        public RobustQueue(RobustShape robustShape, int capacity){
            this.capacity = capacity;
            this.robustShape = robustShape;
            this.add((I_Location) robustShape.getHeadLocation());
        }



        public RobustQueue(RobustQueue locations) {
            this.capacity = locations.capacity;
            this.robustShape = locations.robustShape;
            this.addAll(locations);
        }

        public RobustQueue(RobustShape robustShape, RobustQueue prevQueue) {
            this.capacity = prevQueue.capacity;
            this.robustShape = robustShape;
        }

        @Override
        public boolean addAll(Collection<? extends I_Location> locations){
            for (I_Location location : locations) {
                this.add(location);
            }
            return false;
        }


        private void addHead(I_Location location){
            BasicCBS.Instances.Maps.I_Location castLocation = (BasicCBS.Instances.Maps.I_Location) location;
            this.robustShape.setHead( castLocation );
            this.add(location);
        }

        @Override
        public boolean add(I_Location location) {
            if(size() > capacity){
                remove(0);
            }
            return super.add(location);
        }



        public ArrayList<I_Location> getLocationsByOrder(){
            return new ArrayList<>(this);
        }

        public Set<I_Location> getAllLocations(){

            Set<I_Location> locations = new HashSet<>(this);
//            for (int i = 0; i < size() ; i++){
//                locations.add(this.get(i));
//            }
            return locations;
        }

        public int getSize(){
            return super.size();
        }


        @Override
        public String toString() {
            return "RobustQueue{" +
                    "capacity=" + capacity +
                    '}';
        }
    }
}
