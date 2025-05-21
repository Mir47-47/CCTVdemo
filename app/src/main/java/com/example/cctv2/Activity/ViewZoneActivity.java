package com.example.cctv2.Activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cctv2.R;
import com.example.cctv2.View.AreaView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewZoneActivity extends AppCompatActivity {
    ImageView imgView;
    AreaView areaView;
    Button BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewzone);

        imgView = findViewById(R.id.viewZoneImage);
        areaView = findViewById(R.id.viewZoneArea);

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

        Log.i("ViewZoneActivity", "서버로 Area 영역 받아오기");

        // 저장된 server_url 받아오기
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String url = prefs.getString("server_url", "");
        if (url.isEmpty()) {
            Log.e("ViewZoneActivity", "Prefs server_url 없음.");
        } else {
            OkHttpClient client = new OkHttpClient.Builder().build();

            Request request = new Request.Builder()
                    .url(url + "/video/setting/area")
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ViewZoneActivity", "요청 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    if (response.isSuccessful()) {
                        Log.i("ViewZoneActivity", "요청 성공. 응답 코드: " + response.code());
                        try {
                            JSONObject jsonBody = new JSONObject(response.body().string());
                            int count = jsonBody.getInt("count");
                            Log.i("ViewZoneActivity", "저장된 Area count: " + count);
                            JSONArray jsonArray = jsonBody.getJSONArray("result");
                            for (int i = 0; i < count; i++) {
                                float x1, y1, x2, y2;
                                x1 = (float) jsonArray.getJSONObject(i).getDouble("x1");
                                y1 = (float) jsonArray.getJSONObject(i).getDouble("y1");
                                x2 = (float) jsonArray.getJSONObject(i).getDouble("x2");
                                y2 = (float) jsonArray.getJSONObject(i).getDouble("y2");
                                // post를 이용해 view load후 실행 하도록
                                areaView.post(() -> areaView.addRect(x1, y1, x2, y2));
                                Log.i("ViewZoneActivity", String.format("Area View에 area추가 %.2f,%.2f,%.2f,%.2f", x1, y1, x2, y2));
                            }
                        } catch (JSONException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e("ViewZoneActivity", "요청 실패. 응답 코드: " + response.code());
                    }
                }
            });
        }
    }
}
