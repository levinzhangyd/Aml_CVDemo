package com.amlogic.cvdemo.data;

import org.tensorflow.lite.DataType;

import java.util.Arrays;

// for record model input/output data
public class ModelData {
    int[] shape;
    DataType dataType;

/*    public ModelData(int[] shape, DataType dataType) {
        this.shape = shape;
        this.dataType = dataType;
    }*/

    public int[] getShape() {
        return shape;
    }

    public void setShape(int[] shape) {
        this.shape = shape;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "ModelData{" +
                "shape=" + Arrays.toString(shape) +
                ", dataType=" + dataType +
                '}';
    }
}
