package com.amlogic.cvdemo.dataProcess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.utils.BitmapUtils;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.nio.ByteBuffer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
//import org.nd4j.linalg.api.ndarray.INDArray;

public class SemanticDataProcessImpl implements CVDataProcessControllerInterface {
    private static final String TAG = "SemanticDataProcessImpl";
    private ByteBuffer inputBuffer;
    private int outputWidth = 0;
    private int outputHeight = 0;
    private int inputWidth = 0;
    private int inputHeight = 0;
    byte[] byteArray = null;
    TensorImage inputTensorImage;
    private int colorClassNum = 21;
    ImageProcessor processor;
    // 定义每个类别对应的 RGB 颜色
    private final int[] COLORS = {
            Color.BLACK,        // 背景
            Color.argb(255, 128, 0, 0),     // 类别 1
            Color.argb(255, 0, 128, 0),     // 类别 2
            Color.argb(255, 128, 128, 0),   // 类别 3
            Color.argb(255, 0, 0, 128),     // 类别 4
            Color.argb(255, 128, 0, 128),   // 类别 5
            Color.argb(255, 0, 128, 128),   // 类别 6
            Color.argb(255, 192, 192, 192), // 类别 7
            Color.argb(255, 128, 128, 128), // 类别 8
            Color.argb(255, 255, 0, 0),     // 类别 9
            Color.argb(255, 0, 255, 0),     // 类别 10
            Color.argb(255, 255, 255, 0),   // 类别 11
            Color.argb(255, 0, 0, 255),     // 类别 12
            Color.argb(255, 255, 0, 255),   // 类别 13
            Color.argb(255, 0, 255, 255),   // 类别 14
            Color.argb(255, 255, 255, 255), // 类别 15
            Color.argb(255, 128, 128, 128), // 类别 16
            Color.argb(255, 255, 128, 0),   // 类别 17
            Color.argb(255, 0, 255, 128),   // 类别 18
            Color.argb(255, 128, 255, 0),   // 类别 19
            Color.argb(255, 0, 128, 255),   // 类别 20
            Color.argb(255, 255, 0, 128)    // 类别 21
    };
    private Bitmap outBitmap = null;


    @Override
    public boolean init(ModelData in, ModelData out) {
        if (in.getShape().length != 3 || out.getShape().length != 3) {
            Log.e(TAG, "invalid image modelData");
            return false;
        }
        int[] outputSize = out.getShape();
        outputWidth = outputSize[1];
        outputHeight = outputSize[0];
        if (outputWidth * outputHeight <= 0) {
            Log.e(TAG, "invalid output size" + outputWidth);
            return false;
        }

        int[] inputSize = in.getShape();
        if (inputSize[0] * inputSize[1] <= 0) {
            Log.e(TAG, "invalid input size" + outputWidth);
            return false;
        }
        // tensor format NHWC
        inputBuffer = ByteBuffer.allocateDirect(inputSize[0] * inputSize[1] * inputSize[2] * in.getDataType().byteSize());
        inputWidth = inputSize[1];
        inputHeight = inputSize[0];
        byteArray = new byte[outputSize[0] * outputSize[1] * outputSize[2] * out.getDataType().byteSize()];
        colorClassNum = outputSize[2];
        Log.i(TAG, "output cls num = " + colorClassNum);
        inputTensorImage = new TensorImage(in.getDataType());
        Log.i(TAG, "output outputWidth = " + outputWidth + "outputHeight =" + outputWidth);
        outBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        // 创建画布
        Canvas canvas = new Canvas(outBitmap);

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // 设置颜色为黑色

        // 填充整个位图为黑色
        canvas.drawRect(0, 0, outputWidth, outputHeight, paint);
        processor = new ImageProcessor.Builder()
                .add(new ResizeOp(inputWidth, inputHeight, ResizeOp.ResizeMethod.BILINEAR))
                .build();
        return true;
    }

    @Override
    public ByteBuffer preProcess(Bitmap bitmap) {
        Bitmap bmp = BitmapUtils.generateBitmapWithSize(bitmap, inputWidth, inputHeight);
        inputTensorImage.load(bmp);

        Log.d(TAG, "preprocess inputWidth:" + inputWidth + "  inputHeight:" + inputHeight);
//        processor.process(inputTensorImage);
        return inputTensorImage.getBuffer();
    }

    @Override
    public Bitmap postProcess(ByteBuffer outputBuffer) {
        // 创建一个字节数组
        outputBuffer.flip();
        outputBuffer.get(byteArray); // 将 ByteBuffer 的内容读取到字节数组中

        Log.e(TAG,  "output size" + outputBuffer.capacity());
        int maxProb = 0;
        int classId = 0;
        int pixelColor = 0;
        int certValue = 0;
        int i = 0;
        int j = 0;
        // 填充 Bitmap
        for (i = 0; i < outputHeight; i++) {
            for (j = 0; j < outputWidth; j++) {

                // 计算在 ByteBuffer 中的索引，获取类别信息
                // 获取每个像素的类别概率
                classId = -1;
                maxProb = 0;
                for (int c = 0; c < colorClassNum; c++) {
                    certValue = byteArray[(i * outputWidth + j) * colorClassNum + c]; // 获取类别 c 在 (y, x) 的概率
                    certValue = certValue > 0 ? certValue : certValue & 0x00ff;
                    if (certValue > maxProb) {
                        maxProb = certValue;
                        classId = c; // 更新最大概率对应的类别ID
                    }
                }
                classId = Math.max(classId, 0);
                // 选择对应颜色
                pixelColor = COLORS[classId]; // 处理超出范围的情况
//                Log.d(TAG, "ROW = " + i+ "col=" + j + "color =" + pixelColor);
                // 设置像素颜色
                outBitmap.setPixel(j, i, pixelColor);
            }
        }

        return outBitmap;
    }

}
