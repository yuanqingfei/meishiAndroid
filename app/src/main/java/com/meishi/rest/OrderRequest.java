package com.meishi.rest;

import com.meishi.model.Dish;

/**
 * Created by Aaron on 2015/6/17.
 */
public class OrderRequest {

    private Dish dish;

    private double[] location;

    private String clientId;

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
