package com.example.cctv2.Activity.MessageAlram;
//api호출 및 재시도
import android.content.Context;
import android.util.Log;

import com.example.cctv2.Response.MessageResponse;
import com.example.cctv2.Service.ApiService;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageFetcher {

    private static final String BASE_URL = "localhost:8000"; // ← 여기에 REST API 주소

    public static void fetchMessageWithRetry(int retriesLeft, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<MessageResponse> call = apiService.getMessage();

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    MessageStorage.addMessage(message);
                    Log.d("MessageFetcher", "Received: " + message);
                } else {
                    retry();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                retry();
            }

            private void retry() {
                if (retriesLeft > 0) {
                    fetchMessageWithRetry(retriesLeft - 1, context);
                } else {
                    Log.e("MessageFetcher", "Failed after retries");
                }
            }
        });
    }
}

