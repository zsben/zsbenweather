package com.example.zsbenweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.example.zsbenweather.gson.NewsJson;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class NewsActivity extends AppCompatActivity {

    SimpleDraweeView simpleDraweeView;
    NewsJson.StoriesBean storiesBean;
    Toolbar toolbar;

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
        toolbar.setTitle("近期新闻");
    }


    private void initview(){
        simpleDraweeView = findViewById(R.id.news_title_image);

        toolbar = findViewById(R.id.news_toolbar);

    }
}
