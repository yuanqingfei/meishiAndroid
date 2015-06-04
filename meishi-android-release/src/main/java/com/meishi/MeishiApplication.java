package com.meishi;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MeishiApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}
}
