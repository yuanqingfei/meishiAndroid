package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.meishi.core.order.OrderListAdapter;
import com.meishi.model.Order;

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
public class GetTask extends AsyncTask<Void, Void, List<Order>> {

    private String TAG = GetTask.class.getSimpleName();

    private Activity activity;

    private SimpleAsync async;

    private List<Order> orderList = new ArrayList<>();

    private OrderListAdapter listAdapter;

    public GetTask(Activity activity, OrderListAdapter listAdapter) {
        this.activity = activity;
        async = new SimpleAsync(activity);
        this.listAdapter = listAdapter;
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请等待，正在获取后台数据...");
    }

    @Override
    protected List<Order> doInBackground(Void... params) {
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            HttpAuthentication authHeader = new HttpBasicAuthentication(SimpleAsync.REST_USER, SimpleAsync.REST_PASSWORD);
            requestHeaders.setAuthorization(authHeader);
            requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//        requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json", StandardCharsets.UTF_8)));

            HttpEntity<Order> requestEntity = new HttpEntity<Order>(null, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            // Make the network request, posting the message, expecting a String in response from the server
            ResponseEntity<String> response = restTemplate.exchange(SimpleAsync.GET_URL, HttpMethod.GET, requestEntity,
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

            return orderList;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Order> orderList) {
        async.dismissProgressDialog();
        listAdapter.addAll(orderList);
        listAdapter.notifyDataSetChanged();
    }


}
