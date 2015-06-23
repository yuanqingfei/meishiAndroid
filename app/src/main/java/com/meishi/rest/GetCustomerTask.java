package com.meishi.rest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.meishi.MeishiActivity;
import com.meishi.model.Customer;
import com.meishi.support.Constants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/19.
 */
public class GetCustomerTask extends AsyncTask<Void, Void, List<Customer>> {

    private static final String TAG = GetCustomerTask.class.getSimpleName();

    private final String identity;
    private final String mPassword;
    private SimpleAsync async;
    private EditText passwordView;


    private Activity activity;

    public GetCustomerTask(Activity activity, EditText passwordView, String identity, String password) {
        this.activity = activity;
        this.passwordView = passwordView;
        async = new SimpleAsync(activity);
        this.identity = identity;
        this.mPassword = password;
    }

    @Override
    protected List<Customer> doInBackground(Void... params) {
        HttpEntity<Object> requestEntity = async.createGetRequest(identity, mPassword);
        RestTemplate client = createRestTemplate();

        List<Customer> customers = new ArrayList<>();
        ResponseEntity<String> response = null;

        try {
            response = client.exchange(Constants.FIND_CUSTOMER_URL + identity, HttpMethod.GET, requestEntity,
                    String.class);

            Log.d(TAG, response.getBody().toString());
            JSONObject resultJson = new JSONObject(response.getBody());
            if (resultJson != null) {
                JSONArray orderArray = resultJson.getJSONArray("links");
                for (int i = 0; i < orderArray.length(); i++) {
                    JSONObject orderInArray = (JSONObject) orderArray.get(i);
                    if ("customer".equals(orderInArray.getString("rel"))) {
                        String orderUrl = orderInArray.getString("href");
                        String orderId = orderUrl.substring(orderUrl.lastIndexOf("/") + 1);
                        Log.i(TAG, orderId);
                        ResponseEntity<Customer> orderResponse = client.exchange(orderUrl, HttpMethod.GET, requestEntity,
                                Customer.class);
                        Customer customer = orderResponse.getBody();
                        customer.setId(orderId);
                        customers.add(customer);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            netErrorOrAuthenticationError();
        }
        if (response == null || !HttpStatus.OK.equals(response.getStatusCode())) {
            netErrorOrAuthenticationError();
        }
        return customers;
    }

    private void netErrorOrAuthenticationError() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passwordView.setError(activity.getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        });
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Override
    protected void onPostExecute(final List<Customer> customers) {
        if (customers.size() > 0) {
            activity.finish();

            // set current user to application
            ((MeishiApplication) activity.getApplication()).setCustomerId(identity);
            ((MeishiApplication) activity.getApplication()).setCustomer(customers.get(0));

            Intent intent = new Intent(activity, MeishiActivity.class);
            intent.putExtra(Constants.POSITION_BUNDILE_ID, 0);
            activity.startActivity(intent);
        }
    }

}
