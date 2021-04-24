package com.example.patrolapplication.pojo;

import com.google.gson.annotations.SerializedName;

public class ConditionItem{
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
