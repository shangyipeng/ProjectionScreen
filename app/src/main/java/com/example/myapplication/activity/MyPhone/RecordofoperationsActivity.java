package com.example.myapplication.activity.MyPhone;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.example.myapplication.adapter.RecordofoperationsAdapter;
import com.example.myapplication.bean.RecordofoperationsBean;
import com.example.myapplication.utile.DataUtile;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作记录
 */
public class RecordofoperationsActivity extends BaseActivity implements ContractInterface.View {
    private XRecyclerView RecordofoperationsActivity_recycler;
    private LinearLayout incloud_finish;//返回
    private TextView incloud_title;//头部标题
    private ContractInterface.Presenter presenter;
    private RecordofoperationsAdapter recordofoperationsAdapter;
    private int page=1;
    private List<RecordofoperationsBean.DataDTO.ListsDTO> list=new ArrayList<>();
    private String Token="";
    private LinearLayout RecordofoperationsActivity_LinearLayout;
    private String type;
    private String title;

    @Override
    public int getLayout() {
        return R.layout.activity_recordofoperations;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        Token=getIntent().getStringExtra("Token");
        type=getIntent().getStringExtra("type");
        title=getIntent().getStringExtra("title");
        initFind();
        initData();
    }
    //数据操作
    private void initData() {
        //返回
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager mPerfectCourse = new LinearLayoutManager(this);
        mPerfectCourse.setOrientation(LinearLayoutManager.VERTICAL);
        RecordofoperationsActivity_recycler.setLayoutManager(mPerfectCourse);
        recordofoperationsAdapter = new RecordofoperationsAdapter(list,this);
        RecordofoperationsActivity_recycler.setAdapter(recordofoperationsAdapter);
        recordofoperationsAdapter.setListener(new RecordofoperationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,String content) {
                Toast.makeText(RecordofoperationsActivity.this,content,Toast.LENGTH_SHORT).show();
            }
        });
        RecordofoperationsActivity_recycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                RecordofoperationsActivity_recycler.refreshComplete();
                DataUtile.dissePopup();
            }

            @Override
            public void onLoadMore() {
                DataUtile.dissePopup();
                DataUtile.getpopupwindow(RecordofoperationsActivity.this,RecordofoperationsActivity_LinearLayout);
                //上拉加载
                page++;
                Map<String, Object> map=new HashMap<>();
                map.put("page",page+"");
                map.put("size","20");
                map.put("type",type);
                presenter.presenter(map,"api/user/operateLogList","GET", Token+"");
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus){
            Map<String, Object> map=new HashMap<>();
            map.put("page","1");
            map.put("size","20");
            map.put("type",type);
            presenter.presenter(map,"api/user/operateLogList","GET", Token+"");
            DataUtile.getpopupwindow(RecordofoperationsActivity.this,RecordofoperationsActivity_LinearLayout);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    //初始化
    private void initFind() {
        presenter=new MyPresenter(this);
        RecordofoperationsActivity_LinearLayout=findViewById(R.id.RecordofoperationsActivity_LinearLayout);
        RecordofoperationsActivity_recycler=findViewById(R.id.RecordofoperationsActivity_recycler);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText(title);
    }
    //操作记录接口返回
    @Override
    public void View(String o) {
        DataUtile.dissePopup();
        RecordofoperationsActivity_recycler.loadMoreComplete();
        RecordofoperationsActivity_recycler.refreshComplete();
        Gson gson=new Gson();
        RecordofoperationsBean baseBean=gson.fromJson(o, RecordofoperationsBean.class);
        if (baseBean.code==200){
            if (baseBean.data.lists!=null&&baseBean.data.lists.size()>0){
                List<RecordofoperationsBean.DataDTO.ListsDTO> array =baseBean.data.lists;
                if (array.size() > 0) {
                    boolean isExists = false;
                    for (int i = 0; i < array.size(); i++) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).content.equals(array.get(i).content)) {
                                isExists = true;
                                continue;
                            }
                        }
                        if (!isExists) {
                            list.add(array.get(i));
                        }
                        isExists = false;
                    }

                    recordofoperationsAdapter.notifyDataSetChanged();
                }
            }else {
                Toast.makeText(RecordofoperationsActivity.this,"没有更多操作记录",Toast.LENGTH_SHORT).show();
            }

        }

    }
}