package com.meishi.core.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MapFragment extends Fragment implements MKOfflineMapListener {

    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private LocationClient mLocClient;

    private MKOfflineMap mOffline = null;


    public static MapFragment newInstance(int page) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make use of baidu offline map first.
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        importFromSDCard();
    }

    /**
     * 把vmp文件夹拷入SD卡根目录下的BaiduMapSDK文件夹内, 对于我来说，是H盘而不是I盘
     */
    public void importFromSDCard() {
        int num = mOffline.importOfflineData();
        String msg = "";
        if (num == 0) {
            return;
//            msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
        } else {
            msg = String.format("成功导入 %d 个离线包，可以在下载管理查看", num);
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MapStatus ms = new MapStatus.Builder()
//                .overlook(0).zoom(5)
                .build();
        BaiduMapOptions options = new
                BaiduMapOptions().mapStatus(ms).compassEnabled(true).zoomControlsEnabled(false).scaleControlEnabled(false);
        mMapView = new MapView(getActivity(), options);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapStatusChangeListener(new MyOnMapStatusChangeListener());
        setupLocaion();

        return mMapView;
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    Toast.makeText(getActivity(), String.format("%s : %d%%", update.cityName,
                            update.ratio), Toast.LENGTH_SHORT);
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Toast.makeText(getActivity(), String.format("add offlinemap num:%d", state), Toast.LENGTH_SHORT);
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                break;
        }

    }

    private void setupLocaion() {
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(getActivity().getApplicationContext());
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
    }


    @Override
    public void onPause() {
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mOffline.destroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mLocClient.stop();
        mLocClient = null;
        super.onDestroy();
    }


    private class MyOnMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus arg0) {
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus status) {
            LatLng newCenter = status.target;
//            Toast.makeText(mMapView.getContext(), "location: " + newCenter.latitude + " " + newCenter.longitude, Toast.LENGTH_LONG).show();
            // TODO: I will take this as nearBy search with BaiduAPI then manually add
            // overlay
        }

        @Override
        public void onMapStatusChange(MapStatus arg0) {
        }
    }
}
