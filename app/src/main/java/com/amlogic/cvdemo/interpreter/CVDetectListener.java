package com.amlogic.cvdemo.interpreter;

import android.graphics.Bitmap;

import com.amlogic.cvdemo.data.ModelTime;

public interface CVDetectListener {
    void onResult(int model_type, Bitmap retBitmap, ModelTime kpiTime);
    void onError(int model_type, int errorCode);

}
