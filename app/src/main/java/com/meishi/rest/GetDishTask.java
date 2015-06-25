package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.meishi.meishi.DishListAdapter;
import com.meishi.model.Dish;
import com.meishi.support.Constants;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/24.
 */
public class GetDishTask  extends AsyncTask<Point, Void, List<Dish>> {

    private static final String TAG = "GetDishTask";

    private SimpleAsync async;

    private DishListAdapter adapter;

    private Bundle bundle;

    public GetDishTask(Activity activity, DishListAdapter adapter, Bundle bundle){
        this.async = new SimpleAsync(activity);
        this.adapter = adapter;
        this.bundle = bundle;
    }

    @Override
    protected List<Dish> doInBackground(Point... params) {
        List<Dish> dishList = new ArrayList<>();

        try {
            RestTemplate restTemplate = async.createRestTemplate();
            HttpEntity<Object> requestEntity = async.createGetRequest();
            ResponseEntity<List<Dish>> response = restTemplate.exchange(
                    Constants.FIND_DISH_URL + "location=" + params[0].getX() + "," + params[0].getY()
                            + "&distance=" + Constants.SEARCH_SCOPE + "km", HttpMethod.GET, requestEntity,
                    new ParameterizedTypeReference<List<Dish>>() {});
            Log.d(TAG, response.getBody().toString());

            dishList.addAll(response.getBody());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return dishList;
    }

    @Override
    protected void onPostExecute(List<Dish> dishes) {
        adapter.clear();
        adapter.addAll(dishes);
        adapter.notifyDataSetChanged();
    }
}
