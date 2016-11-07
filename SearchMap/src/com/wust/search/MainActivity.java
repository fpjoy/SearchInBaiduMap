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
	     * 定位的监听器 
	     */  
	//    public MyLocationListener mMyLocationListener;  
	    /** 
	     * 当前定位的模式 
	     */  
	    private LocationMode mCurrentMode = LocationMode.NORMAL;  
	    /*** 
	     * 是否是第一次定位 
	     */  
	    private volatile boolean isFristLocation = true;  
	    /** 
	     * 初始化定位相关代码 
	     */  
//	    private void initMyLocation()  
//	    {  
//	        // 定位初始化  
//	        mLocationClient = new LocationClient(this);  
//	        mMyLocationListener = new MyLocationListener();  
//	        mLocationClient.registerLocationListener(mMyLocationListener);  
//	        // 设置定位的相关配置  
//	        LocationClientOption option = new LocationClientOption();  
//	        option.setOpenGps(true);// 打开gps  
//	        option.setCoorType("bd09ll"); // 设置坐标类型  
//	        option.setScanSpan(1000);  
//	        mLocationClient.setLocOption(option);  
//	    }  
	
	
    MapView mMapView = null;  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);  
        //获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView);  
        
    }  
    
    /**
     * 
     * @author fpjoy
     *	实现位置回调监听
     */
//    public class MyLocationListener implements BDLocationListener{
//
//    	@Override
//    	public void onReceiveLocation(BDLocation arg0) {
//    		// TODO Auto-generated method stub
//    		 // map view 销毁后不在处理新接收的位置  
//            if (location == null || mMapView == null)  
//                return;  
//            // 构造定位数据  
//            MyLocationData locData = new MyLocationData.Builder()  
//                    .accuracy(location.getRadius())  
//                    // 此处设置开发者获取到的方向信息，顺时针0-360  
//                    .direction(mXDirection).latitude(location.getLatitude())  
//                    .longitude(location.getLongitude()).build();  
//            mCurrentAccracy = location.getRadius();  
//            // 设置定位数据  
//            mBaiduMap.setMyLocationData(locData);  
//            mCurrentLantitude = location.getLatitude();  
//            mCurrentLongitude = location.getLongitude();  
//            // 设置自定义图标  
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
//                    .fromResource(R.drawable.navi_map_gps_locked);  
//            MyLocationConfigeration config = new MyLocationConfigeration(  
//                    mCurrentMode, true, mCurrentMarker);  
//            mBaiduMap.setMyLocationConfigeration(config);  
//            // 第一次定位时，将地图位置移动到当前位置  
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
    }