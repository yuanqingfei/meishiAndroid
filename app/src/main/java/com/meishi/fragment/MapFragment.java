package com.meishi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.meishi.mymeishi.R;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MapFragment extends Fragment {

    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private LocationClient mLocClient;

    public static MapFragment newInstance(int page) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.baidu_map_fragment, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                LatLng newCenter = status.target;
                Toast.makeText(mMapView.getContext(), "location£º " + newCenter.latitude + " " + newCenter.longitude, Toast.LENGTH_LONG).show();
                // TODO: I will take this as nearBy search with BaiduAPI then manually add
                // overlay
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });

        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null || mMapView == null) {
                    return;
                }

                MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
                        .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location
                        .getLongitude()));
                mBaiduMap.animateMapStatus(u);
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocClient.setLocOption(option);
        mLocClient.start();

        return view;
    }
}
