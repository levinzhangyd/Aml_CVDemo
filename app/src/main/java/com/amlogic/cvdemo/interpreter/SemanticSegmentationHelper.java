
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
import android.util.Log;

import com.amlogic.cvdemo.utils.AssetUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class for wrapping Image Classification actions
 */
public class SemanticSegmentationHelper {
    private static final String TAG = "SemanticSegmentationHelper";
    private final CVDetectListener mListener;
    private  Context mContext;
    private SemanticSegmentationInterpreter semanticInterpreter;

    public static final boolean DEBUG_MODEL = true;
    public static final boolean SUPPORT_DUMP_IMAGE = false;
    private ExecutorService poolExecutor = null;
    private ByteBuffer localBuf;
    private boolean clsModelWorking = false;
    private boolean objectModelWorking = true;
//    Runnable classifyRunnable = new Runnable() {
//        @Override
//        public void run() {
//            semanticInterpreter.detectGameScene(localBuf);
//        }
//    };

    Runnable modelInferenceRunnable = new Runnable() {
        @Override
        public void run() {
            semanticInterpreter.modelInference(localBuf);
        }
    };




    public SemanticSegmentationHelper(Context context,
                      CVDetectListener listener) {

        poolExecutor = Executors.newFixedThreadPool(3);
        mContext = context;
        mListener = listener;
    }

    public List<String> getModelList(int modeType) {
        return AssetUtils.listFilesInAssetFolder(mContext, "semantic_segmentation");
    }

    public boolean initInterpreter(String modelName) {
        semanticInterpreter = SemanticSegmentationInterpreter.create(mContext, modelName, mListener);
        return false;
    }

    public void detect(ByteBuffer buf) {
        localBuf = buf;
        try {
            poolExecutor.execute(modelInferenceRunnable);
        } catch (Exception e) {
            Log.e(TAG, "threadPool execute exception" + e);
        }
    }


    public void clearInterpreter() {
        poolExecutor.shutdownNow();
        semanticInterpreter.clearInterpreter();
    }

}
