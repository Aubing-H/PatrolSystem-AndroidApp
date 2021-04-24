package com.example.patrolapplication.network.response;

import com.example.patrolapplication.pojo.User;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("state")
    public int state;
    @SerializedName("msg")
    public String msg;
    @SerializedName("user")
    public User user;
}
