package com.gongpingjia.gpjdetector.fragment;

import android.view.View;
import android.widget.CheckBox;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.kZDBItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EFragment(R.layout.fragment_0_3)
public class Fragment0_3 extends BaseFragment {
	@ViewById
    CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6,
            checkbox7, checkbox8, checkbox9, checkbox10, checkbox11, checkbox12,
            checkbox13;
    View views[];

    MainActivity_ mainActivity;
    ArrayList<kZDBItem> list;

    @AfterViews
    public void afterViews() {
        checkbox1.setTag("ZPZY");
        checkbox2.setTag("TC_");
        checkbox3.setTag("DGNFXP");
        checkbox4.setTag("DSXH");
        checkbox5.setTag("DDZY");
        checkbox6.setTag("JYZY");
        checkbox7.setTag("ZYJR");
        checkbox8.setTag("ESP");
        checkbox9.setTag("ZDKT");
        checkbox10.setTag("YKYS");
        checkbox11.setTag("DCYX");
        checkbox12.setTag("GPSDH");
        checkbox13.setTag("YJQD");

        views = new View[]{checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6,
                checkbox7, checkbox8, checkbox9, checkbox10, checkbox11, checkbox12,
                checkbox13};
        mainActivity = (MainActivity_) getParentFragment().getActivity();
        list = mainActivity.getDB03Items();

    }

    public void initView() {
        for (int i = 0; i < views.length; ++i) {
            mainActivity.loadDatatoView(list, views[i]);
        }
    }

    @Background
    void saveDatafromView() {
        for (int i = 0; i < views.length; ++i) {
            mainActivity.saveDatafromView(list, views);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}
