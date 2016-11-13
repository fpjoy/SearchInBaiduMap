package com.wust.search;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.wust.search.MyOrientationListener;
import com.wust.search.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private MapView mMapView;
	private Button mLocButton;

	// �ٶȵ�ͼ����
	private BaiduMap mBaiduMap;

	// ��λ����Ŀͻ���
	private LocationClient mLocClient;

	// ��λ����ص��ӿ�
	private BDLocationListener mLocListener;

	// ��λͼ����ʾ��ʽ COMPASS,FOLLOWING,NORMAL
	private LocationMode mCurrentMode;

	// �Ƿ��ǵ�һ�ζ�λ
	private boolean isFirstLoc;

	// ��ǰͼ�� �Լ� markͼ��
	private BitmapDescriptor mCurrentIcon;
	private BitmapDescriptor markerIcon;

	// ��ǰ�ľ�γ��
	private double mCurrentLng;
	private double mCurrentLat;

	// ���򴫸���������
	private MyOrientationListener mOrientationListener;

	// ��ǰ�ľ���
	private float mCurrentAccracy;

	// ���򴫸���X�����ֵ
	private int mXDirection;

	// �ڵ�ͼ����ʾһ����Ϣ����
	private InfoWindow mInfoWindow;

	// �û���ǵ�ļ���
	private List<Marker> markers = new ArrayList<Marker>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����͸��ActionBar
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		ColorDrawable mDrawable = new ColorDrawable();
		mDrawable.setColor(Color.BLACK);
		mDrawable.setAlpha(100);
		getActionBar().setBackgroundDrawable(mDrawable);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		// ��ȡ��ͼ�ؼ�����

		initView();

		initLoc();

		// ��������ʼ��
		initOritationListener();

		// marker����¼�
		initMarkerClick();

		// map����¼�
		initMapClick();

	}

	// ��ͼ��ʼ��
	private void initView() {
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mLocButton = (Button) findViewById(R.id.button_location);

		mLocButton.setOnClickListener(this);

	}

	// ��������ʼ��
	private void initOritationListener() {
		// TODO Auto-generated method stub
		mOrientationListener = new MyOrientationListener(
				getApplicationContext());
		mOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;
						// ���춨λ����
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mCurrentAccracy)
								// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
								.direction(mXDirection).latitude(mCurrentLat)
								.longitude(mCurrentLng).build();
						// ���ö�λ����
						mBaiduMap.setMyLocationData(locData);
					}
				});
	}

	// ��λ��ʼ��
	private void initLoc() {
		isFirstLoc = true;
		mCurrentMode = LocationMode.NORMAL;
		mLocClient = new LocationClient(this);
		mLocListener = new MyLocationListener();
		mLocClient.registerLocationListener(mLocListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_location:
			switch (mCurrentMode) {
			case NORMAL:
				mCurrentMode = LocationMode.FOLLOWING;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentIcon));
				break;
			case FOLLOWING:
				mCurrentMode = LocationMode.COMPASS;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentIcon));
				break;
			case COMPASS:
				mCurrentMode = LocationMode.NORMAL;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentIcon));
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// �˵�menu��Ŀ����¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.id_marker:
			LatLng point = new LatLng(mCurrentLat + (Math.random()-0.5) * 0.005,
					mCurrentLng + (Math.random()-0.5) * 0.005);
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.icon_gcoding);
			OverlayOptions option = new MarkerOptions().position(point).icon(
					markerIcon).draggable(true);

			Marker marker = (Marker) mBaiduMap.addOverlay(option);

			markers.add(marker);
			break;
		case R.id.id_lines:
			List<LatLng> points = new ArrayList<LatLng>();
			points.add(new LatLng(mCurrentLat, mCurrentLng));
			for(Marker m : markers){
				points.add(m.getPosition());
			}
			BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");

			OverlayOptions ooPolyline = new PolylineOptions().width(10)
			       .points(points).dottedLine(true).customTexture(mRedTexture);
			//����ڵ�ͼ��
			Polyline  mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
			break;
		case R.id.clear:
			// ���ͼ��
			mMapView.getMap().clear();
			// ���markers������ά����mark��
			markers.clear();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// Ϊmarker���õ���¼�
	private void initMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.location_tips);
				// OnInfoWindowClickListener listener = null;
				button.setText("ɾ��");
				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						marker.remove();
						mBaiduMap.hideInfoWindow();
					}
				});
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(button, ll, -47);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});

	}

	// map����¼�
	private void initMapClick() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// ����marker���������button
				mBaiduMap.hideInfoWindow();
			}
		});
		
		//����BaiduMap�����setOnMarkerDragListener��������marker��ק�ļ���
		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
		    public void onMarkerDrag(Marker marker) {
		        //��ק��
		    }
		    public void onMarkerDragEnd(Marker marker) {
		        //��ק����
		    	markers.add(marker);
		    	for(Marker m: markers){
		    		System.out.println(m.getPosition());
		    	}
		    }
		    public void onMarkerDragStart(Marker marker) {
		        //��ʼ��ק
		    	for(int x=0;x<markers.size();x++){
		    		if(markers.get(x).getPosition()==marker.getPosition()){
		    			markers.remove(x);
		    			x--;
		    		}
		    	}
		    }
		});
	}
	
	//
	
	
	// Ĭ�ϵ��menu�˵����˵����ʵͼ�꣬����ǿ������ʾ
	 
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}

		}
		return super.onMenuOpened(featureId, menu);
	}

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			mCurrentAccracy = location.getRadius();
			if (isFirstLoc) {
				isFirstLoc = false;
				mCurrentLat = location.getLatitude();
				mCurrentLng = location.getLongitude();
				
				LatLng ll = new LatLng(mCurrentLat, mCurrentLng);
				
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(18.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory
						.newMapStatus(builder.build()));
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}

	}

	@Override
	protected void onStart() {
		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
		// // �������򴫸���
		mOrientationListener.start();
		super.onStart();
	}

	//
	@Override
	protected void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();

		// �رշ��򴫸���
		mOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}
}