package com.gongpingjia.gpjdetector.fragment;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.adapter.TabsFragmentPagerAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EFragment(R.layout.fragment_0)
public class Fragment0 extends Fragment {

    private ArrayList<Fragment> fragmentList;
    MainActivity_ mainActivity;
    SlidingMenu menu;

    @ViewById
    public ViewPager vPager;
    @ViewById
    TextView tab_0, tab_1, tab_2;
    @ViewById
    Button slidingmenu_toggler, extra;
    @ViewById
    TextView banner_title;

    public Fragment0_1_ fragment0_1;
    public Fragment0_2_ fragment0_2;
    public Fragment0_3_ fragment0_3;


    @Click
    public void slidingmenu_toggler() {
        mainActivity.getSlidingMenu().toggle();
    }

    private int currIndex = 0;
    private Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void afterViews () {
        tab_0.setOnClickListener(new TabsOnClickListener(0));
        tab_1.setOnClickListener(new TabsOnClickListener(1));
        tab_2.setOnClickListener(new TabsOnClickListener(2));

        mainActivity = (MainActivity_) getActivity();
        menu = mainActivity.getSlidingMenu();
        resources = mainActivity.getResources();
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        banner_title.setText("车辆信息");

        initViewPager();
    }

    private void initViewPager() {
        fragmentList = new ArrayList<Fragment>();
        fragment0_1 = new Fragment0_1_();
        fragment0_2 = new Fragment0_2_();
        fragment0_3 = new Fragment0_3_();

        fragmentList.add(fragment0_1);
        fragmentList.add(fragment0_2);
        fragmentList.add(fragment0_3);

        currIndex = 0;
        vPager.setAdapter(new TabsFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        vPager.setCurrentItem(0);
        vPager.setOnPageChangeListener(new TabsOnPageChangeListener());
        vPager.setOffscreenPageLimit(2);

        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                saveData();
            }
        });
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
                    if (currIndex == 1) {
                        tab_1.setTextColor(resources.getColor(R.color.tab_text_off));
                        tab_1.setBackgroundDrawable(new ColorDrawable(resources.getColor(R.color.white)));
                    } else if (currIndex == 2) {
                        tab_2.setTextColor(resources.getColor(R.color.tab_text_off));
                        tab_2.setBackgroundResource(R.drawable.right_off_bg);
                    }
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    tab_0.setTextColor(resources.getColor(R.color.white));
                    tab_0.setBackgroundResource(R.drawable.left_on_bg);
                    break;
                case 1:
                    if (currIndex == 0) {
                        tab_0.setTextColor(resources.getColor(R.color.tab_text_off));
                        tab_0.setBackgroundResource(R.drawable.left_off_bg);
                    } else if (currIndex == 2) {
                        tab_2.setTextColor(resources.getColor(R.color.tab_text_off));
                        tab_2.setBackgroundResource(R.drawable.right_off_bg);
                    }
                    tab_1.setTextColor(resources.getColor(R.color.white));
                    tab_1.setBackgroundResource(R.drawable.right_on_bg);
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    mainActivity.menu_status[0] = true;
                    break;
                case 2:
                    if (currIndex == 0) {
                        tab_0.setTextColor(resources.getColor(R.color.tab_text_off));
                        tab_0.setBackgroundResource(R.drawable.left_off_bg);
                    } else if (currIndex == 1) {
                        tab_1.setTextColor(resources.getColor(R.color.tab_text_off));
                        tab_1.setBackgroundDrawable(new ColorDrawable(resources.getColor(R.color.white)));
                    }
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    tab_2.setTextColor(resources.getColor(R.color.white));
                    tab_2.setBackgroundResource(R.drawable.right_on_bg);
                    mainActivity.menu_status[0] = true;
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


    @Background
    public void saveData() {
        fragment0_1.saveDatafromView();
        fragment0_2.saveDatafromView();
        fragment0_3.saveDatafromView();
    }

}
