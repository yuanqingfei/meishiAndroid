package com.meishi.rest;

/**
 * Created by Aaron on 2015/6/11.
 */
public interface AsyncInterface {

    String POST_URL = "http://192.168.0.119:8080/entity/customers";
    String GET_URL = "http://192.168.0.119:8080/entity/orders";
    String REST_USER = "123456";
    String REST_PASSWORD = "111";

    public void showLoadingProgressDialog();

    public void showProgressDialog(CharSequence message);

    public void dismissProgressDialog();

}
