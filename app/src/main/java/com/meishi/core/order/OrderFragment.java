package com.meishi.core.order;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class OrderFragment extends ListFragment {

    private String TAG = OrderFragment.class.getName();

    private OrderListAdapter adapter;
    private GetOrderTask getTask;
    private List<Order> orders = new ArrayList<Order>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_list_layout, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new OrderListAdapter(getActivity(), R.layout.order_row_layout, orders);
        setListAdapter(adapter);

        MeishiApplication app = (MeishiApplication) getActivity().getApplication();
        if (app.getCustomerId() != null) {
            Log.i(TAG, app.getCustomerId());

            getTask = new GetOrderTask(getActivity(), adapter);
            getTask.execute(app.getCustomerId());
        } else {
            Log.i(TAG, "customerId is null");
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }



}
