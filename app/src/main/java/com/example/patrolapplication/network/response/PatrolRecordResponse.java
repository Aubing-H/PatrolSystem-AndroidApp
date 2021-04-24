package com.example.patrolapplication.network.response;

import com.example.patrolapplication.pojo.PatrolRecord;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatrolRecordResponse {
    @SerializedName("state")
    public int state;
    @SerializedName("msg")
    public String msg;
    @SerializedName("patrolRecord")
    public List<PatrolRecord> patrolRecord;
}
