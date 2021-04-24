package com.example.patrolapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.patrolapplication.component.MyDialogFragment;
import com.example.patrolapplication.network.DataController;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.network.response.ConditionItemResponse;
import com.example.patrolapplication.network.view.MenuListAdapter;
import com.example.patrolapplication.overlayutil.WalkingRouteOverlay;
import com.example.patrolapplication.pojo.ConditionItem;
import com.example.patrolapplication.pojo.Location;
import com.example.patrolapplication.utils.LocalDataController;
import com.example.patrolapplication.utils.MyData;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.patrolapplication.utils.MyData.menuList;

public class MainActivity extends FragmentActivity{
    private static final String TAG = "## Activity ## ";

    /** 百度地图模块 */
    // 百度地图 中心坐标
    private static final LatLng BUPTCenterLocation = new LatLng(39.96900, 116.364594);
    // 视图界面
    private MapView mMapView = null;
    // 百度地图控制器
    private BaiduMap baiduMap = null;
    // 路径规划
    private RoutePlanSearch routePlanSearch;

    /** NFC模块 */
    private NfcAdapter nfcAdapter;
    private PendingIntent pdi;
    private static boolean patrollingOn = false;

    /** 巡更打卡 */
    // 进度条
    private ProgressBar progressBar;
    private LinearLayout barLayout;
    private Set<Location> locationList = new HashSet<>(); // 所有的地理位置
    private Set<Location> visitedLocations = new HashSet<>(); // 已经访问的地理位置
    private String[] nameItems; // MyDialogFragment菜单
    private String startTime;
    private int abnormalNum;
    private String currentLocId = MyData.defaultLocId;
    private String currentLocName = MyData.defaultLocName;
    private List<String> abnormalItems = new ArrayList<>();

    /** 菜单栏 */
    // 顶部工具条
    private Toolbar toolbar = null;
    // 左侧功能菜单栏
    private DrawerLayout drawerLayout = null;
    // 菜单列表视图
    private ListView listView = null;

    // 关联工具条监听类
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(TAG + "onCreate");
        // 初始化百度地图配置
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        // 设置视图主界面
        setContentView(R.layout.activity_main);
        // 初始化（绑定）视图对象
        drawerLayout = findViewById(R.id.drawerLayout);
        listView = findViewById(R.id.menuList);
        toolbar = findViewById(R.id.toolbar);
        mMapView = findViewById(R.id.mapView);
        baiduMap = mMapView.getMap();
        barLayout = findViewById(R.id.barLayout);
        progressBar = findViewById(R.id.progressBar);

        // 将地图定位到北邮
        LocateBUPT();
        initMenu();

        // 初始化NFC模块组件
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pdi = PendingIntent.getActivity(this, 0, intent, 0);

