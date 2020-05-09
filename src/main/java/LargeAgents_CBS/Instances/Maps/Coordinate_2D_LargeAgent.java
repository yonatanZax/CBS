package LargeAgents_CBS.Instances.Maps;

import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;

import java.util.Arrays;

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


    @Override
    public boolean equals(Object obj) {

        if( !(obj instanceof Coordinate_2D_LargeAgent) ){
            return false;
        }

        Coordinate_2D_LargeAgent other = (Coordinate_2D_LargeAgent) obj;

        if( this.getHeight() != other.getHeight() || this.getWidth() != other.getWidth()){
            return false;
        }

        for (int i = 0; i < this.coordinates.length; i++) {
            for (int j = 0; j < this.coordinates[i].length; j++) {
                if(this.coordinates[i][j] == null){ continue; }
                if( this.coordinates[i][j].x_value != other.coordinates[i][j].x_value ||
                    this.coordinates[i][j].y_value != other.coordinates[i][j].y_value){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String result = "";
        for (Coordinate_2D[] coordinate : this.coordinates) {
            result +=  Arrays.toString(coordinate);
        }
        return result;
    }
}
