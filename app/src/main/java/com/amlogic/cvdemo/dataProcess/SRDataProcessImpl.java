package com.amlogic.cvdemo.dataProcess;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.utils.BitmapUtils;

import org.opencv.android.Utils;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.nio.ByteBuffer;
//import org.nd4j.linalg.api.ndarray.INDArray;

public class SRDataProcessImpl implements CVDataProcessControllerInterface {
    private static final String TAG = "SRDataProcessImpl";
    private ByteBuffer inputBuffer;
    private int outputWidth = 0;
    private int outputHeight = 0;
    private int inputWidth = 0;
    private int inputHeight = 0;
    byte[] byteArray = null;
    TensorImage inputTensorImage;
    private int colorClassNum = 0;
    private Bitmap outBitmap = null;
    ImageProcessor processor;
    int[] destPixels = null;

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
        processor = new ImageProcessor.Builder()
                .add(new ResizeOp(inputWidth, inputHeight, ResizeOp.ResizeMethod.BILINEAR))
                .build();
        outBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        destPixels = new int[outputWidth * outputHeight];
        // 创建画布
        Canvas canvas = new Canvas(outBitmap);

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // 设置颜色为黑色

        // 填充整个位图为黑色
        canvas.drawRect(0, 0, outputWidth, outputHeight, paint);
        return true;
    }

    @Override
    public ByteBuffer preProcess(Bitmap bitmap) {
//        Bitmap bmp = BitmapUtils.generateBitmapWithSize(bitmap, inputWidth, inputHeight);
        Log.d(TAG, "preprocess inputWidth:" + inputWidth + "  inputHeight:" + inputHeight);
        inputTensorImage.load(bitmap);
//        processor.process(TensorImage.fromBitmap(bitmap));
        inputTensorImage = processor.process(inputTensorImage);
        return inputTensorImage.getBuffer();
    }

    @Override
    public Bitmap postProcess(ByteBuffer outputBuffer) {
        // 创建一个字节数组
        outputBuffer.flip();
        outputBuffer.get(byteArray); // 将 ByteBuffer 的内容读取到字节数组中

        Log.e(TAG, "output size" + outputBuffer.capacity());
        int startPos = 0;
        int pixelColor = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int i = 0;
        int j =0;
        int index = 0;
        // 填充 Bitmap
        for (i = 0; i < outputHeight; i++) {
            for (j = 0; j < outputWidth; j++) {
                startPos = (i * outputWidth + j) * colorClassNum;

                // 选择对应颜色
/*                pixelColor = 0xFF000000
                            |  (((int)byteArray[startPos]) << 16)
                            |  (((int)byteArray[startPos + 1]) << 8)
                            |  (((int)byteArray[startPos + 2]));*/
//                Log.d(TAG, "ROW = " + i+ "col=" + j + "color =" + pixelColor);
                // 设置像素颜色
                red = byteArray[startPos] & 0xff;
                green = byteArray[startPos + 1] & 0xff;
                blue = byteArray[startPos + 2] & 0xff;
//                pixelColor = Color.argb(0xff, red, green, blue);
//                outBitmap.setPixel(j, i, pixelColor);
                destPixels[index++] = Color.argb(0xff, red, green, blue);
            }
        }
        outBitmap.setPixels(destPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight);

        return outBitmap;
    }

    @Override
    public void destroy() {
        if (byteArray != null) {
            byteArray = null;
        }

        if (destPixels != null) {
            destPixels = null;
        }

        if (outBitmap != null) {
            outBitmap.recycle();
            outBitmap = null;
        }

        if (processor != null) {
            processor = null;
        }

        if (inputTensorImage != null) {
            inputBuffer = null;
        }
    }

}
