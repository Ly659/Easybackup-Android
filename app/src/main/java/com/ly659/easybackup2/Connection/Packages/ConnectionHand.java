package com.ly659.easybackup2.Connection.Packages;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 本封装用于存储手机的基本信息（型号等）。
 * App端连接PC端时将此封装传输给PC端，作为握手信息。
 *
 */
public final class ConnectionHand implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final String phoneName;
    private final String phoneIp;
    private final String appPort;
    private final List<TypeInfo> fileTypes;

    /**
     * @param phoneName 手机名
     * @param phoneIp   手机IP地址
     * @param appPort   App端口号
     * @param fileTypes App端将要上传的文件类型及其总数（封装类）
     */
    public ConnectionHand(String phoneName, String phoneIp, String appPort,
                          List<TypeInfo> fileTypes) {
        this.phoneName = phoneName;
        this.phoneIp = phoneIp;
        this.appPort = appPort;
        this.fileTypes = fileTypes;
    }

    public String phoneName() {
        return phoneName;
    }

    public String phoneIp() {
        return phoneIp;
    }

    public String appPort() {
        return appPort;
    }

    public List<TypeInfo> fileTypes() {
        return fileTypes;
    }
}
