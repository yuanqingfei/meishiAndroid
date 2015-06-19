package com.meishi.core.order;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

//    private int index = -1;
//    private int top = 0;

    private String TAG = OrderFragment.class.getName();

    private OrderListAdapter adapter;
    private GetOrderTask getTask;
    private List<Order> orders = new ArrayList<Order>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list_layout, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv = (ListView)getActivity().findViewById(android.R.id.list);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


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

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
//        if (index != -1) {
//            this.getListView().setSelectionFromTop(index, top);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        try {
//            index = this.getListView().getFirstVisiblePosition();
//            View v = this.getListView().getChildAt(0);
//            top = (v == null) ? 0 : v.getTop();
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }


}
