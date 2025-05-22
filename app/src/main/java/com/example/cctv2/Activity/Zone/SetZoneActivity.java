package com.example.cctv2.Activity.Zone;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cctv2.R;
import com.example.cctv2.View.AreaSelectorView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetZoneActivity extends AppCompatActivity {
    ImageView imgView;
    AreaSelectorView selectorView;
    Button saveBtn;
    Button BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setzone); // 이 부분이 XML 레이아웃을 화면에 표시함

        imgView = findViewById(R.id.setZoneImage);
        selectorView = findViewById(R.id.setZoneArea);
        saveBtn = findViewById(R.id.saveZoneBtn);

        BackBtn = findViewById(R.id.buttonBack);
        BackBtn.setOnClickListener(v -> {
            finish(); // 현재 Activity 종료
        });

        // intent와 함께 넘어온 이미지 바이트 배열을 decode해서 imgView에 보여주기
        byte[] imgBytes = getIntent().getByteArrayExtra("frame");
        if (imgBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            imgView.setImageBitmap(bitmap);
        }

        saveBtn.setOnClickListener(v -> {
            RectF rect = selectorView.getSelectedRect();
            RectF normalizedRect = new RectF(
                    rect.left / imgView.getWidth(),
                    rect.top / imgView.getHeight(),
                    rect.right / imgView.getWidth(),
                    rect.bottom / imgView.getHeight());

            Toast.makeText(this,
                    String.format("(% .2f, % .2f, % .2f, % .2f) Area",
                            normalizedRect.left,
                            normalizedRect.top,
                            normalizedRect.right,
                            normalizedRect.bottom),
                    Toast.LENGTH_SHORT).show();

            Log.i("SetZoneActivity", "서버로 Area 영역 전송");

            // 저장된 server_url 받아오기
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String url = prefs.getString("server_url", "");
            if (url.isEmpty()) {
                Log.e("SetZoneActivity", "Prefs server_url 없음.");
            } else {
                // post 형식으로 데이터 서버로 전송
                OkHttpClient client = new OkHttpClient.Builder().build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("x1", String.format("%.4f", normalizedRect.left))
                        .add("y1", String.format("%.4f", normalizedRect.top))
                        .add("x2", String.format("%.4f", normalizedRect.right))
                        .add("y2", String.format("%.4f", normalizedRect.bottom))
                        .build();

                Request request = new Request.Builder()
                        .url(url + "/video/setting/area")
                        .post(requestBody) // get , post 등 요청할 HTTP METHOD 작성
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("SetZoneActivity", "요청 실패: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        if (response.isSuccessful()) {
                            Log.i("SetZoneActivity", "요청 성공. 응답 코드: " + response.code());
                        } else {
                            Log.e("SetZoneActivity", "요청 실패. 응답 코드: " + response.code());
                        }
                    }
                });
            }

            finish();
        });
    }
}
