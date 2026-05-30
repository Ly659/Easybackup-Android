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
    /**
     * 权限数组索引：
     * [0] 网络权限
     * [1] 文件读取权限
     */
    static final Permission[] permissions = new Permission[] {
        new Permission(Manifest.permission.INTERNET, 100),              // 网络权限
        new Permission(Manifest.permission.READ_EXTERNAL_STORAGE, 101)  // 文件读取权限
    };
}

public class MainActivity extends AppCompatActivity {

    /**
     * 检查指定的权限是否已经获得。
     * @param permission 指定权限
     * @return 若已获得，则返回true；否则返回false。
     */
    private boolean checkPermission(@NonNull Permissions.Permission permission) {
        return ActivityCompat.checkSelfPermission(this, permission.permissionName) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 向用户请求指定的权限。
     * 注：如果指定的权限已经取得，执行此方法不会有任何效果。要检查权限是否获得，请参照Permission变量。
     * @param permission 指定权限
     */
    private void askPermission(@NonNull Permissions.Permission permission) {
        // 检查权限是否已经拥有
        if (ActivityCompat.checkSelfPermission(this, permission.permissionName) == PackageManager.PERMISSION_GRANTED) return;
        // 向用户请求权限
        ActivityCompat.requestPermissions(this, new String[] {permission.permissionName}, permission.requestCode);
    }

    /**
     * 开始与PC端的网络连接和数据传输操作。
     * @param returnPack 用户自定义的连接参数
     */
    private void startConnect(@NonNull SetConnectPackage returnPack) {
        // 获取必要的权限（若获取失败则终止操作）
        if (checkPermission(Permissions.permissions[0]) && checkPermission(Permissions.permissions[1])) {
            // 封装用户选择的文件类型选项
            List<TypeInfo> userTypes = new ArrayList<>();
            if (returnPack.fileType_videos) userTypes.add(new TypeInfo(Types.Video, 999));
            if (returnPack.fileType_photos) userTypes.add(new TypeInfo(Types.Photo, FileHelper.getNum_Photos()));

            // 获取并启动网络连接线程
            Thread thread_Network = getThread_network(returnPack, userTypes);
            thread_Network.setName("Thread_NETWORK");
            thread_Network.start();
        } else {
            Toast.makeText(this, "若要继续操作，请您授予必要的权限。", Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Thread getThread_network(@NonNull SetConnectPackage returnPack, List<TypeInfo> userTypes) {
        // 封装握手信息
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

        // 获取权限
        askPermission(Permissions.permissions[0]);
        askPermission(Permissions.permissions[1]);
    }
}