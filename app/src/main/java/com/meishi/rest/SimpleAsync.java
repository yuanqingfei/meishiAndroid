package com.meishi.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.meishi.model.Customer;
import com.meishi.model.OrderRequest;
import com.meishi.support.Constants;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * Created by Aaron on 2015/6/12.
 */
public class SimpleAsync implements AsyncInterface {
    private Activity activity;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    public SimpleAsync(Activity activity) {
        this.activity = activity;

        // add attached alert diaglog

    }

    public AlertDialog getDialog(String title){
        alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        return alertDialog;
    }

    public void showLoadingProgressDialog() {
        this.showProgressDialog("请等待...");
    }

    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(activity);
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    public HttpEntity<Object> createGetRequest() {
        return new HttpEntity<Object>(getHttpHeaders(Constants.ADMIN_TEST_USER, Constants.ADMIN_TEST_PASSWORD));
    }

    public HttpEntity<Object> createGetRequest(String userName, String password) {
        return new HttpEntity<Object>(getHttpHeaders(userName, password));
    }


    public HttpEntity<Customer> createPostCustomerRequest(Customer param) {
        HttpHeaders requestHeaders = getHttpHeaders(Constants.ADMIN_TEST_USER, Constants.ADMIN_TEST_PASSWORD);
        return new HttpEntity<Customer>(param, requestHeaders);
    }

    public HttpEntity<OrderRequest> createPostOrderRequest(OrderRequest or, String userName, String password) {
        HttpHeaders requestHeaders = getHttpHeaders(userName, password);
        return new HttpEntity<OrderRequest>(or, requestHeaders);
    }

    public HttpHeaders getHttpHeaders(String userName, String password) {
        System.setProperty("http.keepAlive", "false");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = new HttpBasicAuthentication(userName, password);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return requestHeaders;
    }
}
