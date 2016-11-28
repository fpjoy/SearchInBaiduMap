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
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
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

public class MainActivity extends Activity implements OnClickListener,
		OnGetRoutePlanResultListener {

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

	// 当前图标 以及 mark图标
	private BitmapDescriptor mCurrentIcon;
	private BitmapDescriptor markerIcon;

	// 当前的经纬度
	private double mCurrentLng;
	private double mCurrentLat;

	// 方向传感器监听器
	private MyOrientationListener mOrientationListener;

	// 当前的精度
	private float mCurrentAccracy;

	// 方向传感器X方向的值
	private int mXDirection;

	// 在地图中显示一个信息窗口
	private InfoWindow mInfoWindow;

	// 用户标记点的集合
	private List<Marker> markers = new ArrayList<Marker>();

	// 路径规划搜索接口
	private RoutePlanSearch mSearch;

	// 路线数据结构的基类
	private RouteLine route = null;
	private OverlayManager routeOverlay = null;

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

		initLoc();

		// 传感器初始化
		initOritationListener();

		// marker点击事件
		initMarkerClick();

		// map点击事件
		initMapClick();

		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

	}

	// 视图初始化
	private void initView() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mLocButton = (Button) findViewById(R.id.button_location);

		mLocButton.setOnClickListener(this);

	}

	// 传感器初始化
	private void initOritationListener() {
		// TODO Auto-generated method stub
		mOrientationListener = new MyOrientationListener(
				getApplicationContext());
		mOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;
						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mCurrentAccracy)
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(mXDirection).latitude(mCurrentLat)
								.longitude(mCurrentLng).build();
						// 设置定位数据
						mBaiduMap.setMyLocationData(locData);
					}
				});
	}

	// 定位初始化
	private void initLoc() {
		isFirstLoc = true;
		mCurrentMode = LocationMode.NORMAL;
		mLocClient = new LocationClient(this);
		mLocListener = new MyLocationListener();
		mLocClient.registerLocationListener(mLocListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000);
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

	// 菜单menu项目点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.id_marker:
			LatLng point = new LatLng(mCurrentLat + (Math.random() - 0.5)
					* 0.03, mCurrentLng + (Math.random() - 0.5) * 0.03);
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.icon_gcoding);
			OverlayOptions option = new MarkerOptions().position(point)
					.icon(markerIcon).draggable(true);

			Marker marker = (Marker) mBaiduMap.addOverlay(option);

			markers.add(marker);
			break;
		case R.id.id_lines:
			List<LatLng> points = new ArrayList<LatLng>();
			points.add(new LatLng(mCurrentLat, mCurrentLng));
			for (Marker m : markers) {
				points.add(m.getPosition());
			}
			BitmapDescriptor mRedTexture = BitmapDescriptorFactory
					.fromAsset("icon_road_green_arrow.png");

			OverlayOptions ooPolyline = new PolylineOptions().width(10)
					.points(points).dottedLine(true).customTexture(mRedTexture);
			// 添加在地图中
			Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
			break;
		case R.id.clear:
			// 清除图层
			mMapView.getMap().clear();
			// 清除markers集合中维护的mark点
			markers.clear();
			break;
		case R.id.carrouting:
			LatLng start = new LatLng(mCurrentLat, mCurrentLng);
			LatLng end = null;

			if (!markers.isEmpty()) {
				end = markers.get(markers.size() - 1).getPosition();
			}
			// 途径点的集合
			List<PlanNode> wayPoints = new ArrayList<PlanNode>();
			for (int x = 0; x < markers.size() - 1; x++) {
				PlanNode wayPoint = PlanNode.withLocation(markers.get(x)
						.getPosition());
				wayPoints.add(wayPoint);
			}
			for (PlanNode pn : wayPoints){
				System.out.println(pn);
			}

			PlanNode stNode = PlanNode.withLocation(start);
			PlanNode enNode = PlanNode.withLocation(end);
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.passBy(wayPoints).to(enNode)
					.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC)
				//	.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST)
				//	.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST)
					.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_AVOID_JAM)
					);
			break;
		case R.id.walkrouting:
			System.out.println("-------6---------");
			LatLng ptb1 = new LatLng(mCurrentLat, mCurrentLng);
			LatLng ptb2 = null;
			if (!markers.isEmpty()) {
				end = markers.get(0).getPosition();
			}
			PlanNode stbNode = PlanNode.withLocation(ptb1);
			PlanNode enbNode = PlanNode.withLocation(ptb2);
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stbNode)
					.to(enbNode));
			System.out.println("-------7---------");
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// 为marker设置点击事件
	private void initMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.location_tips);
				// OnInfoWindowClickListener listener = null;
				button.setText("删除");
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

	// map点击事件
	private void initMapClick() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// 隐藏marker点击弹出的button
				mBaiduMap.hideInfoWindow();
			}
		});

		// 调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) {
				// 拖拽中
			}

			public void onMarkerDragEnd(Marker marker) {
				// 拖拽结束
				markers.add(marker);
				for (Marker m : markers) {
					System.out.println(m.getPosition());
				}
			}

			public void onMarkerDragStart(Marker marker) {
				// 开始拖拽
				for (int x = 0; x < markers.size(); x++) {
					if (markers.get(x).getPosition() == marker.getPosition()) {
						markers.remove(x);
						x--;
					}
				}
			}
		});
	}

	//

	// 默认点击menu菜单，菜单项不现实图标，反射强制其显示

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
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
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
				builder.target(ll).zoom(15.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory
						.newMapStatus(builder.build()));
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
		mOrientationListener.start();
		super.onStart();
	}

	//
	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();

		// 关闭方向传感器
		mOrientationListener.stop();
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

	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		}

		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

			if (result.getRouteLines().size() >= 1) {
				System.out.println("-------4---------");
				route = result.getRouteLines().get(0);
				DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
						mBaiduMap);
				routeOverlay = overlay;
				mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();

			} else {
				Log.d("route result", "结果数<0");
				return;
			}
		}

	}

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (true) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (true) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetMassTransitRouteResult(MassTransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		// TODO Auto-generated method stub
		System.out.println("-------8---------");
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			System.out.println("-------3---------");
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			System.out.println("-------1---------");
			if (result.getRouteLines().size() >= 1) {
				System.out.println("-------2---------");
				// 直接显示
				route = result.getRouteLines().get(0);
				WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(
						mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(overlay);
				routeOverlay = overlay;
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();

			} else {
				Log.d("route result", "结果数<0");
				return;
			}

		}
	}

	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (true) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (true) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}
}