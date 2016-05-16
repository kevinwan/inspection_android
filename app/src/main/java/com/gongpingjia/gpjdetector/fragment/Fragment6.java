package com.gongpingjia.gpjdetector.fragment;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.adapter.ItemListAdapter;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EFragment(R.layout.fragment_6)
public class Fragment6 extends Fragment {
    MainActivity_ mainActivity;
    @ViewById
    TextView banner_title;
    @ViewById
    Button slidingmenu_toggler;
    @ViewById
    ListView item_list;

    ArrayList<kZDBItem> list = null;

    kZDatabase db;

    @AfterViews
    public void afterViews() {
        banner_title.setText("发动机舱检查");
        mainActivity = (MainActivity_)getActivity();
        list = mainActivity.getDB6Items();

        item_list.setAdapter(new ItemListAdapter(mainActivity, list));

        db = mainActivity.getDatabase();
        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                for (kZDBItem item:list) {
                    db.updateItem(item.getKey(), item.getValue());
                }

            }
        });
    }
    @Click
    public void slidingmenu_toggler() {
        mainActivity.getSlidingMenu().toggle();
    }
}
