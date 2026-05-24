package com.ly659.easybackup2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ly659.easybackup2.Connection.ConnectPC;
import com.ly659.easybackup2.Connection.Packages.Command.SendPackageEnded;
import com.ly659.easybackup2.Connection.Packages.ConnectionHand;
import com.ly659.easybackup2.Connection.Packages.PhotoPackage.PhotoPackage;
import com.ly659.easybackup2.Connection.Packages.PhotoPackage.PhotoSize;
import com.ly659.easybackup2.Connection.Packages.PhotoPackage.PhotoTypes;
import com.ly659.easybackup2.Connection.Packages.TypeInfo;
import com.ly659.easybackup2.Connection.Packages.Types;
import com.ly659.easybackup2.Tools.FileHelper;
import com.ly659.easybackup2.Tools.ThumbClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用于集中管理申请的权限，包含所有可以请求的封装好的权限。
 * 可以直接传入给askPermission方法，向用户请求权限。
 */
class Permissions {
    /**
     * 权限的封装类，包括权限常量名和请求代码。
     */
    static class Permission {
        public final String permissionName;
        public final int requestCode;

        public Permission(String permissionName, int requestCode) {
            this.permissionName = permissionName;
            this.requestCode = requestCode;
        }
    }

    // 所有可能申请的的权限类型
    public static final Permission PERMISSION_NETWORK =
            new Permission(Manifest.permission.INTERNET, 100);              // 网络权限
    public static final Permission PERMISSION_STORAGE_READ =
            new Permission(Manifest.permission.READ_EXTERNAL_STORAGE, 101); // 文件读取权限
}

public class MainActivity extends AppCompatActivity {

    /**
     * 向用户请求指定的权限。
     * @param permission 指定权限
     * @return 权限是否成功获取。若成功获取（用户授权），返回true；否则返回false。
     * 注：若权限已经拥有，无需再请求，返回true。
     */
    private boolean askPermission(Permissions.Permission permission) {
        // 检查权限是否已经拥有
        if (ActivityCompat.checkSelfPermission(this, permission.permissionName) == PackageManager.PERMISSION_GRANTED) return true;
        // 向用户请求权限
        ActivityCompat.requestPermissions(this, new String[] {permission.permissionName}, permission.requestCode);
        // 再次检查权限是否获得，若成功获得则返回true，否则返回false
        return ActivityCompat.checkSelfPermission(this, permission.permissionName) == PackageManager.PERMISSION_GRANTED;
    }

    private void startConnect(@NonNull SetConnectPackage returnPack) {
        // 封装用户选择的文件类型选项
        List<TypeInfo> userTypes = new ArrayList<>();
        //if (returnPack.fileType_videos) userTypes.add(new TypeInfo(Types.Video, 999));
        if (returnPack.fileType_photos) userTypes.add(new TypeInfo(Types.Photo, FileHelper.getNum_Photos()));

        // 获取并启动网络连接线程
        Thread thread_Network = getThread_network(returnPack, userTypes);
        thread_Network.setName("Thread_NETWORK");
        thread_Network.start();
    }

    @NonNull
    private Thread getThread_network(@NonNull SetConnectPackage returnPack, List<TypeInfo> userTypes) {
        ConnectionHand handPack = new ConnectionHand(FileHelper.getName_Phone(), returnPack.hostIp, returnPack.hostPort, userTypes);

        // 建立与PC端的连接
        return new Thread(() -> {
            try (ConnectPC pc = new ConnectPC(returnPack.hostIp, returnPack.hostPort)) {
                // 尝试建立连接
                pc.tryConnect();
                // 发送握手信息
                pc.sendConnectionHand(handPack);

                // 发送缩略图
                ThumbClient thumbClient = new ThumbClient(30, 640, 480, getCacheDir());     // 缩略图处理器
                for (File photoFile: FileHelper.getFile_Photos()) {
                    pc.sendPackage(new PhotoPackage(thumbClient.compress(photoFile), photoFile.getName(), 999, PhotoTypes.JPG, new PhotoSize(999, 999)));
                }
                pc.sendPackage(new SendPackageEnded());     // 发送结束的标志

            } catch (IOException e) {
                Toast.makeText(this, "IO错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 创建Activity启动器，用于启动SetConnectActivity
        final ActivityResultLauncher<Intent> luncher_SetConnect = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                // Activity结束并返回数据时会执行此lambda
                o -> {
                    if (o.getResultCode() == RESULT_OK) {
                        // 获取Activity的返回值数据
                        Intent returnData = o.getData();
                        // 取出序列化对象
                        assert returnData != null;
                        SetConnectPackage setConnectPackage = (SetConnectPackage) returnData.getSerializableExtra("SetConnect_UserChoice");
                        assert setConnectPackage != null;
                        startConnect(setConnectPackage);
                    }
                }
        );

        // 绑定UI控件
        Button button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener((v -> {
            // 点击按钮时，启动SetConnectActivity
            Intent intent = new Intent(this, SetConnectActivity.class);
            luncher_SetConnect.launch(intent);
        }));
    }
}