package com.example.cctv2.Activity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cctv2.R;

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
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class SettingActivity extends AppCompatActivity {

    private ImageButton recordButton;
    private MediaRecorder mediaRecorder;
    private File audioFile;
    private String severUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        //서버 주소 가져오기
        severUrl = getIntent().getStringExtra("serverUrl");
        Log.d("SettingActivity", "서버 주소: " + severUrl);

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
        try {
            // 녹음 파일 경로 설정
            audioFile = new File(getExternalFilesDir(null), "recorded_audio.3gp");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d("Recording", "녹음 시작");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e("Recording", "녹음 시작 실패: " + e.getMessage());
        }
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
                Log.e("Recording", "녹음 종료 실패: " + e.getMessage());
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

        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile);

        Call<ResponseBody> call = service.uploadAudio(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Upload", "파일 업로드 성공");
                    deleteAudioFile(); // 업로드 성공 시 파일 삭제
                } else {
                    Log.e("Upload", "파일 업로드 실패");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Upload", "에러: " + t.getMessage());
            }
        });

    }

    private void deleteAudioFile() {
        if (audioFile != null && audioFile.exists()) {
            boolean deleted = audioFile.delete();
            if (deleted) {
                Log.d("Delete", "파일 삭제 성공");
            } else {
                Log.e("Delete", "파일 삭제 실패");
            }
        }
    }

    interface UploadService {
        @Multipart
        @POST("upload") // 서버의 업로드 엔드포인트
        Call<ResponseBody> uploadAudio(@Part MultipartBody.Part file);
    }
}
