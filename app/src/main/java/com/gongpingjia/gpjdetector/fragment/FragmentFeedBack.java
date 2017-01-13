package com.gongpingjia.gpjdetector.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


/**
 * Created by Administrator on 2016/3/23.
 */
public class FragmentFeedBack extends BaseFragment {


    View mainV;
    MainActivity_ mainActivity;

    EditText contentE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainV = inflater.inflate(R.layout.fragment_feedback, null);
        initView();
        return mainV;
    }

    private void initView() {
        mainActivity = (MainActivity_) getActivity();
        TextView banner_titleT = (TextView) mainV.findViewById(R.id.banner_title);
        banner_titleT.setText("车况补充说明");
        contentE = (EditText) mainV.findViewById(R.id.content);
        contentE.setTag("JCBG");

        Cursor cursor;
        cursor = mainActivity.getDatabase().getDBItems(8);
        if (null != cursor) {
            contentE.setText(cursor.getString(2));
        }
        cursor.close();


        contentE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(contentE.getText().toString())) {
                    mainActivity.menu_status[7] = true;
                    mainActivity.content = contentE.getText().toString();
                } else {
                    mainActivity.menu_status[7] = false;
                }

            }
        });

        mainV.findViewById(R.id.slidingmenu_toggler).setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                mainActivity.getSlidingMenu().toggle();
                                                                            }
                                                                        }


        );

        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                saveDatafromView();
            }
        });

    }

    public void saveDatafromView() {
        mainActivity.getDatabase().updateItem("JCBG", contentE.getText().toString());
    }

}
