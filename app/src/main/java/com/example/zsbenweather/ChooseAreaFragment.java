package com.example.zsbenweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.zsbenweather.db.City;
import com.example.zsbenweather.db.Country;
import com.example.zsbenweather.db.Province;
import com.example.zsbenweather.util.HttpUtil;
import com.example.zsbenweather.util.Utility;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {

        private List<String> mAreaList;
        class ViewHolder extends RecyclerView.ViewHolder{
            View areaView;
            TextView areaName;
            ImageView blackline;
            public ViewHolder(View view){
                super(view);
                areaView = view;
                areaName = (TextView) view.findViewById(R.id.area_name);
                blackline = (ImageView) view.findViewById(R.id.black_line1);
            }
        }

        public AreaAdapter(List<String> list){
            mAreaList = list;
        }

        @NonNull
        @Override
        public AreaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item,parent,false);
            final ViewHolder holder = new ViewHolder(view);
            holder.areaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if(currentLevel == LEVEL_PROVINCE){ //选择一个省,需要查询省里的所有市，更新到列表上
                        selectedProvince = provinceList.get(position);
                        queryCities();
                    } else if(currentLevel == LEVEL_CITY){//查询一个市，需要将市里所有县更新到列表上
                        selectedCity = cityList.get(position);
                        queryCountries();
                    } else if(currentLevel == LEVEL_COUNTRY){//查询一个县，启动WeatherActivity
                        String weatherId = countryList.get(position).getWeatherId();
                        if(getActivity() instanceof MainActivity){//碎片在mainactivity里，说明要启动显示天气的活动
                            Intent intent = new Intent(getActivity(), WeatherActivity.class);
                            intent.putExtra("weather_Id",weatherId);
                            startActivity(intent);
                            getActivity().finish();
                        }else if(getActivity() instanceof  WeatherActivity){//如果碎片是在weatheractivity里启动的，说明是手动更新天气
                            WeatherActivity activity = (WeatherActivity)getActivity();//先得到weatheractivity活动实例
                            activity.drawerLayout.closeDrawers();  //把滑动菜单关闭
                            activity.requestWeather(weatherId); //加载新的天气
                        }

                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String area = mAreaList.get(position);
            holder.areaName.setText(area);
        }

        @Override
        public int getItemCount() {
            return mAreaList.size();
        }
    }


    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    public static final String TAG = "zsbenn";


    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private List<String> dataList = new ArrayList<>();

    RecyclerView recyclerView;
    AreaAdapter areaAdapter;

    RefreshLayout refreshLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<Country> countryList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    //碎片创建视图
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        areaAdapter = new AreaAdapter(dataList);
        recyclerView.setAdapter(areaAdapter);

        //设置下拉刷新
        refreshLayout = (RefreshLayout)view.findViewById(R.id.refresh_layout);
        refreshLayout.setEnableAutoLoadMore(true);//监听列表在拉到底部时触发加载事件
        refreshLayout.setEnableOverScrollBounce(true);//越界回弹
        refreshLayout.setDisableContentWhenLoading(true);//加载或刷新时不允许操作视图
        refreshLayout.setDisableContentWhenRefresh(true);

        return view;
    }

    //碎片和活动绑定
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {//向上退一级
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+String.valueOf(currentLevel));
                if(currentLevel == LEVEL_COUNTRY){
                    Log.d(TAG, "onClick: "+"backcity");
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    Log.d(TAG, "onClick: "+"backpro");
                    queryProvinces();
                }
            }
        });

        queryProvinces();//碎片的初始状态是所有省的列表
    }

    //查询全国所有省，优先从数据库查，查不到从服务器查
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//设置标题栏，隐藏返回按钮

        provinceList = LitePal.findAll(Province.class);//从数据库里查
        if(provinceList.size()>0){
            dataList.clear();
            for(int i=0;i<provinceList.size();i++) {
                dataList.add(provinceList.get(i).getProvinceName());
            }
            areaAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;//更新当前目录级别
        }else { //从服务器查
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询全省所有市
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = LitePal.where("provinceid = ?",String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(int i=0;i<cityList.size();i++)
                dataList.add(cityList.get(i).getCityName());
            areaAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    //查询全市所有县
    private void queryCountries(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);

        countryList = LitePal.where("cityId = ?",String.valueOf(selectedCity.getId()))
                .find(Country.class);
        if(countryList.size()>0){
            dataList.clear();
            for(int i=0;i<countryList.size();i++)
                    dataList.add(countryList.get(i).getCountryName());
           areaAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTRY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            Log.d(TAG, "queryCountries: "+String.valueOf(cityCode));
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }

    }

    //根据传入的地址和查询类型，从服务器上查询省市县数据,存进数据库里，然后再到数据库里去访问
    private void queryFromServer(String address,final String type){
        Log.d("zsbenn","queryFromServer1");
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {//开启子线程请求数据，传进去一个Callback对象用来监听回掉
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {//回到主线程处理逻辑
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//把请求结果存到数据库里
                String responText = response.body().string();    //得到请求结果
                Log.d("zsbenn",responText);
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responText);
                } else if("city".equals(type)){
                    result = Utility.handleCityResponse(responText,selectedProvince.getId());
                } else if("country".equals(type)){
                    Log.d(TAG, "onResponse: "+selectedCity.getCityCode());
                    result = Utility.handleCountryResponse(responText,selectedCity.getId());
                }

                if(result){//处理成功，再回到数据库里访问对应数据并更新UI
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            } else if("city".equals(type)){
                                queryCities();
                            } else if("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }
        });
    }

    //显示进度条对话框
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度条对话框
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
