package com.example.zsbenweather.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;

public class DrawableUtil {
    Context mContext;
    public DrawableUtil(Context context){
        mContext = context;
    }
    public Drawable utils(Drawable drawable){
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int draWidth = drawable.getIntrinsicWidth();
        int draHeight = drawable.getIntrinsicHeight();

        int resWidth = draWidth,resHeight = draHeight;
        if(draWidth<width && draHeight<height){
            resWidth = (int) (draWidth*2.5);
            resHeight = (int) (draHeight*2.5);
        }else if (draHeight > width || draHeight > height){
            int value = draWidth/width;
            resWidth = draWidth/value;
            resHeight = draHeight/value;
        }
        drawable.setBounds(0,0,resWidth,resHeight);
        return drawable;
    }
}
