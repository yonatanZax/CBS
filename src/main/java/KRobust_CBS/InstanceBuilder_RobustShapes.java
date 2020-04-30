package KRobust_CBS;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.MapDimensions;
import LargeAgents_CBS.Instances.InstanceBuilder_Shapes;

public class InstanceBuilder_RobustShapes extends InstanceBuilder_Shapes {


    protected MapDimensions.Enum_mapOrientation mapOrientation = MapDimensions.Enum_mapOrientation.Y_HORIZONTAL_X_VERTICAL;


    private int k = 0;

    public InstanceBuilder_RobustShapes(){ }

    public InstanceBuilder_RobustShapes(int k){
        super();
        this.k = k;
    }


    @Override
    protected Agent buildSingleAgent(int dimensions, String line){

        String[] agentLine = line.split(SEPARATOR_SCENARIO);

        if( agentLine.length < 1){ return null; /* invalid agent line */ }

        int agentID = Integer.parseInt(agentLine[0]);

        /*      source values    */
        int source_xValue = Integer.valueOf(agentLine[this.INDEX_AGENT_SOURCE_XVALUE]);
        int source_yValue = Integer.valueOf(agentLine[this.INDEX_AGENT_SOURCE_YVALUE]);
        Coordinate_2D source = new Coordinate_2D(source_xValue, source_yValue);
        /*      Target values    */
        int target_xValue = Integer.valueOf(agentLine[this.INDEX_AGENT_TARGET_XVALUE]);
        int target_yValue = Integer.valueOf(agentLine[this.INDEX_AGENT_TARGET_YVALUE]);
        Coordinate_2D target = new Coordinate_2D(target_xValue, target_yValue);

        Agent agent = new Agent(agentID, source, target);

        return this.createAgent(agent, this.k);
    }




    protected Agent createAgent(Agent agent, int k){
        return new RobustAgent(agent, k);
    }


}
