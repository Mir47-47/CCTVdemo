package com.example.cctv2.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cctv2.R;
import com.example.cctv2.Service.MyForegroundService;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private View videoPlaceholder;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_main.xml 연결

//        //백그라운드
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);


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
