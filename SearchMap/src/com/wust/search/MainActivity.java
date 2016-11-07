package com.wust.search;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;



public class MainActivity extends Activity {  
	
	
	 private LocationClient mLocationClient;  
	    /** 
	     * ��λ�ļ����� 
	     */  
	//    public MyLocationListener mMyLocationListener;  
	    /** 
	     * ��ǰ��λ��ģʽ 
	     */  
	    private LocationMode mCurrentMode = LocationMode.NORMAL;  
	    /*** 
	     * �Ƿ��ǵ�һ�ζ�λ 
	     */  
	    private volatile boolean isFristLocation = true;  
	    /** 
	     * ��ʼ����λ��ش��� 
	     */  
//	    private void initMyLocation()  
//	    {  
//	        // ��λ��ʼ��  
//	        mLocationClient = new LocationClient(this);  
//	        mMyLocationListener = new MyLocationListener();  
//	        mLocationClient.registerLocationListener(mMyLocationListener);  
//	        // ���ö�λ���������  
//	        LocationClientOption option = new LocationClientOption();  
//	        option.setOpenGps(true);// ��gps  
//	        option.setCoorType("bd09ll"); // ������������  
//	        option.setScanSpan(1000);  
//	        mLocationClient.setLocOption(option);  
//	    }  
	
	
    MapView mMapView = null;  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);  
        //��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) findViewById(R.id.bmapView);  
        
    }  
    
    /**
     * 
     * @author fpjoy
     *	ʵ��λ�ûص�����
     */
//    public class MyLocationListener implements BDLocationListener{
//
//    	@Override
//    	public void onReceiveLocation(BDLocation arg0) {
//    		// TODO Auto-generated method stub
//    		 // map view ���ٺ��ڴ����½��յ�λ��  
//            if (location == null || mMapView == null)  
//                return;  
//            // ���춨λ����  
//            MyLocationData locData = new MyLocationData.Builder()  
//                    .accuracy(location.getRadius())  
//                    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
//                    .direction(mXDirection).latitude(location.getLatitude())  
//                    .longitude(location.getLongitude()).build();  
//            mCurrentAccracy = location.getRadius();  
//            // ���ö�λ����  
//            mBaiduMap.setMyLocationData(locData);  
//            mCurrentLantitude = location.getLatitude();  
//            mCurrentLongitude = location.getLongitude();  
//            // �����Զ���ͼ��  
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
//                    .fromResource(R.drawable.navi_map_gps_locked);  
//            MyLocationConfigeration config = new MyLocationConfigeration(  
//                    mCurrentMode, true, mCurrentMarker);  
//            mBaiduMap.setMyLocationConfigeration(config);  
//            // ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��  
//            if (isFristLocation)  
//            {  
//                isFristLocation = false;  
//                LatLng ll = new LatLng(location.getLatitude(),  
//                        location.getLongitude());  
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);  
//                mBaiduMap.animateMapStatus(u);  
//            }  
//    	}
//
//    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	getMenuInflater().inflate(R.menu.main, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
        }  
    }