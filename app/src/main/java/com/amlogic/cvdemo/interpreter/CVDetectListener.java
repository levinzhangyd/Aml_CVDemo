package com.amlogic.cvdemo.interpreter;

import android.graphics.Bitmap;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.data.ModelKpiTime;

public interface CVDetectListener {
    void onResult(int model_type, Bitmap retBitmap, ModelKpiTime kpiTime);
    void onError(int model_type, int errorCode);
    void onLoadSuccess(ModelData in, ModelData out);
}
