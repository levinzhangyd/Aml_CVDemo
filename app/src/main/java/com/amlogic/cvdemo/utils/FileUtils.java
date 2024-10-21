package com.amlogic.cvdemo.utils;

import static com.amlogic.cvdemo.model.ModelUtils.MODEL_TYPE_SEMANTIC_SEGMENTATION;
import static com.amlogic.cvdemo.model.ModelUtils.MODEL_TYPE_SUPER_RESOLUTION;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static  List<String> getImageSrcList(Context context, int modelType) {
        StringBuilder builder = new StringBuilder(context.getFilesDir().getAbsolutePath());
        builder.append("/AI/src");
        switch (modelType) {
            case MODEL_TYPE_SEMANTIC_SEGMENTATION:
                builder.append("/semantic_segmentation");
                break;
            case MODEL_TYPE_SUPER_RESOLUTION:
                builder.append("/super_resolution");
                break;
            default:
                break;
        }
        builder.append("/image_src");
        Log.d(TAG, "string =" + builder.toString());
        return getFileNameListByDir(builder.toString());
    }

    private static List<String> getFileNameListByDir(String dirPath) {
        File directory = new File(dirPath);
        List<String> nameList = new ArrayList<>();
        // 检查路径是否有效并且是一个文件夹
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是文件夹，递归调用
                    //listFiles(file.getAbsolutePath(), fileNames);
                } else {
                    // 否则，添加文件名到列表
                    nameList.add(file.getAbsolutePath());
                }
            }
        } else {
            Log.e(TAG, "提供的路径无效或不是一个文件夹: " + dirPath);
        }
        return nameList;
    }

    public static List<String> getModelList(Context context, int modelType) {
        StringBuilder builder = new StringBuilder(context.getFilesDir().getAbsolutePath());
        builder.append("/AI/src");
        switch (modelType) {
            case MODEL_TYPE_SEMANTIC_SEGMENTATION:
                builder.append("/semantic_segmentation");
                break;
            case MODEL_TYPE_SUPER_RESOLUTION:
                builder.append("/super_resolution");
                break;
            default:
                break;
        }
        builder.append("/models");
        Log.d(TAG, "string =" + builder.toString());
        return getFileNameListByDir(builder.toString());
    }

    public static void writeByteArrayToFile(byte[] data, String filePath) {
        FileOutputStream fos = null;
        try {
            // 创建文件
            File file = new File(filePath);
            fos = new FileOutputStream(file);

            // 写入 byte[] 到文件
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    // 关闭文件输出流
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
