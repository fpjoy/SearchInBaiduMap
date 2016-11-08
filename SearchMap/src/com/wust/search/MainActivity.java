package com.wust.search;

import java.lang.reflect.Method;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {

	private LocationClient mLocationClient;
	/**
	 * 定位的监听器
	 */
	// public MyLocationListener mMyLocationListener;
	/**
	 * 当前定位的模式
	 */
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	/***
	 * 是否是第一次定位
	 */
	private volatile boolean isFristLocation = true;
	/**
	 * 方向传感器X方向的值
	 */
	private int mXDirection;
	/**
	 * 当前的精度
	 */
	private float mCurrentAccracy;
	/**
	 * 地图实例
	 */
	private BaiduMap mBaiduMap;
	/**
	 * 最新一次的经纬度
	 */
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	/**
	 * 定位的监听器
	 */
	public MyLocationListener mMyLocationListener;

	MapView mMapView = null;

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
		mMapView = (MapView) findViewById(R.id.bmapView);
		isFristLocation = true;
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		
		// 获得地图的实例
		mBaiduMap = mMapView.getMap();
	//	mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		initMyLocation();

	}

	/**
	 * 
	 * @author fpjoy 实现位置回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// 设置定位数据
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			// 设置自定义图标
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps);
			MyLocationConfiguration config = new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// 第一次定位时，将地图位置移动到当前位置
			if (isFristLocation) {
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				System.out.println("-----5------lat,lng"+location.getLatitude()+","+location.getLongitude());
				mBaiduMap.animateMapStatus(u);
			}
		}

	}

	/**
	 * 初始化定位相关代码
	 */
	private void initMyLocation() {
		// 定位初始化
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
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
		System.out.println("-------1---------");
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		System.out.println("-------2---------");
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		System.out.println("-------3---------");
		mBaiduMap.animateMapStatus(u);
		System.out.println("-------4---------");
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
	public boolean onMenuOpened(int featureId, Menu menu)
	{

		if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null)
		{
			if (menu.getClass().getSimpleName().equals("MenuBuilder"))
			{
				try
				{
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e)
				{
				}
			}

		}
		return super.onMenuOpened(featureId, menu);
	}
	// @Override
	// protected void onStart()
	// {
	// // 开启图层定位
	// mBaiduMap.setMyLocationEnabled(true);
	// if (!mLocationClient.isStarted())
	// {
	// mLocationClient.start();
	// }
	// // 开启方向传感器
	// myOrientationListener.start();
	// super.onStart();
	// }
	//
	// @Override
	// protected void onStop()
	// {
	// // 关闭图层定位
	// mBaiduMap.setMyLocationEnabled(false);
	// mLocationClient.stop();
	//
	// // 关闭方向传感器
	// myOrientationListener.stop();
	// super.onStop();
	// }

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