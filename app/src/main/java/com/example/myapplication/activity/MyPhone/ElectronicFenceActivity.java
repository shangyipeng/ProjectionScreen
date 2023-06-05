package com.example.myapplication.activity.MyPhone;

import androidx.annotation.NonNull;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.BaseActivity;
import com.example.myapplication.bean.DeleElectronicBean;
import com.example.myapplication.bean.DetailListBean;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.view.AddDianZiDialog;
import com.example.myapplication.view.ChaKanDianZiDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电子围栏
 */
public class ElectronicFenceActivity extends BaseActivity implements BaiduMap.OnMapTouchListener, ContractInterface.View{
    private TextView tv_tishi;
    private LinearLayout incloud_finish;
    private TextView incloud_title;
    private ImageView addDianzi;
    private ImageView imgChakan;

    private boolean canMove = true;
    String uid;

    boolean aBoolean;
    private ApplicTion trackApp = null;
    private String type="新增";
    private ContractInterface.Presenter presenter;
    private String Token="";
    private MapView mapView;
    private MyLocationConfiguration.LocationMode locationMode;
    private LocationClient mLocationClient = null;
    private  AddDianZiDialog dialogs;
    private LinearLayout ElectronicFenceActivity_Linear;
    @Override
    public int getLayout() {
        return R.layout.activity_electronic_fence;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        presenter=new MyPresenter(this);
        Token=getIntent().getStringExtra("Token");
        initFind();
        try {
            initDate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initFind(){
        tv_tishi=findViewById(R.id.tv_tishi);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        addDianzi=findViewById(R.id.add_dianzi);
        imgChakan=findViewById(R.id.img_chakan);
        mapView=findViewById(R.id.mapView);
        ElectronicFenceActivity_Linear=findViewById(R.id.ElectronicFenceActivity_Linear);
    }
    private void initDate() throws Exception {
        incloud_title.setText("电子围栏");
        trackApp = (ApplicTion) getApplication();
        mapView.getMap().setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
        // 可选，设置地址信息
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true);
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        mapView.getMap().setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
        //开启地图定位图层
        mLocationClient.start();
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addDianzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!aBoolean){
                    tv_tishi.setVisibility(View.VISIBLE);
                    if (canMove) {
                        incloud_title.setText("区域防护");
                        canMove = false;
                        mapView.getMap().clear();
                        mapView.getMap().getUiSettings().setScrollGesturesEnabled(false);
                        mapView.getMap().setOnMapTouchListener(ElectronicFenceActivity.this);
                    } else {
                        enableMapGestures();
                    }
                }else{
                    Toast.makeText(ElectronicFenceActivity.this,"只能创建一个围栏",Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChaKanDianZiDialog.show(ElectronicFenceActivity.this, null,uid,Token,ElectronicFenceActivity_Linear,ElectronicFenceActivity.this).setListener(new ChaKanDianZiDialog.OnDialogClickListener() {
                    @Override
                    public void sure(String type,String fenceId) {
                        if(type.equals("1")){
                            initDelete(fenceId);
                        }
                    }

                    @Override
                    public void item(List<DetailListBean.DetailListDTO> detailList) {
                        arrayList.clear();
                        enableMapGestures();
                        for (int i = 0; i < detailList.size(); i++) {
                            LatLng latLng3 = new LatLng(Double.valueOf(detailList.get(i).latitude), Double.valueOf(detailList.get(i).longitude));
                            arrayList.add(latLng3);
                        }
                        mapView.getMap().addOverlay(new PolygonOptions().points(arrayList).stroke(new Stroke(4, Color.parseColor("#3893ff"))).fillColor(Color.parseColor("#753893ff")));
                    }
                });
            }
        });
    }
    private void initDelete(String fenceId) {
        type="删除";
        //删除电子围栏
        Map<String,Object> map=new HashMap<>();
        map.put("id",fenceId);
        presenter.presenter(map,"api/user/deleteOperateLog","POST", Token+"");
        DataUtile.getpopupwindow(ElectronicFenceActivity.this,ElectronicFenceActivity_Linear);
    }
    private void enableMapGestures() {
        mapView.getMap().clear();
        mapView.getMap().getUiSettings().setScrollGesturesEnabled(true);
        mapView.getMap().setOnMapTouchListener(null);
        canMove = true;
    }
    //数据返回
    @Override
    public void View(String o) {
        if (type.equals("删除")){
            DataUtile.dissePopup();
            Gson gson=new Gson();
            DeleElectronicBean deleElectronicBean=gson.fromJson(o,DeleElectronicBean.class);
            if (deleElectronicBean.code==200){
                enableMapGestures();
                aBoolean=false;
            }else {
                Toast.makeText(ElectronicFenceActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 添加一个区域
     * @param latLng
     * @param latLng2
     */
    ArrayList arrayList = new ArrayList();
    private void addOverlay(LatLng latLng,LatLng latLng2){
        LatLng latLng3 = new LatLng(latLng.latitude, latLng2.longitude);
        LatLng latLng4 = new LatLng(latLng2.latitude, latLng.longitude);
        arrayList.clear();
        arrayList.add(latLng);
        arrayList.add(latLng3);
        arrayList.add(latLng2);
        arrayList.add(latLng4);
        this.mapView.getMap().addOverlay(new PolygonOptions().points(arrayList).stroke(new Stroke(4, Color.parseColor("#3893ff"))).fillColor(Color.parseColor("#753893ff")));
    }


    @Override
    public void onTouch(MotionEvent motionEvent) {
        try {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                this.m = this.mapView.getMap().getProjection().fromScreenLocation(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                this.n = this.mapView.getMap().getProjection().fromScreenLocation(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));
                if (this.o != null) {
                    this.o.remove();
                }
                //绘制范围
                show(this.m, this.n);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                AddDianZiDialog dialog = AddDianZiDialog.show(ElectronicFenceActivity.this, null);
                dialog.setListener(new AddDianZiDialog.OnDialogClickListener() {
                    @Override
                    public void sure(String name, boolean notify) {
                        tv_tishi.setVisibility(View.GONE);
                        incloud_title.setText("电子围栏");
                        AddBacth addBacth=new AddBacth();
                        addBacth.setUid(uid+"");
                        addBacth.setFenceName(name);
                        addBacth.setIsNotify(notify?"0":"1");
                        List<AddBacth.DetailListBean> detailList = new ArrayList<>();
                        for (int i = 0; i < arrayList1.size(); i++) {
                            AddBacth.DetailListBean detailListBean=new AddBacth.DetailListBean();
                            LatLng latLng5= (LatLng) arrayList1.get(i);
                            detailListBean.setLatitude(latLng5.latitude+"");
                            detailListBean.setLongitude(latLng5.longitude+"");
                            detailList.add(detailListBean);
                        }
                        addBacth.setDetailList(detailList);
                        Gson gson=new Gson();
                        String s = gson.toJson(addBacth);
                        initAddBeath(s);
                    }

                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        tv_tishi.setVisibility(View.GONE);
                        incloud_title.setText("电子围栏");
                        enableMapGestures();
                    }
                });
            } else {
                if (this.n.latitude == this.m.latitude && this.n.longitude == this.m.longitude) {
                    this.mapView.getMap().clear();
                    this.mapView.getMap().setOnMapTouchListener(null);
                    this.mapView.getMap().getUiSettings().setScrollGesturesEnabled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAddBeath(String s) {
        type="新增";
        LogUtils.e("-------ElectronicFenceActivity.class电子围栏上传 316-",s);
        Map<String ,Object> map=new HashMap<>();
        map.put("type","4");//type="1"来表示通讯录，"2"=电话，"3"=短信
        map.put("content",s);
        presenter.presenter(map,"/api/user/addOperateLog?","POST", Token+"");
    }

    private LatLng m;
    private LatLng n;
    private Polygon o;
    ArrayList arrayList1 = new ArrayList();
    private void show(LatLng latLng, LatLng latLng2) {
        LatLng latLng3 = new LatLng(latLng.latitude, latLng2.longitude);
        LatLng latLng4 = new LatLng(latLng2.latitude, latLng.longitude);
        arrayList1.clear();
        arrayList1.add(latLng);
        arrayList1.add(latLng3);
        arrayList1.add(latLng2);
        arrayList1.add(latLng4);

        this.o = (Polygon) this.mapView.getMap().addOverlay(new PolygonOptions().points(arrayList1).stroke(new Stroke(4, Color.parseColor("#3893ff"))).fillColor(Color.parseColor("#753893ff")));
    }

    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        MapStatus mapStatus = builder.target(point).zoom(zoom).build();
        mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mapView == null){
                return;
            }

            // 如果是第一次定位
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            if (isFirstLocate) {
//                isFirstLocate = false;
//                //给地图设置状态
            mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
//            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mapView.getMap().setMyLocationData(locData);

//            // ------------------  以下是可选部分 ------------------
//            // 自定义地图样式，可选
//            // 更换定位图标，这里的图片是放在 drawble 文件下的
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.iocn_ranks_destination);
//            int accuracyCircleFillColor = 0xAAFFFF88;//自定义精度圈填充颜色
//            int accuracyCircleStrokeColor = 0xAA00FF00;//自定义精度圈边框颜色;
//            // 定位模式 地图SDK支持三种定位模式：NORMAL（普通态）, FOLLOWING（跟随态）, COMPASS（罗盘态）
//            locationMode = MyLocationConfiguration.LocationMode.NORMAL;
//            // 定位模式、是否开启方向、设置自定义定位图标、精度圈填充颜色以及精度圈边框颜色5个属性（此处只设置了前三个）。
//            MyLocationConfiguration mLocationConfiguration = new MyLocationConfiguration(locationMode,true,mCurrentMarker,accuracyCircleFillColor,accuracyCircleStrokeColor);
//            // 使自定义的配置生效
//            mapView.getMap().setMyLocationConfiguration(mLocationConfiguration);
            // ------------------  可选部分结束 ------------------

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    private void getPlace(){

    }

    public static class AddBacth{

        /**
         * uid :
         * fenceName :
         * detailList : [{"longitude":0,"latitude":0}]
         */

        private String uid;
        private String fenceName;
        private String isNotify;
        private List<DetailListBean> detailList;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getFenceName() {
            return fenceName;
        }

        public void setFenceName(String fenceName) {
            this.fenceName = fenceName;
        }

        public String getIsNotify() {
            return isNotify;
        }

        public void setIsNotify(String isNotify) {
            this.isNotify = isNotify;
        }

        public List<DetailListBean> getDetailList() {
            return detailList;
        }

        public void setDetailList(List<DetailListBean> detailList) {
            this.detailList = detailList;
        }

        public static class DetailListBean {
            /**
             * longitude : 0
             * latitude : 0
             */

            private String longitude;
            private String latitude;

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }
        }
    }
}