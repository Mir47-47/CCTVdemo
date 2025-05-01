package com.example.cctv2.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.*;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cctv2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.*;

public class MyForegroundService extends Service {
    private String HostUrl = "https://localhost:8000";
    private Handler handler = new Handler();
    private Runnable runnable;
    private final int interval = 10000; // 10초

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("백그라운드 작업 실행 중")
                .setContentText("REST API 요청을 보내는 중...")
                .setSmallIcon(R.drawable.alram_icon)
                .build();

        startForeground(1, notification);

        runnable = new Runnable() {
            @Override
            public void run() {
                sendApiRequest();
                handler.postDelayed(this, interval);
            }
        };
        handler.post(runnable);

        return START_STICKY;
    }

    private void sendApiRequest() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(HostUrl+"/message/message") // GET 또는 POST 필요 시 설정
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String message = json.getString("message");

                        Log.d("Service", "받은 메시지: " + message);

                        // ➕ 예: 알림으로 표시하거나 SharedPreferences에 저장
                        showNotification(message);
                        saveMessageToFile(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void saveMessageToFile(String message) {
        String filename = "messages.txt";
        File file = new File(getFilesDir(), filename);

        try (FileWriter writer = new FileWriter(file, true)) { // true = append
            writer.append(message).append("\n");
            Log.d("Service", "메시지를 파일에 저장함: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String message) {
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("새 메시지 도착")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(2, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "channel_id", "Background Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }
    @Override public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

