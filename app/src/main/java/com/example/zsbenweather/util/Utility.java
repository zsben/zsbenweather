package com.example.zsbenweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.zsbenweather.db.City;
import com.example.zsbenweather.db.Country;
import com.example.zsbenweather.db.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {
    public static final String TAG = "zsbenn";
    /*
    * 解析处理服务器返回的省级数据
    * */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handleCountryResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCountries = new JSONArray(response);
                for(int i=0;i<allCountries.length();i++){
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(countryObject.getString("name"));
                    country.setCityId(cityId);
                    country.setWeatherId(countryObject.getString("weather_id"));
                    Log.d(TAG, "handleCountryResponse: "+country.toString());
                    country.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
