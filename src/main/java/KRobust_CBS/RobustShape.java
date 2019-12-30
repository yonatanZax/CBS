package KRobust_CBS;

import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;

import java.util.*;

public class RobustShape implements I_Location {

    private TimeLocation head;
    private RobustQueue locations;


    public RobustShape(TimeLocation head, int capacity){
        this.head = head;
        this.locations = new RobustQueue(head.location, capacity);
    }

    public RobustShape(TimeLocation head, RobustShape prevRobustShape) {
        this.head = head;
        this.locations = new RobustQueue<I_Location>(prevRobustShape.locations, head.location);
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
        Set<I_Location> otherLocations = ((RobustShape) other).getAllLocations();

        Set<I_Location> minSizedList =    myLocations.size() <= otherLocations.size() ?
                                          myLocations : otherLocations;

        Set<I_Location> maxSizedList =    otherLocations == otherLocations ?
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


    public Set<I_Location> getAllLocations(){
        return this.locations.getAllLocations();
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

        public RobustQueue(I_Location location, int capacity){
            this.capacity = capacity;
            this.add(location);
        }


        public RobustQueue(RobustQueue prevQueue, I_Location location){
            this.capacity = prevQueue.capacity;
            this.addAll(prevQueue);
            this.add(location);
        }

        @Override
        public boolean addAll(Collection<? extends I_Location> locations){
            for (I_Location location : locations) {
                this.add(location);
            }
            return false;
        }

        @Override
        public boolean add(I_Location location) {
            if(size() >= capacity){
                remove(0);
            }
            return super.add(location);
        }


        public Set<I_Location> getAllLocations(){
            Set<I_Location> locations = new HashSet<>();
            for (int i = 0; i < size() ; i++){
                locations.add(this.get(i));
            }
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
