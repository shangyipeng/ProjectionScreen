package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.ContactsBean;
import com.example.myapplication.bean.PhoneBean;
import com.example.myapplication.bean.RecordofoperationsBean;
import com.example.myapplication.bean.SMSBean;
import com.example.myapplication.utile.DataUtile;
import com.google.gson.Gson;

import java.util.List;

/**
 * 操作记录适配器
 */
public class RecordofoperationsAdapter  extends RecyclerView.Adapter<RecordofoperationsAdapter.Holder> {
    List<RecordofoperationsBean.DataDTO.ListsDTO> list ;
    Context context;
    OnItemClickListener listener;
    public RecordofoperationsAdapter(List<RecordofoperationsBean.DataDTO.ListsDTO> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recordofoperations_adapter,viewGroup,false);
        return new Holder(view);
    }
    public void setData(List<RecordofoperationsBean.DataDTO.ListsDTO> mData) {
        this.list = mData;
        notifyDataSetChanged();//通知更新
    }
    @Override
    public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int i) {
        String content=list.get(i).content;
        int type=list.get(i).type;//"1"来表示通讯录，"2"=电话，"3"=短信
        Gson gson=new Gson();
        if (type==1){//通讯录
            ContactsBean contactsBean=gson.fromJson(list.get(i).content,ContactsBean.class);
            String Number=contactsBean.getNumber();
            if (Number.indexOf("+86 ")!=-1){
                Number=Number.replace("+86 ","");
            }
            if (contactsBean.getType().equals("新增")){
                holder.RecordofoperationsAdapter_Content.setText("通讯录新增联系人 "+Number);
            }else {
                holder.RecordofoperationsAdapter_Content.setText("通讯录删除联系人 "+Number);
            }
            holder.RecordofoperationsAdapter_Phone.setText(Number+"");
            String time=list.get(i).createTime;
            holder.RecordofoperationsAdapter_Time.setText(time+"");
        }else if (type==2){//电话
            PhoneBean phoneBean=gson.fromJson(list.get(i).content,PhoneBean.class);
            if (phoneBean.getType().equals("拨打")){
                holder.RecordofoperationsAdapter_Content.setText("拨打电话 "+phoneBean.getPhone());
            }else if (phoneBean.getType().equals("响铃")){
                holder.RecordofoperationsAdapter_Content.setText("来电 "+phoneBean.getPhone());
            }else if (phoneBean.getType().equals("接听")){
                holder.RecordofoperationsAdapter_Content.setText("接听电话 "+phoneBean.getPhone());
            }else if (phoneBean.getType().equals("新增")){
                holder.RecordofoperationsAdapter_Content.setText("新增电话 "+phoneBean.getPhone());
            }else if (phoneBean.getType().equals("删除")){
                holder.RecordofoperationsAdapter_Content.setText("删除电话 "+phoneBean.getPhone());
            }
            holder.RecordofoperationsAdapter_Phone.setText(phoneBean.getPhone()+"");
            String time=list.get(i).createTime;
            holder.RecordofoperationsAdapter_Time.setText(time+"");
        }else if (type==3){//短信
            SMSBean smsBean=gson.fromJson(list.get(i).content,SMSBean.class);
            if (smsBean.getType().equals("接收")){
                if (smsBean.isDelete()){//删除
                    holder.RecordofoperationsAdapter_Content.setText("收件箱删除短信 "+smsBean.getBody());
                }else {//新增
                    holder.RecordofoperationsAdapter_Content.setText("收件箱新增短信 "+smsBean.getBody());
                }
            }else if (smsBean.getType().equals("发送")){
                if (smsBean.isDelete()){//删除
                    holder.RecordofoperationsAdapter_Content.setText("发件箱删除短信 "+smsBean.getBody());
                }else {//新增
                    holder.RecordofoperationsAdapter_Content.setText("发件箱新增短信 "+smsBean.getBody());
                }
            }
            String Number=smsBean.getAddress();
            if (Number.indexOf("+86 ")!=-1){
                Number=Number.replace("+86 ","");
            }
            holder.RecordofoperationsAdapter_Phone.setText(Number+"");
            String time=list.get(i).createTime;
            holder.RecordofoperationsAdapter_Time.setText(time+"");
        }

        holder.RecordofoperationsAdapterLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(i,holder.RecordofoperationsAdapter_Content.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private TextView RecordofoperationsAdapter_Content;
        private TextView RecordofoperationsAdapter_Time;
        private TextView RecordofoperationsAdapter_Phone;
        private LinearLayout RecordofoperationsAdapterLinearLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            RecordofoperationsAdapterLinearLayout=itemView.findViewById(R.id.RecordofoperationsAdapterLinearLayout);
            RecordofoperationsAdapter_Content=itemView.findViewById(R.id.RecordofoperationsAdapter_Content);
            RecordofoperationsAdapter_Time=itemView.findViewById(R.id.RecordofoperationsAdapter_Time);
            RecordofoperationsAdapter_Phone=itemView.findViewById(R.id.RecordofoperationsAdapter_Phone);
        }
    }
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position,String content);
    }

}
