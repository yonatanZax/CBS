package BasicCBS.Instances.Maps;

import GraphMapPackage.I_InstanceBuilder;

public class MapDimensions{



    // todo - change constructors

    public enum Enum_mapOrientation {
        /*  (X0,Y0)|(X1,Y0)
            (X0,Y1)|(X1,Y1)
        */
        X_HORIZONTAL_Y_VERTICAL, // MovingAi instances

        /*  (Y0,X0)|(Y1,X0)
            (Y0,X1)|(Y1,X1)
        */
        Y_HORIZONTAL_X_VERTICAL // BGU instances
    }


    public int numOfDimensions;
    public int xAxis_length;
    public int yAxis_length;
    public int zAxis_length;
    public Enum_mapOrientation mapOrientation;


    public MapDimensions(){
        this.numOfDimensions = 0; // Indicates unknown size
    }

    public void setMapOrientation(Enum_mapOrientation mapOrientation) {
        this.mapOrientation = mapOrientation;
    }


    public MapDimensions(int[] dimensions, I_InstanceBuilder instanceBuilder){
        this(dimensions, instanceBuilder.getMapOrientation());
    }



    public MapDimensions(int[] dimensions, Enum_mapOrientation mapOrientation){

        this.setMapOrientation(mapOrientation);

        switch ( dimensions.length ){
            case 2:
                this.numOfDimensions = 2;

                if( this.mapOrientation.equals(Enum_mapOrientation.X_HORIZONTAL_Y_VERTICAL)){
                    this.yAxis_length = dimensions[0];
                    this.xAxis_length = dimensions[1];
                }else if( this.mapOrientation.equals(Enum_mapOrientation.Y_HORIZONTAL_X_VERTICAL)){
                    this.xAxis_length = dimensions[0];
                    this.yAxis_length = dimensions[1];
                }

                break;

            case 3:
                // nicetohave - set for 3d
                break;
        }

    }



    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MapDimensions)) return false;
        MapDimensions that = (MapDimensions) other;
        return  this.numOfDimensions    == that.numOfDimensions &&
                this.mapOrientation == that.mapOrientation &&
                this.xAxis_length       == that.xAxis_length &&
                this.yAxis_length       == that.yAxis_length &&
                this.zAxis_length       == that.zAxis_length;
    }

}
