package com.example.patrolapplication.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TripRecord {
    @SerializedName("userId")
    String userId;
    @SerializedName("startTime")
    Date startTime;
    @SerializedName("endTime")
    Date endTime;
    @SerializedName("totalLocTime")
    int totalLocNum;
    @SerializedName("patrolNum")
    int patrolNum;
    @SerializedName("abnormalNum")
    int abnormalNum;

    public String getUserId() {
        return userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getTotalLocNum() {
        return totalLocNum;
    }

    public int getPatrolNum() {
        return patrolNum;
    }

    public int getAbnormalNum() {
        return abnormalNum;
    }
}
