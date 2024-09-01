package com.amlogic.cvdemo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AssetUtils {
    private static final String TAG = "AssetUtils";

    public static List<String> listFilesInAssetFolder(Context context, String folderName) {
        AssetManager assetManager = context.getAssets();
        List<String> list = new LinkedList<>();
        try {
            // 获取指定文件夹下的所有文件
            String[] files = assetManager.list(folderName);
            if (files != null) {
                for (String file : files) {
                    list.add(file);
                    Log.d(TAG, "add file " + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // 处理异常
        }
        return list;
    }
}
