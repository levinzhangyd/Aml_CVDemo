
/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amlogic.cvdemo.interpreter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amlogic.cvdemo.data.ModelParams;
import com.amlogic.cvdemo.utils.AssetUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class for wrapping Image Classification actions
 */
public class SuperResolutionHelper {
    private static final String TAG = "SuperResolutionHelper";
    private final CVDetectListener mListener;
    private final Context mContext;
    private SuperResolutionInterpreter superResolutionInterpreter;

    public static final boolean DEBUG_MODEL = true;
    public static final boolean SUPPORT_DUMP_IMAGE = false;
    private ExecutorService poolExecutor = null;
    private ByteBuffer localBuf;
    private String filePath;

    Runnable modelInferenceRunnable = new Runnable() {
        @Override
        public void run() {
//            Bitmap bitmap  = TFUtils.loadImageFromAssets(mContext, filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            Log.d(TAG, "current filepath =" + filePath);
            superResolutionInterpreter.predict(bitmap);
        }
    };


    public SuperResolutionHelper(Context context,
                                 CVDetectListener listener) {

        poolExecutor = Executors.newFixedThreadPool(1);
        mContext = context;
        mListener = listener;
    }



    public void initInterpreter(ModelParams modelName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                superResolutionInterpreter = SuperResolutionInterpreter.create(mContext, modelName, mListener);
                if (superResolutionInterpreter != null) {
//            ModelData inputData = semanticInterpreter.get
                }
            }
        }).start();
    }

    public void inference(String path) {
        try {
            filePath = path;
            poolExecutor.execute(modelInferenceRunnable);
        } catch (Exception e) {
            Log.e(TAG, "threadPool execute exception" + e);
        }
    }


    public void clearInterpreter() {
        poolExecutor.shutdownNow();
        superResolutionInterpreter.clearInterpreter();
    }

}
