package LargeAgents_CBS.Instances;


import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;

public class InstanceBuilder_BGU_LA extends InstanceBuilder_BGU {

   @Override
   protected Agent buildSingleAgent(int dimensions, String line){

       String[] agentLine = line.split(this.SEPARATOR_AGENTS);

       if( agentLine.length < 1){ return null; /* invalid agent line */ }

       int agentID = Integer.parseInt(agentLine[0]);
       dimensions = ( dimensions == 0 ? dimensions = this.defaultNumOfDimensions : dimensions);

       if(dimensions == 2) {
           /*      source values    */
           int source_xValue = Integer.valueOf(agentLine[this.INDEX_AGENT_SOURCE_XVALUE]);
           int source_yValue = Integer.valueOf(agentLine[this.INDEX_AGENT_SOURCE_YVALUE]);
           Coordinate_2D source = new Coordinate_2D(source_xValue, source_yValue);
           /*      Target values    */
           int target_xValue = Integer.valueOf(agentLine[this.INDEX_AGENT_TARGET_XVALUE]);
           int target_yValue = Integer.valueOf(agentLine[this.INDEX_AGENT_TARGET_YVALUE]);
           Coordinate_2D target = new Coordinate_2D(target_xValue, target_yValue);

           return new LargeAgent(new Agent(agentID, source, target));
       }



       return null; // Bad dimensions input
   }

}