package com.gongpingjia.gpjdetector.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.adapter.CarSelectHeadAdapter;
import com.gongpingjia.gpjdetector.adapter.CityAdapter;
import com.gongpingjia.gpjdetector.data.CityData;
import com.gongpingjia.gpjdetector.data.Province;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.kZViews.MyGridView;
import com.gongpingjia.gpjdetector.utility.UserLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择城市界面   wang
 *
 * @author Administrator
 */
public class CityActivity extends BaseActivity {

    private CityData mCityData;

    private ExpandableListView mExpListView;

    private CityAdapter mAdapter;

    MyGridView gridV;

    CarSelectHeadAdapter headAapter;

    List<String> labelList;

    boolean isletterorder = false;

    public ExpandableListView.OnChildClickListener mOnClcikListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            String city = mAdapter.getGroup(groupPosition).getCitylist().get(childPosition).getName();
            Intent it = getIntent();
            it.putExtra("city", city);
            setResult(Activity.RESULT_OK, it);
            finish();
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        ImageView title_back = (ImageView)findViewById(R.id.title_back);
        title_back.setBackgroundResource(R.drawable.back);
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCityData = ((GPJApplication) getApplication()).getCityData();

        mExpListView = (ExpandableListView) findViewById(R.id.list_view);

//        mExpListView.setGroupIndicator(null);
//        View headV = LayoutInflater.from(CitySelectAvtivity.this).inflate(R.layout.head_city_select, null);

//        mExpListView.addHeaderView(headV);

        labelList = new ArrayList<String>();
        labelList.add("A");
        labelList.add("B");
        labelList.add("C");
        labelList.add("F");
        labelList.add("G");
        labelList.add("H");
        labelList.add("J");
        labelList.add("L");
        labelList.add("N");
        labelList.add("Q");
        labelList.add("S");
        labelList.add("T");
        labelList.add("X");
        labelList.add("Y");
        labelList.add("Z");


        gridV = (MyGridView) findViewById(R.id.gridView);
        gridV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    isletterorder = true;
                    String letter = labelList.get(position - 1);
                    mExpListView.setSelectedGroup(getProvinceListByLetter(letter));
//                    mAdapter.setData(getProvinceListByLetter(letter));
                }
            }
        });
        headAapter = new CarSelectHeadAdapter(CityActivity.this, labelList);
        gridV.setAdapter(headAapter);

        TextView currentCity = (TextView) findViewById(R.id.city);

        if (UserLocation.getInstance().isIslocation()) {
            currentCity.setText( UserLocation.getInstance().getCity());
        } else {
            currentCity.setText("GPS定位未开启,请开启后重试");
            currentCity.setTextColor(getResources().getColor(R.color.text_title_color));
        }

        findViewById(R.id.location_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserLocation.getInstance().isIslocation()) {
                    Intent it = getIntent();
                    it.putExtra("city", UserLocation.getInstance().getCity());
                    setResult(Activity.RESULT_OK, it);
                    finish();
                } else {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.ok).setVisibility(View.GONE);

        mAdapter = new CityAdapter(CityActivity.this, mCityData.getNoProvinceData());

        mExpListView.setAdapter(mAdapter);
        mExpListView.setGroupIndicator(null);
        mExpListView.setOnChildClickListener(mOnClcikListener);
        mExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });
        setTitle("选择城市");
    }


    private int getProvinceListByLetter(String letter) {
        int position = 0;

        List<Province> provinceList = mCityData.getNoProvinceData();
        for (int i = 0; i < provinceList.size(); i++) {

            Province province = provinceList.get(i);


            if (!TextUtils.isEmpty(province.getLetter()) && province.getLetter().equals(letter)) {
                position = i;
                break;
            }

        }

        return position;

    }


}
