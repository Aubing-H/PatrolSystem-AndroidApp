package com.example.patrolapplication.pojo;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    // 重写hashcode方法，判断地点集合中是否存在地点重复
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Location)
            return id.equals(((Location) obj).getId());
        return false;
    }
}
