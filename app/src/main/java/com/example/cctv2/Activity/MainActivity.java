package com.example.cctv2.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private View videoPlaceholder;
    private TextureView videoView;
    private MediaPlayer mediaPlayer;


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

        // raw 폴더에서 비디오 파일을 가져오는 코드
        String videoUriString = "android.resource://" + getPackageName() + "/raw/videosample"; // raw 폴더에서 직접 URI를 생성

        try {
            // videoView의 서피스를 이용해 동영상 재생
            videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    // 서피스가 이용 가능해 지면 호출되는 함수
                    Surface previewSurface = new Surface(surface);
                    mediaPlayer = new MediaPlayer();
                    try {
                        // 미디어 플레이어에 동영상 제공
                        mediaPlayer.setDataSource(MainActivity.this, Uri.parse(videoUriString));

                        // 재생될 서피스 제공 -> videoView의 서피스
                        mediaPlayer.setSurface(previewSurface);

                        // 영상 무한 재생
                        mediaPlayer.setLooping(true);

                        // prepared되면 호출
                        mediaPlayer.setOnPreparedListener(mp -> {
                            // 영상 재생 및 placeholder 안보이게
                            mp.start();
                        });

                        // 비동기 함수
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            });
        } catch (Exception e) {
            // 예외가 발생한 경우 placeholder를 표시하고 오류 메시지를 출력
            e.printStackTrace();  // 로그에 오류 출력
            showPlaceholder();    // Placeholder 화면 표시
        }
        showVideo();


        // 버튼 6개를 참조
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);


        // 다른 버튼들도 동일한 방식으로 설정 가능
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 videoView의 화면을 비트맵으로 가져온뒤 jpg로 압축해서 바이트 배열에 저장후 intent로 넘겨줌
                Bitmap bitmap = videoView.getBitmap();
                Intent intent = new Intent(MainActivity.this, SetZoneActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                byte[] imgBytes = stream.toByteArray();

                intent.putExtra("frame", imgBytes);
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

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = videoView.getBitmap();
                Intent intent = new Intent(MainActivity.this, ViewZoneActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                byte[] imgBytes = stream.toByteArray();

                intent.putExtra("frame", imgBytes);
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

    // videoPlaceholder를 안 보이게 하고 videoView를 보이게
    private void showVideo() {
        Log.i("Main", "showVideo called.");
        videoPlaceholder.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
    }

    // videoPlaceholder를 보이게 하고 videoView를 안 보이게
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
