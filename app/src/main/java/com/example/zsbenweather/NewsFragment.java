package com.example.zsbenweather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zsbenweather.gson.NewsJson;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFragment extends Fragment {

    public static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
        private Context mContext;
        private List<NewsJson.StoriesBean> storiesBeans;

        class ViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            SimpleDraweeView newsImage;
            TextView newsName;

            public ViewHolder(View view){
                super(view);
                cardView = (CardView)view;
                newsImage = (SimpleDraweeView)view.findViewById(R.id.news_image);
                newsName = (TextView)view.findViewById(R.id.news_name);

            }
        }

        public NewsAdapter(List<NewsJson.StoriesBean> storiesBeans){
            this.storiesBeans = storiesBeans;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(mContext == null){
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item,parent,false);
            Log.d(TAG, "onCreateViewHolder: ok");
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    NewsJson.StoriesBean storiesBean = storiesBeans.get(position);
                    Intent intent = new Intent(mContext,NewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("story",storiesBean);
                    intent.putExtras(bundle);

                    mContext.startActivity(intent);
                }
            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NewsJson.StoriesBean storiesBean = storiesBeans.get(position);
            holder.newsName.setText(storiesBean.getTitle());
            holder.newsImage.setImageURI(storiesBean.getImages().get(0));
            Log.d(TAG, "onBindViewHolder: ok1");
        }

        @Override
        public int getItemCount() {
            return storiesBeans.size();
        }
    }

    public static String TAG = "zsbenn";
    NewsAdapter adapter;
    RecyclerView recyclerView;
    Retrofit retrofit;
    List<NewsJson.StoriesBean> storiesBeans = new ArrayList<>();
    RefreshLayout refreshLayout;

    public static NewsFragment newInstance(){
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment,container,false);
        setHasOptionsMenu(true);
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("近期新闻");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        recyclerView = (RecyclerView)view.findViewById(R.id.news_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(storiesBeans);
        recyclerView.setAdapter(adapter);

        refreshLayout = (RefreshLayout)view.findViewById(R.id.news_refresh);

        Log.d(TAG, "onCreateView: NewsFragment"+recyclerView.getAdapter());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestNews();

        refreshLayout.setEnableAutoLoadMore(true);
        refreshLayout.setEnableOverScrollBounce(true);
        refreshLayout.setDisableContentWhenLoading(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestNews();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestNews();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.close:
                Toast.makeText(getContext(),"backup",Toast.LENGTH_LONG).show();
                break;
            case R.id.homeAsUp:
                break;
            case R.id.refresh:
                Toast.makeText(getContext(),"正在刷新新闻",Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    private void requestNews(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://news-at.zhihu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        Call<NewsJson> call = request.getNewsCall("api/4/news/latest");
        Log.d(TAG, "onActivityCreated: Newsfragment");
        call.enqueue(new Callback<NewsJson>() {
            @Override
            public void onResponse(Call<NewsJson> call, Response<NewsJson> response) {
                storiesBeans = response.body().getStories();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new NewsAdapter(storiesBeans);
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(storiesBeans.size()-1);
                        refreshLayout.finishRefresh(1000 ,true);//传入false表示刷新失败
                        refreshLayout.finishLoadMore(1000,true,false);
                    }
                });
                Log.d(TAG, "onResponse: "+storiesBeans.get(0).getUrl());
            }
            @Override
            public void onFailure(Call<NewsJson> call, Throwable t) {
                Log.d(TAG, "onFailure: ok");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh(2000 ,false);//传入false表示刷新失败
                        refreshLayout.finishLoadMore(2000,false,false);
                    }
                });
            }
        });
    }
}
