package com.example.patrolapplication.pojo;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AbnormalRecord {
    @SerializedName("userId")
    String userId;
    @SerializedName("patrolLocId")
    String patrolLocId;
    @SerializedName("time")
    Date time;
    @SerializedName("abnormalItem")
    String abnormalItem;
    @SerializedName("abnormalDetail")
    String abnormalDetail;
    @SerializedName("pictureLink")
    String pictureLink;

    public String getUserId() {
        return userId;
    }

    public String getPatrolLocId() {
        return patrolLocId;
    }

    public Date getTime() {
        return time;
    }

    public String getAbnormalItem() {
        return abnormalItem;
    }

    public String getAbnormalDetail() {
        return abnormalDetail;
    }

    public String getPictureLink() {
        return pictureLink;
    }
}
