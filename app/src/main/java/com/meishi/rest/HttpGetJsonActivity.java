package com.meishi.rest;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Aaron on 2015/6/11.
 */
public class HttpGetJsonActivity extends AbstractAsyncListActivity {

    protected static final String TAG = HttpGetJsonActivity.class.getSimpleName();

    // ***************************************
    // Activity methods
    // ***************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // when this activity starts, initiate an asynchronous HTTP GET request
        new DownloadStatesTask().execute();
    }


    // ***************************************
    // Private classes
    // ***************************************
    private class DownloadStatesTask extends AsyncTask<Void, Void, List<Object>> {

        @Override
        protected void onPreExecute() {
            showLoadingProgressDialog();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected List<Object> doInBackground(Void... params) {
            try {
                // The URL for making the GET request
                final String url = "";
                final String username = "";
                final String password = "";

                // Populate the HTTP Basic Authentitcation header with the username and password
                HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAuthorization(authHeader);
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json", StandardCharsets.UTF_8)));


                // Populate the headers in an HttpEntity object to use for the request
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Perform the HTTP GET request
                ResponseEntity<Object[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                        Object[].class);

                // convert the array to a list and return it
                return Arrays.asList(responseEntity.getBody());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            dismissProgressDialog();
            //TODO deal with result
        }

    }

}