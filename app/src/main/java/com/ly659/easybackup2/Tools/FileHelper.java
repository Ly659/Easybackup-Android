package com.ly659.easybackup2.Tools;

import android.widget.Toast;

import com.ly659.easybackup2.MainActivity;

import java.io.File;

import java.util.Objects;


public class FileHelper {

    /**
     * 获取当前手机的设备名。
     * @return 当前手机的设备名
     */
    public static String getName_Phone() {
        return "Xiaomi 5";
    }

    /**
     * 获取图片文件的总数。
     * @return 图片文件的总数。若不存在照片文件夹，则返回-1。
     */
    public static int getNum_Photos() {
        File DCIM_Path = new File("/storage/emulated/0/DCIM/Camera");       // 注意！！！路径区分大小写
        if (!DCIM_Path.isDirectory()) return -1;

        return Objects.requireNonNull(DCIM_Path.listFiles((dir, name) -> {
            if (name.endsWith(".jpg")) return true;
            return false;
        })).length;
    }

    /**
     * 获取所有图片文件的列表。
     * @return 所有图片文件的列表
     */
    public static File[] getFile_Photos() {
        return Objects.requireNonNull(new File("/storage/emulated/0/DCIM/Camera").listFiles((dir, name) -> {
            if (name.endsWith(".jpg")) return true;
            return false;
        }));
    }
}
