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
import android.view.Window;

public class MainActivity extends Activity {
	
	private MapView mMapView;
	
	// �ٶȵ�ͼ����
	private BaiduMap mBaiduMap;
	
	// ��λ����Ŀͻ���
	private LocationClient mLocClient;
	
	// ��λ����ص��ӿ�
	private BDLocationListener mLocListener;
	
	// ��λͼ����ʾ��ʽ COMPASS,FOLLOWING,NORMAL
	private LocationMode mCurrentMode;
	
	// λͼ������Ϣ
	private BitmapDescriptor mCurrentIcon;
	
	// ��ǰ�ľ�γ��
	private double mCurrentLng;
	private double mCurrentLat;

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
		
	}

	

	private void initView() {
		// TODO Auto-generated method stub
		mMapView = (MapView) findViewById(R.id.bmapView);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * ��ͼ�ƶ����ҵ�λ��,�˴��������·���λ����Ȼ��λ�� ֱ�������һ�ξ�γ�ȣ������ʱ��û�ж�λ�ɹ������ܻ���ʾЧ������
	 */
	private void center2myLoc() {
		System.out.println("-------1---------");
		LatLng ll = new LatLng(mCurrentLat, mCurrentLng);
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
	 * Ĭ�ϵ��menu�˵����˵����ʵͼ�꣬����ǿ������ʾ
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
	 @Override
	 protected void onStart()
	 {
	 // ����ͼ�㶨λ
	 mBaiduMap.setMyLocationEnabled(true);
	 if (!mLocClient.isStarted())
	 {
	 mLocClient.start();
	 }
	// // �������򴫸���
	// myOrientationListener.start();
	 super.onStart();
	 }
	//
	 @Override
	 protected void onStop()
	 {
	 // �ر�ͼ�㶨λ
	 mBaiduMap.setMyLocationEnabled(false);
	 mLocClient.stop();
	
	 // �رշ��򴫸���
	// myOrientationListener.stop();
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