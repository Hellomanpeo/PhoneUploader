package com.pms.phoneuploader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadService extends Service {

    static final String CHANNEL_ID = "upload_channel";

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String action = intent.getAction();

        if ("UPLOAD".equals(action)) {
            String filePath = intent.getStringExtra("file_path");
            if (filePath != null) {
                // 알림 제거
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.cancel(1001);

                // 백그라운드에서 업로드
                new Thread(() -> uploadFile(filePath)).start();
            }
        } else if ("CANCEL".equals(action)) {
            // 알림 제거
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(1001);
            showNotification("전송 취소됨", "파일 전송이 취소되었습니다.", false);
        }

        return START_NOT_STICKY;
    }

    void uploadFile(String filePath) {
        SharedPreferences prefs = getSharedPreferences("PhoneUploaderPrefs", MODE_PRIVATE);
        String ip = prefs.getString("pc_ip", "192.168.219.113");
        String port = prefs.getString("pc_port", "9876");

        File file = new File(filePath);
        if (!file.exists()) {
            showNotification("전송 실패", "파일을 찾을 수 없습니다: " + filePath, true);
            return;
        }

        String uploadUrl = "http://" + ip + ":" + port + "/upload";

        try {
            String boundary = "----Boundary" + System.currentTimeMillis();
            URL url = new URL(uploadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            // 파일 파트
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            dos.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            fis.close();

            dos.writeBytes("\r\n--" + boundary + "--\r\n");
            dos.flush();
            dos.close();

            int responseCode = conn.getResponseCode();
            conn.disconnect();

            if (responseCode == 200) {
                showNotification("전송 완료", file.getName() + " 전송 성공", false);
            } else {
                showNotification("전송 실패", "서버 응답: " + responseCode, true);
            }

        } catch (Exception e) {
            showNotification("전송 실패", e.getMessage(), true);
        }
    }

    void showNotification(String title, String text, boolean isError) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(isError ? android.R.drawable.ic_dialog_alert : android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(isError ? 1002 : 1003, builder.build());
    }

    void createNotificationChannel() {
        NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "파일 전송",
            NotificationManager.IMPORTANCE_DEFAULT);
        ch.setDescription("통화 녹음 파일 전송 상태");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(ch);
    }
}
