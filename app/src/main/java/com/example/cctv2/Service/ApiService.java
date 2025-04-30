package com.example.cctv2.Service;

import com.example.cctv2.Response.MessageResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/message/message") // 예: "api/get-message"
    Call<MessageResponse> getMessage();
}

