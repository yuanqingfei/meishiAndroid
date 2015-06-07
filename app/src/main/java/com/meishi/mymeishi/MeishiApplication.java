package com.meishi.mymeishi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
