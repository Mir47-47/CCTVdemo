package com.example.cctv2.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.R;
import com.example.cctv2.domain.VoiceItem;

import java.util.ArrayList;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {

    private final ArrayList<VoiceItem> itemList;

    public SampleAdapter(ArrayList<VoiceItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoiceItem item = itemList.get(position);
        holder.sampleName.setText(item.getName());

        // 삭제 버튼 클릭 이벤트
        holder.btnDelete.setOnClickListener(v -> {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sampleName;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sampleName = itemView.findViewById(R.id.sample_name);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
