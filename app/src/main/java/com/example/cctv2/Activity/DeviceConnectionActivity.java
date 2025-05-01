package com.example.cctv2.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cctv2.R;
import com.example.cctv2.Service.MyForegroundService;

public class DeviceConnectionActivity extends AppCompatActivity {
    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceconnection); // 이 부분이 XML 레이아웃을 화면에 표시함

        textStatus = findViewById(R.id.textStatus);

        // SharedPreferences에서 상태 읽기
        SharedPreferences prefs = getSharedPreferences("ServerPrefs", MODE_PRIVATE);
        String status = prefs.getString("server_status", "상태 정보 없음");
        textStatus.setText("서버 상태: " + status);

        Button restartBtn = findViewById(R.id.buttonRetry);
        restartBtn.setOnClickListener(v -> {
            // 서비스 재시작 (요청 재시도)
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);

            Toast.makeText(this, "서버 요청을 다시 시작합니다", Toast.LENGTH_SHORT).show();
        });
    }
}
