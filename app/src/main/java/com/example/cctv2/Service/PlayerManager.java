package com.example.cctv2.Service;

import static androidx.media3.common.Player.REPEAT_MODE_ONE;

import android.content.Context;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class PlayerManager {
    private static PlayerManager instance;
    private ExoPlayer player;

    private PlayerManager(Context context) {
        player = new ExoPlayer.Builder(context.getApplicationContext()).build();
    }

    public static synchronized PlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerManager(context);
        }
        return instance;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void prepare(Uri uri, long seekPositionMs) {
        MediaItem item = MediaItem.fromUri(uri);
        player.setMediaItem(item);
        player.setRepeatMode(REPEAT_MODE_ONE);
        player.prepare();
        player.seekTo(seekPositionMs);
        player.setPlayWhenReady(true);
    }

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void releasePlayer() {
        player.release();
        player = null;
        instance = null;
    }
}
