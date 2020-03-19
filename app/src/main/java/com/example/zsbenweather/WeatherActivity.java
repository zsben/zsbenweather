package com.example.zsbenweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zsbenweather.gson.WeatherJson;
import com.example.zsbenweather.service.AutoUpdateService;
import com.example.zsbenweather.util.Utility;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.function.Predicate;

//将数据显示到界面上
public class WeatherActivity extends AppCompatActivity {

    public static String TAG = "zsbenn";

    private ScrollView weatherLayout;   //整体滚动布局

    private TextView titleCity;         //title里的城市名字
    private TextView titleUpdateTime;   //title里的更新时间

    private TextView degreeText;        //now里的当前温度
    private TextView weatherInfoText;   //now里的当前天气

    private LinearLayout forecastLayout;//forecast里的预测

    private TextView aqiText;
    private TextView pm25Text;          //aqi里的空气质量

    private TextView comfortText;       //suggestion里的建议
    private TextView carWashText;
    private TextView sportText;

    private ImageView bingPicImg;
    SimpleDraweeView simple_DraweeView;

    public RefreshLayout refreshLayout;

    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;

    Retrofit retrofit;//请求对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);

        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);

        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);

        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);

        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);

        refreshLayout = (RefreshLayout)findViewById(R.id.refresh_layout1);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);//左侧滑动菜单
        navButton = (Button) findViewById(R.id.nav_button);
        
        //创建请求对象
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://guolin.tech/api/")
                .build();

        //加载背景图片，优先从缓存里获取
        simple_DraweeView = (SimpleDraweeView)findViewById(R.id.simple_DraweeView);
        loadBingPic();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWeatherId = prefs.getString("weather_Id",null);
        if(mWeatherId==null){
            //无缓存时去服务器查询天气*/
            mWeatherId = getIntent().getStringExtra("weather_Id");//从intent里获取weatherId
            weatherLayout.setVisibility(View.INVISIBLE);//查询时先把界面隐藏起来
        }
        requestWeather(mWeatherId);


        //上拉加载，下拉刷新
        refreshLayout.setEnableAutoLoadMore(true);//监听列表在拉到底部时触发加载事件
        refreshLayout.setEnableOverScrollBounce(true);//越界回弹
        refreshLayout.setDisableContentWhenLoading(true);//加载或刷新时不允许操作视图
        refreshLayout.setDisableContentWhenRefresh(true);
        
        //下拉刷新监听器
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestWeather(mWeatherId);
            }
        });
        //上拉加载监听器
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestWeather(mWeatherId);
            }
        });

        //加载滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //根据天气id从服务器请求城市天气信息，更新缓存，再到主线程里更新UI
    public void requestWeather(final String weatherId){
        Log.d(TAG, "requestWeather: "+weatherId);
        mWeatherId = weatherId;
        String weatherUrl = "weather?cityid="+ weatherId+ "&key=fd848bfa1c384d7499dc32e731c88f1f";
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        retrofit2.Call<WeatherJson> call = request.getWeatherCall(weatherUrl);
        call.enqueue(new retrofit2.Callback<WeatherJson>() {
            @Override
            public void onResponse(retrofit2.Call<WeatherJson> call, retrofit2.Response<WeatherJson> response) {
                Log.d(TAG, "onResponse: ok1");
                final WeatherJson.HeWeatherBean weatherBean = response.body().getHeWeather().get(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weatherBean != null && "ok".equals(weatherBean.getStatus()))  {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather_Id",weatherBean.getBasic().getCid());
                            editor.apply();
                            showWeatherInfo(weatherBean);
                            Log.d(TAG, "run: success on weather");
                            refreshLayout.finishRefresh(2000 ,true);//传入false表示刷新失败
                            refreshLayout.finishLoadMore(2000,true,false);
                        }else {
                            Log.d(TAG, "run: fail on weather");
                            refreshLayout.finishRefresh(2000 ,false);//传入false表示刷新失败
                            refreshLayout.finishLoadMore(2000,false,false);
                        }
                    }
                });
            }
            @Override
            public void onFailure(retrofit2.Call<WeatherJson> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: fail on httputil");
                        refreshLayout.finishRefresh(2000 ,false);//传入false表示刷新失败
                        refreshLayout.finishLoadMore(2000,false,false);
                    }
                });
            }
        });

        loadBingPic();//每次请求天气信息时也会刷新背景图片
    }

    //处理并展示Weather实体类中的数据，同时启动后台更新服务
    private void showWeatherInfo(WeatherJson.HeWeatherBean weather){
        if(weather != null && "ok".equals(weather.getStatus())) {
            //展示前两个布局title和now
            String cityName = weather.getBasic().getCity();
            String uptimeTime = weather.getBasic().getUpdate().getLoc().split(" ")[1];//第一个是日期，第二个是时间
            String degree = weather.getNow().getTmp() + "摄氏度";
            String weatherInfo = weather.getNow().getCond().getTxt();
            titleCity.setText(cityName);
            titleUpdateTime.setText(uptimeTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);

            //展示forecast布局，其实是更新里面的LinearLayout
            forecastLayout.removeAllViews();
            for (WeatherJson.HeWeatherBean.DailyForecastBean forecast:weather.getDaily_forecast()) {
                //用一个view加载一个forecast_item布局
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                SimpleDraweeView simpleDraweeView = (SimpleDraweeView)view.findViewById(R.id.item_simple_DraweeView);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                if("晴".equals(forecast.getCond().getTxt_d())) {
                    simpleDraweeView.setImageResource(R.drawable.ic_sun);
                    //Toast.makeText(this,"ok",Toast.LENGTH_LONG).show();
                }
                if(forecast.getCond().getTxt_d().matches(".*雨.*")){
                    simpleDraweeView.setImageResource(R.drawable.ic_rain);
                }
                if(forecast.getCond().getTxt_d().matches(".*云.*")){
                    simpleDraweeView.setImageResource(R.drawable.ic_cloud);
                }
                if(forecast.getCond().getTxt_d().matches(".*阴.*")){
                    simpleDraweeView.setImageResource(R.drawable.ic_cloud);
                }
                if(forecast.getCond().getTxt_d().matches(".*雷.*")){
                    simpleDraweeView.setImageResource(R.drawable.ic_thunder);
                }
                if(forecast.getCond().getTxt_d().matches(".*雪.*")){
                    simpleDraweeView.setImageResource(R.drawable.ic_snow);
                }
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.getDate());
                infoText.setText(forecast.getCond().getTxt_d());
                maxText.setText(forecast.getTmp().getMax());
                minText.setText(forecast.getTmp().getMin());
                forecastLayout.addView(view);
            }

            //展示aqi布局
            if (weather.getAqi() != null) {
                aqiText.setText(weather.getAqi().getCity().getAqi());
                pm25Text.setText(weather.getAqi().getCity().getPm25());
            }

            //展示suggestion布局
            String comfort = "舒适度: " + weather.getSuggestion().getComf().getTxt();
            String carWash = "洗车指数: " + weather.getSuggestion().getCw().getTxt();
            String sport = "运动建议: " + weather.getSuggestion().getSport().getTxt();
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);

            weatherLayout.setVisibility(View.VISIBLE);

            // 启动自动更新服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    //从服务器获取bing每日一图，放到缓存里，然后切换到主线程里更新UI
    public void loadBingPic(){
        simple_DraweeView.setImageURI("https://uploadbeta.com/api/pictures/random/?key=BingEverydayWallpaperPicture");
    }
}
