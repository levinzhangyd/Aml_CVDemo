package com.amlogic.cvdemo.dump;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.amlogic.cvdemo.data.ModelData;

import java.nio.ByteBuffer;

public class DumpUtils {

    private static final String TAG = "DumpUtils";
    private static volatile DumpUtils mDumInstance;
    private static String parentDir;
    public static boolean init(Context context, String subdir) {
        parentDir = context.getFilesDir() + subdir;
        return true;
    }

    public static DumpUtils getInstance() {
        if (mDumInstance == null) {
            Log.e(TAG, "not init, stop");
        }
        return mDumInstance;
    }

    public static boolean dumpTFData(ByteBuffer byteBuffer, ModelData dataFormat) {
        
        return true;
    }

    public static boolean dumpProcessData() {

        return true;
    }

    public static boolean dumpBitmap(Bitmap bitmap, String name) {
        return true;
    }
}
