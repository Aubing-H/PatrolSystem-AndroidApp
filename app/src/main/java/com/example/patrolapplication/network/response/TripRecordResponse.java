package com.example.patrolapplication.network.response;

import com.example.patrolapplication.pojo.TripRecord;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripRecordResponse {
    @SerializedName("state")
    public int state;
    @SerializedName("msg")
    public String msg;
    @SerializedName("tripRecord")
    public List<TripRecord> tripRecord;
}
