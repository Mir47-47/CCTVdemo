package com.example.cctv2.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.cctv2.R;

import java.util.Locale;

public class VideoPlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;
    private Handler handler = new Handler();

    private Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();

                seekBar.setMax((int) duration);
                seekBar.setProgress((int) position);

                currentTime.setText(formatTime(position));
                totalTime.setText(formatTime(duration));
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.player_view);
        seekBar = findViewById(R.id.video_seekbar);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // 비디오 로드
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("video_uri"); // ← 키 맞춰줌
        if (videoPath == null) {
            Toast.makeText(this, "영상 경로를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Uri videoUri = Uri.parse(videoPath);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        Long startPosition = intent.getLongExtra("current_position", 0);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.seekTo(startPosition);
        player.play();

        // SeekBar 동기화
        handler.post(updateSeekbar);

        // 사용자가 SeekBar 조작 시 이동
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player != null) {
                    player.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String formatTime(long millis) {
        int totalSeconds = (int) (millis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekbar);
        player.release();
    }
}

