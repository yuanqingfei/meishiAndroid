package com.meishi.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.meishi.MeishiActivity;
import com.meishi.MeishiApplication;
import com.meishi.model.Customer;
import com.meishi.model.OrderRequest;
import com.meishi.support.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Aaron on 2015/6/17.
 */
public class PostOrderTask extends AsyncTask<OrderRequest, Void, HttpStatus> {

    private String TAG = PostOrderTask.class.getSimpleName();

    private SimpleAsync async;

    private Activity activity;

    public PostOrderTask(Activity activity) {
        this.async = new SimpleAsync(activity);
        this.activity = activity;
    }

    @Override
    protected HttpStatus doInBackground(OrderRequest... params) {
        if (params.length < 1) {
            return HttpStatus.BAD_REQUEST;
        }

        RestTemplate client = async.createRestTemplate();
        Customer customer = ((MeishiApplication)activity.getApplication()).getCustomer();
        HttpEntity<OrderRequest> orderRequestHttpEntity = async.createPostOrderRequest(params[0],
                customer.getIdentity(),customer.getPassword());
        ResponseEntity<String> response = client.exchange(Constants.CRATE_ORDER_URL, HttpMethod.POST,
                orderRequestHttpEntity, String.class);
        return response.getStatusCode();
    }

    @Override
    protected void onPostExecute(HttpStatus result) {
        AlertDialog alertDialog = async.getDialog("下单");
        if (HttpStatus.OK.equals(result)) {
            alertDialog.setMessage("恭喜，下单成功了！");
        } else {
            alertDialog.setMessage("抱歉，下单失败！");
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(activity, MeishiActivity.class);
                        intent.putExtra(Constants.POSITION_BUNDILE_ID, 1);
                        activity.startActivity(intent);
                    }
                });

        alertDialog.show();

    }
}
