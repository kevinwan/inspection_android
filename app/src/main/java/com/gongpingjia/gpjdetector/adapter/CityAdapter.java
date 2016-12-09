package com.gongpingjia.gpjdetector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.City;
import com.gongpingjia.gpjdetector.data.Province;

import java.util.List;


/**
 * Created by Administrator on 2016/6/24.
 */
public class CityAdapter extends BaseExpandableListAdapter {
    Context context;

    List<Province> list;

    public CityAdapter(Context context, List<Province> list) {
        this.context = context;
        this.list = list;
    }


    public void setData(List<Province> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public City getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getCitylist().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (list.get(groupPosition).getCitylist() == null) {
            return 0;
        }
        return list.get(groupPosition).getCitylist().size();
    }


    @Override
    public View getChildView(final int groupPosition,  int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_new_city_child, null);
        }
        childPosition = childPosition ;
        TextView nameT = (TextView) convertView.findViewById(R.id.name);
        ImageView img = (ImageView) convertView.findViewById(R.id.check);
        img.setVisibility(View.GONE);
        final City city = (City) getChild(groupPosition, childPosition);
        String cityname = city.getName();
        nameT.setText(cityname);

        //全省不显示
//        if(childPosition == 0){
//            convertView.setVisibility(View.GONE);
//        }else{
//            convertView.setVisibility(View.VISIBLE);
//        }
        return convertView;
    }

    @Override
    public Province getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_new_city_group, null);
        }

        TextView nameT = (TextView) convertView.findViewById(R.id.name);
        String prov = getGroup(groupPosition).getName();
        nameT.setText(prov);
        //textView.setTextSize(16);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



}

