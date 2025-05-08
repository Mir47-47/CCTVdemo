package com.example.cctv2.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.R;
import com.example.cctv2.domain.VoiceItem;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private VoiceItem voiceItem;
    private ImageButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        playButton = findViewById(R.id.btn_play_voice_sample);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //재생 코드
            }
        });

        // Add 버튼 클릭 이벤트
        ImageButton btnAddSample = findViewById(R.id.refresh_button);
        btnAddSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshVoice();
            }
        });
    }

    // 리스트에 아이템 추가
    private void refreshVoice() {
        Toast t = Toast.makeText(getApplicationContext(), "시연에서는 인터넷 불안정으로 인해\nRefresh가 지원되지 않습니다.",Toast.LENGTH_SHORT);
        t.show();
    }
}
