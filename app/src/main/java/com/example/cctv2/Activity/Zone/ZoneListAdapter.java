package com.example.cctv2.Activity.Zone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cctv2.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ZoneListAdapter extends RecyclerView.Adapter<ZoneListAdapter.ZoneViewHolder> {
    private final List<RectF> zoneList;
    private final Context context;

    private ZoneListAdapter.HighlightZoneEvent highlightZoneEvent = null;
    private ZoneListAdapter.DeleteZoneEvent deleteZoneEvent = null;

    public ZoneListAdapter(List<RectF> list, Context ctx) {
        this.zoneList = list;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zonelist_item, parent, false);
        return new ZoneListAdapter.ZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        RectF item = zoneList.get(position);
        holder.title.setText("금지구역 #" + (position + 1));

        holder.rect.setText(String.format("(%.2f,%.2f,%.2f,%.2f)", item.left, item.top, item.right, item.bottom));

        // 존 하이라이트
        holder.itemView.setOnClickListener(v -> {
            if (highlightZoneEvent != null)
                highlightZoneEvent.event(position);
        });

        // 삭제 버튼 기능 추가
        holder.deleteButton.setOnClickListener(v -> {
            sendDeleteRequest(position); // 파일에서도 삭제
            zoneList.remove(position); // 리스트에서 제거
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, zoneList.size());

            if (deleteZoneEvent != null)
                deleteZoneEvent.event();
        });
    }

    private void sendDeleteRequest(int pos) {
        SharedPreferences prefs = this.context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String url = prefs.getString("server_url", "");
        if (url.isEmpty()) {
            Log.e("ZoneListAdapter", "Prefs server_url 없음.");
        } else {
            OkHttpClient client = new OkHttpClient.Builder().build();

            RequestBody requestBody = new FormBody.Builder()
                    .add("index", String.valueOf(pos))
                    .build();

            Request request = new Request.Builder()
                    .url(url + "/video/clear")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ZoneListAdapter", "요청 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    if (response.isSuccessful()) {
                        Log.i("ZoneListAdapter", "요청 성공. 응답 코드: " + response.code());
                    } else {
                        Log.e("ViewZoneActivity", "요청 실패. 응답 코드: " + response.code());
                    }
                }
            });
        }
    }

    public void setHighlightZoneEvent(HighlightZoneEvent event) {
        this.highlightZoneEvent = event;
    }

    public void setDeleteZoneEvent(DeleteZoneEvent event) {
        this.deleteZoneEvent = event;
    }

    @Override
    public int getItemCount() {
        return this.zoneList.size();
    }

    @FunctionalInterface
    public interface HighlightZoneEvent {
        void event(int pos);
    }

    @FunctionalInterface
    public interface DeleteZoneEvent {
        void event();
    }

    public static class ZoneViewHolder extends RecyclerView.ViewHolder {
        TextView title, rect;
        Button deleteButton;
        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.zoneTitle);
            this.rect = itemView.findViewById(R.id.zoneRect);
            this.deleteButton = itemView.findViewById(R.id.deleteZoneButton);
        }
    }

}
