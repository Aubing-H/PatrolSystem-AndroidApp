package com.example.patrolapplication.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PatrolRecord {
    @SerializedName("userId")
    String userId;
    @SerializedName("patrolLocId")
    String patrolLocId;
    @SerializedName("time")
    Date time;
    @SerializedName("condition")
    int condition;

    public String getUserId() {
        return userId;
    }

    public String getPatrolLocId() {
        return patrolLocId;
    }

    public Date getTime() {
        return time;
    }

    public int getCondition() {
        return condition;
    }
}
