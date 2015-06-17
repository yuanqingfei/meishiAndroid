package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;

import com.meishi.model.Dish;
import com.meishi.support.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/17.
 */
public class GetDishTask extends AsyncTask<String, Void, List<Dish>> {

    private SimpleAsync async;

    public GetDishTask(Activity activity){
        this.async = new SimpleAsync(activity);
    }

    @Override
    protected List<Dish> doInBackground(String... params) {
        List<Dish> dishList = new ArrayList<>();

        RestTemplate client = async.createRestTemplate();
        HttpEntity<Object> request = async.createGetRequest();
        for(String dishId : params){
            ResponseEntity<Dish> result = client.exchange(Constants.DISHES_URL + "/" + dishId, HttpMethod.GET, request,
                    Dish.class);
            dishList.add(result.getBody());
        }
        return dishList;
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请等待，正在获取后台数据...");
    }

    @Override
    protected void onPostExecute(List<Dish> cooks) {
        async.dismissProgressDialog();
    }
}
