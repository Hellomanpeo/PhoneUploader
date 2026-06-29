package com.pms.phoneuploader;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    EditText editIp, editPort;
    Button btnSave;
    TextView txtStatus;

    static final String PREFS_NAME = "PhoneUploaderPrefs";
    static final int PERM_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editIp = findViewById(R.id.editIp);
        editPort = findViewById(R.id.editPort);
        btnSave = findViewById(R.id.btnSave);
        txtStatus = findViewById(R.id.txtStatus);

        // 저장된 설정 로드
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editIp.setText(prefs.getString("pc_ip", "192.168.219.113"));
        editPort.setText(prefs.getString("pc_port", "9876"));

        btnSave.setOnClickListener(v -> {
            String ip = editIp.getText().toString().trim();
            String port = editPort.getText().toString().trim();
            if (ip.isEmpty()) { Toast.makeText(this, "PC IP를 입력하세요", Toast.LENGTH_SHORT).show(); return; }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pc_ip", ip);
            editor.putString("pc_port", port.isEmpty() ? "9876" : port);
            editor.apply();

            txtStatus.setText("저장 완료: " + ip + ":" + (port.isEmpty() ? "9876" : port));
            Toast.makeText(this, "설정이 저장되었습니다", Toast.LENGTH_SHORT).show();
        });

        // 권한 요청
        requestPermissions();
    }

    void requestPermissions() {
        String[] perms = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        };

        boolean needRequest = false;
        for (String p : perms) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }
        if (needRequest) {
            ActivityCompat.requestPermissions(this, perms, PERM_REQUEST);
        } else {
            txtStatus.setText("모든 권한 허용됨. PC IP를 입력하고 저장하세요.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST) {
            boolean allGranted = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) { allGranted = false; break; }
            }
            txtStatus.setText(allGranted ? "모든 권한 허용됨" : "일부 권한이 거부됨. 설정에서 권한을 허용하세요.");
        }
    }
}
