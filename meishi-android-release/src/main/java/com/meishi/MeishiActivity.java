package com.meishi;

import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

public class MeishiActivity extends Activity implements OnGetPoiSearchResultListener, OnGetGeoCoderResultListener {

	private static final String LTAG = MeishiActivity.class.getSimpleName();

	private MapView mMapView;

	private BaiduMap mBaiduMap;

	private LocationClient mLocClient;

	private MyLocationListenner myListener = new MyLocationListenner();

	private PoiSearch mPoiSearch = PoiSearch.newInstance();

	private GeoCoder mSearch = GeoCoder.newInstance();

	private LatLng northEast;

	private LatLng northWest;

	private LatLng southEast;

	private LatLng southWest;

	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
	BitmapDescriptor bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
	BitmapDescriptor bdC = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
	BitmapDescriptor bdD = BitmapDescriptorFactory.fromResource(R.drawable.icon_markd);
	BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();

		mSearch.setOnGetGeoCodeResultListener(this);

		mPoiSearch.setOnGetPoiSearchResultListener(this);

		// mSearch.geocode(new GeoCodeOption().city("上海").address("老沪闵路1296弄"));
		// mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new
		// LatLng(31.126794, 121.445964)));

		// mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng location) {
		// BitmapDescriptor bitmap =
		// BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		// OverlayOptions option = new
		// MarkerOptions().position(location).icon(bitmap);
		// mBaiduMap.addOverlay(option);
		// }
		//
		// @Override
		// public boolean onMapPoiClick(MapPoi arg0) {
		// return false;
		// }
		//
		// });

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus status) {
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus status) {
				LatLng newCenter = status.target;

//				OverlayOptions ooA = new MarkerOptions().position(newCenter).icon(bdA).zIndex(5);
//				Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
//				mMarkerA.setTitle("中心点");
				
			
//				OverlayOptions circle3000 = new CircleOptions().center(newCenter).radius(3000);
//				mBaiduMap.addOverlay(circle3000);

				search(newCenter, 3000);
			}

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				// TODO Auto-generated method stub

			}
		});

		// mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
		//
		// @Override
		// public void onTouch(MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// searchInScreen();
		// case MotionEvent.ACTION_UP:
		// searchInScreen();
		// case MotionEvent.ACTION_MOVE:
		// searchInScreen();
		// }
		// }
		//
		// });

		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		// mBaiduMap.setTrafficEnabled(true);
		// mBaiduMap.setBaiduHeatMapEnabled(true);

		mBaiduMap.setMaxAndMinZoomLevel(15, 19);

		mBaiduMap.setMyLocationEnabled(true);
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		mLocClient.setLocOption(option);
		mLocClient.start();

	}

	private void search(LatLng center, Integer radius) {

		Boolean search = mPoiSearch.searchNearby(new PoiNearbySearchOption().location(center).radius(radius)
				.keyword("商店"));
		Log.i(LTAG, "search is successful? " + search);

		// mPoiSearch.searchInCity((new
		// PoiCitySearchOption()).city("上海").keyword("商店"));
	}

	private void searchInScreen() {
		Projection projection = mBaiduMap.getProjection();
		northWest = projection.fromScreenLocation(new Point(0, 0));
		northEast = projection.fromScreenLocation(new Point(mMapView.getWidth(), 0));
		southWest = projection.fromScreenLocation(new Point(0, mMapView.getHeight()));
		southEast = projection.fromScreenLocation(new Point(mMapView.getWidth(), mMapView.getHeight()));

		Log.i(LTAG,
				mMapView.getWidth() + "  " + mMapView.getHeight() + "  " + mMapView.getLeft() + " "
						+ mMapView.getRight());

		mPoiSearch.searchInBound(new PoiBoundSearchOption().bound(
				new LatLngBounds.Builder().include(northEast).include(southWest).include(northWest).include(southEast)
						.build()).keyword("商店"));

	}

	@Override
	protected void onPause() {
		mMapView.setVisibility(View.INVISIBLE);
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.setVisibility(View.VISIBLE);
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mBaiduMap.setMyLocationEnabled(false);
		mPoiSearch.destroy();
		mSearch.destroy();
		mMapView.onDestroy();
		bdA.recycle();
		bdB.recycle();
		bdC.recycle();
		bdD.recycle();
		bd.recycle();
		bdGround.recycle();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null) {
				return;
			}

			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MeishiActivity.this, "没找到", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			// overlay.zoomToSpan();
			return;
		}
	}

	public void onGetPoiDetailResult(final PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MeishiActivity.this, "没找到", Toast.LENGTH_SHORT).show();
		} else {
			OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
				public void onInfoWindowClick() {
//					LatLng ll = marker.getPosition();
//					LatLng llNew = new LatLng(ll.latitude + 0.005,
//							ll.longitude + 0.005);
//					marker.setPosition(llNew);
//					mBaiduMap.hideInfoWindow();
					
					String details = result.getName() + ": " + result.getAddress();
					OverlayOptions ooText = new TextOptions().text(details).fontSize(11).position(result.getLocation());
					mBaiduMap.addOverlay(ooText);
				}
			};
			
			String details = result.getName() + ": " + result.getAddress();
			Button button = new Button(getApplicationContext());
			button.setText(details);
//			button.setBackgroundResource(R.drawable.popup);
			InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), result.getLocation(), -47, listener);
			mBaiduMap.showInfoWindow(mInfoWindow);
			
			
		}
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			return true;
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MeishiActivity.this, "没找到", Toast.LENGTH_SHORT).show();
			return;
		}

		LatLng location = result.getLocation();
		Toast.makeText(MeishiActivity.this, "老沪闵路路1296 坐标: " + location.latitude + " " + location.longitude,
				Toast.LENGTH_LONG).show();

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MeishiActivity.this, "没找到", Toast.LENGTH_SHORT).show();
			return;
		}

		StringBuilder finalResult = new StringBuilder();
		finalResult.append("Address: ").append(result.getAddress()).append(" Circle: ")
				.append(result.getBusinessCircle());
		// .append(" Details ").append(result.getAddressDetail());
		List<PoiInfo> poiInfos = result.getPoiList();
		for (PoiInfo info : poiInfos) {
			finalResult.append(" " + info.name + " ");
		}

		Toast.makeText(MeishiActivity.this, finalResult.toString(), Toast.LENGTH_LONG).show();
	}
}
