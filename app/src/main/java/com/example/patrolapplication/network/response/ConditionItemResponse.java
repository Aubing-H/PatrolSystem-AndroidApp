package com.example.patrolapplication.network.response;

import android.content.Context;

import com.example.patrolapplication.pojo.ConditionItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConditionItemResponse {
    @SerializedName("state")
    public int state;
    @SerializedName("msg")
    public String msg;
    @SerializedName("conditionItem")
    public List<ConditionItem> conditionItem;
}


