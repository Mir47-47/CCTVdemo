package com.example.cctv2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.Activity.MessageAlram.AlarmAdapter;
import com.example.cctv2.Activity.MessageAlram.MessageAdapter;
import com.example.cctv2.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlramListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_alramlist);

        recyclerView = findViewById(R.id.AlramListrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<AlarmItem> alarmList = loadAlarmListFromFile();
        AlarmAdapter adapter = new AlarmAdapter(alarmList, this);
        recyclerView.setAdapter(adapter);

        BackBtn = findViewById(R.id.buttonBack);
        BackBtn.setOnClickListener(v -> {
            finish(); // 현재 Activity 종료
        });
    }

    private List<AlarmItem> loadAlarmListFromFile() {
        List<AlarmItem> itemList = new ArrayList<>();
        File file = new File(getFilesDir(), "message_log.json");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                String message = json.optString("message", "");
                long date = json.optInt("date", 0) * 1000L; // s -> ms
                int type = json.optInt("message_type", 0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                String dateString = dateFormat.format(date);
                itemList.add(new AlarmItem(message, dateString, type));
            }
        } catch (Exception e) {
            Log.e("AlarmListActivity", "파일 읽기 오류", e);
        }

        return itemList;
    }

}




