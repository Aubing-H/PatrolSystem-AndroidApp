package com.example.patrolapplication.network;

import com.example.patrolapplication.network.response.ConditionItemResponse;
import com.example.patrolapplication.network.response.LocationResponse;
import com.example.patrolapplication.network.response.PatrolRecordResponse;
import com.example.patrolapplication.network.response.UniformResponse;
import com.example.patrolapplication.network.response.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("user/{opt}")
    Call<UserResponse> operate(@Body RequestBody body, @Path("opt") String opt);

    @POST("location/{opt}")
    Call<LocationResponse> locationOperate(@Body RequestBody body, @Path("opt") String opt);

    @GET("location/queryList")
    Call<LocationResponse> queryLocationList();

    @GET("conditionItem/queryList")
    Call<ConditionItemResponse> queryConditionItemList();

    @POST("patrolRecord/{opt}")
    Call<PatrolRecordResponse> patrolRecordOperate(@Body RequestBody body, @Path("opt") String opt);

    @GET("{obj}/{opt}")
    Call<UniformResponse> uniformGet(@Path("obj") String obj, @Path("opt") String opt);

    @POST("{obj}/{opt}")
    Call<UniformResponse> uniformOperate(@Body RequestBody body, @Path("obj") String obj,
                                         @Path("opt") String opt);

    @Multipart
    @POST("uploadPhoto")
    Call<UniformResponse> uploadPhoto( @Part("text") String text, @Part MultipartBody.Part file);
}
