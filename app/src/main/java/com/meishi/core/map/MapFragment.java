package com.meishi.core.map;

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
import com.meishi.R;
import com.meishi.rest.GetCookTask;

import org.springframework.data.geo.Point;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MapFragment extends Fragment implements MKOfflineMapListener, OnGetGeoCoderResultListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private LocationClient mLocClient;

    private MKOfflineMap mOffline;

    private GeoCoder mSearch = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make use of baidu offline map first.
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        importFromSDCard();

        // create location converter for future
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        BaiduMapOptions options = new BaiduMapOptions().compassEnabled(true).zoomControlsEnabled(false)
                .scaleControlEnabled(false).rotateGesturesEnabled(false).overlookingGesturesEnabled(false);
        mMapView = new MapView(getActivity(), options);

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(getActivity().getApplicationContext());

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
        ImageButton locateButton = new ImageButton(getActivity());
        locateButton.setImageDrawable(getResources().getDrawable(R.drawable.locate));
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocClient.start();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLocClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null || mMapView == null) {
                    return;
                }
                LatLng baiduLoc = new LatLng(location.getLatitude(), location.getLongitude());
                MyLocationData locData = new MyLocationData.Builder().
//                        .accuracy(location.getRadius()).direction(100)
        latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);


                MapStatus newStatus = new MapStatus.Builder().target(baiduLoc).zoom(14).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(newStatus));

                showCooksAndAddress(baiduLoc);
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocClient.setLocOption(option);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocClient.start();
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
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                break;
        }

    }

    private void showCooksAndAddress(LatLng baiduLoc) {
        mBaiduMap.clear();
        OverlayOptions dotOO = new DotOptions().center(baiduLoc).color(Color.RED).radius(10);
        OverlayOptions circleOO = new CircleOptions().center(baiduLoc).radius(3000)
                .fillColor(0X1f000000).stroke(new Stroke(2, Color.BLUE));
        mBaiduMap.addOverlay(dotOO);
        mBaiduMap.addOverlay(circleOO);

        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(baiduLoc));
        GetCookTask getCookTask = new GetCookTask(getActivity(), mBaiduMap);
        try {
            getCookTask.execute(new Point[]{new Point(baiduLoc.latitude, baiduLoc.longitude)}).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (TimeoutException e) {
            Log.e(TAG, e.getMessage(), e);
        }
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
        super.onDestroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，您提供的地址无法定位，请输入更具体地址。", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
//
//        Toast.makeText(getActivity(), result.getLocation().longitude + " " + result.getLocation().latitude,
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，您提供的地址无法定位，请输入更具体地址。", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Toast.makeText(getActivity(), result.getAddress(), Toast.LENGTH_SHORT).show();

//        TextOptions textOptions = new TextOptions().text(result.getAddress()).fontSize(10).fontColor(Color.BLUE).
//                position(result.getLocation());
//        mBaiduMap.addOverlay(textOptions);
    }
}
