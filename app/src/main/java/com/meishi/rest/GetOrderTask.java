package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.meishi.core.order.OrderListAdapter;
import com.meishi.model.Order;
import com.meishi.support.Constants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/12.
 */
public class GetOrderTask extends AsyncTask<String, Void, List<Order>> {

    private String TAG = GetOrderTask.class.getSimpleName();

    private SimpleAsync async;

    private OrderListAdapter listAdapter;

    public GetOrderTask(Activity activity, OrderListAdapter listAdapter) {
        this.async = new SimpleAsync(activity);
        this.listAdapter = listAdapter;
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请等待，正在获取订单数据...");
    }

    @Override
    protected List<Order> doInBackground(String... params) {
        List<Order> orderList = new ArrayList<>();
        try {
            RestTemplate restTemplate = async.createRestTemplate();

            HttpEntity<Object> requestEntity = async.createGetRequest();
            ResponseEntity<String> response = restTemplate.exchange(Constants.FIND_ORDER_URL + params[0], HttpMethod.GET, requestEntity,
                    String.class);

            Log.d(TAG, response.getStatusCode().toString());

            JSONObject resultJson = new JSONObject(response.getBody());
            if (resultJson != null) {
                JSONArray orderArray = resultJson.getJSONArray("links");
                for (int i = 0; i < orderArray.length(); i++) {
                    JSONObject orderInArray = (JSONObject) orderArray.get(i);
                    if ("order".equals(orderInArray.getString("rel"))) {
                        String orderUrl = orderInArray.getString("href");
                        String orderId = orderUrl.substring(orderUrl.lastIndexOf("/") + 1);
                        Log.i(TAG, orderId);
                        ResponseEntity<Order> orderResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, requestEntity,
                                Order.class);
                        Order order = orderResponse.getBody();
                        order.setId(orderId);
                        orderList.add(order);
                    }
                }
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return orderList;
    }

    @Override
    protected void onPostExecute(List<Order> orderList) {
        async.dismissProgressDialog();
        listAdapter.addAll(orderList);
        listAdapter.notifyDataSetChanged();
    }


}
