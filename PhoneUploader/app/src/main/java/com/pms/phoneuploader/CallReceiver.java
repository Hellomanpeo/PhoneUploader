package com.pms.phoneuploader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import androidx.core.app.NotificationCompat;

import java.io.File;

public class CallReceiver extends BroadcastReceiver {

    static final String CHANNEL_ID = "call_upload_channel";
    static boolean wasRinging = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            wasRinging = true;
        }

        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) && wasRinging) {
            wasRinging = false;
            // 통화 종료 → 녹음 파일 찾기 → 알림 표시
            handleCallEnd(context);
        }
    }

    void handleCallEnd(Context context) {
        // 최근 녹음 파일 찾기
        String recordingPath = findLatestRecording(context);

        if (recordingPath == null) {
            // 녹음 파일 없으면 알림 없이 종료
            return;
        }

        // "전송하시겠습니까?" 알림 표시
        showUploadNotification(context, recordingPath);
    }

    String findLatestRecording(Context context) {
        // 1. 통화 녹음 폴더에서 최근 파일 찾기
        String[] paths = {
            "/storage/emulated/0/Recordings/Call/",
            "/storage/emulated/0/Download/cubeacr_rec/",
            "/storage/emulated/0/MIUI/sound_recorder/call_rec/",
            "/storage/emulated/0/Call/",
            "/storage/emulated/0/Sounds/Call/"
        };

        File latest = null;
        for (String dir : paths) {
            File folder = new File(dir);
            if (!folder.exists()) continue;
            File[] files = folder.listFiles();
            if (files == null) continue;
            for (File f : files) {
                if (!f.getName().toLowerCase().endsWith(".m4a") &&
                    !f.getName().toLowerCase().endsWith(".mp3") &&
                    !f.getName().toLowerCase().endsWith(".wav")) continue;
                // 최근 5분 이내 파일만
                if (System.currentTimeMillis() - f.lastModified() > 5 * 60 * 1000) continue;
                if (latest == null || f.lastModified() > latest.lastModified()) {
                    latest = f;
                }
            }
        }

        return latest != null ? latest.getAbsolutePath() : null;
    }

    void showUploadNotification(Context context, String filePath) {
        createNotificationChannel(context);

        // "예" → UploadService 시작
        Intent yesIntent = new Intent(context, UploadService.class);
        yesIntent.setAction("UPLOAD");
        yesIntent.putExtra("file_path", filePath);
        PendingIntent yesPending = PendingIntent.getService(context, 1, yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // "아니오" → 알림 제거
        Intent noIntent = new Intent(context, UploadService.class);
        noIntent.setAction("CANCEL");
        PendingIntent noPending = PendingIntent.getService(context, 2, noIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String fileName = new File(filePath).getName();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("통화 녹음 전송")
            .setContentText(fileName + " 을(를) 전송하시겠습니까?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_upload, "예", yesPending)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "아니오", noPending);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1001, builder.build());
    }

    void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "통화 녹음 전송",
                NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("통화 녹음 파일 전송 알림");
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(ch);
        }
    }
}
