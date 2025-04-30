package com.example.cctv2.Service;

import android.app.*;
import android.content.Intent;
import android.os.*;
import androidx.core.app.NotificationCompat;
import com.example.cctv2.Activity.MessageAlram.MessageFetcher;
import com.example.cctv2.R;

public class MessageService extends Service {
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final int INTERVAL = 10000;

    @Override
    public void onCreate() {
        super.onCreate();
        // Foreground 서비스 알림을 등록하는 부분을 onStartCommand()로 이동
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        runnable = new Runnable() {
            @Override
            public void run() {
                MessageFetcher.fetchMessageWithRetry(3, getApplicationContext());
                handler.postDelayed(this, INTERVAL); // 10초마다 반복
            }
        };
        handler.post(runnable);
    }

    private void startForegroundService() {
        String channelId = "MessageServiceChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Message Fetcher", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Receiving Messages")
                .setContentText("Fetching messages every 10 seconds")
                .setSmallIcon(R.drawable.alram_icon) // 아이콘 필요
                .build();

        startForeground(1, notification);  // startForeground()는 반드시 5초 이내에 호출되어야 합니다.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Foreground 서비스 시작
        startForegroundService();
        return START_STICKY; // 서비스가 종료되지 않도록 유지
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // 서비스 종료 시, 반복 작업 종료
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
