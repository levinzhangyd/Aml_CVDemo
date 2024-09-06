package com.amlogic.cvdemo.dataProcess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.utils.TFUtils;

import org.tensorflow.lite.support.image.TensorImage;

import java.nio.ByteBuffer;

import android.util.Log;

public class SemanticDataProcessImpl implements CVDataProcessControllerInterface {
    private static final String TAG = "SemanticDataProcessImpl";
    private  Bitmap bitmap;
    private  ByteBuffer inputBuffer;
    private  int outputWidth = 0;
    private  int outputHeight = 0;
    byte[] byteArray = null;
    // 定义每个类别对应的 RGB 颜色
    private  final int[][] COLORS = {
            {0, 0, 0},        // 背景
            {128, 0, 0},     // 类别 1
            {0, 128, 0},     // 类别 2
            {128, 128, 0},   // 类别 3
            {0, 0, 128},     // 类别 4
            {128, 0, 128},   // 类别 5
            {0, 128, 128},   // 类别 6
            {192, 192, 192}, // 类别 7
            {128, 128, 128}, // 类别 8
            {255, 0, 0},     // 类别 9
            {0, 255, 0},     // 类别 10
            {255, 255, 0},   // 类别 11
            {0, 0, 255},     // 类别 12
            {255, 0, 255},   // 类别 13
            {0, 255, 255},   // 类别 14
            {255, 255, 255}, // 类别 15
            {127, 127, 127}, // 类别 16
            {255, 127, 0},   // 类别 17
            {0, 255, 127},   // 类别 18
            {127, 255, 0},   // 类别 19
            {0, 127, 255},   // 类别 20
            {255, 0, 127}    // 类别 21
    };


    @Override
    public boolean init(ModelData in, ModelData out) {
        if (in.getShape().length != 3 || out.getShape().length != 3) {
            Log.e(TAG,  "invalid image modelData");
            return false;
        }
        int[] outputSize = out.getShape();
        outputWidth = outputSize[1];
        outputHeight =  outputSize[0];
        if (outputWidth * outputHeight <= 0) {
            Log.e(TAG,  "invalid output size" + outputWidth);
            return false;
        }
        bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);

        int[] inputSize = in.getShape();
        if (inputSize[0] * inputSize[1] <= 0) {
            Log.e(TAG,  "invalid input size" + outputWidth);
            return false;
        }
        inputBuffer = ByteBuffer.allocateDirect(inputSize[0] * inputSize[1] * inputSize[2] * in.getDataType().byteSize());
        byteArray = new byte[outputSize[0] * outputSize[1] * outputSize[2] * out.getDataType().byteSize()];
        return true;
    }

    @Override
    public ByteBuffer preProcess(String path) {
//        Bitmap image_new = TFUtils.loadImageFromAssets(mContext, "semantic_segmentation_voc.jpg");
        return null;
    }

    @Override
    public Bitmap postProcess(ByteBuffer outputBuffer) {
        // 创建一个字节数组
        outputBuffer.get(byteArray); // 将 ByteBuffer 的内容读取到字节数组中

        // 创建 Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // 如果 Bitmap 的宽高与预期不符，可以进行缩放
//        if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
//            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
//        }

        return bitmap;
    }

}
