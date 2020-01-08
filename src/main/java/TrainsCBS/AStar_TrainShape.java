package TrainsCBS;

import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.Solution;
import KRobust_CBS.AStar_RobustShape;
import KRobust_CBS.RobustAgent;
import KRobust_CBS.RobustShape;

import java.util.ArrayList;
import java.util.List;

public class AStar_TrainShape extends AStar_RobustShape {


    protected RobustShape stayInPlace(RobustShape shape){
        return shape;
    }

}
