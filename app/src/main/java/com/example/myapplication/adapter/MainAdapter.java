package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.MainBean;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    List<MainBean> list ;
    Context context;
    LayoutInflater inflater;
    OnItemClickListener listener;
    public MainAdapter(List<MainBean> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.MainAdapter_RelativeLayoutTextView.setText(list.get(position).getName());
        if (list.get(position).isType()){
            holder.MainAdapter_RelativeLayoutSwitch.setBackgroundResource(R.mipmap.switch_close);
        }else {
            holder.MainAdapter_RelativeLayoutSwitch.setBackgroundResource(R.mipmap.switch_open);
        }
        holder.MainAdapter_RelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout MainAdapter_RelativeLayout;
        TextView MainAdapter_RelativeLayoutTextView;
        ImageView MainAdapter_RelativeLayoutSwitch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MainAdapter_RelativeLayout = itemView.findViewById(R.id.MainAdapter_RelativeLayout);
            MainAdapter_RelativeLayoutTextView = itemView.findViewById(R.id.MainAdapter_RelativeLayoutTextView);
            MainAdapter_RelativeLayoutSwitch = itemView.findViewById(R.id.MainAdapter_RelativeLayoutSwitch);

        }
    }
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
