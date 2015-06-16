package com.meishi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiApplication extends Application {

    private String customerId;


    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
