package com.example.zsbenweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.example.zsbenweather.gson.ContentJson;
import com.example.zsbenweather.gson.NewsJson;
import com.example.zsbenweather.util.HttpUtil;
import com.example.zsbenweather.util.UrlImageGetter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {

    SimpleDraweeView simpleDraweeView;
    NewsJson.StoriesBean storiesBean;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView newsTitleText;
    TextView newsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_news);
        initview();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        storiesBean = (NewsJson.StoriesBean) bundle.getSerializable("story");
        if(storiesBean!=null)
            simpleDraweeView.setImageURI(storiesBean.getImages().get(0));

        newsTitleText.setText(storiesBean.getTitle());
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                Log.d("zsbenn", "onOffsetChanged: "+i+" "+collapsingToolbarLayout.getHeight());
                if(i <= -collapsingToolbarLayout.getHeight()*3/4){
                    Log.d("zsbenn", "onOffsetChanged: ok");
                    collapsingToolbarLayout.setTitle("近期新闻");
                }else{
                    collapsingToolbarLayout.setTitle("");
                }
            }
        });

        String url = "http://news-at.zhihu.com/api/4/news/"+storiesBean.getId();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                Gson gson = new Gson();
                final ContentJson content = gson.fromJson(string,ContentJson.class);
                final Spanned spanned = Html.fromHtml(content.getBody(),new UrlImageGetter(NewsActivity.this),null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsText.setText(spanned);
                    }
                });
            }
        });
    }

    private void initview(){
        simpleDraweeView = findViewById(R.id.news_title_image);
        toolbar = findViewById(R.id.news_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout = findViewById(R.id.collapseAction_toolbar);
        appBarLayout = findViewById(R.id.appbar);
        newsTitleText = findViewById(R.id.news_title_text);
        newsText = findViewById(R.id.news_text);
    }
}
