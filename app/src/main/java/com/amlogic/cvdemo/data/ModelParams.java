package com.amlogic.cvdemo.data;

public class ModelParams {
    public String modelName;
    public String modelFilePath;

    public String getModelFilePath() {
        return modelFilePath;
    }

    public void setModelFilePath(String modelFilePath) {
        this.modelFilePath = modelFilePath;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getDelegatePlatform() {
        return delegatePlatform;
    }

    public void setDelegatePlatform(int delegatePlatform) {
        this.delegatePlatform = delegatePlatform;
    }

    public int delegatePlatform;

    @Override
    public String toString() {
        return "ModelParams{" +
                "modelName='" + modelName + '\'' +
                ", modelFilePath='" + modelFilePath + '\'' +
                ", delegatePlatform=" + delegatePlatform +
                '}';
    }
}
