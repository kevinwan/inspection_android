package com.gongpingjia.gpjdetector.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.adapter.TabsFragmentPagerAdapter;
import com.gongpingjia.gpjdetector.fragment.Fragment_History_1_;
import com.gongpingjia.gpjdetector.fragment.Fragment_History_2_;
import com.gongpingjia.gpjdetector.utility.kZDatabase;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by Kooze on 14-9-4.
 */
@EActivity(R.layout.activity_history)
public class HistoryActivity extends FragmentActivity {

    @ViewById
    TextView banner_title;

    @ViewById
    Button slidingmenu_toggler, extra;

    @ViewById
    ViewPager vPager;

    @ViewById
    TextView tab_0, tab_1;


    int currIndex = 0;
    Resources resources;

    kZDatabase database;

    public kZDatabase getDatabase() {
        return database;
    }

    @Click
    void extra () {
        onBackPressed();
    }

    @AfterViews
    void afterViews() {
        banner_title.setText("检测定价记录");
        extra.setBackgroundResource(R.drawable.back);
        slidingmenu_toggler.setVisibility(View.INVISIBLE);
        resources = getResources();

        database = new kZDatabase(HistoryActivity.this);

        tab_0.setOnClickListener(new TabsOnClickListener(0));
        tab_1.setOnClickListener(new TabsOnClickListener(1));
        initViewPager();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.getInt("isFinish") == 0) {
                vPager.setCurrentItem(1);
            }
        }
    }

    private ArrayList<Fragment> fragmentList;
    Fragment_History_1_ history_1;
    Fragment_History_2_ history_2;

    private void initViewPager() {
        fragmentList = new ArrayList<Fragment>();
        history_1 = new Fragment_History_1_();
        history_2 = new Fragment_History_2_();

        fragmentList.add(history_1);
        fragmentList.add(history_2);

        currIndex = 0;
        vPager.setAdapter(new TabsFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList));
        vPager.setCurrentItem(0);
        vPager.setOnPageChangeListener(new TabsOnPageChangeListener());

    }

    public class TabsOnClickListener implements View.OnClickListener {
        private int index = 0;

        public TabsOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            vPager.setCurrentItem(index);
        }
    };

    public class TabsOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    tab_1.setTextColor(resources.getColor(R.color.tab_text_off));
                    tab_1.setBackgroundResource(R.drawable.right_off_bg);
                    tab_0.setTextColor(resources.getColor(R.color.white));
                    tab_0.setBackgroundResource(R.drawable.left_on_bg);
                    break;
                case 1:
                    tab_0.setTextColor(resources.getColor(R.color.tab_text_off));
                    tab_0.setBackgroundResource(R.drawable.left_off_bg);
                    tab_1.setTextColor(resources.getColor(R.color.white));
                    tab_1.setBackgroundResource(R.drawable.right_on_bg);
                    break;
            }
            currIndex = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
