package com.meishi.meishi;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.rest.GetCookTask;
import com.meishi.support.Constants;

import org.springframework.data.geo.Point;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiMapFragment extends Fragment implements MKOfflineMapListener, OnGetGeoCoderResultListener {

    private static final String TAG = MeishiMapFragment.class.getSimpleName();

    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private LocationClient mLocationClient;

    private MKOfflineMap mOffline;

    private GeoCoder mGeoCoder = null;

    private Boolean needLocate = true;

    private Boolean isFirst = true;

    public MeishiMapFragment() {
        setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Map onCreate()");

        // make use of baidu offline map first.
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        importFromSDCard();

        // create location converter for future
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(this);

        BaiduMapOptions options = new BaiduMapOptions().compassEnabled(true).zoomControlsEnabled(false)
                .scaleControlEnabled(false).rotateGesturesEnabled(false).overlookingGesturesEnabled(false);
        mMapView = new MapView(getActivity(), options);

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getActivity().getApplicationContext());

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                showCooksAndAddress(mapStatus.target);
            }
        });


        mLocationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null || mMapView == null) {
                    return;
                }

                LatLng baiduLoc = new LatLng(location.getLatitude(), location.getLongitude());
                MyLocationData locData = new MyLocationData.Builder()
//                        .accuracy(location.getRadius()).direction(100)
                        .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);

                if(needLocate){
                    needLocate = false;
                    MapStatus newStatus = new MapStatus.Builder().target(baiduLoc).zoom(14).build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(newStatus));
                    showCooksAndAddress(baiduLoc);
                    mLocationClient.stop();
                }

            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setProdName("meishi");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);

        LatLng previousLoc = ((MeishiApplication) getActivity().getApplication()).getCurrentLoc();
        if(previousLoc != null){
            LatLng baiduLoc = new LatLng(previousLoc.latitude, previousLoc.longitude);
            MyLocationData locData = new MyLocationData.Builder()
//                        .accuracy(location.getRadius()).direction(100)
                    .latitude(previousLoc.latitude).longitude(previousLoc.longitude).build();
            mBaiduMap.setMyLocationData(locData);

            MapStatus newStatus = new MapStatus.Builder().target(previousLoc).zoom(14).build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(newStatus));
            showCooksAndAddress(previousLoc);
            mLocationClient.stop();
        } else {
            mLocationClient.start();
        }
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

        Log.d(TAG, "Map onCreateView()");

        ImageButton locateButton = new ImageButton(getActivity());
        locateButton.setImageDrawable(getResources().getDrawable(R.drawable.locate));
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needLocate = true;
                mLocationClient.start();
            }
        });

        RelativeLayout.LayoutParams mapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout layout = new RelativeLayout(getActivity());
        layout.addView(mMapView, mapParams);
        layout.addView(locateButton, buttonParams);

        return layout;
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                if (update != null) {
                    Toast.makeText(getActivity(), String.format("%s : %d%%", update.cityName,
                            update.ratio), Toast.LENGTH_SHORT);
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                Toast.makeText(getActivity(), String.format("add offlinemap num:%d", state), Toast.LENGTH_SHORT);
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
//                 MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                break;
        }

    }

    private void showCooksAndAddress(LatLng baiduLoc) {
        mBaiduMap.clear();

        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(baiduLoc));

        OverlayOptions dotOO = new DotOptions().center(baiduLoc).color(Color.RED).radius(10);
        OverlayOptions circleOO = new CircleOptions().center(baiduLoc).radius(Integer.valueOf(Constants.SEARCH_SCOPE) * 1000)
                .fillColor(0X1f000000).stroke(new Stroke(2, Color.BLUE));
        mBaiduMap.addOverlay(dotOO);
        mBaiduMap.addOverlay(circleOO);

        new GetCookTask(getActivity(), mBaiduMap).execute(new Point[]{new Point(baiduLoc.latitude, baiduLoc.longitude)});

        // for list fragment
        ((MeishiApplication) getActivity().getApplication()).setCurrentLoc(baiduLoc);
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
        mLocationClient.stop();
        super.onDestroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), getString(R.string.can_not_parse_out_latlog), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), result.getLocation().longitude + " " + result.getLocation().latitude,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), getString(R.string.can_not_parse_out_address), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), result.getAddress(), Toast.LENGTH_SHORT).show();

//        TextOptions textOptions = new TextOptions().text(result.getAddress()).fontSize(10).fontColor(Color.BLUE).
//                position(result.getLocation());
//        mBaiduMap.addOverlay(textOptions);
    }
}
