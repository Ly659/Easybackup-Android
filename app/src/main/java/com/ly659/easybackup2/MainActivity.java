package com.ly659.easybackup2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
                o -> {
                    if (o.getResultCode() == RESULT_OK) {
                        // 获取Activity的返回值数据
                        Intent returnData = o.getData();
                        // 取出序列化对象
                        assert returnData != null;
                        SetConnectPackage returnPack = returnData.getParcelableExtra("SetConnect_UserChoice");

                    }
                }
        );

        // 绑定UI控件
        Button button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener((v -> {
            final ActivityResultLauncher<Intent> luncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    o -> {
                        if (o.getResultCode() == RESULT_OK) {
                            // 获取Activity的返回值数据
                            Intent returnData = o.getData();
                            // 取出序列化对象
                            assert returnData != null;
                            SetConnectPackage returnPack = returnData.getParcelableExtra("SetConnect_UserChoice");

                        }
                    }
            );
        }));
    }
}