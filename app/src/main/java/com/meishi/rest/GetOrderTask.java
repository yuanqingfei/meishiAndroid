package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.meishi.core.order.OrderListAdapter;
import com.meishi.model.Order;
import com.meishi.support.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/12.
 */
public class GetOrderTask extends AsyncTask<Void, Void, List<Order>> {

    private String TAG = GetOrderTask.class.getSimpleName();

    private Activity activity;

    private SimpleAsync async;

    private OrderListAdapter listAdapter;

    private String customerId;

    public GetOrderTask(Activity activity, OrderListAdapter listAdapter, String customerId) {
        this.activity = activity;
        this.async = new SimpleAsync(activity);
        this.listAdapter = listAdapter;
        this.customerId = customerId;
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请等待，正在获取后台数据...");
    }

    @Override
    protected List<Order> doInBackground(Void... params) {
        List<Order> orderList = new ArrayList<>();
        try {
            RestTemplate restTemplate = createRestTemplate();

            HttpEntity<Object> requestEntity = createGetRequest();
            ResponseEntity<String> response = restTemplate.exchange(Constants.FIND_ORDER_URL + customerId, HttpMethod.GET, requestEntity,
                    String.class);

            Log.i(TAG, response.getStatusCode().toString());

            String result = response.getBody();

            JSONObject resultJson = null;
            try {
                resultJson = new JSONObject(result);
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

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return orderList;
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    private HttpEntity<Object> createGetRequest() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = new HttpBasicAuthentication(Constants.ADMIN_TEST_USER, Constants.ADMIN_TEST_PASSWORD);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new HttpEntity<Object>(requestHeaders);
    }

    @Override
    protected void onPostExecute(List<Order> orderList) {
        async.dismissProgressDialog();
        for(Order order : orderList){
           listAdapter.add(order);
        }
        listAdapter.notifyDataSetChanged();
    }


}
