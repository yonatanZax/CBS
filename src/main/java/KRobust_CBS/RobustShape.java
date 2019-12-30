package KRobust_CBS;

import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Instances.Maps.Enum_MapCellType;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import GraphMapPackage.GraphMapVertex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RobustShape implements I_Location {


    private TimeLocation head;
    private RobustQueue<TimeLocation> queue;

    public RobustShape(TimeLocation head, int k) {
        this.head = head;
        this.queue = new RobustQueue<>(this, k);
    }

    @Override
    public Enum_MapCellType getType() {
        return null;
    }

    @Override
    public List<I_Location> getNeighbors() {
        return this.head.location.getNeighbors();
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
        Set<TimeLocation> myLocations = this.getAllTimeLocations();
        Set<TimeLocation> otherLocations = ((RobustShape) other).getAllTimeLocations();

        Set<TimeLocation> minSizedList =    myLocations.size() <= otherLocations.size() ?
                                            myLocations : otherLocations;

        Set<TimeLocation> maxSizedList =    otherLocations == otherLocations ?
                                            myLocations : otherLocations;


        for (TimeLocation timeLocation : minSizedList) {
            return maxSizedList.contains(timeLocation);
        }

        return false;
    }


    public TimeLocation getHead() {
        return head;
    }

    private void setHead(TimeLocation head) {
        this.head = head;
    }


    public void addCurrentLocation(TimeLocation timeLocation){
        this.queue.add(timeLocation);
    }


    public Set<TimeLocation> getAllTimeLocations(){
        return new HashSet<>(this.queue);
    }



    private class RobustQueue<TimeLocation> extends LinkedList<TimeLocation> {
        private int capacity;
        private RobustShape robustShape;

        public RobustQueue(RobustShape robustShape, int capacity){
            this.capacity = capacity;
            this.robustShape = robustShape;
        }

        @Override
        public boolean add(TimeLocation timeLocation) {
            if(size() >= capacity){
                removeFirst();
                this.robustShape.setHead((BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation) this.peek());
            }
            return super.add(timeLocation);
        }
    }
}
