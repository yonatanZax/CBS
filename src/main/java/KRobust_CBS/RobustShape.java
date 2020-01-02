package KRobust_CBS;

import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import GraphMapPackage.GraphMapVertex;

import java.util.*;

public class RobustShape implements I_Location {

    private TimeLocation head;
    private RobustQueue locations;


    public RobustShape(TimeLocation head, int capacity){
        this.head = head;
        this.locations = new RobustQueue(this, capacity);
    }

    public RobustShape(TimeLocation head, RobustShape prevRobustShape) {
        this.head = head;
        this.locations = new RobustQueue<I_Location>(this, prevRobustShape.locations, head.location);
    }

    public RobustShape(TimeLocation head, RobustQueue<I_Location> locations) {
        this.head = head;
        this.locations = locations;
    }

    public static RobustShape stayInPlace(RobustShape prevShape){
        RobustShape robustShape = new RobustShape(prevShape.getHead(), prevShape);
        return robustShape;
    }

    public static RobustShape stayInGoal(RobustShape prevShape){
        RobustQueue<I_Location> locations = prevShape.locations;
        if(locations.size() == 0){
            return null;
        }
        locations.remove(0);
        TimeLocation timeLocation = new TimeLocation(prevShape.head.time + 1, prevShape.head.location);
        RobustShape robustShape = new RobustShape(timeLocation, locations);
        return robustShape;
    }

    @Override
    public Enum_MapCellType getType() {
        return null;
    }

    @Override
    public List<I_Location> getNeighbors() {
        List<I_Location> headNeighbors = this.head.location.getNeighbors();
        List<I_Location> neighbors = new ArrayList<>();

        for (I_Location neighbor : headNeighbors) {
            neighbors.add( new RobustShape(new TimeLocation(this.head.time,neighbor),this));
        }
        return neighbors;
    }

    @Override
    public I_Coordinate getCoordinate() {
        return this.head.location.getCoordinate();
    }

    @Override
    public boolean isNeighbor(I_Location other) {
        return this.head.location.isNeighbor(((RobustShape)other).getHead().location);
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


    public void setHead(TimeLocation head) {
        this.head = head;
    }

    public TimeLocation getHead() {
        return head;
    }

    public int getHeadTime(){
        return this.head.time;
    }

    public I_Location getHeadLocation(){
        return this.head.location;
    }

    public int getSize(){
        return this.locations.getSize();
    }




    public Set<I_Location> getAllLocations(){
        Set<I_Location> locations = this.locations.getAllLocations();
        locations.add(this.getHead().location);
        return locations;
    }


    @Override
    public String toString() {
        return "RobustShape{" +
                "head=" + head +
                ", locations=" + locations +
                '}';
    }

    private class RobustQueue<I_Location> extends ArrayList<I_Location> {
        private final int capacity;
        private final RobustShape robustShape;

        public RobustQueue(RobustShape robustShape, int capacity){
            this.capacity = capacity;
            this.robustShape = robustShape;
            this.add((I_Location) robustShape.getHead().location);
        }


        public RobustQueue(RobustShape robustShape, RobustQueue prevQueue, I_Location headLocation){
            this.capacity = prevQueue.capacity;
            this.robustShape = robustShape;
            this.addAll(prevQueue);
            this.addHead(headLocation);
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
            this.robustShape.setHead(new TimeLocation(this.robustShape.head.time + 1, castLocation));
            this.add(location);
        }

        @Override
        public boolean add(I_Location location) {
            if(size() > capacity){
                remove(0);
            }
            return super.add(location);
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
