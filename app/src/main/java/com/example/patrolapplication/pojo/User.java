package com.example.patrolapplication.pojo;

import com.google.gson.annotations.SerializedName;

public class User {
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_PATROL = 1;

    @SerializedName("id")
    String id;
    @SerializedName("name")
    String name;
    @SerializedName("password")
    String password;
    @SerializedName("type")
    int type;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getType() {
        return type;
    }
}
