package com.amlogic.cvdemo.model;

import com.amlogic.cvdemo.utils.AssetUtils;

import java.util.LinkedList;
import java.util.List;

public class ModelUtils {
    public static final int MODEL_TYPE_SEMANTIC_SEGMENTATION = 0;
    List<String> getModelListByTag(int model_type) {
        List<String> modelList = new LinkedList<>();

        switch (model_type) {
            case MODEL_TYPE_SEMANTIC_SEGMENTATION:
//                AssetUtils.
                break;
            default:
                break;
        }
        return modelList;
    }

}
