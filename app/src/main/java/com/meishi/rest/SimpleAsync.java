package com.meishi.rest;

import android.app.Activity;
import android.app.ProgressDialog;

import com.meishi.MeishiApplication;
import com.meishi.model.Customer;
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

    public SimpleAsync(Activity activity) {
        this.activity = activity;
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
        HttpHeaders requestHeaders = new HttpHeaders();
        String currentClientId = ((MeishiApplication)activity.getApplication()).getCustomerId();
        String loginUser = currentClientId == null ? Constants.ADMIN_TEST_USER : currentClientId;
        HttpAuthentication authHeader = new HttpBasicAuthentication(loginUser, Constants.ADMIN_TEST_PASSWORD);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new HttpEntity<Object>(requestHeaders);
    }


    public HttpEntity<Customer> createPostCustomerRequest(Customer param) {
        System.setProperty("http.keepAlive", "false");
        HttpHeaders requestHeaders = new HttpHeaders();
//            requestHeaders.set("Connection", "Close");
        HttpAuthentication authHeader = new HttpBasicAuthentication(Constants.ADMIN_TEST_USER, Constants.ADMIN_TEST_PASSWORD);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new HttpEntity<Customer>(param, requestHeaders);
    }

    public HttpEntity<String> createPostOrderRequest(OrderRequest or) {
        System.setProperty("http.keepAlive", "false");
        HttpHeaders requestHeaders = new HttpHeaders();
//            requestHeaders.set("Connection", "Close");
        HttpAuthentication authHeader = new HttpBasicAuthentication(Constants.ADMIN_TEST_USER, Constants.ADMIN_TEST_PASSWORD);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        String request = "{\"meishiList\" : \"" + or.getDish().getName() + "\", \"clientLocation\" : \"\", " +
                "\"clientId\" : \"" + or.getClientId() + "\"}";
        return new HttpEntity<String>(request, requestHeaders);
    }
}
