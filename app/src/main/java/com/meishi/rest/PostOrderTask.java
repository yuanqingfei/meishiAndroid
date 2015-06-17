package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.meishi.support.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Aaron on 2015/6/17.
 */
public class PostOrderTask extends AsyncTask<OrderRequest, Void, HttpStatus> {

    private String TAG = PostOrderTask.class.getSimpleName();

    private SimpleAsync async;

    private Activity activity;

    public PostOrderTask(Activity activity) {
        this.async = new SimpleAsync(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请稍等，正在下单中...");
    }

    @Override
    protected HttpStatus doInBackground(OrderRequest... params) {
        if (params.length < 1) {
            return HttpStatus.BAD_REQUEST;
        }

        RestTemplate client = async.createRestTemplate();
        HttpEntity<String> createPostOrderRequest = async.createPostOrderRequest(params[0]);
        ResponseEntity<String> response = client.exchange(Constants.CRATE_ORDER_URL, HttpMethod.POST,
                createPostOrderRequest, String.class);
        return response.getStatusCode();
    }

    @Override
    protected void onPostExecute(HttpStatus result) {
        async.dismissProgressDialog();
        if(HttpStatus.CREATED.equals(result)){
            Toast.makeText(activity, "下单成功", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(activity, "下单失败", Toast.LENGTH_SHORT);
        }
    }
}
