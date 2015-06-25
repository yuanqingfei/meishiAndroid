package com.meishi.meishi;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.model.Dish;
import com.meishi.rest.GetDishTask;

import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2015/6/23.
 */
public class MeishiListFragment extends ListFragment {

    private static final String TAG = "MeishiListFragment";

    private DishListAdapter adapter;

    private List<Dish> dishes = new ArrayList<>();

    public MeishiListFragment(){
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "List onCreateView()");
        return inflater.inflate(R.layout.dish_list_layout, container, false);
    }

    /**
     * adapter must be set here
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new DishListAdapter(getActivity(), R.layout.dish_row_layout, dishes);
        setListAdapter(adapter);

        Bundle mySavedInstanceState = getArguments();

        GetDishTask getDishTask = new GetDishTask(getActivity(), adapter, mySavedInstanceState);
        LatLng loc = ((MeishiApplication) getActivity().getApplication()).getCurrentLoc();
        getDishTask.execute(new Point(loc.latitude, loc.longitude));

    }

}
