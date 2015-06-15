package com.meishi.core.order;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.meishi.MeishiApplication;
import com.meishi.R;
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
    private ListView list;
    private OrderListAdapter adapter;

    private GetOrderTask getTask;

    private List<Order> orders = new ArrayList<Order>();

    public static OrderFragment newInstance(int page) {
        return new OrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list_layout, null);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(v.getContext(), adapter.getItem(position).getId(), Toast.LENGTH_SHORT);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new OrderListAdapter(getActivity(), R.layout.order_row_layout, orders);
        setListAdapter(adapter);

        // refresh data when click tab every time.
        if (getActivity() != null) {
            MeishiApplication app = (MeishiApplication) getActivity().getApplication();

            if (app.getCustomerId() != null) {
                Log.i(TAG, app.getCustomerId());

                getTask = new GetOrderTask(this.getActivity(), adapter, app.getCustomerId());
                getTask.execute();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
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
