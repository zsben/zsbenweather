package com.example.zsbenweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public String TAG = "zsbenn";
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    ViewPager view_Pager;
    TabLayout tab_Layout;
    SimpleDraweeView simple_DraweeView_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        fragments.add(ChooseAreaFragment.newInstance());
        titles.add("ChooseArea");
        view_Pager = findViewById(R.id.view_Pager);
        tab_Layout = findViewById(R.id.tab_Layout);

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
            @Override
            public int getCount() {
                return fragments.size();
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        };
        view_Pager.setAdapter(fragmentPagerAdapter);
        tab_Layout.setupWithViewPager(view_Pager);

        NavigationView navigationView = findViewById(R.id.person_nav);
        navigationView.setCheckedItem(R.id.person_call);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return true;
            }
        });

    }
}
