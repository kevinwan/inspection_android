package com.gongpingjia.gpjdetector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class ModelDetailYearAadpter extends BaseAdapter {
    Context mContext;

    List<HashMap<String, Object>> list;

    LayoutInflater mLayoutInflater;

    int currentposition = -1;

    public ModelDetailYearAadpter(Context context, List<HashMap<String, Object>> list) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.list = list;
    }


    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public HashMap<String, Object> getItem(int position) {
        return list.get(position);
    }

    public void setCurrentPosition(int position) {
        currentposition = position;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_year, null);
        }

        TextView nameT = (TextView) convertView.findViewById(R.id.name);
        HashMap map = getItem(position);
//        TextView orderT = (TextView) convertView.findViewById(R.id.order);

//            orderT.setVisibility(View.GONE);
        nameT.setText(map.get("year").toString());
        if (position == currentposition) {
            nameT.setBackgroundResource(R.drawable.fillet_10_blue_bg);
            nameT.setTextColor(mContext.getResources().getColor(R.color.text_blue));
        } else {
            nameT.setBackgroundResource(R.drawable.fillet_10_grey_bg);
            nameT.setTextColor(mContext.getResources().getColor(R.color.text_grey));
        }

        return convertView;
    }
}

