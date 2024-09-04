package com.amlogic.cvdemo.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFUtils {
    public static MappedByteBuffer loadModelFile(Context context, String modelName) {
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getAssets().openFd(modelName);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap loadImageFromAssets(Context mContext, String image) {
        return null;
    }

}
