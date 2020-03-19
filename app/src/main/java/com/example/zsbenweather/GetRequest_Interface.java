package com.example.zsbenweather;

import com.example.zsbenweather.gson.WeatherJson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetRequest_Interface {
    @GET()
    Call<WeatherJson>getWeatherCall(@Url String url);

}
