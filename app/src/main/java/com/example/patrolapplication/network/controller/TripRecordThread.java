package com.example.patrolapplication.network.controller;

import com.example.patrolapplication.network.ApiService;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.network.response.PatrolRecordResponse;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripRecordThread extends Thread {
    private static final String TAG = "## TripRecordThread ## ";

    private UserDao userDao;
    private Map<String, Object> map;
    private String operation;

    public TripRecordThread(Map<String, Object> map, UserDao userDao, String operation) {
        this.userDao = userDao;
        this.map = map;
        this.operation = operation;
    }

    @Override
    public void run() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SyncData.ipAddress)
                .client(SyncData.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        final RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(map));
        apiService.patrolRecordOperate(requestBody, operation).enqueue(new Callback<PatrolRecordResponse>() {
            @Override
            public void onResponse(Call<PatrolRecordResponse> call, Response<PatrolRecordResponse> response) {
                if(response.body() != null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", response.body().msg);
                    map.put("state", response.body().state);
                    String json = new Gson().toJson(response.body().patrolRecord);
                    map.put("user", json);
                    userDao.userOperate(map);
                }
            }

            @Override
            public void onFailure(Call<PatrolRecordResponse> call, Throwable t) {
                System.out.println(TAG + t.getMessage());
                userDao.timeout();
            }
        });
    }
}
