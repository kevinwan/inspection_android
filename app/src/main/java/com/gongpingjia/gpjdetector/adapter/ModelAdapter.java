package com.gongpingjia.gpjdetector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.kZViews.NetworkImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/23.
 */
public class ModelAdapter extends BaseExpandableListAdapter {
    Context context;
    int groupselectPosition = -1;
    int childselectPosition = -1;
    private Map<String, List<Map<String, String>>> modelUnderBrand;
    private List<String> list;

    public ModelAdapter(Context context) {
        this.context = context;
    }


    public void setData(Map<String, List<Map<String, String>>> modelUnderBrand,List<String> list) {
        this.modelUnderBrand = modelUnderBrand;
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return modelUnderBrand.get(list.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return modelUnderBrand.get(list.get(groupPosition)).size();
    }


    public void setCurrentSelectPosition(int goupposition, int childposition) {
        this.groupselectPosition = goupposition;
        this.childselectPosition = childposition;
        notifyDataSetChanged();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_model_item, null);
        }


        NetworkImageView picI = (NetworkImageView) convertView.findViewById(R.id.modelLogo);
        TextView nameT = (TextView) convertView.findViewById(R.id.modelText);
        ImageView currentI = (ImageView) convertView.findViewById(R.id.current);
        View BgV = convertView.findViewById(R.id.bg);


        HashMap<String, String> jo = (HashMap<String, String>) getChild(groupPosition, childPosition);


        nameT.setText(jo.get("name"));
        String filename = jo.get("logo_img");
        if (null != filename && !filename.isEmpty() && !filename.equals("")) {
            String model_logo_url =
                    Constant.IMG_DOMAIN
                            + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                            + filename + "?imageView2/0/w/200/h/100";

//                ImageLoad.LoadImage(picI, model_logo_url, R.drawable.brandnull, R.drawable.brandnull);
            Glide.with(context).load(model_logo_url).placeholder(R.drawable.brandnull).into(picI);
        } else {
            picI.setImageResource(R.drawable.carnull);
        }

        if (groupselectPosition == groupPosition && childselectPosition == childPosition) {
            currentI.setVisibility(View.VISIBLE);
            BgV.setBackgroundColor(context.getResources().getColor(R.color.select_list_bg));
        } else {
            currentI.setVisibility(View.INVISIBLE);
            BgV.setBackgroundColor(context.getResources().getColor(R.color.nothing));
        }

        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return modelUnderBrand.get(list.get(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return modelUnderBrand.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_model_group_list, null);
        }

        TextView nameT = (TextView) convertView.findViewById(R.id.name);
        nameT.setText(list.get(groupPosition));
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

