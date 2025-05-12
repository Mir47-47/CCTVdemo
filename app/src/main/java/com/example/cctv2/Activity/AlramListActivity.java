package com.example.cctv2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.Activity.MessageAlram.MessageAdapter;
import com.example.cctv2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlramListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MessageAdapter adapter;
    Button BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_alramlist); // 이 부분이 XML 레이아웃을 화면에 표시함

        recyclerView = findViewById(R.id.AlramListrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> messages = readMessagesFromFile();
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        BackBtn = findViewById(R.id.buttonBack);
        BackBtn.setOnClickListener(v -> {
            finish(); // 현재 Activity 종료
        });


    }
    private List<String> readMessagesFromFile() {
        List<String> messages = new ArrayList<>();
        File file = new File(getFilesDir(), "messages.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
