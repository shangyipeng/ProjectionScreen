package com.example.myapplication.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.RBaseAdapter;
import com.example.myapplication.adapter.RViewHolder;
import com.example.myapplication.utile.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择工作模式
 * create by Sincerly on 9999/9/9 0009
 **/
public class JobPatternDialog extends Dialog {
    private Context mContext;
    private OnDialogClickListener listener;
    private PopupWindow mpopup_custom;
    private List<String> mlistCustom = new ArrayList<>();
    private TextView text,text1,text2,text3;
    private PopupWindow mpopup_custom3;
    private static String onceTime="1";
    private List<String> mlistCustom3 = new ArrayList<>();
    public JobPatternDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    private View init() {
        View view = View.inflate(mContext, R.layout.dialog_job_pattern, null);
        text = view.findViewById(R.id.text);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);
        text3 = view.findViewById(R.id.text3);
        text3.setText(""+onceTime);
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCustom();
            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCustom3();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void initCustom() {
        mlistCustom.clear();
        mlistCustom.add("实时在线模式");
        View popview = getLayoutInflater().inflate(R.layout.item_popwindow, null);
        RecyclerView recyclerView = popview.findViewById(R.id.rv_pop_info);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RBaseAdapter<String> rBaseAdapterAllOffice = new RBaseAdapter<String>(mContext, R.layout.item_item_popwindow, mlistCustom) {
            @Override
            protected void fillItem(RViewHolder holder, String item, int position) {
                TextView text_title = holder.getView(R.id.text_title);
                text_title.setText(item);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text2.setText(""+item);
                        mpopup_custom.dismiss();
                    }
                });
            }

            @Override
            protected int getViewType(String item, int position) {
                return 0;
            }
        };
        recyclerView.setAdapter(rBaseAdapterAllOffice);
        mpopup_custom = new PopupWindow(popview, text2.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup_custom.setOutsideTouchable(true);
        mpopup_custom.setFocusable(true);
        mpopup_custom.showAsDropDown(text2, 0, 0);
    }

    private void initCustom3() {
        mlistCustom3.clear();
        for (int i = 0; i < 10; i++) {
            mlistCustom3.add(""+(i+1));
        }
        View popview = getLayoutInflater().inflate(R.layout.item_popwindow, null);
        RecyclerView recyclerView = popview.findViewById(R.id.rv_pop_info);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RBaseAdapter<String> rBaseAdapterAllOffice = new RBaseAdapter<String>(mContext, R.layout.item_item_popwindow, mlistCustom3) {
            @Override
            protected void fillItem(RViewHolder holder, String item, int position) {
                TextView text_title = holder.getView(R.id.text_title);
                text_title.setText(item);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text3.setText(""+item);
                        mpopup_custom3.dismiss();
                    }
                });
            }

            @Override
            protected int getViewType(String item, int position) {
                return 0;
            }
        };
        recyclerView.setAdapter(rBaseAdapterAllOffice);
        mpopup_custom3 = new PopupWindow(popview, text3.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopup_custom3.setOutsideTouchable(true);
        mpopup_custom3.setFocusable(true);
        mpopup_custom3.showAsDropDown(text3, 0, 0);
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
            lp.width = DisplayUtils.getDisplayWidth(mContext) * 8 / 10;
            getWindow().setAttributes(lp);
            getWindow().setGravity(Gravity.CENTER);
        }
    }
    public OnDialogClickListener getListener() {
        return listener;
    }

    public void setListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public static JobPatternDialog show(Context context, OnDialogClickListener listener,String onceTimes) {
        JobPatternDialog dialog = new JobPatternDialog(context, R.style.BottomDialogStyles);
        dialog.setListener(listener);
        onceTime=onceTimes;
        dialog.showDialog();
        return dialog;
    }

    public interface OnDialogClickListener {
        void sure(String type);
    }
}
