package com.example.zsbenweather.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.zsbenweather.GetRequest_Interface;
import com.example.zsbenweather.MainActivity;
import com.example.zsbenweather.R;
import com.example.zsbenweather.WeatherActivity;
import com.example.zsbenweather.gson.WeatherJson;
import com.example.zsbenweather.util.HttpUtil;
import com.example.zsbenweather.util.Utility;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = prefs.getString("cityName",null);
        String weatherInfo = prefs.getString("weatherInfo",null);
        String degree = prefs.getString("degree",null);
        String notifyContent = "当前无天气信息";
        if(cityName!=null){
            notifyContent = cityName+" : "+weatherInfo+","+degree;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "com.example.zsbenweather";
            String channelName = "name";
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,channelName, NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            Intent intent = new Intent(this, WeatherActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
            Notification notification = new Notification.Builder(this)
                    .setChannelId(channelId)
                    .setContentTitle("实时天气信息")
                    .setContentText(notifyContent)
                    .setSmallIcon(R.drawable.ic_cloud)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pi)
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            startForeground(1,notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 1*60*60*1000;//1小时更新一次
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;//开机后1小时更新一次
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //更新天气信息,存到缓存里
    private void updateWeather(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://guolin.tech/api/")
                .build();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mWeatherId = prefs.getString("weather_Id",null);
        String weatherUrl = "weather?cityid="+ mWeatherId+ "&key=fd848bfa1c384d7499dc32e731c88f1f";
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        Call<WeatherJson> call = request.getWeatherCall(weatherUrl);
        call.enqueue(new Callback<WeatherJson>() {
            @Override
            public void onResponse(Call<WeatherJson> call, Response<WeatherJson> response) {
                WeatherJson.HeWeatherBean weatherBean = response.body().getHeWeather().get(0);
                String cityName = weatherBean.getBasic().getCity();
                String weatherInfo = weatherBean.getNow().getCond().getTxt();
                String degree = weatherBean.getNow().getTmp()+"摄氏度";
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit();
                editor.putString("cityName",cityName);
                editor.putString("weatherInfo",weatherInfo);
                editor.putString("degree",degree);
                editor.apply();
            }
            @Override
            public void onFailure(Call<WeatherJson> call, Throwable t) {
            }
        });
    }
}
