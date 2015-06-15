package com.meishi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.meishi.model.Customer;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiApplication extends Application {


    private Customer currentCustomer;

    private String customerId;


    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
