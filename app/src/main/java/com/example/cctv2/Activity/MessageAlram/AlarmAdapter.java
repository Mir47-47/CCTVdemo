package com.example.cctv2.Activity.MessageAlram;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.Activity.AlarmItem;
import com.example.cctv2.R;

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
        new AlertDialog.Builder(context)
                .setTitle("알람 상세")
                .setMessage("메시지: " + item.getMessage() + "\n날짜: " + item.getDate())
                .setPositiveButton("확인", null)
                .show();
    }
}

