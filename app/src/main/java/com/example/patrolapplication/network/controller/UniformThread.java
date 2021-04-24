package com.example.patrolapplication.network.controller;

import com.example.patrolapplication.network.ApiService;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.network.response.PatrolRecordResponse;
import com.example.patrolapplication.network.response.UniformResponse;
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

public class UniformThread extends Thread {
    private static final String TAG = "## UniformThread ## ";

    private UserDao userDao;
    private Map<String, Object> map;
    private String obj;
    private String operation;

    public UniformThread(Map<String, Object> map, UserDao userDao, String obj, String operation) {
        this.userDao = userDao;
        this.map = map;
        this.obj = obj;
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
        apiService.uniformOperate(requestBody, obj, operation).enqueue(new Callback<UniformResponse>() {
            @Override
            public void onResponse(Call<UniformResponse> call, Response<UniformResponse> response) {
                if(response.body() != null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", response.body().msg);
                    map.put("state", response.body().state);
                    String json = new Gson().toJson(response.body().data);
                    map.put("data", json);
                    userDao.userOperate(map);
                }
            }

            @Override
            public void onFailure(Call<UniformResponse> call, Throwable t) {
                System.out.println(TAG + t.getMessage());
                userDao.timeout();
            }
        });
    }
}
