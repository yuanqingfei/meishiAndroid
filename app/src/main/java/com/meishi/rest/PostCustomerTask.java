package com.meishi.rest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.meishi.MeishiApplication;
import com.meishi.main.MainActivity;
import com.meishi.model.Customer;
import com.meishi.register.RegisterActivity;
import com.meishi.support.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Aaron on 2015/6/12.
 */
public class PostCustomerTask extends AsyncTask<Customer, Void, HttpStatus> {

    private String TAG = PostCustomerTask.class.getSimpleName();

    private RegisterActivity registerActivity;
    private SimpleAsync async;
    private String identity;
    private Customer customer;

    public PostCustomerTask(RegisterActivity activity, String identity) {
        this.registerActivity = activity;
        async = new SimpleAsync(activity);
        this.identity = identity;
    }

    @Override
    protected void onPreExecute() {
        async.showProgressDialog("请稍等，正在创建账户中...");
    }

    @Override
    protected HttpStatus doInBackground(Customer... params) {
        if (params.length < 1) {
            return null;
        }
        customer = params[0];
        try {
            HttpEntity<Customer> requestEntity = async.createPostCustomerRequest(customer);
            RestTemplate restTemplate = async.createRestTemplate();

            // Make the network request, posting the message, expecting a String in response from the server
            ResponseEntity<String> response = restTemplate.exchange(Constants.CUSTOMERS_URL, HttpMethod.POST, requestEntity,
                    String.class);

            Log.i(TAG, response.getStatusCode().toString());
            return response.getStatusCode();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }


    @Override
    protected void onPostExecute(HttpStatus result) {
        async.dismissProgressDialog();
        AlertDialog alertDialog = async.getDialog("账户创建");
        if (HttpStatus.CREATED.equals(result)) {
            alertDialog.setMessage("恭喜，账户创建成功，您可以下单了！");
        } else {
            alertDialog.setMessage("抱歉，账户创建失败！");
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        ((MeishiApplication) registerActivity.getApplication()).setCustomerId(identity);
                        ((MeishiApplication) registerActivity.getApplication()).setCustomer(customer);

                        Intent intent = new Intent(registerActivity, MainActivity.class);
                        intent.putExtra(Constants.POSITION_BUNDILE_ID, 1);
                        registerActivity.startActivity(intent);
                    }
                });
        alertDialog.show();

    }
}

