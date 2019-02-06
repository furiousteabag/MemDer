package com.MemDerPack.Fragments;

import com.MemDerPack.Notifications.MyResponse;
import com.MemDerPack.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAga6dx4A:APA91bEHTP3fKwqPr5WuFNoYLJaWP-j4okNJ-f4mDExM-RMY0NLUu3cpZWmZ8PAUX6T0u11dIlfOJmFOVt75rz3mdpDmCLgneFUNoxDTDnp_qPdYz6YbrFhCXRHnSfw4tUtSwlJY0jZ4"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}