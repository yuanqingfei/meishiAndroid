package com.meishi.rest;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.meishi.model.Cook;
import com.meishi.support.Constants;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/16.
 */
public class GetCookTask extends AsyncTask<Point, Void, List<Cook>> {

    private static final String TAG = GetCookTask.class.getSimpleName();

    private SimpleAsync async;

    public GetCookTask() {
        this.async = new SimpleAsync(null);
    }

    @Override
    protected List<Cook> doInBackground(Point... params) {
        List<Cook> orderList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            RestTemplate restTemplate = async.createRestTemplate();
            HttpEntity<Object> requestEntity = async.createGetRequest();
            Distance distance = new Distance(3, Metrics.KILOMETERS);
            ResponseEntity<String> response = restTemplate.exchange(
                    Constants.FIND_COOK_URL + "location=" + URLEncoder.encode(gson.toJson(params[0]), "UTF-8") +
                            "&distance=" + URLEncoder.encode(gson.toJson(distance), "UTF-8"),
                    HttpMethod.GET, requestEntity, String.class);

            Log.i(TAG, response.getStatusCode().toString());

//            JSONObject resultJson = new JSONObject(response.getBody());
//            if (resultJson != null) {
//                JSONArray orderArray = resultJson.getJSONArray("links");
//                for (int i = 0; i < orderArray.length(); i++) {
//                    JSONObject orderInArray = (JSONObject) orderArray.get(i);
//                    if ("order".equals(orderInArray.getString("rel"))) {
//                        String orderUrl = orderInArray.getString("href");
//                        String orderId = orderUrl.substring(orderUrl.lastIndexOf("/") + 1);
//                        Log.i(TAG, orderId);
//                        ResponseEntity<Cook> orderResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, requestEntity,
//                                Cook.class);
//                        Cook cook = orderResponse.getBody();
//                        cook.setId(orderId);
//                        orderList.add(cook);
//                    }
//                }
//            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return orderList;
    }


    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请等待，正在获取后台数据...");
    }

    @Override
    protected void onPostExecute(List<Cook> orderList) {
        async.dismissProgressDialog();
    }
}
