package com.meishi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.meishi.model.Customer;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiApplication extends Application {

    private String customerId;

    private Customer customer;

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


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
