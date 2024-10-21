package com.amlogic.cvdemo.dump;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.model.ModelUtils;
import com.amlogic.cvdemo.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public  class AMLDump {

    private static final String TAG = "DumpUtils";
    private static volatile AMLDump mDumInstance;
    private String parentDir;
    private static boolean dumpEnable = false;
    public  boolean init(Context context, int modelType, boolean enable) {
        parentDir = context.getFilesDir() + "/AI/dump";
        switch (modelType) {
            case ModelUtils.MODEL_TYPE_SEMANTIC_SEGMENTATION:
                parentDir += "/semantic_segmentation/";
                break;
            case ModelUtils.MODEL_TYPE_SUPER_RESOLUTION:
                parentDir += "/super_resolution/";
                break;
        }
        dumpEnable = enable;
        return true;
    }

    public  static AMLDump getInstance() {
        if (mDumInstance == null) {
            Log.e(TAG, "not init, stop");
            mDumInstance = new AMLDump();
        }
        return mDumInstance;
    }

    public  boolean dumpTFData(ByteBuffer data, ModelData modelData, int type) {
        if (!dumpEnable) {
            Log.d(TAG, "dump is not enabled");
            return false;
        }

        String fileName = DumpUtils.generateTFModelDataName(modelData, type);
        Log.d(TAG, "dumpTFData TYPE =" + type + "path=" + fileName);
        // 获取应用的文件目录
        File fileDir = new File(parentDir);

        // 检查文件夹是否存在，如果不存在则创建
        if (!fileDir.exists()) {
            boolean isCreated = fileDir.getAbsoluteFile().mkdirs(); // 创建多级目录
            if (!isCreated) {
                // 文件夹创建失败
                System.out.println("Failed to create directory: " + parentDir);
                return false;
            }
        }

        // 创建文件
        File file = new File(parentDir, fileName);
        FileOutputStream fos = null;
        try {
            // 创建文件输出流
            fos = new FileOutputStream(file);

            // 写入 byte[] 到文件
            fos.write(data.array());
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

        return true;
    }



    public  boolean dumpBitmap(Bitmap bitmap, String name) {

        return true;
    }
}
