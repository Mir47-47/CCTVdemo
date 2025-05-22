package com.example.cctv2.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import okhttp3.*;

public class MyForegroundService extends Service {
    //에뮬은 localhost대신 10.0.2.2를 사용해야 pc localhost로 연결됨
    private final String HostUrl = ServerUrl.getServerUrl(); //서버 주소
    private Handler handler = new Handler();
    private Runnable runnable;
    private final int interval = 10000; // 10초
    private int failureCount = 0; // 실패 카운트 초기화
    private final int MAX_FAILURES = 3; // 최대 실패 횟수

    public static final String ACTION_STATUS_UPDATE = "com.example.status.UPDATE";
    public static final String EXTRA_STATUS = "status";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스 시작 로그
        Log.i("Service", "Start service");

        saveServerUrl(HostUrl);//서버 주소 저장

        createNotificationChannel();//무음 채널
        createSoundNotificationChannel();//소리 채널
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("백그라운드 작업 실행 중")
                .setContentText("REST API 요청을 보내는 중...")
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

        // ① 요청 시작 시 상태를 "요청 중"으로 저장
        sendStatusUpdate("요청 중");

        Request request = new Request.Builder()
                .url(HostUrl+"/message/message") // GET 또는 POST 필요 시 설정
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                failureCount++; // 실패 카운트 증가
                Log.e("Service", "API 요청 실패: " + e.getMessage());
                if (failureCount >= 3) {
                    // 서버 연결 실패 3회 이상 시
                    updateNotification("서버 연결 실패", "API 요청이 3회 실패했습니다.");
                    sendStatusUpdate("요청 실패");
                    stopSelf(); // 서비스 종료
                }
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    sendStatusUpdate("서버 요청 성공");
                    failureCount = 0; // 실패 카운트 초기화
                    String responseBody = response.body() != null ? response.body().string() : "";
                    try {
                        if (responseBody != null && !responseBody.trim().isEmpty()) {
                            JSONObject json = new JSONObject(responseBody);

                            if (json.has("message") && !json.isNull("message")) {
                                String message = json.getString("message");
                                showNotification(message);         // 알림 띄우기
                                saveJsonToFile(json);              // 전체 응답(json) 저장
                            } else {
                                Log.w("Response", "'message' 키 없음 또는 null");
                            }
                        } else {
                            Log.w("Response", "responseBody가 비어 있음, 무시하고 넘어감");
                        }
                    } catch (JSONException e) {
                        Log.e("Service", "JSON 파싱 실패: " + e.getMessage());
                    }
                } else {
                    sendStatusUpdate("응답 실패");
                    failureCount++;
                    Log.e("Service", "응답 실패, HTTP 코드: " + response.code());
                    if (failureCount >= MAX_FAILURES) {
                        // 응답 실패 3회 이상 시
                        updateNotification("서버 연결 실패", "서버 응답 오류가 발생했습니다.");
                        stopSelf(); // 서비스 종료
                    }
                }
            }
        });
    }
//서버 요청에 대한 확인데이터 전파
    private void sendStatusUpdate(String status) {
        // 상태 저장 (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("ServerPrefs", MODE_PRIVATE);
        prefs.edit().putString("server_status", status).apply();
        // 상태 업데이트를 MainActivity로 전달
        Intent broadcastIntent = new Intent(ACTION_STATUS_UPDATE);
        broadcastIntent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(broadcastIntent);  // Broadcast로 상태 전송
    }

    // 알림 내용 업데이트 함수
    private void updateNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.alarm_icon)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification); // 알림 갱신
        }
    }

    private void saveJsonToFile(JSONObject jsonObject) {
        try {
            String filename = "message_log.json";
            File file = new File(getFilesDir(), filename);

            FileOutputStream fos = new FileOutputStream(file, true);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.newLine(); // 줄 단위 저장
            bufferedWriter.flush();
            bufferedWriter.close();

            Log.d("SaveJSON", "Saved JSON: " + jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showNotification(String message) {
        //알람 이미지를 bitmap으로 변환
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.aihomecamlogo_round);

        Notification notification = new NotificationCompat.Builder(this, "sound_channel_id")
                .setContentTitle("감지됨")
                .setContentText(message)
                .setSmallIcon(R.drawable.aihomecamlogo_round)//알림 아이콘
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(this).notify(2, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "API 요청 채널";
            String description = "서버 요청 실패 알림 채널";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void createSoundNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "소리 알림 채널";
            String description = "이 채널의 알림은 소리를 냅니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH;  // HIGH 이상이어야 소리+팝업 가능

            NotificationChannel channel = new NotificationChannel("sound_channel_id", name, importance);
            channel.setDescription(description);

            channel.setSound(null,null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    //서버 URL을 setting으로 넘기기 위한 저장
    private void saveServerUrl(String serverUrl) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("server_url", serverUrl).apply();
    }


    @Override public IBinder onBind(Intent intent) { return null; }
    @Override public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

