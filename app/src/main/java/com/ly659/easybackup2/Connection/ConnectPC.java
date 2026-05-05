package com.ly659.easybackup2.Connection;

import com.ly659.easybackup2.Connection.Packages.ConnectionHand;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * 此类是EasyBackup App的核心功能类，负责管理App与PC端软件的连接和数据的传输操作。<br><br>
 * 要使用该类，您需要先创建该类的实例。创建实例时，您需要传入PC端的Ip地址和PC端软件的端口号。<br><br>
 * 以下是该类实例中常用的方法：<br>
 * void tryConnect() - 尝试建立连接；<br>
 * void sendConnectionHand(ConnectHand hand) - 发送握手封装对象；<br>
 * void sendPackage(Serializable package) - 发送文件封装对象。<br>
 * void close() - 关闭连接。
 *
 * @author 远大的理想
 */
public class ConnectPC implements Closeable {
    private Socket socket;
    private OutputStream outputStream;
    private ObjectOutputStream objectSender;

    public final String hostIp;     // PC端Ip地址
    public final String hostPort;   // PC端端口号


    public ConnectPC(String hostIp, String hostPort) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
    }

    /**
     * 尝试建立与PC端的网络连接。
     * @throws IOException 创建Socket或输出流时发生IO错误。
     */
    public void tryConnect() throws IOException {
        // 创建socket
        socket = new Socket(hostIp, Integer.parseInt(hostPort));
        outputStream = socket.getOutputStream();
    }

    /**
     * 向PC端发送用于握手的封装对象。<br>
     * 注意：这个方法需在成功建立连接后调用！
     * @param hand 握手封装对象
     * @throws IOException 发送时发生IO异常。
     */
    public void sendConnectionHand(ConnectionHand hand) throws IOException {
        // 将握手包发送给PC端
        objectSender = new ObjectOutputStream(outputStream);
        objectSender.writeObject(hand);
        objectSender.flush();
    }

    /**
     * 向PC端发送文件封装对象。<br>
     * 注意：此方法需在握手成功后调用！
     * @param pack 要发送的封装对象。
     * @throws IOException 发送时发生IO异常。
     */
    public void sendPackage(Serializable pack) throws IOException {
        objectSender.writeObject(pack);
        objectSender.flush();
    }

    /**
     * 关闭与PC端的连接。
     * @throws IOException 关闭时发生IO异常。
     */
    @Override
    public void close() throws IOException {
        objectSender.close();
        outputStream.close();
        socket.close();
    }
}
