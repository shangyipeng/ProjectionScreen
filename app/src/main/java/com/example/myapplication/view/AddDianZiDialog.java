package com.example.myapplication.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.utile.DisplayUtils;

/**
 * 创建电子围栏
 * create by Sincerly on 9999/9/9 0009
 **/
public class AddDianZiDialog extends Dialog {
    private Context mContext;
    private OnDialogClickListener listener;
    private TextView text1,text2;
    private EditText name;
    private CheckBox check;

    public AddDianZiDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;

    }

    private View init() {
        View view = View.inflate(mContext, R.layout.dialog_add_dianzi, null);
        text1 = view.findViewById(R.id.text1);
        name = view.findViewById(R.id.name);
        check = view.findViewById(R.id.check);
        text2 = view.findViewById(R.id.text2);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.sure(name.getText().toString().trim(),check.isChecked());
            }
        });
        return view;
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
            lp.width = DisplayUtils.getDisplayWidth(mContext) * 10 / 10;
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

    public static AddDianZiDialog show(Context context, OnDialogClickListener listener) {
        AddDianZiDialog dialog = new AddDianZiDialog(context, R.style.BottomDialogStyles);
        dialog.setListener(listener);
        dialog.showDialog();
        return dialog;
    }

    public interface OnDialogClickListener {
        void sure(String name,boolean notify);
    }
}
