package com.amlogic.cvdemo.data;

import android.util.Log;

import java.util.Arrays;

public class ModelKpiTime {
    public int preProcessTime;
    public int inferenceTime;
    public int postInferenceTime;
    public int[] reservedTime = new int[3];

    public int getPreProcessTime() {
        return preProcessTime;
    }

    public void setPreProcessTime(int preProcessTime) {
        this.preProcessTime = preProcessTime;
    }

    public int getInferenceTime() {
        return inferenceTime;
    }

    public void setInferenceTime(int inferenceTime) {
        this.inferenceTime = inferenceTime;
    }

    public int getPostInferenceTime() {
        return postInferenceTime;
    }

    public void setPostInferenceTime(int postInferenceTime) {
        this.postInferenceTime = postInferenceTime;
    }

    public int[] getReservedTime() {
        return reservedTime;
    }

    public void setReservedTime(int time, int index) {
        if (index >= this.reservedTime.length) {
            Log.e("temp", "invalid index, quit!");
            return;
        }
        this.reservedTime[index] = time;
    }

    @Override
    public String toString() {
        return "ModelKpiTime{" +
                "preProcessTime=" + preProcessTime +
                ", inferenceTime=" + inferenceTime +
                ", postInferenceTime=" + postInferenceTime +
//                ", reservedTime=" + Arrays.toString(reservedTime) +
                '}';
    }
}
