package com.example.cctv2.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.cctv2.R;
import com.example.cctv2.Service.MyForegroundService;
import com.example.cctv2.Service.PlayerManager;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private PlayerView videoView;
    private PlayerManager playerManager;


    private TextView statusTextView;
    private BroadcastReceiver statusReceiver;

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_main.xml 연결

        // 필요한 권한 요청
        // 위치 (백그라운드 서비스는 필수로 요구)
        // 알람 보내는 권한
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS,
                        //소리 녹음 권한
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                1
        );


        // 모든 권한이 있을 경우
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            // 서비스 실행
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);

        } else {
            // 권한 없음. 서비스 시작 불가
        }


        // 비디오 플레이어를 참조
        videoView = findViewById(R.id.videoView);

        // raw 폴더에서 비디오 파일을 가져오는 코드
        String videoUriString = "android.resource://" + getPackageName() + "/raw/videosample"; // raw 폴더에서 직접 URI를 생성

        playerManager = PlayerManager.getInstance(this);
        ExoPlayer player = playerManager.getPlayer();
        videoView.setPlayer(player);
        playerManager.prepare(Uri.parse(videoUriString), 0);


        // 버튼 6개를 참조
        Button setZoneBtn = findViewById(R.id.btn3);
        Button alarmListBtn = findViewById(R.id.btn4);
//        Button btn5 = findViewById(R.id.btn5);
        Button settingBtn = findViewById(R.id.btn6);
        Button viewZoneBtn = findViewById(R.id.btn7);
        ImageButton btn8 = findViewById(R.id.btn8);


        // 다른 버튼들도 동일한 방식으로 설정 가능
        setZoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 videoView의 화면을 비트맵으로 가져온뒤 jpg로 압축해서 바이트 배열에 저장후 intent로 넘겨줌
                Bitmap bitmap = getBitmapFromVideoView();
                if (bitmap == null)
                    Log.e("MainActivity", "영상에서 Bitmap 추출 실패");
                Intent intent = new Intent(MainActivity.this, SetZoneActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                byte[] imgBytes = stream.toByteArray();

                intent.putExtra("frame", imgBytes);
                startActivity(intent);
            }
        });

        alarmListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
                startActivity(intent);
            }
        });

//        btn5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DeviceConnectionActivity.class);
//                startActivity(intent);
//            }
//        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("server_url", getServerUrl());
                startActivity(intent);
            }
        });

        viewZoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getBitmapFromVideoView();
                if (bitmap == null)
                    Log.e("MainActivity", "영상에서 Bitmap 추출 실패");
                Intent intent = new Intent(MainActivity.this, ViewZoneActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                byte[] imgBytes = stream.toByteArray();

                intent.putExtra("frame", imgBytes);
                startActivity(intent);
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //broadcasting
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // BroadcastReceiver 해제
        unregisterReceiver(statusReceiver);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.setPlayer(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.setPlayer(playerManager.getPlayer());
        Log.i("MainActivity", "Resume");
    }

    @OptIn(markerClass = UnstableApi.class)
    private Bitmap getBitmapFromVideoView() {
        View videoSurface = videoView.getVideoSurfaceView();
        return ((TextureView)videoSurface).getBitmap();
    }

    //서버 주소 가져오기
    private String getServerUrl() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getString("server_url", ""); // 기본값은 빈 문자열
    }
}
