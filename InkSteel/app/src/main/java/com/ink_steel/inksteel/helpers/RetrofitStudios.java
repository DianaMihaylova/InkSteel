package com.ink_steel.inksteel.helpers;

import com.ink_steel.inksteel.model.StudiosResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitStudios {

    String API_KEY = "&key=AIzaSyACnb8t5MxO5woaX7nQpS3qMowHnSZznno";

    @GET("api/place/textsearch/json?query=tattoo" + API_KEY)
    Call<StudiosResponse> getNearbyStudios(@Query("location") String location);

    @GET("api/place/textsearch/json?" + API_KEY)
    Call<StudiosResponse> getStudiosByCity(@Query("query") String city);

    @GET("api/place/photo?maxwidth=400" + API_KEY)
    Call<ResponseBody> getStudioPhoto(@Query("photoreference") String reference);

}
