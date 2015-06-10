package com.meishi.core;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Aaron on 2015/6/7.
 */
public class OrderFragment extends ListFragment {

    public static OrderFragment newInstance(int page) {
        return new OrderFragment();
    }

    private int index = -1;
    private int top = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(index!=-1){
            this.getListView().setSelectionFromTop(index, top);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            index = this.getListView().getFirstVisiblePosition();
            View v = this.getListView().getChildAt(0);
            top = (v == null) ? 0 : v.getTop();
        }
        catch(Throwable t){
            t.printStackTrace();
        }
    }
}
