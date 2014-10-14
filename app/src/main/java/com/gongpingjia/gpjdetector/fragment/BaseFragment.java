package com.gongpingjia.gpjdetector.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity;
import com.gongpingjia.gpjdetector.activity.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_base)
public class BaseFragment extends Fragment {
	@ViewById
    Button slidingmenu_toggler, extra;
    @ViewById
    TextView banner_title;

    MainActivity mainActivity;

    @Click
    public void slidingmenu_toggler() {
        mainActivity.getSlidingMenu().toggle();
    }

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity) getActivity();
    }

    void setBanner_title(String title) {
        if (null != title) {
            banner_title.setText(title);
        }
    }

    void setExtraButton(String text, View.OnClickListener listener) {
        if (null == text) return;
        extra.setText(text);
        if (null != listener) {
            extra.setOnClickListener(listener);
        }
    }
}
