package com.example.myapplication.activity.MyPhone;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.BaseActivity;
import com.example.myapplication.adapter.HistoryAdapter;
import com.example.myapplication.adapter.HistoryDataAdapter;
import com.example.myapplication.bean.HistoryActivityBean;
import com.example.myapplication.bean.HistoryBean;
import com.example.myapplication.bean.WeekDayBean;
import com.example.myapplication.utile.DataUtile;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 手机行为记录-详情
 */
public class HistoryActivity extends BaseActivity implements ContractInterface.View {
    private XRecyclerView HistoryActivity_recycler;
    private LinearLayout incloud_finish;//返回
    private TextView incloud_title;//头部标题
    private HistoryAdapter historyAdapter;
    private HistoryDataAdapter historyDataAdapter;
    private RecyclerView HistoryActivity_DataView;//日历控件
    private List<HistoryBean> list=new ArrayList<>();
    private List<HistoryBean> Alllist=new ArrayList<>();
    private List<WeekDayBean> DataList=new ArrayList<>();
    private int size=20;
    private int size1=20;
    private String token="";
    private String type="";
    private String InterfaceType="本机";
    private ContractInterface.Presenter presenter;
    private int pageSize=20;
    private long dataTime=0;
    private LinearLayout HistoryActivity_LinearLayout;

    @Override
    public int getLayout() {
        return R.layout.activity_history;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        token=getIntent().getStringExtra("Token");
        type=getIntent().getStringExtra("type");
        initFind();
        initData();
//        if (type.equals("MyPhoneActivity")){//本机查询
//            InterfaceType="本机";
//            getUsageList(HistoryActivity.this, DataUtile.compareDayTime(Cdata),DataUtile.compareDayTime(Edata));
//        }else {
//            InterfaceType="远端";
     //   }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String data=simpleDateFormat.format(date);
            String Cdata=data+" 00:00:00";//开始时间
            String Edata=data+" 23:59:59";//开始时间
            dataTime=DataUtile.compareDayTime(Cdata);
            Map<String ,Object> map=new HashMap<>();
            map.put("pageNO","1");
            map.put("pageSize",pageSize+"");
            map.put("createTime",dataTime);
            map.put("type","5");
            presenter.presenter(map,"api/user/operateLogList?","GET", token+"");
            DataUtile.getpopupwindow(HistoryActivity.this,HistoryActivity_LinearLayout);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    private void initData() {
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager mPerfectCourse = new LinearLayoutManager(this);
        mPerfectCourse.setOrientation(LinearLayoutManager.VERTICAL);
        HistoryActivity_recycler.setLayoutManager(mPerfectCourse);
        historyAdapter = new HistoryAdapter(list,this);
        HistoryActivity_recycler.setAdapter(historyAdapter);
        HistoryActivity_recycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                HistoryActivity_recycler.refreshComplete();
                DataUtile.dissePopup();
            }

            @Override
            public void onLoadMore() {
//                if (InterfaceType.equals("本机")){
//                    //上拉加载
//                    size+=20;
//                    size1=list.size();
//                    list.clear();
//                    if (Alllist.size()>size){
//                        for (int i = 0; i < size; i++) {
//                            list.add(Alllist.get(i));
//                        }
//                    }else {
//                        list.addAll(Alllist);
//                        Toast.makeText(HistoryActivity.this,"没有更多记录",Toast.LENGTH_SHORT).show();
//                    }
//                    historyAdapter.notifyDataSetChanged();
//                    HistoryActivity_recycler.loadMoreComplete();
//                }else {
                DataUtile.dissePopup();
                DataUtile.getpopupwindow(HistoryActivity.this,HistoryActivity_LinearLayout);
                    pageSize+=20;
                    Map<String ,Object> map=new HashMap<>();
                    map.put("pageNO","1");
                    map.put("pageSize",pageSize+"");
                    map.put("createTime",dataTime);
                    map.put("type","5");
                    presenter.presenter(map,"api/user/operateLogList?","GET", token+"");
                }

    //        }
        });
        LinearLayoutManager mPerfectCourse1 = new LinearLayoutManager(this);
        mPerfectCourse1.setOrientation(LinearLayoutManager.HORIZONTAL);
        HistoryActivity_DataView.setLayoutManager(mPerfectCourse1);
        historyDataAdapter=new HistoryDataAdapter(DataList,this);
        HistoryActivity_DataView.setAdapter(historyDataAdapter);
        historyDataAdapter.setListener(new HistoryDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < DataList.size(); i++) {
                    DataList.get(i).setSelect(false);
                }
                DataList.get(position).setSelect(true);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                historyDataAdapter.notifyDataSetChanged();
                String data=simpleDateFormat.format(DataList.get(position).getDayTim());
                String Cdata=data+" 00:00:00";//开始时间
                LogUtils.e("****************"+Cdata);
                String edata=data+" 23:59:59";
