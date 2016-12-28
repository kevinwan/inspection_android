package com.gongpingjia.gpjdetector.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.EnhancedEditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.kZDBItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EFragment(R.layout.fragment_0_2)
public class Fragment0_2 extends Fragment {
    @ViewById
    EditText edittext1, edittext3, edittext4, edittext5,
            edittext6, edittext7, edittext8, edittext9, edittext10;

    @ViewById
    EnhancedEditText edittext2;

    @ViewById
    LinearLayout baise, heise, yinse, huise, hongse, zongse, hese, lanse,
    jinse, chengse, mise, huangse, zise, qingse, lvse, otherse;

    LinearLayout[] colors;

    @ViewById
    TableLayout colorTable;

    MainActivity_ mainActivity;
    View views[];
    ArrayList<kZDBItem> list;

    View.OnTouchListener listener;

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity_) getParentFragment().getActivity();
        list = mainActivity.getDB02Items();

        edittext1.setTag("YS");
        edittext2.setTag("LCS");
        edittext3.setTag("PL");
//        edittext4.setTag("CLLX");
//        edittext5.setTag("CMS");
//        edittext6.setTag("CSXS");
//        edittext7.setTag("DY");
        edittext8.setTag("BSQ");
//        edittext9.setTag("RLLX");
//        edittext10.setTag("QDFS");

        views = new View[]{edittext1, edittext2, edittext3, edittext4,
                edittext5, edittext6, edittext7, edittext8, edittext9, edittext10};
        colors = new LinearLayout[] {baise, heise, yinse, huise, hongse, zongse, hese, lanse,
                jinse, chengse, mise, huangse, zise, qingse, lvse, otherse};

        listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                mainActivity.showSelectDialog(view);
                return false;
            }
        };

        edittext1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                colorTable.setVisibility(View.VISIBLE);
                return false;
            }
        });

        edittext2.setSuffixColor(Color.parseColor("#ff585858"));
        edittext2.setSuffixText("万公里");

        edittext3.setOnTouchListener(listener);
        edittext5.setOnTouchListener(listener);
        edittext6.setOnTouchListener(listener);
        edittext8.setOnTouchListener(listener);
        edittext10.setOnTouchListener(listener);
        edittext4.setOnTouchListener(listener);
        edittext9.setOnTouchListener(listener);


        for (int i = 0; i < colors.length; ++i) {
            colors[i].setOnClickListener(colorCilck);
        }

    }

    View.OnClickListener colorCilck = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            colorTable.setVisibility(View.GONE);
            switch (v.getId()) {
                case R.id.baise:
                    setColor("白色", 0xffffffff);
                    break;
                case R.id.heise:
                    setColor("黑色", 0xff000000);
                    break;
                case R.id.yinse:
                    setColor("银色", 0xffEDECEA);
                    break;
                case R.id.huise:
                    setColor("灰色", 0xffC5C2BC);
                    break;
                case R.id.hongse:
                    setColor("红色", 0xffff0000);
                    break;
                case R.id.zongse:
                    setColor("棕色", 0xffA87247);
                    break;
                case R.id.hese:
                    setColor("褐色", 0xff7B422B);
                    break;
                case R.id.lanse:
                    setColor("蓝色", 0xff003C66);
                    break;
                case R.id.lise:
                    setColor("栗色", 0xff6A2823);
                    break;
                case R.id.jinse:
                    setColor("金色", 0xffFF9A00);
                    break;
                case R.id.chengse:
                    setColor("橙色", 0xffFF8000);
                    break;
                case R.id.mise:
                    setColor("米色", 0xffEFDFA3);
                    break;
                case R.id.huangse:
                    setColor("黄色", 0xffFEC757);
                    break;
                case R.id.zise:
                    setColor("紫色", 0xffAB00C4);
                    break;
                case R.id.qingse:
                    setColor("青色", 0xff006354);
                    break;
                case R.id.lvse:
                    setColor("绿色", 0xff008830);
                    break;
                case R.id.otherse:
                    setColor("其他", 0xffffffff);
                    break;
                default:
                    break;
            }
        }
    };

    void setColor(String name, int value) {
        edittext1.setText(name);
    }

    public void initView() {
        for (int i = 0; i < views.length; ++i) {
            mainActivity.loadDatatoView(list, views[i]);
        }
    }

    public void saveDatafromView() {
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
