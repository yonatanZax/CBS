package LargeAgents_CBS.Instances;


import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import LargeAgents_CBS.Instances.Maps.Coordinate_2D_LargeAgent;

public class LargeAgent extends Agent {


    private int height;
    private int width;

    public LargeAgent(int iD, I_Coordinate source, I_Coordinate target) {
        super(iD, source, target);

        this.height = ((Coordinate_2D_LargeAgent)source).getHeight();
        this.width = ((Coordinate_2D_LargeAgent)source).getWidth();
    }


    public LargeAgent(Agent other){
        super(other.iD, new Coordinate_2D_LargeAgent((Coordinate_2D) other.source), new Coordinate_2D_LargeAgent((Coordinate_2D) other.target));

        this.height = 1;
        this.width = 1;
    }
}
