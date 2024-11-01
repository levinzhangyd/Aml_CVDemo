package com.amlogic.cvdemo.interpreter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.data.ModelKpiTime;
import com.amlogic.cvdemo.data.ModelParams;
import com.amlogic.cvdemo.dataProcess.CVDataProcessControllerInterface;
import com.amlogic.cvdemo.dataProcess.SRDataProcessImpl;
import com.amlogic.cvdemo.dump.AMLDump;
import com.amlogic.cvdemo.utils.Constants;
import com.amlogic.cvdemo.utils.TFUtils;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.RuntimeFlavor;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.gpu.GpuDelegateFactory;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

public class SuperResolutionInterpreter {
    private static final String TAG = "SuperResolutionInterpreter";
    private static final int DELEGATE_CPU = 0;
    private static final int DELEGATE_GPU = 1;
    private static final int DELEGATE_NNAPI = 2;
    static String[] mResultArrays;
    private final Context mContext;
    private final ModelParams modelParams;
    private float threshold;
    private int numThreads;
    private int maxResults;
    private int currentDelegate;
    private int currentModel;
    private Interpreter mInterpreter = null;
    private ByteBuffer inputByteBuffer = null;
//    private ByteBuffer outputByteBuffer = null;
    int[] inputShape;
    DataType inputDataType;
    ModelData inputModelData;
    ModelData outputModelData;
    ModelKpiTime inferenceKpiTime;

    private TensorBuffer outputBuffer = null;
    private CVDetectListener mInterpreterCallback;
    private CVDataProcessControllerInterface SRImpl;
    private Bitmap resultBitmap = null;

    /**
     * Helper class for wrapping objection detection actions
     */
    public SuperResolutionInterpreter(Float threshold,
                                      int numThreads,
                                      int maxResults,
                                      int currentDelegate,
                                      int currentModel,
                                      Context context,
                                      ModelParams modelParams,
                                      CVDetectListener listener) {
        this.threshold = threshold;
        this.numThreads = numThreads;
        this.maxResults = maxResults;
        this.currentDelegate = currentDelegate;
        this.currentModel = currentModel;
        this.mContext = context;
        this.mInterpreterCallback = listener;
        this.inferenceKpiTime = new ModelKpiTime();
        this.modelParams = modelParams;
        initVariable(context);
        setupImageInterpreter(this.modelParams);
    }

    public static SuperResolutionInterpreter create(
            Context context,
            ModelParams modelName,
            CVDetectListener listener) {

        return new SuperResolutionInterpreter(
                0.5f,
                4,
                3,
                1,
                0,
                context,
                modelName,
                listener
        );
    }

    private  void initVariable(Context context) {
//        mResultArrays = context.getResources().getStringArray(R.array.str_pred_result);
        inputModelData = new ModelData();
        outputModelData = new ModelData();
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void setCurrentDelegate(int currentDelegate) {
        this.currentDelegate = currentDelegate;
    }

    public void setCurrentModel(int currentModel) {
        this.currentModel = currentModel;
    }

    private void setupImageInterpreter(ModelParams modelParams) {
        Interpreter.Options options = new Interpreter.Options();
        CompatibilityList compatList = new CompatibilityList();
        options.setNumThreads(numThreads);

        // use gpu to save time
        currentDelegate = modelParams.getDelegatePlatform();
        Log.d(TAG, "currentDelegate = " + currentDelegate);
        switch (currentDelegate) {
            case DELEGATE_CPU:
                // Default
                break;
            case DELEGATE_GPU:
//                compatList.getBestOptionsForThisDevice();
                if (compatList.isDelegateSupportedOnThisDevice()/* || true*/) {
//                    GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
                    if (true) {
//                        GpuDelegateFactory.Options delegateOptions = new GpuDelegateFactory.Options();
                        GpuDelegateFactory.Options delegateOptions = compatList.getBestOptionsForThisDevice();

                        // Set the precision configuration
                        delegateOptions.setPrecisionLossAllowed(true); // enable lower precision computation
                        delegateOptions.setInferencePreference(1); // set the inference priority to high
                        delegateOptions.setForceBackend(GpuDelegateFactory.Options.GpuBackend.OPENCL);

                        // Create a GpuDelegate with the options
                        GpuDelegateFactory factory = new GpuDelegateFactory(delegateOptions);
                        GpuDelegate gpuDelegate = (GpuDelegate) factory.create(RuntimeFlavor.APPLICATION);

//                      GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
                        options.addDelegate(gpuDelegate);

                    } else {
                        GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
                        GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
                        options.addDelegate(gpuDelegate);
                    }

                    Log.d(TAG, "add gpu delegate");
                } else {
                    options.setNumThreads(4);
                    // zhangyd todo, should toast users that GPU is not supported
                    Log.e(TAG, "GPU is not supported on "
                            + "this device");
                }
                break;
            case DELEGATE_NNAPI:
                NnApiDelegate nnApiDelegate = new NnApiDelegate();
                options.addDelegate(nnApiDelegate);
                break;
        }


        try {
            // todo,spinner selected model name
            MappedByteBuffer fileModel = TFUtils.loadModelFromStorage(mContext, modelParams.getModelFilePath());
            mInterpreter = new Interpreter(fileModel, options);

            int[] temp = mInterpreter.getInputTensor(0).shape();
            inputShape = new int[]{temp[1], temp[2], temp[3]};
            inputDataType = mInterpreter.getInputTensor(0).dataType();

            inputModelData.setShape(inputShape);
            inputModelData.setDataType(inputDataType);
            Log.d(TAG, "input data" + inputModelData);
//            inputBuffer = ByteBuffer.allocateDirect(inputShape[0] * inputShape[1] * inputShape[2] * inputShape[3] * inputDataType.byteSize())
//                    .order(ByteOrder.nativeOrder()).asIntBuffer();
            inputByteBuffer = ByteBuffer.allocateDirect(inputShape[0] * inputShape[1] * inputShape[2] * inputDataType.byteSize())
                    .order(ByteOrder.nativeOrder());

            // tf.squeeze. discard batch size
            temp =  mInterpreter.getOutputTensor(0).shape();
            int[] outputShape = new int[]{temp[1], temp[2], temp[3]};
            DataType outputDataType = mInterpreter.getOutputTensor(0).dataType();
            outputModelData.setDataType(outputDataType);
            outputModelData.setShape(outputShape);

            Log.d(TAG, "outputShape shape" + outputShape[0] + outputShape[1] + outputShape[2]);
            Log.d(TAG, "outputDataType type" + outputDataType);
            Log.d(TAG, "outputDataType msg" + outputModelData);
            Log.d(TAG, "output_dataLen" + mInterpreter.getOutputTensorCount());

            outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType);
//            outputByteBuffer = ByteBuffer.allocateDirect(outputShape[0] * outputShape[1] * outputShape[2] * outputDataType.byteSize())
//                    .order(ByteOrder.nativeOrder());
            Log.d(TAG, "inputbuffer size:" + inputByteBuffer.capacity());
            Log.d(TAG, "outputBuffer size:" + outputBuffer.getBuffer().capacity());
//            Log.d(TAG, "outputBuffer size 2:" + outputByteBuffer.capacity());
            SRImpl = new SRDataProcessImpl();
            SRImpl.init(inputModelData, outputModelData);
            mInterpreterCallback.onLoadSuccess(inputModelData, outputModelData);
        } catch (Exception e) {
            mInterpreterCallback.onError(0, Constants.ERROR_CODE_LOAD_ERROR);
            Log.e(TAG, "TFLite failed to load model with error: "
                    + e.getMessage());
        }
    }

