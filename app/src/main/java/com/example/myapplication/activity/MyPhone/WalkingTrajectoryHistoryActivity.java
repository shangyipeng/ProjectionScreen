package com.example.myapplication.activity.MyPhone;

import static android.R.attr.tag;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.platform.comapi.basestruct.Point;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.BaseActivity;
import com.example.myapplication.bean.BDLocationBean;
import com.example.myapplication.bean.ChaKanDianZiDiaBean;
import com.example.myapplication.bean.WeekDay;
import com.example.myapplication.service.TRTCService;
import com.example.myapplication.utile.AddressUtils;
import com.example.myapplication.utile.AppConfig;
import com.example.myapplication.utile.Base64Util;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.DatePhotoUtils;
import com.example.myapplication.utile.ForegroundServiceUtils;
import com.example.myapplication.utile.MapUtil;
import com.example.myapplication.utile.SPUtilss;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.example.myapplication.view.JobPatternDialog;
import com.example.myapplication.view.TrackAnalysisInfoLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行走轨迹记录
 */
public class WalkingTrajectoryHistoryActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener, View.OnClickListener, ContractInterface.View {
    private TextView textDirectionType;
    private TextView battery;
    private TextView name1;
    private LocationClient locationClient;
    private LinearLayout incloud_finish;
    private TextView incloud_title;
    private TextView textFollowmore;
    private TextView textTrajectory;
    private LinearLayout line2;
    private LinearLayout line3;
    private LinearLayout line4;
    private TextView textLastWeek;
    private TextView textThisWeek;
    private TextView textYesterday;
    private TextView textToday;
    private TextView textLine3Start;
    private TextView textLine3End;
    private TextView textQx;
    private TextView textQd;
    private TextView textTime;
    private TextView textTrack;
    private MapView mapView;
    private String isu;
    private String textDianliang;
    private String name;
    private SeekBar barPercentProgress;
    private SeekBar barPercentProgress1;
    private ImageView playStartStop;
    private TextView mSpeedTV;
    private TextView timeStart;
    private TextView timeStop;
    private TextView textAddress;
    private List<LatLng> trackPoints = new ArrayList<>();
    private List<String> trackPoints1 = new ArrayList<>();
    private List<String> trackPoints2 = new ArrayList<>();
    private List<Integer> trackPoints3 = new ArrayList<>();
    private List<Double> trackPoints5 = new ArrayList<>();
    private TimePickerView pvTime;//时间选择器
    private BaiduMap mBaiduMap;
    private boolean aBoolean = false;
    private int day_type = 4;//0 上周 1 本周 2 昨天 3 今天
    private int day_start_end = 0;//0 开始 1 结束
    private long start_time = 0;//开始时间
    private long end_time = 0;//结束时间
    private LocationClient mLocationClient ;
    private String token;
    private ContractInterface.Presenter presenter;
    @Override
    public int getLayout() {
        return R.layout.activity_walking_trajectory_history;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        isu=getIntent().getStringExtra("toId");
        token=getIntent().getStringExtra("token");
        initFind();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setcCreates(savedInstanceState);
            }
        });


    }
    public void initFind(){
        presenter=new MyPresenter(this);
        textDirectionType=findViewById(R.id.text_directionType);
        battery=findViewById(R.id.battery);
        name1=findViewById(R.id.name1);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        textFollowmore=findViewById(R.id.text_followmore);
        textTrajectory=findViewById(R.id.text_trajectory);
        line2=findViewById(R.id.line2);
        line3=findViewById(R.id.line3);
        line4=findViewById(R.id.line4);
        textLastWeek=findViewById(R.id.text_last_week);
        textThisWeek=findViewById(R.id.text_this_week);
        textYesterday=findViewById(R.id.text_yesterday);
        textToday=findViewById(R.id.text_today);
        textLine3Start=findViewById(R.id.text_line3_start);
        textLine3End=findViewById(R.id.text_line3_end);
        textQx=findViewById(R.id.text_qx);
        textQd=findViewById(R.id.text_qd);
        textTime=findViewById(R.id.text_time);
        textTrack=findViewById(R.id.text_track);
        mapView=findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        barPercentProgress=findViewById(R.id.bar_percent_progress);
        barPercentProgress1=findViewById(R.id.bar_percent_progress1);
        playStartStop=findViewById(R.id.play_start_stop);
        mSpeedTV=findViewById(R.id.mSpeedTV);
        timeStart=findViewById(R.id.time_start);
        timeStop=findViewById(R.id.time_stop);
        textAddress=findViewById(R.id.text_address);

    }
    public void setcCreates(Bundle savedInstanceState) {
        incloud_title.setText("行走轨迹");
        MapUtil.init();
        mapView.onCreate(this, savedInstanceState);
        name=getIntent().getStringExtra("name");
        name1.setText(name+"");
        textDianliang=SharedPreferencesUtils.getBatteryLevel();
        battery.setText("GPS  电量  " + textDianliang + "");
        trackAnalysisInfoLayout = new TrackAnalysisInfoLayout(WalkingTrajectoryHistoryActivity.this, mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(WalkingTrajectoryHistoryActivity.this);
        initClick();
        textToday.setSelected(true);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, 0);
        calendar2.getTime();
        textLine3Start.setText("" + getTime1(calendar2.getTime()) + "00:00:00");
        textLine3End.setText("" + getTime1(calendar2.getTime()) + "23:59:59");
        start_time = DatePhotoUtils.getTime3(textLine3Start.getText().toString());
        end_time = DatePhotoUtils.getTime3(textLine3End.getText().toString());
        timeStart.setText("" + textLine3Start.getText().toString());
        timeStop.setText("" + textLine3End.getText().toString());
        //showLoadingDialog();
        iadd = 0;
        trackPoints.clear();
        weekDay = new ArrayList<>();
        weekDay.clear();
        WeekDay weekDay1 = new WeekDay();
        // 获取星期的显示名称，例如：周一、星期一、Monday等等
        weekDay1.week = "";
        weekDay1.day = "";
        weekDay.add(weekDay1);
        initAddRess();
        //进度
        barPercentProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lat_log = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                aBoolean = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                aBoolean = true;
            }
        });
        //速度
        barPercentProgress1.setMax(10);
        barPercentProgress1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TIME_INTERVAL = TIME_INTERVAL - (int) (progress + (progress * 0.2) * 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                aBoolean = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                aBoolean = true;
            }
        });
       // initLocationOption();
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

    // 一次只能查询一天
    private void initAddRess() {
        //开始时间（Unix时间戳）
        int startTime = (int) (start_time / 1000);
        //结束时间（Unix时间戳）
        int endTime = (int) (end_time / 1000);
        Map<String ,Object> map=new HashMap<>();
        map.put("pageNO","1");
        map.put("pageSize","10080");
        map.put("type","6");
        map.put("startTime",startTime+"");
        map.put("endTime",endTime+"");
        presenter.presenter(map,"api/user/operateLogList?","GET", token+"");
        moveLooper();
    }

    private int iadd = 0;

    List<WeekDay> weekDay;

    private boolean aBoolean1 = false;
    public void initClick(){
        incloud_finish.setOnClickListener(this);
        textFollowmore.setOnClickListener(this);
        textTrajectory.setOnClickListener(this);
        textLastWeek.setOnClickListener(this);
        textThisWeek.setOnClickListener(this);
        textYesterday.setOnClickListener(this);
        textToday.setOnClickListener(this);
        textLine3Start.setOnClickListener(this);
        textLine3End.setOnClickListener(this);
        textQx.setOnClickListener(this);
        textQd.setOnClickListener(this);
        textTime.setOnClickListener(this);
        textTrack.setOnClickListener(this);
        playStartStop.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.incloud_finish:
                try {
//                    if (mapView != null) {
//                        mapView.onDestroy();
//                    }
//                    if ( mBaiduMap!=null){
//                        mBaiduMap.clear();
//                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (locationClient != null) {
                    locationClient.stop();
                    locationClient.disableLocInForeground(true);
                }
                finish();
                break;
            case R.id.text_followmore://更多
                day_type = 0;
                JobPatternDialog.show(WalkingTrajectoryHistoryActivity.this, null, onceTime).setListener(new JobPatternDialog.OnDialogClickListener() {
                    @Override
                    public void sure(String type) {
                        if (!type.equals("")) {
                            initSetUp(type);
                        }
                    }
                });
                break;
            case R.id.text_trajectory://轨迹
                line2.setVisibility(View.GONE);
                line3.setVisibility(View.VISIBLE);
                line4.setVisibility(View.GONE);
                day_type = 3;
                break;
            case R.id.text_track://跟踪
                textFollowmore.setVisibility(View.GONE);
                textTrajectory.setVisibility(View.GONE);
                textTrack.setVisibility(View.GONE);
                day_type = 5;
                trackPoints.clear();
                //查询对方定位
                initGenZhong();
                break;
            case R.id.text_last_week://上周
                iadd = 0;
                weekDay = DatePhotoUtils.getWeekDay1();
                textLine3Start.setText("" + weekDay.get(0).getDay() + " 00:00:00");
                textLine3End.setText("" + weekDay.get(6).getDay() + " 23:59:59");
                day_type = 0;
                textLastWeek.setSelected(true);
                textThisWeek.setSelected(false);
                textYesterday.setSelected(false);
                textToday.setSelected(false);
                break;
            case R.id.text_this_week://本周
                iadd = 0;
                weekDay = DatePhotoUtils.getWeekDay();
                textLine3Start.setText("" + weekDay.get(0).getDay() + " 00:00:00");
                textLine3End.setText("" + weekDay.get(6).getDay() + " 23:59:59");
                day_type = 1;
                textLastWeek.setSelected(false);
                textThisWeek.setSelected(true);
                textYesterday.setSelected(false);
                textToday.setSelected(false);
                break;
            case R.id.text_yesterday://昨天
                Calendar calendar2 = Calendar.getInstance();
                calendar2.add(Calendar.DATE, -1);
                calendar2.getTime();
                textLine3Start.setText("" + getTime1(calendar2.getTime()) + " 00:00:00");
                textLine3End.setText("" + getTime1(calendar2.getTime()) + " 23:59:59");
                day_type = 2;
                textLastWeek.setSelected(false);
                textThisWeek.setSelected(false);
                textYesterday.setSelected(true);
                textToday.setSelected(false);
                break;
            case R.id.text_today://今天
                day_type = 3;
                textLastWeek.setSelected(false);
                textThisWeek.setSelected(false);
                textYesterday.setSelected(false);
                textToday.setSelected(true);
                Calendar calendar4 = Calendar.getInstance();
                calendar4.add(Calendar.DATE, 0);
                calendar4.getTime();
                textLine3Start.setText("" + getTime1(calendar4.getTime()) + "00:00:00");
                textLine3End.setText("" + getTime1(calendar4.getTime()) + "23:59:59");
                break;
            case R.id.text_line3_start://开始时间
                day_start_end = 0;
                initTimePicker1();
                break;
            case R.id.text_line3_end://结束时间
                day_start_end = 1;
                initTimePicker1();
                break;
            case R.id.text_qx://取消
                line2.setVisibility(View.GONE);
                line3.setVisibility(View.GONE);
                line4.setVisibility(View.VISIBLE);
                break;
            case R.id.text_qd://确定
                // 正在加载中的弹框  showLoadingDialog();
                trackPoints.clear();
                trackPoints1.clear();
                trackPoints2.clear();
                trackPoints3.clear();
                trackPoints5.clear();
                aBoolean1 = true;
                latlngs.clear();
                if (day_type == 1) { //本周
                    trackPoints.clear();
                    iadd = 0;
                    weekDay.clear();
                    WeekDay weekDay1 = new WeekDay();
                    // 获取星期的显示名称，例如：周一、星期一、Monday等等
                    weekDay1.week = "";
                    weekDay1.day = textLine3Start.getText().toString();
                    weekDay.add(weekDay1);
                    weekDay = DatePhotoUtils.getDifference(textLine3Start.getText().toString(), textLine3End.getText().toString());
                    WeekDay weekDay2 = new WeekDay();
                    // 获取星期的显示名称，例如：周一、星期一、Monday等等
                    weekDay2.week = "";
                    weekDay2.day = textLine3End.getText().toString();
                    weekDay.add(weekDay2);
                    if (weekDay != null || weekDay.size() > 0) {
                        start_time = DatePhotoUtils.getTime3(weekDay.get(0).getDay() + " 00:00:00");
                        end_time = DatePhotoUtils.getTime3(weekDay.get(0).getDay() + " 23:59:59");
                        initAddRess();
                    }
                } else if (day_type == 0) {//上周
                    iadd = 0;
                    trackPoints.clear();
                    weekDay.clear();
                    WeekDay weekDay1 = new WeekDay();
                    // 获取星期的显示名称，例如：周一、星期一、Monday等等
                    weekDay1.week = "";
                    weekDay1.day = textLine3Start.getText().toString();
                    weekDay.add(weekDay1);
                    weekDay = DatePhotoUtils.getDifference(textLine3Start.getText().toString(), textLine3End.getText().toString());
                    WeekDay weekDay2 = new WeekDay();
                    // 获取星期的显示名称，例如：周一、星期一、Monday等等
                    weekDay2.week = "";
                    weekDay2.day = textLine3End.getText().toString();
                    weekDay.add(weekDay2);
                    if (weekDay != null || weekDay.size() > 0) {
                        start_time = DatePhotoUtils.getTime3(weekDay.get(0).getDay() + " 00:00:00");
                        end_time = DatePhotoUtils.getTime3(weekDay.get(0).getDay() + " 23:59:59");
                        initAddRess();
                    }
                } else {//昨天 今天
                    line2.setVisibility(View.GONE);
                    line3.setVisibility(View.GONE);
                    line4.setVisibility(View.VISIBLE);
                    iadd = 0;
                    trackPoints.clear();
                    weekDay.clear();
                    WeekDay weekDay1 = new WeekDay();
                    // 获取星期的显示名称，例如：周一、星期一、Monday等等
                    weekDay1.week = "";
                    weekDay1.day = "";
                    weekDay.add(weekDay1);
                    start_time = DatePhotoUtils.getTime3(textLine3Start.getText().toString());
                    end_time = DatePhotoUtils.getTime3(textLine3End.getText().toString());
                    initAddRess();
                }
                timeStart.setText("" + textLine3Start.getText().toString());
                timeStop.setText("" + textLine3End.getText().toString());
                break;
            case R.id.text_time:
                line2.setVisibility(View.GONE);
                line3.setVisibility(View.VISIBLE);
                line4.setVisibility(View.GONE);
                break;
            case R.id.play_start_stop:
                if (latlngs.size() == 0) {
                    return;
                }
                if (!aBoolean) {
                    aBoolean = true;
                    Glide.with(WalkingTrajectoryHistoryActivity.this).load(R.mipmap.icon_stop1).into(playStartStop);
                } else {
                    aBoolean = false;
                    Glide.with(WalkingTrajectoryHistoryActivity.this).load(R.mipmap.icon_bofang).into(playStartStop);
                }
                break;
        }
    }


    private void initGenZhong() {
        LogUtils.e("查询对方定位***************");
    }

    private void initTimePicker1() {
        //选择出生年月日
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter_year = new SimpleDateFormat("yyyy ");
        String year_str = formatter_year.format(curDate);
        int year_int = (int) Double.parseDouble(year_str);


        SimpleDateFormat formatter_mouth = new SimpleDateFormat("MM ");
        String mouth_str = formatter_mouth.format(curDate);
        int mouth_int = (int) Double.parseDouble(mouth_str);

        SimpleDateFormat formatter_day = new SimpleDateFormat("dd ");
        String day_str = formatter_day.format(curDate);
        int day_int = (int) Double.parseDouble(day_str);


        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(year_int, mouth_int - 1, day_int);

        //时间选择器
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //查询轨迹
//                queryTraceByUid();
                long time = DatePhotoUtils.getTime(getTime(date));//选择的时间
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0);
                calendar.getTime();
                long time3 = DatePhotoUtils.getTime3(getTime1(calendar.getTime()) + "00:00:00");//今天
                long timeMillis = Calendar.getInstance().getTimeInMillis();//实时时间
                if (day_type == 3) {//今天
                    if (time > timeMillis) {
                        Toast.makeText(WalkingTrajectoryHistoryActivity.this, "请选择正确的时间段",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (time < time3) {
                        Toast.makeText(WalkingTrajectoryHistoryActivity.this, "请选择正确的时间段",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (day_type == 2) {//昨
                    calendar.add(Calendar.DATE, -1);
                    Date date1 = calendar.getTime();
                    Date date2 = DatePhotoUtils.getDate(getTime1(date1) + " 00:00:00");
                    Date date3 = DatePhotoUtils.getDate(getTime1(date1) + " 23:59:59");
                    boolean effectiveDate = DatePhotoUtils.isEffectiveDate(date, date2, date3);
                    if (!effectiveDate) {
                        Toast.makeText(WalkingTrajectoryHistoryActivity.this, "请选择正确的时间段",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (day_type == 1) {//本周
                    List<WeekDay> weekDay = DatePhotoUtils.getWeekDay();
                    Date date2 = DatePhotoUtils.getDate(weekDay.get(0).getDay() + " 00:00:00");
                    Date date3 = DatePhotoUtils.getDate(weekDay.get(6).getDay() + " 23:59:59");
                    boolean effectiveDate = DatePhotoUtils.isEffectiveDate(date, date2, date3);
                    if (!effectiveDate) {
                        Toast.makeText(WalkingTrajectoryHistoryActivity.this, "请选择正确的时间段",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (day_type == 0) {//上周
                    List<WeekDay> weekDay = DatePhotoUtils.getWeekDay1();
                    Date date2 = DatePhotoUtils.getDate(weekDay.get(0).getDay() + " 00:00:00");
                    Date date3 = DatePhotoUtils.getDate(weekDay.get(6).getDay() + " 23:59:59");
                    boolean effectiveDate = DatePhotoUtils.isEffectiveDate(date, date2, date3);
                    if (!effectiveDate) {
                        Toast.makeText(WalkingTrajectoryHistoryActivity.this, "请选择正确的时间段",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (day_start_end == 0) {
                    textLine3Start.setText("" + getTime(date));
                } else {
                    textLine3End.setText("" + getTime(date));
                }
            }
        })
                .setType(new boolean[]{true, true, true, true, true, true}) //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("", "", "", "", "", "")//默认设置为年月日时分秒
                .isCenterLabel(false)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.WHITE)//设置选中项的颜色
                .setTextColorOut(Color.WHITE)//设置没有被选中项的颜色
                .setSubmitColor(getResources().getColor(R.color.color_3893FF))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.color_FF0101))//取消按钮文字颜色
                .setContentTextSize(13)
                .setDate(selectedDate)
                .setBgColor(getResources().getColor(R.color.color_1D1E22))
                .setLineSpacingMultiplier(2f)
                .setRangDate(startDate, endDate)
                .setTitleBgColor(getResources().getColor(R.color.color_1D1E22))
                .setOutSideCancelable(false)
                .setDecorView(null)
                .build();
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    private String getTime1(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
        return format.format(date);
    }

    private List<Marker> stayPointMarkers = new ArrayList<>();
    private List<LatLng> latlngs = new ArrayList<>();
    private Polyline mPolyline;
    private Marker mMoveMarker;
    private BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("Icon_road_green_arrow.png");
    private BitmapDescriptor mBitmapCar = BitmapDescriptorFactory.fromResource(R.mipmap.icon_car);
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static int TIME_INTERVAL = 1200;
    private static final double DISTANCE = 0.000005;

    /**
     * 所放置
     *
     * @param point
     * @param zoom
     */
    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        MapStatus mapStatus = builder.target(point).zoom(zoom).build();
        mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    /**
     * 缩放至起点终点
     *
     * @param points
     */
    public void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        mapView.getMap().animateMapStatus(msUpdate);
    }

    /**
     * 轨迹分析详情框布局
     */
    private TrackAnalysisInfoLayout trackAnalysisInfoLayout = null;

    //TODO 绘制轨迹路线
    private void drawPolyLine() {
        LogUtils.e("**********************");
        if (mapView.getMap() == null) {
            return;
        }
        List<LatLng> polylines = new ArrayList<>();
        polylines.addAll(latlngs);
        polylines.add(latlngs.get(0));
        // 绘制纹理PolyLine
        PolylineOptions polylineOptions =
                new PolylineOptions().points(polylines).width(10).customTexture(mGreenTexture).dottedLine(true);
        mPolyline = (Polyline) mapView.getMap().addOverlay(polylineOptions);
        barPercentProgress.setMax((polylines.size() - 1));


        // 添加小车marker
        OverlayOptions markerOptions = new MarkerOptions()
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(mBitmapCar).
                position(polylines.get(0))
                .rotate((float) getAngle(0));
        mMoveMarker = (Marker) mapView.getMap().addOverlay(markerOptions);

        int juli = 0;
        for (int i = 0; i < trackPoints3.size(); i++) {
            juli += trackPoints3.get(i);
        }

        int number = 0;
        double sudu = 0;
        for (int i = 0; i < trackPoints5.size(); i++) {
            if (trackPoints5.get(i) > 0) {
                //有效速度
                number = number + 1;
                sudu += trackPoints5.get(i);
            }
        }
        Log.d("ret", "test    总次数 " + number + "   总速度" + sudu);
        int suduInt = 0;
        if (sudu > 0 && number > 0) {
            suduInt = (int) (sudu / number);
        }
        trackAnalysisInfoLayout.titleText.setText(name);//标题
        trackAnalysisInfoLayout.key1.setText("状态:");
        trackAnalysisInfoLayout.value1.setText("运动");
        trackAnalysisInfoLayout.key2.setText("时间:");
        double shijian = 0;
        if (juli > 0 && suduInt > 0) {
            shijian = ((juli / 1000) / suduInt) * 10;
        }
        Log.d("ret", "test   时间===" + shijian);
        trackAnalysisInfoLayout.value2.setText(minConvertDayHourMin(shijian));
        trackAnalysisInfoLayout.key3.setText("里程:");
        if (juli > 0) {
            trackAnalysisInfoLayout.value3.setText((juli / 1000) + "km");
        } else {
            trackAnalysisInfoLayout.value3.setText("0km");
        }
        trackAnalysisInfoLayout.key4.setText("速度:");
        trackAnalysisInfoLayout.value4.setText(suduInt + "km/h");
        // 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
      //  InfoWindow trackAnalysisInfoWindow = new InfoWindow(trackAnalysisInfoLayout.mView, mMoveMarker.getPosition(), -47);

      //  mapView.getMap().showInfoWindow(trackAnalysisInfoWindow);
    }

    private int lat_log = 0;

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper() {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        initmoveLooper();
                        Thread.sleep(TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void initmoveLooper() {
        if (aBoolean) {
            if (latlngs.size()>0){
                if (lat_log<latlngs.size()-1){
                    final LatLng startPoint = latlngs.get(lat_log);
                    final LatLng endPoint = latlngs.get(lat_log + 1);
                    mMoveMarker.setPosition(startPoint);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // refresh marker's rotate
                            if (mapView == null) {
                                return;
                            }
                            mMoveMarker.setRotate((float) getAngle(startPoint, endPoint));
                        }
                    });
                    double slope = getSlope(startPoint, endPoint);
                    // 是不是正向的标示
                    boolean isYReverse = (startPoint.latitude > endPoint.latitude);
                    boolean isXReverse = (startPoint.longitude > endPoint.longitude);
                    double intercept = getInterception(slope, startPoint);
                    double xMoveDistance =
                            isXReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);
                    double yMoveDistance =
                            isYReverse ? getYMoveDistance(slope) : -1 * getYMoveDistance(slope);
                    int finalI = lat_log;
                    for (double j = startPoint.latitude, k = startPoint.longitude;
                         !((j > endPoint.latitude)
                                 ^ isYReverse)
                                 && !((k > endPoint.longitude)
                                 ^ isXReverse); ) {
                        LatLng latLng = null;
                        if (slope == Double.MAX_VALUE) {
                            latLng = new LatLng(j, k);
                            j = j - yMoveDistance;
                        } else if (slope == 0.0) {
                            latLng = new LatLng(j, k - xMoveDistance);
                            k = k - xMoveDistance;
                        } else {
                            latLng = new LatLng(j, (j - intercept) / slope);
                            j = j - yMoveDistance;
                        }

                        final LatLng finalLatLng = latLng;
                        if (finalLatLng.latitude == 0 && finalLatLng.longitude == 0) {
                            continue;
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (mapView == null) {
                                    return;
                                }
                                mMoveMarker.setPosition(finalLatLng);
                                // 设置 Marker 覆盖物的位置坐标,并同步更新与Marker关联的InfoWindow的位置坐标.
                                mMoveMarker.setPositionWithInfoWindow(finalLatLng);
                                barPercentProgress.setProgress(finalI);

                            }
                        });
                    }
                    lat_log = lat_log + 1;
                    if (lat_log == latlngs.size() - 1) {
                        aBoolean = false;
                        return;
                    }
                }
            }
        }

    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mPolyline.getPoints().size()) {
            return -1.0;
        }
        LatLng startPoint = mPolyline.getPoints().get(startIndex);
        LatLng endPoint = mPolyline.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * 1 / slope) / Math.sqrt(1 + 1 / (slope * slope)));
    }

    /**
     * 计算y方向每次移动的距离
     */
    private double getYMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        } else if (slope == 0.0) {
            if (toPoint.longitude > fromPoint.longitude) {
                return -90;
            } else {
                return 90;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {
        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude
                - fromPoint.longitude));
        return slope;
    }

    /**
     * 处理轨迹覆盖物
     *
     * @param points
     */
    private void handleOverlays(List<LatLng> points) {
        for (LatLng point : points) {
            BitmapDescriptor bitmap = MapUtil.bmArrowPoint;
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(point)
                    .icon(bitmap).zIndex(9).draggable(true);
            Marker marker = (Marker) mapView.getMap().addOverlay(overlayOptions);
            stayPointMarkers.add(marker);
        }
    }

    private void clear() {
        clearOverlays(stayPointMarkers);
    }

    private void clearOverlays(List<Marker> markers) {
        if (null == markers) {
            return;
        }
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    private void initLocationOption() {

        try {
            locationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        //注册监听函数
        locationClient.registerLocationListener(new MyLocationListener());
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.enableLocInForeground(ForegroundServiceUtils.id, ForegroundServiceUtils.silentForegroundNotification(ApplicTion.mContext, ForegroundServiceUtils.id).build());
        locationClient.start();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        // 如果bundle为空或者marker不可见，则过滤点击事件
        if (null == bundle || !marker.isVisible()) {
            Log.d("ret", "test   bundle== null");
            return false;
        } else {
            Log.d("ret", "test   bundle!= null");
        }


        return false;
    }

    @Override
    public void View(String o) {
        Gson gson=new Gson();
        ChaKanDianZiDiaBean chaKanDianZiDiaBean=gson.fromJson(o, ChaKanDianZiDiaBean.class);
        if (chaKanDianZiDiaBean.code==200){
            if (chaKanDianZiDiaBean.data.lists!=null&&chaKanDianZiDiaBean.data.lists.size()>0) {
                BDLocationBean[] bdLocationBeans = new BDLocationBean[chaKanDianZiDiaBean.data.lists.size()];
                for (int i = 0; i < chaKanDianZiDiaBean.data.lists.size(); i++) {
                    BDLocationBean detailListBean = new Gson().fromJson(chaKanDianZiDiaBean.data.lists.get(i).content,
                            BDLocationBean.class);
                    bdLocationBeans[i] = detailListBean;
                }
                if (bdLocationBeans[0].getAddress() != null) {
                    textAddress.setText("地址:" + bdLocationBeans[0].getAddress());
                } else {
                    textAddress.setText("地址:未知");
                }
                LatLng[] p = new LatLng[bdLocationBeans.length];
                for (int i = 0; i < bdLocationBeans.length; i++) {
                    LatLng p1 = new LatLng(bdLocationBeans[i].getLatitude(), bdLocationBeans[i].getLongitude());
                    p[i] = p1;
                }
                latlngs.clear();
                latlngs.addAll(Arrays.asList(p));
                for (int i = 0; i < latlngs.size(); i++){  //外循环是循环的次数
                    for (int j = latlngs.size() - 1 ; j > i; j--){  //内循环是 外循环一次比较的次数
                        if (latlngs.get(i) == latlngs.get(j)) {
                            latlngs.remove(j);
                        }
                    }
                }
                animateMapStatus(latlngs);
                lat_log = 0;
                TIME_INTERVAL = 1000;
                if ( mapView.getMap()!=null){
                    mapView.getMap().clear();
                    mapView.getMap().getUiSettings().setScrollGesturesEnabled(true);
                    mapView.getMap().setOnMapTouchListener(null);
                }
                drawPolyLine();
            }else {
                latlngs.clear();
                lat_log = 0;
                TIME_INTERVAL = 1000;
                if ( mapView.getMap()!=null){
                    mapView.getMap().clear();
                    mapView.getMap().getUiSettings().setScrollGesturesEnabled(true);
                    mapView.getMap().setOnMapTouchListener(null);
                }
            }
        }
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (textAddress != null) {
                textAddress.setText("地址:" + bdLocation.getAddrStr());
            }
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()), 18));//最大等级21
//            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//            BitmapDescriptor bitmap = BitmapDescriptorFactory
//                    .fromResource(R.mipmap.iocn_ranks_destination);
//            OverlayOptions option = new MarkerOptions()
//                    .position(ll)
//                    .icon(bitmap);
//            mBaiduMap.addOverlay(option);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mapView != null) {
//            mapView.onDestroy();
//        }
//        if (mBaiduMap!=null){
//            mBaiduMap.clear();
//        }
        if (locationClient != null) {
            locationClient.stop();
            locationClient.disableLocInForeground(true);
        }
    }

    private String onceTime = "1";

    private void initSettingUid() {
        //查询本机设置response
        Log.e("--------WalkTrajectoryActivity--1443","查询本机设置response");
    }


    private void initSetUp(String type) {
        Log.e("--------WalkTrajectoryActivity--1483","修改本机设置response");
        //修改本机设置response
    }


    public static String minConvertDayHourMin(Double min) {

        String html = "0分";

        if (min != null) {

            Double m = (Double) min;

            String format;

            Object[] array;

            Integer days = (int) (m / (60 * 24));

            Integer hours = (int) (m / (60) - days * 24);

            Integer minutes = (int) (m - hours * 60 - days * 24 * 60);

            if (days > 0) {

                format = "%1$,d天%2$,d时%3$,d分";

                array = new Object[]{days, hours, minutes};

            } else if (hours > 0) {

                format = "%1$,d时%2$,d分";

                array = new Object[]{hours, minutes};

            } else {

                format = "%1$,d分";

                array = new Object[]{minutes};

            }

            html = String.format(format, array);

        }

        return html;

    }
}