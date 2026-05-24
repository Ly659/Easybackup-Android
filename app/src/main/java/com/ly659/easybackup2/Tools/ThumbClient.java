/**
*用于图片压缩的类，用于创建缩略图。
 */
package com.ly659.easybackup2.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ThumbClient {
    private final int quality;
    private final int width;
    private final int height;
    private final File tempDir;

    /**
     * 初始化本对象。一个对象可以用于压缩一批jpg图片到一定的质量。
     * @param quality 指定压缩的质量百分比（0~100）
     * @param width 输出图片的分辨率（水平）
     * @param height 输出图片的分辨率（垂直）
     * @param tempDir 自定义存储临时文件的目录
     */
    public ThumbClient(int quality, int width, int height, File tempDir) {
        this.quality = quality;
        this.width = width;
        this.height = height;
        this.tempDir = tempDir;
    }

    /**
     * 执行图片压缩操作。此方法只压缩照片质量（清晰度），不压缩照片分辨率。返回压缩后的图片。
     * @param imageFile 原图片文件的File对象。不能为null。
     * @return 处理完成后图片的字节数据。
     */
    public byte[] compress_quality(@NonNull File imageFile) {
        String path = imageFile.getAbsolutePath();  // 获取绝对路径
        // byte[] fileData = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();       // 输出流，用于输出压缩后的图片

        // bitmap = BitmapFactory.decodeResource()  从res/drawable资源文件夹加载bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);    // 创建bitmap对象，将图片读取到内存中
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);     // 压缩图片，将结果输出到输出流的缓冲区

        return outputStream.toByteArray();          // 返回输出流的缓冲区内容
    }

    /**
     * 执行图片压缩操作。此方法只改变图片分辨率，不改变图片质量（清晰度）。
     * @param imageFile 源文件的File对象。不能为null。
     * @return 处理完后的文件的字节数据
     */
    public byte[] compress_resolution(@NonNull File imageFile) {
        String path = imageFile.getAbsolutePath();
        Matrix matrix = new Matrix();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Bitmap对象
        Bitmap bitmap = BitmapFactory.decodeFile(path);     // 读取到内存中
        // 设置缩放分辨率
        final int src_width = bitmap.getWidth();    // 源文件的宽像素数
        final int src_height = bitmap.getHeight();
        final float sx = this.width / (float) src_width;   // 计算scale大小
        final float sy = this.height / (float) src_height;
        matrix.setScale(sx, sy);

        Bitmap new_bitmap = Bitmap.createBitmap(bitmap, 0, 0, src_width, src_height, matrix, false);
        new_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return outputStream.toByteArray();
    }

    /*
    public byte[] compress_resolution(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 设置缩放分辨率
        final int src_width = bitmap.getWidth();    // 源文件的宽像素数
        final int src_height = bitmap.getHeight();
        final float sx = this.width / (float) src_width;   // 计算scale大小
        final float sy = this.height / (float) src_height;
        matrix.setScale(sx, sy);

        Bitmap new_bitmap = Bitmap.createBitmap(bitmap, 0, 0, src_width, src_height, matrix, false);
        new_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return outputStream.toByteArray();
    }

     */

    /**
     * 执行压缩图片操作。先压缩分辨率，再压缩画质。
     * @param imageFile 源文件的File对象。
     * @return 处理后图片的字节数据。
     * @throws IOException 文件输出流创建失败或临时文件写入失败时，会抛出此异常。
     */
    public byte[] compress(File imageFile) throws IOException {
        // 初始化临时文件和变量
        File tempFile = new File(this.tempDir, imageFile.getName() + ".tmp");
        // 若临时文件有重名，则重命名直到不存在为止
        if (tempFile.exists()) {
            for (int i = 1; i < 99999; i++) {
                tempFile = new File(this.tempDir, imageFile.getName() + "_" + i + ".tmp");
                if (!tempFile.exists()) {break;}
            }
        }
        byte[] temp;

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            // 1、压缩分辨率
            temp = compress_resolution(imageFile);
            fos.write(temp);
            fos.flush();
            // 2、压缩画质
            temp = compress_quality(tempFile);
        } catch (IOException e) {
            throw new IOException(e);
        }
        return temp;        // 最终的处理结果
    }

}
