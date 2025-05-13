package com.example.cctv2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cctv2.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);  // 로고 화면에 해당하는 레이아웃 연결

        // 일정 시간 후 MainActivity로 넘어감 (3초 후)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // MainActivity로 이동
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // SplashActivity 종료
            }
        }, 3000);  // 3초 후 실행
    }
}
