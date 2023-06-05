package com.example.myapplication.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.MyPhone.ElectronicFenceActivity;
import com.example.myapplication.adapter.RBaseAdapter;
import com.example.myapplication.adapter.RViewHolder;
import com.example.myapplication.bean.ChaKanDianZiDiaBean;
import com.example.myapplication.bean.DetailListBean;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.DisplayUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看电子围栏
 * create by Sincerly on 9999/9/9 0009
 **/
public class ChaKanDianZiDialog extends Dialog implements ContractInterface.View {
    private Context mContext;
    private OnDialogClickListener listener;
    private static String id="";
    private static String Token="";
    private static View view;
    private ContractInterface.Presenter presenter;
    private RecyclerView recyclerView;
    private List<DetailListBean> list=new ArrayList<>();
    private RBaseAdapter<DetailListBean> adapter;
    private static Activity activity;
    public ChaKanDianZiDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        presenter=new MyPresenter(this);
        this.mContext = context;

    }

    private View init() {
        View view = View.inflate(mContext, R.layout.dialog_add_chakan, null);
        recyclerView=view.findViewById(R.id.recyclerView);
        initList();
        return view;
    }

    private void initList() {
        adapter = new RBaseAdapter<DetailListBean>(mContext, R.layout.item_add_chakan, list) {
            @Override
            protected void fillItem(RViewHolder holder, DetailListBean item, int position) {
                TextView name = holder.getView(R.id.name);
                ImageView close = holder.getView(R.id.close);
                name.setText("" + list.get(position).fenceName);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(item.id+"");
                        dismiss();
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.item(item.detailList);
                        dismiss();
                    }
                });
            }

            @Override
            protected int getViewType(DetailListBean item, int position) {
                return 0;
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        Map<String ,Object> map=new HashMap<>();
        map.put("pageNO","1");
        map.put("pageSize","60");
        map.put("type","4");
        presenter.presenter(map,"api/user/operateLogList?","GET", Token+"");
        DataUtile.getpopupwindow(activity,view);
    }

    private void delete(String id) {
        listener.sure("1",id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(init());
    }
    //    lp.width = DisplayUtils.getDisplayWidth(mContext) * 10 / 10;     lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    public void showDialog() {
        if (!isShowing()) {
            show();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.width = DisplayUtils.getDisplayWidth(mContext) * 9 / 10;
            getWindow().setAttributes(lp);
            getWindow().setGravity(Gravity.BOTTOM);
        }
    }
    public OnDialogClickListener getListener() {
        return listener;
    }

    public void setListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public static ChaKanDianZiDialog show(Context context, OnDialogClickListener listener, String uid, String uToken, View views, Activity activiy) {
        ChaKanDianZiDialog dialog = new ChaKanDianZiDialog(context, R.style.BottomDialogStyles);
        dialog.setListener(listener);
        id=uid;
        Token=uToken;
        view=views;
        activity=activiy;
        dialog.showDialog();
        return dialog;
    }
    //上传电子围栏
    @Override
    public void View(String o) {
        DataUtile.dissePopup();
        Gson gson=new Gson();
        ChaKanDianZiDiaBean chaKanDianZiDiaBean=gson.fromJson(o, ChaKanDianZiDiaBean.class);
        if (chaKanDianZiDiaBean.code==200){
            if (chaKanDianZiDiaBean.data.lists!=null&&chaKanDianZiDiaBean.data.lists.size()>0){
                list.clear();
                DetailListBean[] lists=new DetailListBean[chaKanDianZiDiaBean.data.lists.size()];
                for (int i = 0; i < chaKanDianZiDiaBean.data.lists.size(); i++) {
                    DetailListBean detailListBean=gson.fromJson(chaKanDianZiDiaBean.data.lists.get(i).content,DetailListBean.class);
                    detailListBean.id=chaKanDianZiDiaBean.data.lists.get(i).id;
                    detailListBean.userId=chaKanDianZiDiaBean.data.lists.get(i).userId;
                    detailListBean.type=chaKanDianZiDiaBean.data.lists.get(i).type;
                    lists[i]=detailListBean;
                }
                list.addAll(Arrays.asList(lists));
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(mContext,"没有更多数据",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnDialogClickListener {
        void sure(String type,String id);
        void item(List<DetailListBean.DetailListDTO> detailList);
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }
}
