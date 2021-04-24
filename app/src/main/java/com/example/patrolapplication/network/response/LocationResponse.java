package com.example.patrolapplication.network.response;

import com.example.patrolapplication.pojo.Location;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationResponse {
    @SerializedName("state")
    public int state;
    @SerializedName("msg")
    public String msg;
    @SerializedName("location")
    public List<Location> location;
}
