package com.amlogic.cvdemo.utils;

import android.util.Log;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.data.ModelKpiTime;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static int MODEL_TYPE_IN = 0;
    public static int MODEL_TYPE_OUT = 1;

    public static String convertModelData2String(int modelTypeInt, ModelData data) {
        StringBuilder buffer = new StringBuilder();
        switch (modelTypeInt) {
            //todo, magic number
            case 0:
                buffer.append("Model Input:  ");
                break;
            case 1:
                buffer.append("Model Output: ");
                break;
            default:
                break;
        }
        int len = data.getShape().length;
        int[] shape = data.getShape();
        buffer.append("[");
        for (int i = 0; i < len -1; i++) {
            buffer.append("").append(shape[i]).append(", ");
        }
        buffer.append("").append(shape[len - 1]).append("],  ");
        buffer.append("DataType: ").append("").append(data.getDataType().toString());
        return buffer.toString();
    }

    public static String convertKpiData2String(ModelKpiTime kpiTime) {

        String buffer = "[Preprocess Time:" + kpiTime.getPreProcessTime() + "ms]     " +
                "[Inference Time:" + kpiTime.getInferenceTime() + "ms]     " +
                "[PostProcess Time:" + kpiTime.getPostInferenceTime() + "ms]";
        return buffer;
    }

    public static List<String> getNameFromFullPath(List<String> fullNameList) {

        List<String>  nameList = new ArrayList<>();
        for(int i = 0; i < fullNameList.size(); i++) {
            String fullPath = fullNameList.get(i);
            int lastSlashIndex = fullPath.lastIndexOf('/'); // 找到最后一个斜杠的位置
            String fileName = fullPath.substring(lastSlashIndex + 1); // 获取文件名
            nameList.add(fileName);
        }
        return nameList;
    }
}
