package com.example.patrolapplication.network.controller;

import com.example.patrolapplication.network.ApiService;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.network.response.LocationResponse;
import com.example.patrolapplication.network.response.UserResponse;
import com.example.patrolapplication.pojo.Location;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.time.chrono.ThaiBuddhistEra;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationOperateThread extends Thread {
    private static final String TAG = "## locationOperateThread ## ";

    private Map<String, Object> map;
    private UserDao userDao;
    private String operation;

    public LocationOperateThread(Map<String, Object> map, UserDao userDao, String operation) {
        this.map = map;
        this.userDao = userDao;
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
        apiService.locationOperate(requestBody, operation).enqueue(new Callback<LocationResponse>(){
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if(response.body() != null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", response.body().msg);
                    map.put("state", response.body().state);
                    String json = new Gson().toJson(response.body().location);
                    map.put("location", json);
                    System.out.println(TAG + map);
                    userDao.userOperate(map);
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                System.out.println(TAG + t.getMessage());
                userDao.timeout();
            }
        });
    }
}
