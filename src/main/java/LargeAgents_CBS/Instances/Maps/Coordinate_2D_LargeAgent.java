package LargeAgents_CBS.Instances.Maps;

import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;

public class Coordinate_2D_LargeAgent implements I_Coordinate {

    private Coordinate_2D[][] coordinates;

    public Coordinate_2D_LargeAgent(Coordinate_2D[][] coordinates){
        this.coordinates = coordinates;
    }

    public Coordinate_2D_LargeAgent(Coordinate_2D coordinate_2D){
        this.coordinates = new Coordinate_2D[][]{{coordinate_2D}};
    }



    public int getHeight(){
        return this.coordinates.length;
    }

    public int getWidth(){
        return this.coordinates[0].length;
    }


    @Override
    public float distance(I_Coordinate other) {
        return 0;
    }

    public Coordinate_2D[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinate_2D[][] coordinates) {
        this.coordinates = coordinates;
    }
}
