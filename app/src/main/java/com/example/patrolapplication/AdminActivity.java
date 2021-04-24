package com.example.patrolapplication;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.patrolapplication.network.DataController;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.utils.MyData;
import com.example.patrolapplication.utils.SyncData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends Activity {

    private static final String TAG = "## AdminActivity ## ";

    /** 百度地图模块 */
    // 百度地图 中心坐标
    private static final LatLng BUPTCenterLocation = new LatLng(39.96900, 116.364594);
    // 视图界面
    private MapView adminMapView = null;
    // 百度地图控制器
    private BaiduMap baiduMap = null;

    /** 定位 */
    private LocationManager locationManager;
    private String provider;
    private List<String> list;
    private LatLng mlatLng;

    /** NFC模块 */
    private NfcAdapter nfcAdapter;
    private PendingIntent pdi;
    private String nfcId;

    /** 视图组件 */
    private EditText nfcEdit;
    private EditText locEdit;
    private EditText locNameEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化百度地图配置
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        setContentView(R.layout.activity_admin);

        nfcEdit = findViewById(R.id.nfcIdEdit);
        locEdit = findViewById(R.id.locationEdit);
        locNameEdit = findViewById(R.id.locNameEdit);

        adminMapView = findViewById(R.id.adminMapView);
        baiduMap = adminMapView.getMap();
        locateBUPT();

        // 初始化NFC模块组件
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pdi = PendingIntent.getActivity(this, 0, intent, 0);

        // 定位
        //如果用户并没有同意该权限
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        list = locationManager.getProviders(true);
    }

    private void locateBUPT(){
        // 设置地图缩放级别
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
        // 设置地图中心
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(BUPTCenterLocation));
    }

    private void updateLocView(){
        if(mlatLng == null)
            return;
        locEdit.setText("ltt:" + mlatLng.latitude + ", lgt:" + mlatLng.longitude);
        baiduMap.clear();//这个方法清除地图上所有的mark点
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromAsset("Icon_mark1.png");
        if(bitmapDescriptor != null) {
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(mlatLng)
                    .icon(bitmapDescriptor)
                    .perspective(true);//这个方法是生成一个mark点信息，就是地图上的图标点
            baiduMap.addOverlay(overlayOptions);//将上面生成的mark点添加到地图上
        }else{
            System.out.println(TAG + "bitmapDescriptor is null");
        }
    }

    public void onGPSLocClick(View v){
        System.out.println(TAG + "OnGPSClick");
        // 定位初始化
        if(list.contains(LocationManager.GPS_PROVIDER)){
            // 是否有GPS控制器
            System.out.println("===== GPS Service ====");
            provider = LocationManager.GPS_PROVIDER;
        }else if(list.contains(LocationManager.NETWORK_PROVIDER)){
            System.out.println("===== NETWORK Provider ====");
            provider = LocationManager.NETWORK_PROVIDER;
        }else{
            System.out.println("请检查网络或者GPS是否打开");
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                updateLocView();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //如果用户并没有同意该权限
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }else{
            locationManager.requestLocationUpdates(provider, 2000, 2,
                    locationListener);
            Location location = locationManager.getLastKnownLocation(provider);
            if(location != null){
                mlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                updateLocView();
            }
        }
    }

    public void onMapLocClick(View v){
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mlatLng = latLng;
                updateLocView();
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                // 点击建筑物的处理事件
            }
        });
    }

    public void onBindConfirmClick(View v){
        if(nfcId == null){
            Toast.makeText(this, "未识别到nfc ID卡", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mlatLng == null ){
            Toast.makeText(this, "未识别到地理坐标", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = locNameEdit.getText().toString();
        if(name.length() == 0){
            Toast.makeText(this, "请给该地点命名", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", nfcId);
        map.put("name", name);
        map.put("longitude", mlatLng.longitude);
        map.put("latitude", mlatLng.latitude);
        DataController.locationOperate(map, new UserDao() {
            @Override
            public void userOperate(Map<String, Object> map) {
                String msg = (String)map.get("msg");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void timeout() {
                Toast.makeText(getApplicationContext(),MyData.TIMEOUT, Toast.LENGTH_SHORT).show();
            }
        }, DataController.ADD);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("## onNewIntent");
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            /* 处理Nfc事件 */
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagId = MyData.byteArrayToHexString(tagFromIntent.getId());
            nfcId = tagId;
            System.out.println("## tagId: " + tagId);
            nfcEdit.setText(tagId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adminMapView.onResume();
        if (nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this, pdi, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        adminMapView.onPause();
        if(nfcAdapter!=null){
            nfcAdapter.disableForegroundDispatch(this);//关闭前台发布系统
        }
    }

    @Override
    protected void onDestroy() {
        adminMapView.onDestroy();
        super.onDestroy();
    }
}


