package com.example.tablayouttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Environment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    TabLayout tab_Layout;
    ViewPager view_Pager;
    SimpleDraweeView simple_draweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        simple_draweeView = findViewById(R.id.simple_draweeView);
        simple_draweeView.setImageURI("https://img.ivsky.com/img/tupian/pre/201806/20/dengta-003.jpg");
        simple_draweeView.setImageURI("res://com.example.tablelayout/"+R.drawable.person_icon);

        tab_Layout = (TabLayout)findViewById(R.id.tab_Layout);
        view_Pager = (ViewPager)findViewById(R.id.view_Pager);

        for(int i=0;i<4;i++){
            titles.add("title"+i);
            fragments.add(MyFragment.newInstance("Fragment"+i));
        }

        initViewPager();

    }

    private void initViewPager(){//初始化FragmentAdapter,设置viewpager和tablayout
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(
                getSupportFragmentManager(),fragments,titles
        );
        view_Pager.setAdapter(myFragmentAdapter);
        tab_Layout.setupWithViewPager(view_Pager);
    }
}