//                if (InterfaceType.equals("本机")){
//                    getUsageList(HistoryActivity.this, DataUtile.compareDayTime(Cdata),DataUtile.compareDayTime(edata));
//                }else {
                dataTime=DataUtile.compareDayTime(Cdata);
                Map<String ,Object> map=new HashMap<>();
                map.put("pageNO","1");
                map.put("pageSize",pageSize+"");
                map.put("createTime",dataTime);
                map.put("type","5");
                presenter.presenter(map,"api/user/operateLogList?","GET", token+"");
//          }
            }
        });
            //日期控件
            DataList.clear();
            Calendar calendar = Calendar.getInstance();
            // 获取本周的第一天
            int firstDayOfWeek = calendar.getFirstDayOfWeek();
            WeekDayBean[] weekDayBeans=new WeekDayBean[7];
            for (int i = 0; i < 7; i++) {
                calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + i);
                WeekDayBean weekDay = new WeekDayBean();
                // 获取星期的显示名称，例如：周一、星期一、Monday等等
                weekDay.setWeek(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINESE));
                weekDay.setDay(new SimpleDateFormat("dd").format(calendar.getTime()));
                SimpleDateFormat sdf =new SimpleDateFormat("dd",Locale.getDefault());
                long timecurrentTimeMillis = System.currentTimeMillis();
                String time1 = sdf.format(timecurrentTimeMillis);
                if (time1.equals((new SimpleDateFormat("dd").format(calendar.getTime())))){
                    weekDay.setSelect(true);
                }else {
                    weekDay.setSelect(false);
                }
                weekDay.setDayTim(calendar.getTime().getTime());
                weekDayBeans[i]=weekDay;
            }
            DataList.addAll(Arrays.asList(weekDayBeans));
            historyDataAdapter.notifyDataSetChanged();

    }
    @SuppressWarnings("ResourceType")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getUsageList(Context context,long amount ,long EData) {
        list.clear();
        Alllist.clear();
        amount=amount*1000;
        EData=EData*1000;
        Calendar beginCal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            beginCal = Calendar.getInstance();
            beginCal.set(Calendar.DATE, 31);
            beginCal.set(Calendar.MONTH, 12);
            beginCal.set(Calendar.YEAR, 1970);
            android.icu.util.Calendar endCal = android.icu.util.Calendar.getInstance();
            UsageStatsManager manager=(UsageStatsManager)getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
            List<UsageStats> stats=manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,beginCal.getTimeInMillis(),endCal.getTimeInMillis());
//            for(UsageStats us:stats){
//                if(us.getLastTimeUsed()>amount&&us.getLastTimeUsed()<EData){
//                    Alllist.add(us);
//                }
//            }
            if (Alllist.size()>20){
                for (int i = 0; i < 20; i++) {
                    list.add(Alllist.get(i));
                }
            }else {
                list.addAll(Alllist);
            }
            historyAdapter.notifyDataSetChanged();
        }
    }
    private void initFind() {
        presenter=new MyPresenter(this);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText("行为记录");
        HistoryActivity_recycler=findViewById(R.id.HistoryActivity_recycler);
        HistoryActivity_DataView=findViewById(R.id.HistoryActivity_DataView);
        HistoryActivity_LinearLayout=findViewById(R.id.HistoryActivity_LinearLayout);
    }

    @Override
    public void View(String o) {
        DataUtile.dissePopup();
        HistoryActivity_recycler.refreshComplete();
        HistoryActivity_recycler.loadMoreComplete();
        list.clear();
        Gson gson = new Gson();
        HistoryActivityBean chaKanDianZiDiaBean = gson.fromJson(o, HistoryActivityBean.class);
        if (chaKanDianZiDiaBean.getCode() == 200) {
            if (chaKanDianZiDiaBean.getData().getLists() != null && chaKanDianZiDiaBean.getData().getLists().size() > 0) {
                List<HistoryBean> lists=new ArrayList<>();
                HistoryBean[] historyBean;
                for (int i = 0; i < chaKanDianZiDiaBean.getData().getLists().size(); i++) {
                    historyBean=gson.fromJson(chaKanDianZiDiaBean.getData().getLists().get(i).getContent(),HistoryBean[].class);
                    for (int j = 0; j < historyBean.length; j++) {
                        lists.add(historyBean[j]);
                    }
                }
                list.addAll(SetDeduplication(lists));
                historyAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"没有更多数据了",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static List<HistoryBean> SetDeduplication(List<HistoryBean> list) {
        for (int i = 0; i < list.size(); i++) {  //外循环是循环的次数
            for (int j = list.size() - 1 ; j > i; j--){  //内循环是 外循环一次比较的次数
                if (list.get(i).getPackageName().equals(list.get(j).getPackageName())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

}