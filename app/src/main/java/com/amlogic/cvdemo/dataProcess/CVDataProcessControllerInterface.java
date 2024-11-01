package com.amlogic.cvdemo.dataProcess;

import android.graphics.Bitmap;

import com.amlogic.cvdemo.data.ModelData;

import java.nio.ByteBuffer;

public interface CVDataProcessControllerInterface {
    public boolean init(ModelData in, ModelData out);
    public ByteBuffer preProcess(Bitmap bitmap);
    public Bitmap postProcess(ByteBuffer outputBuffer);
    public void destroy();
}
