package com.example.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

/**
 * 弹框适配
 */
public class PopupwindowAdapter extends BaseAdapter {
    List<String > list;
    Context context;

    public PopupwindowAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.popupwindow_listview, null);
            holder.PopupwindowAdapterTextView= convertView.findViewById(R.id.PopupwindowAdapterTextView);
            holder.PopupwindowAdapterView=convertView.findViewById(R.id.PopupwindowAdapterView);
            holder.PopupwindowAdapterImageView=convertView.findViewById(R.id.PopupwindowAdapterImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (i==0){
            holder.PopupwindowAdapterView.setVisibility(View.GONE);
        }else {
            holder.PopupwindowAdapterView.setVisibility(View.VISIBLE);
        }
        if (list.get(i).equals("操作记录")){
            holder.PopupwindowAdapterImageView.setBackgroundResource(R.mipmap.popupwindow_adapterimageview);
        }
        holder.PopupwindowAdapterTextView.setText(list.get(i)+"");
        return convertView;
    }
    static class ViewHolder {
        private TextView PopupwindowAdapterTextView;
        private ImageView PopupwindowAdapterImageView;
        private View PopupwindowAdapterView;
    }

}