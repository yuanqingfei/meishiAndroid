package com.meishi.rest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.core.cook.CookActivity;
import com.meishi.login.LoginActivity;
import com.meishi.model.Cook;
import com.meishi.support.Constants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/16.
 */
public class GetCookTask extends AsyncTask<Point, Void, List<Cook>> {

    private static final String TAG = GetCookTask.class.getSimpleName();

    private SimpleAsync async;
    private BaiduMap baiduMap;
    private Activity activity;


    public GetCookTask(Activity activity, BaiduMap baiduMap) {
        this.activity = activity;
        this.async = new SimpleAsync(activity);
        this.baiduMap = baiduMap;
    }

    @Override
    protected List<Cook> doInBackground(Point... params) {
        List<Cook> cookList = new ArrayList<>();
        try {
            RestTemplate restTemplate = async.createRestTemplate();
            HttpEntity<Object> requestEntity = async.createGetRequest();
            Distance distance = new Distance(3, Metrics.KILOMETERS);
            ResponseEntity<String> response = restTemplate.exchange(
                    Constants.FIND_COOK_URL + "location=" + params[0].getX()+","+params[0].getY()
                            + "&distance=" + Constants.SEARCH_SCOPE, HttpMethod.GET, requestEntity, String.class);

            Log.d(TAG, response.getBody());

            JSONObject resultJson = new JSONObject(response.getBody());
            if (resultJson != null) {
                JSONArray orderArray = resultJson.getJSONArray("links");
                for (int i = 0; i < orderArray.length(); i++) {
                    JSONObject orderInArray = (JSONObject) orderArray.get(i);
                    if ("cook".equals(orderInArray.getString("rel"))) {
                        String orderUrl = orderInArray.getString("href");
                        String orderId = orderUrl.substring(orderUrl.lastIndexOf("/") + 1);
                        Log.d(TAG, orderId);
                        ResponseEntity<Cook> orderResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, requestEntity,
                                Cook.class);
                        Cook cook = orderResponse.getBody();
                        cook.setId(orderId);
                        cookList.add(cook);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return cookList;
    }

    @Override
    protected void onPostExecute(List<Cook> cooks) {
        for (final Cook cook : cooks) {
            double[] location = cook.getLocation();
            LatLng baiduLoc = new LatLng(location[1], location[0]);
            TextView info = new TextView(activity.getApplicationContext());
            info.setBackgroundResource(R.drawable.popup);
            info.setPadding(30, 20, 30, 50);
            info.setText(cook.getName());
            info.setTextColor(Color.RED);
            BitmapDescriptor bd = BitmapDescriptorFactory.fromView(info);
            OverlayOptions markerOO = new MarkerOptions().position(baiduLoc).icon(bd).zIndex(5)
                    .draggable(false).title(cook.getName());
            final Marker marker = (Marker) (baiduMap.addOverlay(markerOO));
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.COOK_BUNDLE_ID, cook);
            marker.setExtraInfo(bundle);
        }

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                String customerId = ((MeishiApplication)activity.getApplication()).getCustomerId();
                if(customerId == null){
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, CookActivity.class);
                    intent.putExtra(Constants.COOK_BUNDLE_ID, (Cook) marker.getExtraInfo().getSerializable(Constants.COOK_BUNDLE_ID));
                    activity.startActivity(intent);
                }
                return true;
            }
        });
    }
}
