package com.meishi.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.meishi.MeishiApplication;
import com.meishi.core.order.OrderListAdapter;
import com.meishi.model.Customer;
import com.meishi.model.Order;
import com.meishi.support.Constants;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aaron on 2015/6/12.
 */
public class GetOrderTask extends AsyncTask<String, Void, List<Order>> {

    private String TAG = GetOrderTask.class.getSimpleName();

    private SimpleAsync async;

    private OrderListAdapter listAdapter;

    private Activity activity;

    public GetOrderTask(Activity activity, OrderListAdapter listAdapter) {
        this.activity = activity;
        this.async = new SimpleAsync(activity);
        this.listAdapter = listAdapter;
    }

//    @Override
//    protected void onPreExecute() {
//        async.showProgressDialog("请等待，正在获取订单数据...");
//    }

    @Override
    protected List<Order> doInBackground(String... params) {
        List<Order> orderList = new ArrayList<>();
        try {
            RestTemplate restTemplate = async.createRestTemplate();

            Customer customer = ((MeishiApplication) activity.getApplication()).getCustomer();


//            restTemplate.
//            Order[] orders = restTemplate.getForObject(Constants.FIND_ORDER_URL + params[0], Order[].class);

//            + params[0] search/findByClientId

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("clientId", params[0]);

            Traverson traverson = new Traverson(new URI(Constants.ORDERS_URL ), MediaTypes.HAL_JSON);
            traverson.setRestOperations(restTemplate);

            ResponseEntity<OrderResponse> responseEntity = traverson.follow("search", "findByCustomer_Identity")
                    .withHeaders(async.getHttpHeaders(customer.getIdentity(), customer.getPassword()))
                    .withTemplateParameters(parameters).toEntity(OrderResponse.class);

            Log.i(TAG, responseEntity.getBody().getOrders() + "xxxxx");
            orderList.addAll(responseEntity.getBody().getOrders());


//            String name = traverson.follow("links", "movie", "actor").
//                    withTemplateParameters(parameters).
//                    toObject("$.name");



//            HttpEntity<Object> requestEntity = async.createGetRequest();
//            ResponseEntity<Order[]> response = restTemplate.exchange(
//                    Constants.FIND_ORDER_URL + params[0] + "&sort=orderTime&orderTime.dir=desc",
//                    HttpMethod.GET, requestEntity,
//                    Order[].class);
//
//            Log.i(TAG, response.getBody().toString());



//
//            Log.d(TAG, response.getStatusCode().toString());
//
//            JSONObject resultJson = new JSONObject(response.getBody());
//            if (resultJson != null) {
//                JSONArray orderArray = resultJson.getJSONArray("links");
//                for (int i = 0; i < orderArray.length(); i++) {
//                    JSONObject orderInArray = (JSONObject) orderArray.get(i);
//                    if ("order".equals(orderInArray.getString("rel"))) {
//                        String orderUrl = orderInArray.getString("href");
//                        String orderId = orderUrl.substring(orderUrl.lastIndexOf("/") + 1);
//                        Log.i(TAG, orderId);
//                        ResponseEntity<Order> orderResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, requestEntity,
//                                Order.class);
//                        Order order = orderResponse.getBody();
//                        order.setId(orderId);
//                        orderList.add(order);
//                    }
//                }
//            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return orderList;
    }

    @Override
    protected void onPostExecute(List<Order> orderList) {
//        async.dismissProgressDialog();
        listAdapter.addAll(orderList);
        listAdapter.notifyDataSetChanged();
    }

    private static class OrderResponse{

        public OrderResponse(){
        }


        private List<Order> orders;

        public List<Order> getOrders() {
            return orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }
    }

}
