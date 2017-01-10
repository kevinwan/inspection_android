package com.gongpingjia.gpjdetector.fragment;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;


public class BaseFragment extends Fragment {

    String mPageName;

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPageName = getClass().getSimpleName();
        MobclickAgent.onPageStart(mPageName);
    }
}
