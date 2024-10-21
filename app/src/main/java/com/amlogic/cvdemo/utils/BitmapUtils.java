package com.amlogic.cvdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class BitmapUtils {
    private static final int BITMAP_FIXED_MIN_SIZE = 800;
    public static Bitmap generateBitmapWithSize(Bitmap originalBitmap, int newWidth, int newHeight) {

 /*       // 创建一个新的Bitmap对象
        Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, originalBitmap.getConfig());

        // 使用Canvas绘制Bitmap
        Canvas canvas = new Canvas(resizedBitmap);
        Paint paint = new Paint();

        // 设置Bitmap的适当缩放
        float scaleWidth = ((float) newWidth) / originalBitmap.getWidth();
        float scaleHeight = ((float) newHeight) / originalBitmap.getHeight();

        // 选择较小的比例使得Bitmap保持在新Size内，防止拉伸
        float scale = Math.min(scaleWidth, scaleHeight);

        // 计算中心位置
        int scaledWidth = (int) (originalBitmap.getWidth() * scale);
        int scaledHeight = (int) (originalBitmap.getHeight() * scale);
        int offsetX = (newWidth - scaledWidth) / 2;
        int offsetY = (newHeight - scaledHeight) / 2;

        // 使用Paint来绘制Bitmap
        canvas.drawBitmap(originalBitmap, offsetX, offsetY, paint);

        return resizedBitmap;*/
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }

    public static Bitmap adjustBitmapSize(Bitmap originalBitmap) {
        // 获取原始Bitmap的宽高
        float width = originalBitmap.getWidth();
        float height = originalBitmap.getHeight();

        // 计算需要的缩放比例
        float scaleFactor = 1.0f;

        // 如果宽度或高度小于600px，计算放大比例
        if (width < BITMAP_FIXED_MIN_SIZE || height < BITMAP_FIXED_MIN_SIZE) {
            scaleFactor = Math.min(BITMAP_FIXED_MIN_SIZE / width, BITMAP_FIXED_MIN_SIZE / height);
        }

        // 生成缩放后的Bitmap
        int newWidth = Math.round(width * scaleFactor);
        int newHeight = Math.round(height * scaleFactor);
        Log.d("BitmapUtils", "ori width:" + width + "  newWidth:" + newWidth + "scaleFactor =" + scaleFactor);
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }
}