    public void predict(Bitmap bitmap) {
        if (mInterpreter == null) {
            setupImageInterpreter(modelParams);
        }

        if (bitmap == null) {
            Log.d(TAG, "invalid image = ");
            return;
        }


        long inferenceTime = SystemClock.uptimeMillis();
        inputByteBuffer.clear();
        // keep it, for sync input/output with python
        if (SRImpl != null) {
            inputByteBuffer.put(SRImpl.preProcess(bitmap));
        }
/*        if (SemanticSegmentationHelper.DEBUG_MODEL) {


        } else {
//            if(SemanticSegmentationHelper.DEBUG_MODEL) {
//                Log.d(TAG, "input buf =  " + buffer.get(center_point * 4) + "  " + buffer.get(center_point * 4 + 1)
//                        + "  " + buffer.get(center_point * 4 + 2) + "  " + buffer.get(center_point * 4 + 3));
//            }

//            Native.byteArray2IntBuffer(buffer, inputBuffer);
//            Native.byteArray2FloatBuffer(buffer, inputBuffer);

            if(SemanticSegmentationHelper.DEBUG_MODEL) {
//                Log.d(TAG, "input buf final=  " + inputBuffer.get(center_point * 3) + "  " + inputBuffer.get(center_point * 3 + 1)
//                        + "  " + inputBuffer.get(center_point * 3 + 2));
            }
            inputByteBuffer.flip();
        }*/

        Log.d(TAG, "start to inference ");
        long inferenceTime1 = SystemClock.uptimeMillis();
        outputBuffer.getBuffer().clear();
//        outputByteBuffer.clear();
//        mInterpreter.run(inputByteBuffer, outputBuffer.getBuffer());
        mInterpreter.run(inputByteBuffer, outputBuffer.getBuffer());
        long inferenceTime2 = SystemClock.uptimeMillis();
        AMLDump.getInstance().dumpTFData(inputByteBuffer, inputModelData, 0);
        AMLDump.getInstance().dumpTFData(outputBuffer.getBuffer(), outputModelData, 1);
        Log.d(TAG, "inference finished " + outputBuffer.getBuffer().capacity());

        if (SRImpl != null) {
            resultBitmap = SRImpl.postProcess(outputBuffer.getBuffer());
            Log.d(TAG, "start to inference " + resultBitmap);
        }
        long inferenceTime3 = SystemClock.uptimeMillis();
//        Log.d(TAG, "avg = " + imgResult[0] + "stddev = " + imgResult[1]);
        long inferenceTime4 = SystemClock.uptimeMillis();
        long[] inference_time = {
                (inferenceTime1 - inferenceTime),
                (inferenceTime2 - inferenceTime1),
                (inferenceTime3 - inferenceTime2),
                (inferenceTime4 - inferenceTime3)};
        Log.d(TAG, "preprocess camera image cost time = " + inference_time[0]);
        Log.d(TAG, "model inference cost time = " + inference_time[1]);
        Log.d(TAG, "postprocess camera image cost time = " + inference_time[2]);
//        Log.d(TAG, "get image avg&std cost time = " + inference_time[3]);
        inferenceKpiTime.setPreProcessTime((int)inference_time[0]);
        inferenceKpiTime.setInferenceTime((int)inference_time[1]);
        inferenceKpiTime.setPostInferenceTime((int)inference_time[2]);
        inferenceKpiTime.setReservedTime((int)inference_time[3], 0);
        // notify real_time msg to fragment and display it
        mInterpreterCallback.onResult(0, resultBitmap, inferenceKpiTime);

        Log.d(TAG, "inference finshed = ");
    }

    public void clearInterpreter() {
        if (null != mInterpreter) {
            mInterpreter.close();
            mInterpreter = null;
        }

        if (SRImpl != null) {
            SRImpl.destroy();
        }
    }
}
