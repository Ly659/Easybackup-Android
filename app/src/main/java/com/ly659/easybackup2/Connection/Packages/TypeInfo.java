package com.ly659.easybackup2.Connection.Packages;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 存储每种文件的类型和总数。
 */
public final class TypeInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final Types fileType;
    private final int fileNumber;

    /**
     * @param fileType   文件类型（常量）
     * @param fileNumber 该类型的文件总数
     */
    public TypeInfo(Types fileType, int fileNumber) {
        this.fileType = fileType;
        this.fileNumber = fileNumber;
    }

    public Types fileType() {
        return fileType;
    }

    public int fileNumber() {
        return fileNumber;
    }
}
