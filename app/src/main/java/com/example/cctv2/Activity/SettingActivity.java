package com.example.cctv2.Activity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.cctv2.R;
import com.example.cctv2.Service.PlayerManager;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class SettingActivity extends AppCompatActivity {

    private ImageButton recordButton;
    private MediaRecorder mediaRecorder;
    private File audioFile;
    private String severUrl = null;
    private PlayerView videoView;

    Button BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        BackBtn = findViewById(R.id.buttonBack);
        BackBtn.setOnClickListener(v -> {
            finish(); // 현재 Activity 종료
        });

        //서버 주소 가져오기
        severUrl = getIntent().getStringExtra("server_url");
        Log.d("SettingActivity", "서버 주소: " + severUrl);

        videoView = findViewById(R.id.settingVideoView);

        PlayerManager playerManager = PlayerManager.getInstance(this);
        ExoPlayer player = playerManager.getPlayer();
        videoView.setPlayer(player);

        //버튼 누르는 동안 녹음, 녹음이 끝나면 저장
        //서버로 데이터를 보냈으면 데이터삭제
        recordButton = findViewById(R.id.btn_play_voice_sample);
        recordButton.setOnTouchListener((v,event)->{
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    break;
                case MotionEvent.ACTION_UP:
                    stopRecording();
                    break;
            }
            return true;
        });
    }

    private void startRecording() {
        recordButton.setEnabled(false);//버튼 비활성화, 비활성화 해도 누르고 있던건 인식됨
        checkServerConnection(isConnected -> {
            if (isConnected) {
                try {
                    // 녹음 파일 경로 설정
                    audioFile = new File(getExternalFilesDir(null), "recorded_audio.acc");
                    Log.d("AudioFile", "녹음 파일 경로: " + audioFile.getAbsolutePath());
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setAudioEncodingBitRate(128000);
                    mediaRecorder.setAudioSamplingRate(44100);
                    mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    Log.d("Recording", "녹음 시작");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SettingActivity.Recording", "녹음 시작 실패: " + e.getMessage());
                }
                recordButton.setEnabled(true);//버튼 활성화
            } else {
                Toast.makeText(this, "서버와 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.e("SettingActivity", "서버 연결 실패로 녹음 시작 불가");
                recordButton.setEnabled(true);//버튼 활성화
            }
        });

    }

    private void checkServerConnection(ServerConnectionCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(severUrl) // 서버 URL
                .client(new OkHttpClient())
                .build();

        ServerStatusService service = retrofit.create(ServerStatusService.class);

        Call<ResponseBody> call = service.checkStatus();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Retrofit", "요청 실패: " + t.getMessage());
                callback.onResult(false);
            }
        });
    }

    interface ServerStatusService {
        @GET("health") // 서버 상태 확인 엔드포인트
        Call<ResponseBody> checkStatus();
    }

    interface ServerConnectionCallback {
        void onResult(boolean isConnected);
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try{
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Log.d("Recording", "녹음 끝");

                uploadAudioFile();
            } catch(RuntimeException e){
                e.printStackTrace();
                Log.e("SettingActivity.Recording", "녹음 종료 실패: " + e.getMessage());
            }
        }
    }

    private void uploadAudioFile(){
        if (audioFile == null || !audioFile.exists()) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(severUrl) // 서버 URL
                .client(new OkHttpClient())
                .build();

        UploadService service = retrofit.create(UploadService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/acc"), audioFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile);

        Call<ResponseBody> call = service.uploadAudio(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Upload", "파일 업로드 성공");
                    deleteAudioFile(); // 업로드 성공 시 파일 삭제
                } else {
                    Log.e("SettingActivity.Upload", "파일 업로드 실패");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("SettingActivity.Upload", "에러: " + t.getMessage());
            }
        });

    }

    private void deleteAudioFile() {
        if (audioFile != null && audioFile.exists()) {
            int retryCount = 3; // 최대 재시도 횟수
            boolean deleted = false;

            for (int i = 0; i < retryCount; i++) {
                deleted = audioFile.delete();
                if (deleted) {
                    Log.d("SettingActivity.Delete", "파일 삭제 성공");
                    break;
                } else {
                    Log.e("SettingActivity.Delete", "파일 삭제 실패, 재시도 중... (" + (i + 1) + "/" + retryCount + ")");
                }
            }

            if (!deleted) {
                Log.e("SettingActivity.Delete", "파일 삭제 실패: 최대 재시도 횟수 초과");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.setPlayer(null);
    }

    interface UploadService {
        @Multipart
        @POST("/voice/upload-audio") // 서버의 업로드 엔드포인트
        Call<ResponseBody> uploadAudio(@Part MultipartBody.Part file);
    }
}
