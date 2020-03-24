package com.example.zsbenweather.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

import androidx.appcompat.widget.DrawableUtils;

import com.example.zsbenweather.gson.ContentJson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UrlImageGetter implements Html.ImageGetter {
    Context mContext;
    public UrlImageGetter(Context context){
        mContext=context;
    }
    @Override
    public Drawable getDrawable(String source) {
        try{
            //请求获取图片
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(source).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            //加载图片
            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            Drawable drawable = new BitmapDrawable(bitmap);
            //调整图片大小
            DrawableUtil drawableUtil = new DrawableUtil(mContext);
            drawable = drawableUtil.utils(drawable);
            return drawable;
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
