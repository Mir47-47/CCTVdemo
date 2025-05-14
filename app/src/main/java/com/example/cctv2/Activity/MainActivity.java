package com.example.cctv2.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cctv2.R;
import com.example.cctv2.Service.MyForegroundService;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private View videoPlaceholder;
    private VideoView videoView;


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


        // 비디오 플레이어와 플레이스홀더 뷰를 참조
        videoPlaceholder = findViewById(R.id.videoPlaceholder);
        videoView = findViewById(R.id.videoView);

// VideoView를 초기화하고, raw 폴더에서 비디오 파일을 가져오는 코드
        String videoUriString = "android.resource://" + getPackageName() + "/raw/videosample"; // raw 폴더에서 직접 URI를 생성

        try {
            // 비디오 URI를 Uri 객체로 변환 후 재생하는 코드
            Uri videoUri = Uri.parse(videoUriString);  // String을 Uri로 변환
            playVideo(videoUri);
        } catch (Exception e) {
            // 예외가 발생한 경우 placeholder를 표시하고 오류 메시지를 출력
            e.printStackTrace();  // 로그에 오류 출력
            showPlaceholder();    // Placeholder 화면 표시
        }


        // 버튼 6개를 참조
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);


        // 다른 버튼들도 동일한 방식으로 설정 가능
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetZoneActivity.class);
                startActivity(intent);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlramListActivity.class);
                startActivity(intent);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceConnectionActivity.class);
                startActivity(intent);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("server_url", getServerUrl());
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

    private void playVideo(Uri videoUri) {
        videoPlaceholder.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> videoView.start());
    }

    private void showPlaceholder() {
        videoView.setVisibility(View.GONE);
        videoPlaceholder.setVisibility(View.VISIBLE);
    }

    //서버 주소 가져오기
    private String getServerUrl() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getString("server_url", ""); // 기본값은 빈 문자열
    }
}
