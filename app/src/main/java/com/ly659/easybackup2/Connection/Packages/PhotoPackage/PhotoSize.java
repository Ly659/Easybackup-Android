package com.ly659.easybackup2.Connection.Packages.PhotoPackage;

import java.io.Serial;
import java.io.Serializable;

/**
 * 此类用于封装图片尺寸。此对象一经创建，其图片尺寸数据就无法再修改。
 */
public final class PhotoSize implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final int width;
    private final int height;

    /**
     * 初始化封装类。
     * @param width  图片宽度（像素）
     * @param height 图片高度（像素）
     */
    public PhotoSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
