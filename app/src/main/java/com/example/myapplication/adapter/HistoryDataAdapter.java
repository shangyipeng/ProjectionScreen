package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.WeekDayBean;

import java.util.List;

public class HistoryDataAdapter extends RecyclerView.Adapter<HistoryDataAdapter.Holder> {
    List<WeekDayBean> list ;
    Context context;
    OnItemClickListener listener;
    public HistoryDataAdapter(List<WeekDayBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.historydata_adapter,viewGroup,false);
        return new Holder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int i) {
        holder.HistoryDataAdapter_week.setText(list.get(i).getWeek()+"");
        holder.HistoryDataAdapter_day.setText(list.get(i).getDay()+"");
        holder.HistoryDataAdapter_LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(i);
            }
        });
        if (list.get(i).isSelect()){
            holder.HistoryDataAdapter_day.setBackgroundResource(R.drawable.historydata_adapter_bg);
            holder.HistoryDataAdapter_day.setTextColor(Color.parseColor("#FFFFFFFF"));
        }else {
            holder.HistoryDataAdapter_day.setBackgroundResource(R.drawable.historydata_adapter_bg1);
            holder.HistoryDataAdapter_day.setTextColor(Color.parseColor("#FF000000"));
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        private TextView HistoryDataAdapter_week;
        private TextView HistoryDataAdapter_day;
        private LinearLayout HistoryDataAdapter_LinearLayout,HistoryDataAdapter_dayLinearLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            HistoryDataAdapter_LinearLayout=itemView.findViewById(R.id.HistoryDataAdapter_LinearLayout);
            HistoryDataAdapter_dayLinearLayout=itemView.findViewById(R.id.HistoryDataAdapter_dayLinearLayout);
            HistoryDataAdapter_week=itemView.findViewById(R.id.HistoryDataAdapter_week);
            HistoryDataAdapter_day=itemView.findViewById(R.id.HistoryDataAdapter_day);
        }
    }
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
