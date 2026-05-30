package com.ly659.easybackup2.Connection.Packages.PhotoPackage;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用于封装图片数据的Package。此类可以被序列化，通过Socket传输。
 * 此封装包含的信息有：
 * 文件数据
 * 文件路径
 * 照片尺寸
 *
 */
public final class PhotoPackage implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final byte[] photoData;
    private final String filePath;
    private final int fileSize;
    private final PhotoTypes photoType;
    private final PhotoSize photoSize;

    //……//

    /**
     * 初始化封装，需要指定图片的数据和文件名
     * 此构造用于存储和传输缩略图。
     *
     * @param photoData 图片数据
     * @param filePath  文件名
     * @param fileSize  文件大小（单位：MB）
     * @param photoType 文件格式（常量）
     * @param photoSize 原图的尺寸信息
     */
    public PhotoPackage(byte[] photoData, String filePath, int fileSize, PhotoTypes photoType, PhotoSize photoSize) {
        this.photoData = photoData;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.photoType = photoType;
        this.photoSize = photoSize;
    }

    /**
     * 获取图片的文件路径。
     *
     * @return 文件路径
     */
    public String filePath() {
        return filePath;
    }

    /**
     * 获取文件名（包含后缀名）。
     *
     * @return 文件名
     */
    @Override
    public String toString() {
        // 路径的判断（不同系统的分隔符可能不同）
        if (filePath.lastIndexOf("/") != -1) {
            return filePath.substring(filePath.lastIndexOf("/"));
        } else if (filePath.lastIndexOf("\\") != -1) {
            return filePath.substring(filePath.lastIndexOf("\\"));
        } else  {
            return filePath;
        }
    }

    /**
     * 获取图片的数据。
     *
     * @return 图片数据
     */
    public byte[] photoData() {
        return photoData;
    }

    /**
     * 获取图片的尺寸信息。
     *
     * @return 储存信息的封装类（包含宽Width和高Height）
     */
    public PhotoSize photoSize() {
        return photoSize;
    }

    /**
     * 获取文件的格式类型。
     *
     * @return 图片的文件格式（一个常量）
     */
    public PhotoTypes photoType() {
        return photoType;
    }

    /**
     * 获取文件的大小信息。
     *
     * @return 文件大小（单位：MB）
     */
    public int fileSize() {
        return fileSize;
    }
}
