package com.gongpingjia.gpjdetector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;

import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
public class CarSelectHeadAdapter extends BaseAdapter {
    Context mContext;

    List<String> label;

    LayoutInflater mLayoutInflater;

    public CarSelectHeadAdapter(Context context, List<String> label) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.label = label;
    }


    @Override
    public int getCount() {
        if (label == null) {
            return 0;
        }
        return label.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_city_select_head, null);
        }

        TextView nameT = (TextView) convertView.findViewById(R.id.name);

//        TextView orderT = (TextView) convertView.findViewById(R.id.order);

        if (position == 0) {
//            orderT.setVisibility(View.VISIBLE);
            nameT.setText("省级:");
            nameT.setBackgroundResource(R.color.nothing);
        } else {
//            orderT.setVisibility(View.GONE);
            nameT.setVisibility(View.VISIBLE);
            nameT.setText(label.get(position - 1));
            nameT.setBackgroundResource(R.drawable.fillet_10_white_bg);
        }

        return convertView;
    }
}
