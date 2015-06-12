package com.meishi.rest;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.meishi.model.Customer;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * Created by Aaron on 2015/6/12.
 */
public class PostTask extends AsyncTask<Customer, Void, HttpStatus> {

    private String TAG = PostTask.class.getSimpleName();

    private Activity activity;

    private SimpleAsync async;

    public PostTask(Activity activity) {
        this.activity = activity;
        async = new SimpleAsync(activity);
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请稍等，正在创建账户中...");
    }

    @Override
    protected HttpStatus doInBackground(Customer... params) {
        if (params.length < 1) {
            return null;
        }
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            HttpAuthentication authHeader = new HttpBasicAuthentication(SimpleAsync.REST_USER, SimpleAsync.REST_PASSWORD);
            requestHeaders.setAuthorization(authHeader);
            requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//        requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json", StandardCharsets.UTF_8)));

            HttpEntity<Customer> requestEntity = new HttpEntity<Customer>(params[0], requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            // Make the network request, posting the message, expecting a String in response from the server
            ResponseEntity<String> response = restTemplate.exchange(SimpleAsync.POST_URL, HttpMethod.POST, requestEntity,
                    String.class);

            Log.i(TAG, response.getStatusCode().toString());
            return response.getStatusCode();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(HttpStatus result) {
        async.dismissProgressDialog();
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("账户创建");
        if (HttpStatus.CREATED.equals(result)) {
            alertDialog.setMessage("恭喜，账户创建成功，您可以下单了！");
        } else {
            alertDialog.setMessage("抱歉，账户创建失败！");
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

