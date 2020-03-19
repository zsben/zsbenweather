package com.example.tablayouttest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MyFragment extends Fragment {

    private TextView text_View;
    private String value;
    private String TAG = "zsbenn";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container,false);
        text_View = (TextView)view.findViewById(R.id.text_View);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        if(value!=null){
            text_View.setText(value);
        }
        Log.d(TAG, "onActivityCreated: "+value);
    }

    public static MyFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("myFragment",data);
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getData(){
        Bundle bundle = getArguments();
        if(bundle != null){
            value = bundle.getString("myFragment");
        }
    }
}
