package com.meishi.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.meishi.MeishiActivity;

/**
 * Created by Aaron on 2015/6/23.
 */
public class MeishiFragment extends Fragment {

    private static final String TAG = "MeishiFragment";

    private Button searchButton;

    private String currentCityName;

    private TextView currentCity;

    private LocationClient mLocationClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getActivity().getApplicationContext());

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true); // so that we can get city at init.
        option.setScanSpan(5000);
        option.setProdName("meishi");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }

                //                location.getCountry();
                String city = location.getCity();
                if (city != null) {
                    Log.d(TAG, city);
                    ((MeishiApplication) getActivity().getApplication()).setCurrentCity(city);
                    currentCityName = city;
                    mLocationClient.stop();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meishi_fragment_layout, container, false);

        currentCity = (TextView) view.findViewById(R.id.currentCity);
        searchButton = (Button) view.findViewById(R.id.searchButton);

        if(currentCityName != null){
            currentCity.setText(currentCityName);
        } else {
            currentCity.setText("无法定位，网络貌似有问题");
//            searchButton.setEnabled(false);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeishiActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
