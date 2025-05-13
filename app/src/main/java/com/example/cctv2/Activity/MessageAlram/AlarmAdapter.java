package com.example.cctv2.Activity.MessageAlram;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.Activity.AlarmItem;
import com.example.cctv2.R;

import java.io.File;
import java.util.List;

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

        holder.itemView.setOnClickListener(v -> showDetailPopup(item));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }

    private void showDetailPopup(AlarmItem item) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alarmlist_detail, null);

        TextView messageText = view.findViewById(R.id.detailMessage);
        TextView dateText = view.findViewById(R.id.detailDate);
        ImageView imageView = view.findViewById(R.id.detailImage);

        messageText.setText("메시지: " + item.getMessage());
        dateText.setText("날짜: " + item.getDate());

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
}

