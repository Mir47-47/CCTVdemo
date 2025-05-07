package com.example.cctv2.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.R;
import com.example.cctv2.domain.VoiceItem;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private ArrayList<VoiceItem> itemList; // 리스트 데이터 관리
    private SampleAdapter adapter; // RecyclerView 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        // RecyclerView 초기화
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ArrayList 및 Adapter 초기화
        itemList = new ArrayList<>();
        adapter = new SampleAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Add 버튼 클릭 이벤트
        ImageButton btnAddSample = findViewById(R.id.btn_add_sample);
        btnAddSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToList();
            }
        });
    }

    // 리스트에 아이템 추가
    private void addItemToList() {
        itemList.add(new VoiceItem("Sample"+itemList.size(), null, null, 0)); // 새 아이템 추가
        adapter.notifyItemInserted(itemList.size() - 1); // RecyclerView 업데이트
    }
}
