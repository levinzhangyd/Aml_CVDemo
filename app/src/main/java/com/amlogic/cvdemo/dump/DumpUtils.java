package com.amlogic.cvdemo.dump;

import android.os.Build;

import com.amlogic.cvdemo.data.ModelData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DumpUtils {
    public static String generateTFModelDataName(ModelData modelData, int type) {
        StringBuilder builder = new StringBuilder();
        // 获取当前时间的 Instant
        Instant instant = Instant.now();

        // 转换为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        // 格式化日期
        builder.append(localDateTime.format(formatter));
        if (type > 0) {
            builder.append("-output-");
        } else {
            builder.append("-input-");

        }
        int[] shapes = modelData.getShape();
        for (int i = 0; i < shapes.length; i++)
            builder.append(shapes[i]).append("-");

        builder.append("ori_data.txt");
        return builder.toString();
    }

    public static String generateBitmapName(ModelData modelData, int type) {
        StringBuilder builder = new StringBuilder();
        // 获取当前时间的 Instant
        Instant instant = Instant.now();

        // 转换为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        // 格式化日期
        builder.append(localDateTime.format(formatter));
        if (type >= 0) {
            builder.append("-output-");
        } else {
            builder.append("-input-");

        }
        int[] shapes = modelData.getShape();
        for (int i = 0; i < shapes.length; i++)
            builder.append(shapes[i]).append("-");

        builder.append(".jpg");
        return builder.toString();
    }

}
