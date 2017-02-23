package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.EnhancedEditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.activity.CityActivity;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.utility.UserLocation;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


@EFragment(R.layout.fragment_0_1)
public class Fragment0 extends BaseFragment {
    SlidingMenu menu;
    MainActivity_ mainActivity;
    ArrayList<kZDBItem> list;

    @ViewById
    TableLayout colorTable;
    @ViewById
    EditText edittext1, edittext2, edittext3, edittext4, edittext5, edittext6, edittext14, edittext15, edittext16, edittext17;
    @ViewById
    EnhancedEditText edittext7, edittext18;
    @ViewById
    RadioGroup radiogroup8;

    @ViewById
    TextView banner_title;

    @ViewById
    Button slidingmenu_toggler, extra;

    @ViewById
    ImageView known_im;

    View views[];

    PopupWindow mPopupWindow;

    Calendar curCal = Calendar.getInstance();

    ArrayList<HashMap<String, String>> chuxian_list;

    View.OnTouchListener listener;

    LinearLayout[] colors;

    private final int REQUEST_CITY_FRAGMENT1 = 10;

    private final int CHANGE_CITY = 101;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_CITY:
                    String city = msg.obj.toString();
                    edittext17.setText(city);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @ViewById
    LinearLayout baise, heise, yinse, huise, hongse, zongse, hese, lanse,
            jinse, chengse, mise, huangse, zise, qingse, lvse, otherse;
    private boolean isChangCity = false;


    @Click
    public void known_im() {
        known_im.setVisibility(View.GONE);
    }

    @Click
    public void slidingmenu_toggler() {
        mainActivity.getSlidingMenu().toggle();
    }


    @AfterViews
    public void afterViews() {
        edittext1.requestFocus();

        if (getActivity().getSharedPreferences("fragment_first_in", 0).getBoolean("fragment_first_in", true)) {
            known_im.setVisibility(View.VISIBLE);
            getActivity().getSharedPreferences("fragment_first_in", 0).edit().putBoolean("fragment_first_in", false).commit();
        }
        if (UserLocation.getInstance().isIslocation()) {
            edittext17.setText(UserLocation.getInstance().getCity());
        }
        mainActivity = (MainActivity_) getActivity();
        list = mainActivity.getDB01Items();
        chuxian_list = mainActivity.getchuxian_list();
        menu = mainActivity.getSlidingMenu();
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        colors = new LinearLayout[]{baise, heise, yinse, huise, hongse, zongse, hese, lanse,
                jinse, chengse, mise, huangse, zise, qingse, lvse, otherse};
        banner_title.setText("车辆基本信息");
        final View popupView = mainActivity.getLayoutInflater().inflate(R.layout.popupview, null);
        final Button btnComplete = (Button) popupView.findViewById(R.id.complete);
        final Button add = (Button) popupView.findViewById(R.id.add);
        final EnhancedEditText money = (EnhancedEditText) popupView.findViewById(R.id.money);
        final EditText note = (EditText) popupView.findViewById(R.id.note);

        final ListView listView = (ListView) popupView.findViewById(R.id.list);

        final chuxianListAdapter adapter = new chuxianListAdapter();

        listView.setAdapter(adapter);

        mPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));

        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);

        edittext1.setTag("CX");
        edittext2.setTag("SCSPSJ");
        edittext3.setTag("GHCS");
        edittext4.setTag("JQX");
        edittext5.setTag("SYX");
        edittext7.setTag("FPJG");
        radiogroup8.setTag("SYXZ");
        edittext16.setTag("YS");
        edittext17.setTag("CLGSD");
        edittext18.setTag("LCS");
        edittext18.setSuffixColor(Color.parseColor("#ff585858"));
        edittext18.setSuffixText("万公里");
        views = new View[]{edittext1, edittext2, edittext3, edittext4, edittext5, edittext7,
                radiogroup8, edittext17, edittext16, edittext18};
        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                saveData();
            }
        });
        edittext16.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                colorTable.setVisibility(View.VISIBLE);
                return false;
            }
        });

        edittext17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("needProvince", false);
                intent.setClass(getActivity(), CityActivity.class);
                startActivityForResult(intent, REQUEST_CITY_FRAGMENT1);
            }
        });

        edittext7.setSuffixText("万元");
        edittext7.setSuffixColor(Color.parseColor("#ff585858"));


        edittext2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;

                Calendar minCal = Calendar.getInstance();
                Calendar maxCal = Calendar.getInstance();


                if (null != mainActivity.shangpai_time && !TextUtils.isEmpty(edittext1.getText().toString())) {
                    if (mainActivity.shangpai_time[2] == 0) {
                        mainActivity.shangpai_time[2] = 2002;
                    } else {
                        minCal.set(Calendar.YEAR, mainActivity.shangpai_time[2]);
                    }
                    if (mainActivity.shangpai_time[0] != 0) {
                        maxCal.set(Calendar.YEAR, mainActivity.shangpai_time[0]);
                    }

                    //首次上牌时间选择
                    mainActivity.showDateDialog(view, "上牌时间", minCal, maxCal, false);
                } else {
                    Toast.makeText(mainActivity, "请先选择车型", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });


        edittext14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mainActivity.showSelectDialog(view);
            }
        });

        edittext15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mainActivity.showSelectDialog(view);
            }
        });
        Calendar maxCal = Calendar.getInstance();


        //出厂时间
