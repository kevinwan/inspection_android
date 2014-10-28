package com.gongpingjia.gpjdetector.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.EnhancedEditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.global.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


@EFragment(R.layout.fragment_0_1)
public class Fragment0_1 extends Fragment {
    MainActivity_ mainActivity;
    ArrayList<kZDBItem> list;
    @ViewById
    EditText edittext1, edittext2, edittext3, edittext4, edittext5, edittext6, edittext7, edittext8, edittext9, edittext10, edittext15;
    @ViewById
    EnhancedEditText edittext11;
    @ViewById
    RadioGroup radiogroup12;
    @ViewById
    CheckBox checkbox13, checkbox14;

    @ViewById
    LinearLayout layout5;

    View views[];

    PopupWindow mPopupWindow;

    Calendar curCal = Calendar.getInstance();

    ArrayList<HashMap<String, String>> chuxian_list;


    @AfterViews
    public void afterViews() {
        edittext1.requestFocus();
        mainActivity = (MainActivity_) getParentFragment().getActivity();
        list = mainActivity.getDB01Items();
        chuxian_list = mainActivity.getchuxian_list();

        final View popupView = mainActivity.getLayoutInflater().inflate(R.layout.popupview, null);
        final Button btnComplete = (Button) popupView.findViewById(R.id.complete);
        final Button add = (Button) popupView.findViewById(R.id.add);
        final EnhancedEditText money = (EnhancedEditText) popupView.findViewById(R.id.money);
        final EditText note = (EditText) popupView.findViewById(R.id.note);
        final ListView listView = (ListView) popupView.findViewById(R.id.list);

        final chuxianListAdapter adapter = new chuxianListAdapter();

        listView.setAdapter(adapter);

        mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));

        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);

        edittext1.setTag("CX");
        edittext2.setTag("SCSPSJ");
        edittext3.setTag("CCSJ_");
        edittext4.setTag("BGCS");
        edittext5.setTag("ZJBGRQ");
        edittext6.setTag("CPH");
        edittext7.setTag("CJH");
        edittext8.setTag("JQXDQSJ");
        edittext9.setTag("SYXDQSJ");
        edittext10.setTag("NJDQSJ");
        edittext11.setTag("FPJG");
        radiogroup12.setTag("SYXZ");
        checkbox13.setTag("YCZB");
        checkbox14.setTag("BYJL");
        edittext15.setTag("CXZK");

        edittext4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    layout5.setVisibility(View.GONE);
                } else {
                    if (Integer.parseInt(editable.toString()) > 0) {
                        layout5.setVisibility(View.VISIBLE);
                        if (Integer.parseInt(editable.toString()) > 10) {
                            edittext4.setText("10");
                        }
                    } else {
                        layout5.setVisibility(View.GONE);
                    }
                }

            }
        });

        views = new View[]{edittext1, edittext2, edittext3, edittext4, edittext5, edittext6, edittext7, edittext8, edittext9, edittext10, edittext11,
                radiogroup12, checkbox14, checkbox13};

        edittext11.setSuffixText("元");
        edittext11.setSuffixColor(Color.parseColor("#ff585858"));

        edittext2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                Calendar minCal = Calendar.getInstance();
                Calendar maxCal = Calendar.getInstance();

                if (null != mainActivity.shangpai_time) {
                    minCal.set(Calendar.YEAR, mainActivity.shangpai_time[2]);
                    minCal.set(Calendar.MONTH, mainActivity.shangpai_time[3] - 1);

                    maxCal.set(Calendar.YEAR, mainActivity.shangpai_time[0]);
                    maxCal.set(Calendar.MONTH, mainActivity.shangpai_time[1] - 1);

                    //首次上牌时间选择
                    mainActivity.showDateDialog(view, Calendar.getInstance(), minCal, maxCal, false);
                } else {
                    Toast.makeText(mainActivity, "请先选择车型", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        Calendar maxCal = Calendar.getInstance();


        //出厂时间
        getDate(edittext3, null, curCal, false);
        //最近变更时间
        getDate(edittext5, null, curCal, false);
        //交强险到期时间
        maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 3);
        getDate(edittext8, curCal, maxCal, true);
        //商业险到期时间
        getDate(edittext9, curCal, maxCal, true);
        //年检到期时间
        maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 6);
        getDate(edittext10, curCal, maxCal, true);

        edittext1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                mainActivity.startActivityForResult(new Intent().setClass(mainActivity, CategoryActivity.class), Constant.REQUEST_CODE_MODEL);
                return false;
            }
        });

        edittext15.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                mPopupWindow.showAtLocation(getView().findViewById(R.id.root_layout), Gravity.BOTTOM, 0, 0);
                money.setPrefixColor(Color.parseColor("#ff585858"));
                money.setPrefixText("￥");
                money.setSuffixColor(Color.parseColor("#ff585858"));
                money.setSuffixText("元");
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (money.getText().toString().equals("") || note.getText().toString().equals("")) {
                            Toast.makeText(mainActivity, "请输入出险金额和备注信息。", Toast.LENGTH_LONG).show();
                            return;
                        }
                        money.requestFocus();
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("money", money.getText().toString());
                        map.put("note", note.getText().toString());
                        chuxian_list.add(map);
                        money.setText("");
                        note.setText("");
                        adapter.notifyDataSetChanged();
                    }
                });
                btnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
                return false;
            }
        });

    }

    private void getDate(final View view, final Calendar minCal, final Calendar maxCal, final boolean negButton) {
        View.OnTouchListener dateListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                mainActivity.showDateDialog(view, Calendar.getInstance(), minCal, maxCal, negButton);
                return false;
            }
        };
        ((EditText) view).setOnTouchListener(dateListener);
    }

    @UiThread
    public void initView() {
        for (int i = 0; i < views.length; ++i) {
            mainActivity.loadDatatoView(list, views[i]);
        }

        int index = mainActivity.searchIndex("CXZK", list);
        if (-1 != index) {
            if (null != list.get(index).getValue() && !list.get(index).getValue().equals("")) {
                try {
                    chuxian_list = new ArrayList<HashMap<String, String>>();
                    JSONArray jsonArray  = new JSONArray(list.get(index).getValue());
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        String[] item = jsonArray.getString(i).split(",");
                        if (item.length == 2) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("money", item[0]);
                            map.put("note", item[1]);
                            chuxian_list.add(map);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Background
    public void saveDatafromView() {
        for (int i = 0; i < views.length; ++i) {
            mainActivity.saveDatafromView(list, views[i]);
        }

        //出险记录
        JSONArray jsonArray = new JSONArray();
        for (HashMap<String, String> map:chuxian_list) {
            jsonArray.put(map.get("money") + "," + map.get("note"));
        }
        mainActivity.updateDatewithString("CXZK", jsonArray.toString(), list);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    class chuxianListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chuxian_list.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == view) {
                view = LayoutInflater.from(mainActivity).inflate(R.layout.chuxian_list_item, null);
                viewHolder = new ViewHolder((TextView) view.findViewById(R.id.money),
                        (TextView) view.findViewById(R.id.note),
                        (ImageButton) view.findViewById(R.id.delete));
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.money.setText("￥" + chuxian_list.get(position).get("money"));
            viewHolder.note.setText(chuxian_list.get(position).get("note"));
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chuxian_list.remove(position);
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    class ViewHolder {

        public ViewHolder (TextView money, TextView note, ImageButton delete) {
            this.delete = delete;
            this.money = money;
            this.note = note;
        }

        public ImageButton delete;
        public TextView money;
        public TextView note;
    }
}