        abnormalItems.add(MyData.otherItem);
    }

    /** 视图变更事件 */
    private void updateLocView(){
        baiduMap.clear();//这个方法清除地图上所有的mark点
        // 将绿色图标代表为未访问
        BitmapDescriptor descriptorGreen = BitmapDescriptorFactory.fromAsset("Icon_red.png");
        // 将蓝色图标表示为已访问
        BitmapDescriptor descriptorBlue = BitmapDescriptorFactory.fromAsset("Icon_blue.png");
        if(descriptorBlue == null || descriptorGreen == null){
            System.out.println(TAG + "BitmapDescriptor is null");
            return;
        }
        Set<Location> unVisitedLoc = new HashSet<>();
        unVisitedLoc.addAll(locationList);
        unVisitedLoc.removeAll(visitedLocations);
        for (Location location : unVisitedLoc) {
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(descriptorGreen)
                    .perspective(true);//这个方法是生成一个mark点信息，就是地图上的图标点
            baiduMap.addOverlay(overlayOptions);//将上面生成的mark点添加到地图上
        }
        for (Location location : visitedLocations) {
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(descriptorBlue)
                    .perspective(true);//这个方法是生成一个mark点信息，就是地图上的图标点
            baiduMap.addOverlay(overlayOptions);//将上面生成的mark点添加到地图上
        }
        int total_num = locationList.size();
        if(total_num == 0) {
            Toast.makeText(this, "目前还没有巡更任务", Toast.LENGTH_SHORT).show();
            endPatrolling();
        }
        else{
            int margin = 100 / total_num;
            progressBar.setProgress(visitedLocations.size() * margin);
            if(visitedLocations.size() == total_num){
                // 结束巡更
                endPatrolling();
                Toast.makeText(this, "恭喜你完成本次打卡", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** 初始化菜单栏及点击事件处理 */
    private void initMenu(){
        // 自定义List适配器
        MenuListAdapter menuListAdapter = new MenuListAdapter(MainActivity.this,
                R.layout.menu_list_item, MyData.menuList);
        // 绑定List适配器
        listView.setAdapter(menuListAdapter);
        // 设置菜单的监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (menuList[position]){
                    case "巡更打卡":
                        onPatrolMenuClick();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        actionBarDrawerToggle.syncState();
                        break;
                    case "事故上报":
                        jumpToAccident();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        actionBarDrawerToggle.syncState();
                        break;
                    case "记录查询":
                        jumpToQuery();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        actionBarDrawerToggle.syncState();
                        break;
                    default:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        actionBarDrawerToggle.syncState();
                        break;
                }
            }
        });

        // 侧边菜单关联Toolbar工具条
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                0, 0);
        // 初始化状态
        actionBarDrawerToggle.syncState();
        // 注意是add而不是set
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.setScrimColor(0x00000000);
    }

    /** 进入巡更打卡模式 */
    private void onPatrolMenuClick(){
        if (nfcAdapter == null) {
            Toast.makeText(this, "对不起,您的设备不支持nfc功能！", Toast.LENGTH_SHORT).show();
        }else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中开启NFC功能！", Toast.LENGTH_SHORT).show();
        }else{
            patrollingOn = true;    // 开启NFC的事件处理
            barLayout.setVisibility(View.VISIBLE);
            startTime = SyncData.dateToString(new Date());
            abnormalNum = 0;
            // 请求地点信息
            if(locationList == null || locationList.size() == 0){
                DataController.queryLocationList(new UserDao() {
                    @Override
                    public void userOperate(Map<String, Object> map) {
                        String msg = (String)map.get("msg");
                        int state = (int)map.get("state");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        if(state == SyncData.STATE_OK){
                            String json = (String) map.get("data");
                            List<Location> locations = new Gson().fromJson(json,
                                    new TypeToken<List<Location>>() {}.getType());
                            locationList = new HashSet<>(locations);
                            for (Location location : locationList) {
                                System.out.println(location);
                            }
                            updateLocView();
                        }
                    }

                    @Override
                    public void timeout() {
                        Toast.makeText(getApplicationContext(), MyData.TIMEOUT, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            // 请求检查菜单信息
            if(nameItems == null || nameItems.length == 0){
                DataController.queryConditionItemList(new UserDao() {
                    @Override
                    public void userOperate(Map<String, Object> map) {
                        int state = (int)map.get("state");
                        if(state == SyncData.STATE_OK){
                            String condition = (String)map.get("data");
                            System.out.println(TAG + "condition: " + condition);
                            List<ConditionItem> conditionItems = new Gson()
                                    .fromJson(condition, new TypeToken<List<ConditionItem>>() {}.getType());
                            nameItems = new String[conditionItems.size()];
                            for (int i = 0; i < nameItems.length; i++) {
                                nameItems[i] = conditionItems.get(i).getName();
                            }
                        }
                    }

                    @Override
                    public void timeout() {
                        Toast.makeText(getApplicationContext(), MyData.TIMEOUT, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /** NFC刷卡事件处理 */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("## onNewIntent");
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) && patrollingOn){
            /* 处理Nfc事件 */
            nfcProcess(intent);
        }
    }

    /** NFC刷卡事件处理 */
    private void nfcProcess(Intent intent){
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = tagFromIntent.getId();
        String tagId = MyData.byteArrayToHexString(id);
        currentLocId = tagId;

        Set<Location> unVisted = new HashSet<>();
        unVisted.addAll(locationList);
        unVisted.removeAll(visitedLocations);
        boolean isVisited = true;
        for (Location location : unVisted) {
            if(location.getId().equals(tagId)) {
                visitedLocations.add(location);
                currentLocName = location.getName();
                isVisited = false;
                break;
            }
        }
        if(isVisited){
            MyData.makeToast(getApplicationContext(), "该地点已打卡");
            return;
        }
        // 弹出状况确认对话框
        DialogFragment dialog = new MyDialogFragment(nameItems, new MyDialogFragment.MyDialogListener() {
            @Override
            public void onConfirmClick(boolean[] checkItems) {
                abnormalItems.clear();
                boolean isNormal = true;
                for (int i = 0; i < checkItems.length; i++) {
                    if(!checkItems[i]){
                        isNormal = false;
                        abnormalItems.add(nameItems[i]);
                    }
                }
                abnormalItems.add(MyData.otherItem);
                // 更新视图
                updateLocView();
                if(!isNormal)
                    abnormalNum += 1;
                // 记录本条巡更记录
                Map<String, Object> map = new HashMap<>();
                String username = LocalDataController.getUsername(getApplicationContext());
                map.put("username", username);
                map.put("locationId", tagId);
                map.put("time", SyncData.dateToString(new Date()));
                map.put("condition", isNormal ? SyncData.NORMAL : SyncData.ABNORMAL);
                DataController.addPatrolRecord(map, new UserDao() {
                    @Override
                    public void userOperate(Map<String, Object> map) {
                        String msg = (String)map.get("msg");
                        MyData.makeToast(getApplicationContext(), msg);
                    }

                    @Override
                    public void timeout() {
                        MyData.timeoutToast(getApplicationContext());
                    }
                });
            }
        });
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    /** 结束巡更 */
    private void endPatrolling(){
        // 写入数据库
        Map<String, Object> map = new HashMap<>();
        map.put("username", LocalDataController.getUsername(this));
        map.put("start_time", startTime);
        map.put("end_time", SyncData.dateToString(new Date()));
        map.put("total_loc_num", locationList.size());
        map.put("patrol_num", visitedLocations.size());
        map.put("abnormal_num", abnormalNum);
        DataController.addTripRecord(map, new UserDao() {
            @Override
            public void userOperate(Map<String, Object> map) {
                String msg = (String)map.get("msg");
                MyData.makeToast(getApplicationContext(), msg);
            }

            @Override
            public void timeout() {
                MyData.timeoutToast(getApplicationContext());
            }
        });

        progressBar.setProgress(0);
        baiduMap.clear();
        visitedLocations.clear();
        barLayout.setVisibility(View.GONE);
        patrollingOn = false;
    }
    /** 手动结束巡更 */
    public void onExitPatrollingClick(View v){
        endPatrolling();
    }

    // 弹出悬浮菜单，及菜单条目点击事件监听
    public void showPopup(View v){
        PopupMenu popup =  new PopupMenu(this, v);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.user_mng_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.modifyPassword:
                        modifyPassword();
                        return true;
                    case R.id.loginOut:
                        jumpToLogin();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    // 在定向巡更中用到
    private void setPath(){
        LatLng gateS = new LatLng(39.965881,116.363881);
        LatLng gateN1 = new LatLng(39.970705, 116.363666);
        LatLng gateW = new LatLng(39.966828, 116.361914);
        LatLng gm = new LatLng(39.967637, 116.366738);

        System.out.println(DistanceUtil.getDistance(gateS, gm));

        routePlanSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                if (walkingRouteResult.getRouteLines().size() > 0) {
                    overlay.setData(walkingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };

        routePlanSearch.setOnGetRoutePlanResultListener(listener);
        PlanNode startNode = PlanNode.withLocation(gateW);
        PlanNode endNode = PlanNode.withLocation(gateN1);
        routePlanSearch.walkingSearch((new WalkingRoutePlanOption()).from(startNode).to(endNode));
    }

    private void LocateBUPT(){
        // 设置地图缩放级别
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
        // 设置地图中心
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(BUPTCenterLocation));
    }

    private void modifyPassword(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ModifyAcivity.class);
        startActivity(intent);
    }

    private void jumpToLogin(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void jumpToAccident(){
        Intent intent = new Intent(MainActivity.this, AccidentActivity.class);
        intent.putExtra("currentLocId", currentLocId);
        intent.putExtra("currentLocName", currentLocName);
        intent.putExtra("abnormalItems", new Gson().toJson(abnormalItems));
        startActivity(intent);
    }

    private void jumpToQuery(){
        Intent intent = new Intent(MainActivity.this, QueryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println(TAG + "onSaveInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println(TAG + "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println(TAG + "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        System.out.println(TAG + "onDestory");
        mMapView.onDestroy();
        /** 清除用户本地的数据 */
        LocalDataController.clearUserData(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println(TAG + "onResume");
        mMapView.onResume();
        if (nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this, pdi, null, null);
        }
    }

    @Override
    protected  void onPause(){
        super.onPause();
        System.out.println(TAG + "onPause");
        mMapView.onPause();
        if(nfcAdapter!=null){
            nfcAdapter.disableForegroundDispatch(this);//关闭前台发布系统
        }
    }
}
