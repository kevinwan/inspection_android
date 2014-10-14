package com.gongpingjia.gpjdetector.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.kZDBItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EFragment(R.layout.fragment_0_3)
public class Fragment0_3 extends Fragment {
	@ViewById
    CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6,
            checkbox7, checkbox8, checkbox9, checkbox10, checkbox11, checkbox12,
            checkbox13, checkbox14, checkbox15, checkbox16, checkbox17, checkbox18, checkbox19;
    @ViewById
    EditText edittext20;

    View views[];

    MainActivity_ mainActivity;
    ArrayList<kZDBItem> list;

    @AfterViews
    public void afterViews() {
        checkbox1.setTag("ZL");
        checkbox2.setTag("ZKS");
        checkbox3.setTag("JYZY");
        checkbox4.setTag("DC");
        checkbox5.setTag("CDDVD");
        checkbox6.setTag("LLG");
        checkbox7.setTag("DDHSJ");
        checkbox8.setTag("KT_");
        checkbox9.setTag("ZP");
        checkbox10.setTag("WYSQD");
        checkbox11.setTag("TC_");
        checkbox12.setTag("GPSDH");
        checkbox13.setTag("DGNFXP");
        checkbox14.setTag("XH");
        checkbox15.setTag("ABS");
        checkbox16.setTag("DCLD_");
        checkbox17.setTag("ZYJR");
        checkbox18.setTag("DDZY");

        checkbox19.setTag("QN");
        edittext20.setTag("QT_");

        views = new View[]{checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6,
                checkbox7, checkbox8, checkbox9, checkbox10, checkbox11, checkbox12,
                checkbox13, checkbox14, checkbox15, checkbox16, checkbox17, checkbox18, checkbox19, edittext20};
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
            mainActivity.saveDatafromView(list, views[i]);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveDatafromView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}
