package com.ly659.easybackup2.Connection.Packages.Command;


import java.io.Serial;
import java.io.Serializable;

/**
 * 此包用于表示发送端的所有数据都已经发送完毕，接收端可以执行清理操作。
 */
public class SendPackageEnded implements Command, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
