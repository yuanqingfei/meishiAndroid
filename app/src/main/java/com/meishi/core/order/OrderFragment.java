package com.meishi.core.order;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.meishi.R;
import com.meishi.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Aaron on 2015/6/7.
 */
public class OrderFragment extends ListFragment {

    private int index = -1;
    private int top = 0;

    private String TAG = OrderFragment.class.getName();
    private ListView list;
    private OrderListAdapter adapter;

    List<Order> orders = new ArrayList<Order>();

    public static OrderFragment newInstance(int page) {
        return new OrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list_layout, null);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        Toast.makeText(v.getContext(), adapter.getItem(position).getId(), Toast.LENGTH_SHORT);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        View header = (View) getLayoutInflater(savedInstanceState).inflate(R.layout.order_header_layout, null);
//        getListView().addHeaderView(header);

        for (int i = 1; i < 35; i++) {
            Order order = new Order();
            order.setId(new Integer(i).toString());
            order.setOrderTime(new Date());
            order.setTotalPrice(23.5);
            orders.add(order);
        }

        adapter = new OrderListAdapter(getActivity(), R.layout.order_row_layout, orders);
        setListAdapter(adapter);


        Log.i(TAG, "--------onActivityCreated");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "----------onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (index != -1) {
            this.getListView().setSelectionFromTop(index, top);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            index = this.getListView().getFirstVisiblePosition();
            View v = this.getListView().getChildAt(0);
            top = (v == null) ? 0 : v.getTop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
