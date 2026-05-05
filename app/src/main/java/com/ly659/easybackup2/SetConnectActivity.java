package com.ly659.easybackup2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetConnectActivity extends AppCompatActivity {
    private EditText input_ip_1;
    private EditText input_ip_2;
    private EditText input_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_connect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 绑定按钮、输入等控件
        Button button_continue = findViewById(R.id.button_continue);
        input_ip_1 = findViewById(R.id.input_ip1);
        input_ip_2 = findViewById(R.id.input_ip2);
        input_port = findViewById(R.id.input_port);
        CheckBox check_photos = findViewById(R.id.check_photos);
        CheckBox check_videos = findViewById(R.id.check_videos);

        button_continue.setOnClickListener(v -> {
            // 首先检查用户输入数据是否合法
            if (checkInputs()) {
                // 封装，准备传输给MainActivity
                SetConnectPackage returnPack = new SetConnectPackage(
                        "192.168.".concat(getText(input_ip_1).concat(getText(input_ip_2))),
                        getText(input_port),
                        check_photos.isChecked(),       // 复选框是否选中
                        check_videos.isChecked()
                        );

                Intent intent_return = new Intent();
                intent_return.putExtra("SetConnect_UserChoice", returnPack);
                setResult(RESULT_OK, intent_return);

                finish();       // 结束当前Activity，返回上一个Activity（主界面）并传回序列化对象
            } else {
                Toast.makeText(this, "错误：您输入的内容有误，请重新输入。", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取指定输入框控件的文字内容。
     * @param widget 输入框控件
     * @return 内容
     */
    private String getText(EditText widget) {return widget.getText().toString();}


    private boolean IpUnfair(int ipField) {
        return ipField < 0 || ipField > 255;
    }
    private boolean checkPort(int port) {
        return port > 0 && port <= 65535;
    }
    /**
     * 检查用户输入框的所有内容是否合法。
     * @return 检查结果。
     */
    private boolean checkInputs() {
        // 检查IP输入
        if (getText(input_ip_1).isEmpty() || getText(input_ip_2).isEmpty()) return false;
        // 检查端口号输入
        if (getText(input_port).isEmpty()) return false;
        // 检查输入是否只含有数字，且数字取值是否合法
        try {
            if (
                    IpUnfair(Integer.parseInt(getText(input_ip_1))) ||
                    IpUnfair(Integer.parseInt(getText(input_ip_2))) ||
                    !checkPort(Integer.parseInt(getText(input_port)))
            ) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}