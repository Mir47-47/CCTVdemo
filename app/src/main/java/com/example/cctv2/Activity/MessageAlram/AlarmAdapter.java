package com.example.cctv2.Activity.MessageAlram;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.Activity.AlarmItem;
import com.example.cctv2.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmItem> alarmList;
    private Context context;

    public AlarmAdapter(List<AlarmItem> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmItem item = alarmList.get(position);
        holder.messageText.setText(item.getMessage());
        holder.dateText.setText(item.getDate());

        //상세 팝업
        holder.itemView.setOnClickListener(v -> showDetailPopup(item));
        // 삭제 버튼 기능 추가
        holder.deleteButton.setOnClickListener(v -> {
            deleteItemFromFile(item); // 파일에서도 삭제
            alarmList.remove(position); // 리스트에서 제거
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, alarmList.size());
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText;
        Button deleteButton;
        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    private void showDetailPopup(AlarmItem item) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alarmlist_detail, null);

        TextView messageText = view.findViewById(R.id.detailMessage);
        TextView dateText = view.findViewById(R.id.detailDate);
        TextView typeText = view.findViewById(R.id.detailType);
        ImageView imageView = view.findViewById(R.id.detailImage);

        messageText.setText("메시지: " + item.getMessage());
        dateText.setText("날짜: " + item.getDate());// 날짜 들어오는 형태 받아서 계산 해줘야함
        Log.d("AlarmAdapter", "Type: " + item.getType());

        if (item.getType() == 0) {
            typeText.setText("정상");
            typeText.setTextColor(Color.parseColor("#008000")); // 녹색
        } else {
            typeText.setText("비정상");
            typeText.setTextColor(Color.parseColor("#FF0000")); // 빨간색
        }

        // 예시: drawable에서 이미지 설정
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            File imgFile = new File(item.getImagePath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.samplehousepicture); // 기본 이미지
            }
        } else {
            imageView.setImageResource(R.drawable.samplehousepicture); // 기본 이미지
        }
//        imageView.setImageResource(R.drawable.your_image); // 또는

        // 만약 이미지 경로/URL이 있다면 Glide 사용 (예)
        // Glide.with(context).load(item.getImageUrl()).into(imageView);

        new AlertDialog.Builder(context)
                .setTitle("알람 상세")
                .setView(view)
                .setPositiveButton("확인", null)
                .show();
    }

    //삭제 버튼클릭시
    private void deleteItemFromFile(AlarmItem item) {
        File file = new File(context.getFilesDir(), "message_log.json");
        List<String> newLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                String msg = json.optString("message", "");  // 오타 주의
                long date = json.optInt("date", 0)*1000L; // Unix timestamp를 밀리초로 변환
                int type = json.optInt("message_type", 0);
                // SimpleDateFormat을 사용하여 포맷 지정
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                String dateString = dateFormat.format(date);

                // 현재 줄이 삭제 대상이 아니면 유지
                if (!(msg.equals(item.getMessage()) && (dateString.equals(item.getDate())) && (type == item.getType()))) {
                    newLines.add(line);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 새로운 내용으로 파일 덮어쓰기
        try (FileWriter writer = new FileWriter(file, false)) {
            for (String l : newLines) {
                writer.write(l + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

