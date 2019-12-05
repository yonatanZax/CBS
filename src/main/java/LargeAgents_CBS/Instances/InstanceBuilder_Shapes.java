package LargeAgents_CBS.Instances;


import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_MovingAI;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.MapDimensions;
import Environment.IO_Package.IO_Manager;
import Environment.IO_Package.Reader;
import LargeAgents_CBS.Instances.Maps.Coordinate_2D_LargeAgent;

public class InstanceBuilder_Shapes extends InstanceBuilder_MovingAI {

    protected static final MapDimensions.Enum_mapOrientation mapOrientation = MapDimensions.Enum_mapOrientation.X_HORIZONTAL_Y_VERTICAL;


    private final int INDEX_AGENT_SOURCE_XVALUE = 1;
    private final int INDEX_AGENT_SOURCE_YVALUE = 2;
    private final int INDEX_AGENT_TARGET_XVALUE = 3;
    private final int INDEX_AGENT_TARGET_YVALUE = 4;
    private final int INDEX_AGENT_SIZE_XVALUE = 5;
    private final int INDEX_AGENT_SIZE_YVALUE = 6;

    // Skip Lines
    protected final int SKIP_LINES_MAP = 1;
    protected final int SKIP_LINES_SCENARIO = 3;



    protected Agent buildSingleAgent(int id, String agentLine) {

        // done - build agent from string
        String[] splitLine = agentLine.split(super.SEPARATOR_SCENARIO);
        // Init coordinates
        int xSize = this.getSize(splitLine, INDEX_AGENT_SIZE_XVALUE);
        int ySize = this.getSize(splitLine, INDEX_AGENT_SIZE_YVALUE);

        // Initiate source and target coordinates
        int source_xValue = Integer.parseInt(splitLine[this.INDEX_AGENT_SOURCE_XVALUE]);
        int source_yValue = Integer.parseInt(splitLine[this.INDEX_AGENT_SOURCE_YVALUE]);
        Coordinate_2D_LargeAgent source = generateLargeAgentCoordinates(source_xValue, xSize, source_yValue, ySize);

        int target_xValue = Integer.parseInt(splitLine[this.INDEX_AGENT_TARGET_XVALUE]);
        int target_yValue = Integer.parseInt(splitLine[this.INDEX_AGENT_TARGET_YVALUE]);
        Coordinate_2D_LargeAgent target = generateLargeAgentCoordinates(target_xValue, xSize, target_yValue, ySize);

        return new LargeAgent(id, source, target);
    }


    private int getSize(String[] agentLine, int index){
        // Check if index is valid and value is an positive int
        if(agentLine.length < index + 1 || ! IO_Manager.isPositiveInt(agentLine[index])){
            return 1;
        }
        return Integer.parseInt(agentLine[index]); // parse value
    }


    private Coordinate_2D_LargeAgent generateLargeAgentCoordinates(int refPoint_xValue, int xSize, int refPoint_yValue, int ySize){

        Coordinate_2D[][] coordinates = new Coordinate_2D[xSize][ySize];

        for (int xValue = 0; xValue < xSize; xValue++) {
            for (int yValue = 0; yValue < ySize; yValue++) {
                coordinates[xValue][yValue] = new Coordinate_2D(xValue + refPoint_xValue, yValue + refPoint_yValue);
            }
        }
        return new Coordinate_2D_LargeAgent(coordinates);
    }


    /*  = Skip getters =  */
    protected int getSKIP_LINES_MAP(){
        return this.SKIP_LINES_MAP;
    }

    protected int getSKIP_LINES_SCENARIO(){
        return this.SKIP_LINES_SCENARIO;
    }
}
