package com.example.cctv2.Activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.cctv2.R;
import com.example.cctv2.Service.PlayerManager;

public class FullscreenVideoActivity extends AppCompatActivity {
    PlayerView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreenvideo);

        videoView = findViewById(R.id.fullscreenPlayerView);
        ExoPlayer player = PlayerManager.getInstance(this).getPlayer();
        videoView.setPlayer(player);

        long position = getIntent().getLongExtra("position", 0);
        if (player.getMediaItemCount() == 0) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/videosample");
            PlayerManager.getInstance(this).prepare(videoUri, position);
        } else {
            player.seekTo(position);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.setPlayer(null);
    }
}
