package com.example.cctv2.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cctv2.R;
import com.example.cctv2.View.AreaSelectorView;

public class SetZoneActivity extends AppCompatActivity {
    ImageView imgView;
    AreaSelectorView selectorView;
    Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setzone); // 이 부분이 XML 레이아웃을 화면에 표시함

        imgView = findViewById(R.id.setZoneImage);
        selectorView = findViewById(R.id.setZoneArea);
        saveBtn = findViewById(R.id.saveZoneBtn);

        // intent와 함께 넘어온 이미지 바이트 배열을 decode해서 imgView에 보여주기
        byte[] imgBytes = getIntent().getByteArrayExtra("frame");
        if (imgBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            imgView.setImageBitmap(bitmap);
        }

        saveBtn.setOnClickListener(v -> {
            // TODO: Ban Area 구역 저장 구현 할 것
            RectF rect = selectorView.getSelectedRect();
            Toast.makeText(this,
                    String.format("(% .2f, % .2f, % .2f, % .2f) Area", rect.left, rect.top, rect.right, rect.bottom),
                    Toast.LENGTH_SHORT).show();


            finish();
        });
    }
}
