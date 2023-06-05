package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.bean.HistoryBean;
import com.example.myapplication.utile.DataUtile;

import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    List<HistoryBean> list ;
    Context context;
    OnItemClickListener listener;
    public HistoryAdapter(List<HistoryBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_adapter,viewGroup,false);
        return new Holder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int i) {
        try {
            if (list.get(i).getLastTimeUsed()!=null&&!list.get(i).getLastTimeUsed().isEmpty()){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                list.get(i).getLastTimeUsed();
                String LastTimeUse=format.format(new Date(Long.parseLong(list.get(i).getLastTimeUsed())));//上次使用后结束时间
                holder.HistoryAdapter_Time.setText( LastTimeUse+"");
            }else {
                holder.HistoryAdapter_Time.setText("");
            }
            holder.HistoryAdapter_Name.setText(list.get(i).getPackageName()+"");
            //getLastTimeVisible--上次活动时间
            if(Long.parseLong(list.get(i).getLastTimeVisible()) > 0){
                String da=DataUtile.TimeDayCompare(Long.parseLong(list.get(i).getLastTimeVisible()));
                da=da.substring(0,da.indexOf(" "));
                da.replace(" ","");
                String tad=DataUtile.getdataTime1();
                LogUtils.e(tad);
                if (da.equals(tad)){
                    holder.HistoryAdapter_Elapsed.setText("上次运行 "+DataUtile.TimeDayCompare1(Long.parseLong(list.get(i).getLastTimeVisible())));
                }else {
                    holder.HistoryAdapter_Elapsed.setText("上次运行 "+da);
                }
            }else {
                holder.HistoryAdapter_Elapsed.setText("运行中");
            }
                Glide.with(context)
                        .load(DataUtile.byteToDrawable(list.get(i).getDrawable()))
                        .error(R.mipmap.error_image)
                        .into(holder.HistoryAdapter_ImageView);
            holder.HistoryAdapter_LinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,holder.HistoryAdapter_Name.getText().toString()+"",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name;
    }
    public class Holder extends RecyclerView.ViewHolder{
        private LinearLayout HistoryAdapter_LinearLayout;
        private ImageView HistoryAdapter_ImageView;
        private TextView HistoryAdapter_Name;
        private TextView HistoryAdapter_Time;
        private TextView HistoryAdapter_Elapsed;
        public Holder(@NonNull View itemView) {
            super(itemView);
            HistoryAdapter_LinearLayout=itemView.findViewById(R.id.HistoryAdapter_LinearLayout);
            HistoryAdapter_ImageView=itemView.findViewById(R.id.HistoryAdapter_ImageView);
            HistoryAdapter_Name=itemView.findViewById(R.id.HistoryAdapter_Name);
            HistoryAdapter_Time=itemView.findViewById(R.id.HistoryAdapter_Time);
            HistoryAdapter_Elapsed=itemView.findViewById(R.id.HistoryAdapter_Elapsed);
        }
    }
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
