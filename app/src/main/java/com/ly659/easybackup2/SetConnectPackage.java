package com.ly659.easybackup2;

import java.io.Serializable;

/**
 * 本类用于存储和传输SetConnectActivity的返回内容，包含用户输入的数据。
 */
public class SetConnectPackage implements Serializable {
    // 主机IP地址的四个字段，分开储存
    public final String hostIp;
    // 主机端口号
    public final String hostPort;

    public SetConnectPackage(String hostIp, String hostPort) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
    }
}
