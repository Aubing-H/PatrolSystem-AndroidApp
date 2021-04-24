package com.example.patrolapplication.network.controller;

import com.example.patrolapplication.network.ApiService;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.network.response.UniformResponse;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UniformGetThread extends Thread {
    private static final String TAG = "## UniformGetThread ## ";
    private UserDao userDao;
    private String obj;
    private String opt;

    public UniformGetThread(UserDao userDao, String obj, String opt) {
        this.userDao = userDao;
        this.obj = obj;
        this.opt = opt;
    }

    @Override
    public void run() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SyncData.ipAddress)
                .client(SyncData.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.uniformGet(obj, opt).enqueue(new Callback<UniformResponse>(){
            @Override
            public void onResponse(Call<UniformResponse> call, Response<UniformResponse> response) {
                if(response.body() != null) {
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
