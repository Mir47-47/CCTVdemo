package com.example.cctv2.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
                        Manifest.permission.POST_NOTIFICATIONS
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

        statusTextView = findViewById(R.id.textServerStatus);
// BroadcastReceiver 등록
        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 상태 업데이트 받기
                String status = intent.getStringExtra(MyForegroundService.EXTRA_STATUS);
                statusTextView.setText(status);  // 상태 텍스트 업데이트
            }
        };



        // BroadcastReceiver 필터 등록
        IntentFilter filter = new IntentFilter(MyForegroundService.ACTION_STATUS_UPDATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(statusReceiver, filter, RECEIVER_EXPORTED);
        } else {
            registerReceiver(statusReceiver, filter);
        }


        // 비디오 플레이어와 플레이스홀더 뷰를 참조
        videoPlaceholder = findViewById(R.id.videoPlaceholder);
        videoView = findViewById(R.id.videoView);

        // 파일 경로 지정 (예: 내부 저장소에 있는 파일)
        String videoPath = getFilesDir() + "/video.mp4"; // 또는 원하는 경로
        File videoFile = new File(videoPath);

        if (videoFile.exists()) {
            playVideo(videoFile);
        } else {
            showPlaceholder();
        }


        // 버튼 6개를 참조
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);

        // 각 버튼에 클릭 리스너 추가
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // btn1 클릭 시 Clip 액티비티로 이동
//                Intent intent = new Intent(MainActivity.this, ClipActivity.class);
//                startActivity(intent);
//            }
//        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "Click");
                // btn2 클릭 시 다른 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, ClipActivity.class);
                startActivity(intent);
            }
        });

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
    }

    private void playVideo(File file) {
        videoPlaceholder.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        Uri videoUri = Uri.fromFile(file);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> videoView.start());
    }

    private void showPlaceholder() {
        videoView.setVisibility(View.GONE);
        videoPlaceholder.setVisibility(View.VISIBLE);
    }
}