//        getDate(edittext3, null, curCal, false);
      /*  //最近变更时间
        getDate(edittext5, null, curCal, false,"交强险到期");*/
        //交强险到期时间
        maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 3);
        getDate(edittext4, curCal, maxCal, true, "交强险到期");
        //商业险到期时间
        getDate(edittext5, curCal, maxCal, true, "商业险到期");
        //年检到期时间
       /* maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 6);
        getDate(edittext6, curCal, maxCal, true);*/

        edittext1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                mainActivity.startActivityForResult(new Intent().setClass(mainActivity, CategoryActivity.class), Constant.REQUEST_CODE_MODEL);
                return false;
            }
        });


        edittext7.addTextChangedListener(new DoubleTextWatcher(edittext7));
        for (int i = 0; i < colors.length; ++i) {
            colors[i].setOnClickListener(colorCilck);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CITY_FRAGMENT1:
                    isChangCity = true;
                    String city1 = data.getExtras().getString("city");
                    if (city1 != null) {
                        Message msg = Message.obtain();
                        msg.what = CHANGE_CITY;
                        msg.obj = city1;
                        mHandler.sendMessage(msg);
                    }
                    break;
                case Constant.REQUEST_CODE_MODEL:
                    Bundle bundle = data.getExtras();
                    edittext1.setText(bundle.getString("modelName") + bundle.getString("modelDetailName"));
                    break;
                default:
                    break;
            }
        }
    }

    void setColor(String name, int value) {
        edittext16.setText(name);
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

    private void getDate(final View view, final Calendar minCal, final Calendar maxCal, final boolean negButton, final String dialog_title) {
        View.OnTouchListener dateListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;
                mainActivity.showDateDialog(view, dialog_title, minCal, maxCal, negButton);
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
                    JSONArray jsonArray = new JSONArray(list.get(index).getValue());
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


    private class DoubleTextWatcher implements TextWatcher {
        private EditText mEditText;

        public DoubleTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String text = s.toString();
            if (text.contains(".")) {
                int index = text.indexOf(".");
                if (index + 3 < text.length()) {
                    text = text.substring(0, index + 3);
                    mEditText.setText(text);
                    mEditText.setSelection(text.length());
                }
            }
        }

        @Override
        //主要是重置文本改变事件,判断当前输入的内容
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    }

    @Background
    public void saveData() {
        saveDatafromView();
    }


    @Background
    public void saveDatafromView() {
        mainActivity.saveDatafromView(list, views);

        //出险记录
        JSONArray jsonArray = new JSONArray();
        for (HashMap<String, String> map : chuxian_list) {
            jsonArray.put(map.get("money") + "," + map.get("note"));
        }
        mainActivity.updateDatewithString("CXZK", jsonArray.toString(), list);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isChangCity) {
            isChangCity = false;
        } else {
            initView();
        }

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

        public ViewHolder(TextView money, TextView note, ImageButton delete) {
            this.delete = delete;
            this.money = money;
            this.note = note;
        }

        public ImageButton delete;
        public TextView money;
        public TextView note;
    }


}
