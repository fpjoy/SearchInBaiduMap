package com.wust.search;

import java.lang.reflect.Method;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

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

public class MainActivity extends Activity implements OnClickListener {

	private MapView mMapView;
	private Button mLocButton;

	// 百度地图对象
	private BaiduMap mBaiduMap;

	// 定位服务的客户端
	private LocationClient mLocClient;

	// 定位请求回调接口
	private BDLocationListener mLocListener;

	// 定位图层显示方式 COMPASS,FOLLOWING,NORMAL
	private LocationMode mCurrentMode;
	
	// 是否是第一次定位
	private boolean isFirstLoc;
	
	
	// 位图描述信息
	private BitmapDescriptor mCurrentIcon;

	// 当前的经纬度
	private double mCurrentLng;
	private double mCurrentLat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置透明ActionBar
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		ColorDrawable mDrawable = new ColorDrawable();
		mDrawable.setColor(Color.BLACK);
		mDrawable.setAlpha(100);
		getActionBar().setBackgroundDrawable(mDrawable);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		// 获取地图控件引用

		initView();
		 mBaiduMap
         .setMyLocationConfigeration(new MyLocationConfiguration(
                 mCurrentMode, true, mCurrentIcon));
		initLoc();

	}

	// 视图初始化
	private void initView() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		
		mLocButton = (Button) findViewById(R.id.button_location);

		mLocButton.setOnClickListener(this);
	}
	
	// 定位初始化
	private void initLoc(){
		isFirstLoc = true;
		mLocClient = new LocationClient(this);
		mLocListener = new MyLocationListener();
        mLocClient.registerLocationListener(mLocListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_location:
				
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

	/**
	 * 地图移动到我的位置,此处可以重新发定位请求，然后定位； 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
	 */
	private void center2myLoc() {
		LatLng ll = new LatLng(mCurrentLat, mCurrentLng);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.id_menu_map_myLoc:
			center2myLoc();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 默认点击menu菜单，菜单项不现实图标，反射强制其显示
	 */
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
	
	private class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			 // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                mCurrentLat = location.getLatitude();
                mCurrentLng = location.getLongitude();
                LatLng ll = new LatLng(mCurrentLat,
                        mCurrentLng);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
		
	}

	@Override
	protected void onStart() {
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
		// // 开启方向传感器
		// myOrientationListener.start();
		super.onStart();
	}

	//
	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();

		// 关闭方向传感器
		// myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}