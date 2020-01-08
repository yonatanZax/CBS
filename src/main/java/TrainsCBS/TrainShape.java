package TrainsCBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import KRobust_CBS.RobustShape;

import java.util.ArrayList;
import java.util.List;

public class TrainShape extends RobustShape {


    public TrainShape(I_Location head, int capacity) {
        super(head, capacity);
    }

    public TrainShape(I_Location head, TrainShape prevRobustShape) {
        super(head, prevRobustShape);
    }

    public TrainShape(I_Location head, RobustQueue<I_Location> locations) {
        super(head, locations);
    }

    public TrainShape(TrainShape other) {
        super(other.head, other.locations);
    }



    @Override
    public List<I_Location> getNeighbors() {
        List<I_Location> headNeighbors = this.head.getNeighbors();
        List<I_Location> neighbors = new ArrayList<>();

        for (I_Location neighbor : headNeighbors) {
            if( !this.locations.contains(neighbor)){
                neighbors.add( new TrainShape(neighbor,this));
            }
        }
        return neighbors;
    }

    public static TrainShape stayInPlace(TrainShape prevShape){
        return new TrainShape(prevShape);
    }


    public static TrainShape stayInGoal(TrainShape prevShape){
        RobustQueue locations = new RobustQueue<I_Location>(prevShape.locations);
        if(locations.size() == 0){ return null; }
        locations.remove(0);
//        TimeLocation timeLocation = new TimeLocation(prevShape.head.time + 1, prevShape.head.location);
        TrainShape robustShape = new TrainShape(prevShape.getHead(), locations);
        return robustShape;
    }
}
