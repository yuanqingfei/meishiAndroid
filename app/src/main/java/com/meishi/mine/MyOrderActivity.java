package com.meishi.mine;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.login.LoginActivity;
import com.meishi.model.Order;
import com.meishi.rest.GetOrderTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MyOrderActivity extends ListActivity {

    private String TAG = MyOrderActivity.class.getName();

    private OrderListAdapter adapter;
    private GetOrderTask getTask;
    private List<Order> orders = new ArrayList<Order>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list_layout);

        adapter = new OrderListAdapter(this, R.layout.order_row_layout, orders);
        setListAdapter(adapter);

        MeishiApplication app = (MeishiApplication) this.getApplication();
        if (app.getCustomerId() != null) {
            Log.i(TAG, app.getCustomerId());

            getTask = new GetOrderTask(this, adapter);
            getTask.execute(app.getCustomerId());
        } else {
            Log.i(TAG, "customerId is null");
            this.startActivity(new Intent(this, LoginActivity.class));
        }
    }


}
