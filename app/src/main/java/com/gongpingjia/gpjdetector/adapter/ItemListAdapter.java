package com.gongpingjia.gpjdetector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.global.Constant;

import java.util.ArrayList;

/**
 * Created by Kooze on 14-8-27.
 */
public class ItemListAdapter extends BaseAdapter {

    Context context;
    ArrayList<kZDBItem> list;
    ArrayList<String> special;

    public ItemListAdapter(Context context, ArrayList<kZDBItem> list) {
        this.context = context;
        this.list = list;
        special = new ArrayList<String>();
        special.add("ZQZL");
        special.add("ZQJZQXGBF");
        special.add("ZAZ");
        special.add("ZBZ");
        special.add("ZCZ");
        special.add("ZHJZQXGBF");
        special.add("YHJZQXGBF");
        special.add("YCZ");
        special.add("YBZ");
        special.add("YAZ");
        special.add("YQJZQXGBF");
        special.add("YQZL");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.radiogroup_items, null);;
            viewHolder = new ViewHolder((TextView)view.findViewById(R.id.name),
                    (RadioGroup)view.findViewById(R.id.items));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.items.setId(position);
        viewHolder.items.setOnCheckedChangeListener(null);

        if (null != list) {
            viewHolder.name.setText(list.get(position).getName());

            String[] optionArray = list.get(position).getOption().split("\\|");
            if (optionArray.length == 2) {
                (view.findViewById(R.id.radio_neg)).setVisibility(View.GONE);
                (view.findViewById(R.id.radio_neu)).setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2.0f));
                ((RadioButton) view.findViewById(R.id.radio_pos)).setText(optionArray[0]);
                ((RadioButton) view.findViewById(R.id.radio_neu)).setText(optionArray[1]);
            } else if (optionArray.length == 3) {
                (view.findViewById(R.id.radio_neg)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.radio_neu)).setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));
                ((RadioButton) view.findViewById(R.id.radio_pos)).setText(optionArray[0]);
                ((RadioButton) view.findViewById(R.id.radio_neu)).setText(optionArray[1]);
                ((RadioButton) view.findViewById(R.id.radio_neg)).setText(optionArray[2]);
            }

            if (special.contains(list.get(position).getKey())) {
                viewHolder.name.setGravity(Gravity.LEFT);
            }




            if (list.get(position).getValue().equals(Constant.value_POS)) {
                viewHolder.items.check(R.id.radio_pos);
            } else if (list.get(position).getValue().equals(Constant.value_NEG)) {
                viewHolder.items.check(R.id.radio_neg);
            } else if (list.get(position).getValue().equals(Constant.value_NEU)) {
                viewHolder.items.check(R.id.radio_neu);
            } else {
                viewHolder.items.clearCheck();
            }
        }

        viewHolder.items.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_pos:
                        list.get(radioGroup.getId()).setValue(Constant.value_POS);
                        break;
                    case R.id.radio_neg:
                        list.get(radioGroup.getId()).setValue(Constant.value_NEG);
                        break;
                    case R.id.radio_neu:
                        list.get(radioGroup.getId()).setValue(Constant.value_NEU);
                        break;
                    default:
                        break;
                }
            }
        });

        if (list.get(position).getPriority().equals("0")) {
            view.setBackgroundColor(Color.parseColor("#fff3f3f3"));
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public class ViewHolder {
        public TextView name;
        public RadioGroup items;

        public ViewHolder(TextView name, RadioGroup items) {
            this.name = name;
            this.items = items;

        }
    }
}
