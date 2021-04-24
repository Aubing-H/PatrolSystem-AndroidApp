package com.example.patrolapplication.network.controller;

import com.example.patrolapplication.network.ApiService;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.network.response.ConditionItemResponse;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConditionItemThread extends Thread {

    private static final String TAG = "## ConditionItemThread ## ";

    private UserDao userDao;

    public ConditionItemThread(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void run() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SyncData.ipAddress)
                .client(SyncData.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.queryConditionItemList().enqueue(new Callback<ConditionItemResponse>(){
            @Override
            public void onResponse(Call<ConditionItemResponse> call, Response<ConditionItemResponse> response) {
                if(response.body() != null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("msg", response.body().msg);
                    map.put("state", response.body().state);
                    String json = new Gson().toJson(response.body().conditionItem);
                    map.put("conditionItem", json);
                    System.out.println(TAG + map);
                    userDao.userOperate(map);
                }
            }

            @Override
            public void onFailure(Call<ConditionItemResponse> call, Throwable t) {
                System.out.println(TAG + t.getMessage());
                userDao.timeout();
            }
        });
    }
}
