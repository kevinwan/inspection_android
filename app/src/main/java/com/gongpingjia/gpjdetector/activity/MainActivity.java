package com.gongpingjia.gpjdetector.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.EnhancedEditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.CaptureItems;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.data.partItem;
import com.gongpingjia.gpjdetector.fragment.Fragment0_;
import com.gongpingjia.gpjdetector.fragment.Fragment1_;
import com.gongpingjia.gpjdetector.fragment.Fragment1_1_;
import com.gongpingjia.gpjdetector.fragment.Fragment2_;
import com.gongpingjia.gpjdetector.fragment.Fragment3_;
import com.gongpingjia.gpjdetector.fragment.Fragment4_;
import com.gongpingjia.gpjdetector.fragment.Fragment5_;
import com.gongpingjia.gpjdetector.fragment.Fragment6_;
import com.gongpingjia.gpjdetector.fragment.FragmentFeedBack;
import com.gongpingjia.gpjdetector.fragment.MenuFragment_;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.kZViews.kZDatePickerDialog;
import com.gongpingjia.gpjdetector.receiver.NetReceiver;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.Utils;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@EActivity(R.layout.layout_content)
public class MainActivity extends FragmentActivity {

    //检测说明
    public String content = "";

    Fragment menuFragment;
    SlidingMenu menu;

    kZDatabase database;
    private NetReceiver netReceiver;

    public kZDatabase getDatabase() {
        return database;
    }

    Bitmap baseBitmap;

    public void setBaseBitmap(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }

    private long exitTime = 0;
    private boolean pressTag = false;

    //    HashMap<String, String[]> optionMap;
    JSONArray optionJsonArray;

    private boolean isPostivebutton = true;
    private String dateTmp;

    public RequestUtils requestUtils;

    HashMap<String, partItem> partMap;

    ArrayList<HashMap<String, String>> extraList;

    ArrayList<HashMap<String, String>> chuxian_list;

    public int[] shangpai_time;

    Resources res;

    Paint paint;

    boolean isExit = false;

    public boolean[] menu_status;

    ArrayList<kZDBItem> items11list, items12list, items13list, items2list, items3list, items4list, items5list, items6list, items7list;

    ArrayList<CaptureItems> captureList;

    HashMap<String, ArrayList<CaptureItems>> captureListMap;

    public Fragment0_ fragment0;
    public Fragment1_ fragment1;
    public Fragment1_1_ fragment1_1;
    public Fragment2_ fragment2;
    public Fragment3_ fragment3;
    public Fragment4_ fragment4;
    public Fragment5_ fragment5;
    public Fragment6_ fragment6;

    public FragmentFeedBack feedbackFragment;

    private static final String NET_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";


    public SlidingMenu getSlidingMenu() {
        return menu;
    }


    public int lastpart = 8;


    @AfterViews
    public void afterViews() {
        database = new kZDatabase(this);
        paint = new Paint();
        paint.setColor(Color.argb(0xff, 0xf9, 0x3f, 0x25));
        paint.setAntiAlias(true);
        requestUtils = new RequestUtils(MainActivity.this);
        menuFragment = new MenuFragment_();
        menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        menu.setShadowWidthRes(R.dimen.slidingmenuShadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenuOffset);
        menu.setFadeDegree(0.5f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.layout_menu);
        menu.setMode(SlidingMenu.LEFT);

        res = getResources();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_menu, menuFragment).commit();

        netReceiver = new NetReceiver();
        IntentFilter filter = new IntentFilter();//生成一个IntentFilter对象
        filter.addAction(NET_CHANGE);//为IntentFilter添加一个Action
        getApplicationContext().registerReceiver(netReceiver, filter);//将BroadcastReceiver对象注册到系统当中

        fragment0 = new Fragment0_();
        showFragment(fragment0);

        if (SplashActivity.isFirstIn) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.toggle();
                }
            }, 1500);
        }
//        optionMap = new HashMap<String, String[]>();

        try {
            String options = database.getValue(Constant.getTableName(), "OPTIONS");
            if (null != options) {
                optionJsonArray = new JSONArray(options);
            } else {
                optionJsonArray = new JSONArray();

            }

            String times = database.getValue(Constant.getTableName(), "SPTimeArray");
            if (null != times) {
                shangpai_time = new int[4];
                JSONArray time = new JSONArray(times);
                if (time.length() == 4) {
                    for (int i = 0; i < 4; ++i) {
                        shangpai_time[i] = time.getInt(i);
                    }
                }
            } else {
                shangpai_time = new int[4];
            }


        } catch (JSONException e) {
            e.printStackTrace();
            optionJsonArray = new JSONArray();
            shangpai_time = new int[4];
        }

        menu_status = new boolean[8];
        final Drawable left, right;
        left = res.getDrawable(R.drawable.white_dot_shape);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        right = res.getDrawable(R.drawable.menu_complete);
        right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
        menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen(int position) {

                if (menu_status[0]) {
                    ((MenuFragment_) menuFragment).btn_fragment0.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[1]) {
                    ((MenuFragment_) menuFragment).btn_fragment1.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[2]) {
                    ((MenuFragment_) menuFragment).btn_fragment2.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[3]) {
                    ((MenuFragment_) menuFragment).btn_fragment3.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[4]) {
                    ((MenuFragment_) menuFragment).btn_fragment4.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[5]) {
                    ((MenuFragment_) menuFragment).btn_fragment5.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[6]) {
                    ((MenuFragment_) menuFragment).btn_fragment6.setCompoundDrawables(left, null, right, null);
                }
                if (menu_status[7]) {
                    ((MenuFragment_) menuFragment).btn_fragment7.setCompoundDrawables(left, null, right, null);
                }
            }

        });

    }

    public void showExitDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).
                setMessage("现在退出将丢失已检测项目，确定现在退出检测吗？").
                setNegativeButton("确定退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final ProgressDialog progressDialog = Constant.showProgress(MainActivity.this, null, "正在清除本地数据...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                isExit = true;
                                database.deleteHistory(Constant.getTableName());
                                MainActivity.this.finish();
                            }
                        }, 3000);

                    }
                }).
                setPositiveButton("继续检测", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        menu.showContent();
                    }
                }).create();
        dialog.show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public int searchIndex(String id, ArrayList<kZDBItem> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getKey().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private String searchValue(String id, ArrayList<kZDBItem> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getKey().equals(id)) {
                return list.get(i).getValue();
            }
        }
        return null;
    }

    public ArrayList<HashMap<String, String>> getchuxian_list() {
        if (null == chuxian_list) {
            chuxian_list = new ArrayList<HashMap<String, String>>();
        }
        return chuxian_list;
    }

    @Background
    public void saveDatafromView(ArrayList<kZDBItem> list, View[] views) {
        for (int i = 0; i < views.length; ++i) {
            View view = views[i];
            if (null == list || null == view) return;
            int index = searchIndex((String) view.getTag(), list);
            if (-1 == index) return;
            if (view instanceof EditText) {
                list.get(index).setValue(((TextView) view).getText().toString());
            } else if (view instanceof EnhancedEditText) {
                list.get(index).setValue(((EnhancedEditText) view).getText().toString());
            } else if (view instanceof RadioGroup) {
                String value = null;
                switch (((RadioGroup) view).getCheckedRadioButtonId()) {
                    case R.id.radio_pos:
                        value = Constant.value_POS;
                        break;
                    case R.id.radio_neg:
                        value = Constant.value_NEG;
                        break;
                    case R.id.radio_neu:
                        value = Constant.value_NEU;
                        break;
                    default:
                        break;
                }
                list.get(index).setValue(value);
            } else if (view instanceof CheckBox) {
                list.get(index).setValue(((CheckBox) view).isChecked() ? Constant.value_POS : Constant.value_NEU);
            }
        }

        for (kZDBItem item : list) {
            database.updateItem(item.getKey(), item.getValue());
        }
    }

    public void loadDatatoView(ArrayList<kZDBItem> list, View view) {
        if (null == list || null == view) return;
        String ret_value = searchValue((String) view.getTag(), list);
        if (null == ret_value) return;
        if (view instanceof EditText) {
            ((EditText) view).setText(ret_value);
        } else if (view instanceof EnhancedEditText) {
            ((EnhancedEditText) view).setText(ret_value);
        } else if (view instanceof RadioGroup) {
            if (ret_value.equals(Constant.value_POS)) {
                ((RadioGroup) view).check(R.id.radio_pos);
            } else if (ret_value.equals(Constant.value_NEG)) {
                ((RadioGroup) view).check(R.id.radio_neg);
            } else if (ret_value.equals(Constant.value_NEU)) {
                ((RadioGroup) view).check(R.id.radio_neu);
            }
        } else if (view instanceof CheckBox) {
            if (ret_value.equals(Constant.value_POS)) {
                ((CheckBox) view).setChecked(true);
            } else {
                ((CheckBox) view).setChecked(false);
            }
        }
    }

    public HashMap<String, partItem> getPartMap() {
        if (null != partMap) {
            return partMap;
        } else {
            partMap = new HashMap<String, partItem>();
            ArrayList<kZDBItem> list = getDB2Items();
            for (int i = 0; i < list.size(); ++i) {

                if (null != list.get(i).getValue()) {
                    partItem part = new partItem();
                    try {
                        kZDBItem item = list.get(i);

                        JSONObject rootJson = new JSONObject(item.getValue());
                        part = new partItem(Integer.parseInt(rootJson.getString("part_no")), item.getKey(), item.getName());
                        part.setP_level(Integer.valueOf(rootJson.getString("p_level")));
                        JSONArray marks = rootJson.getJSONArray("marks");
                        ArrayList<String> marklist = new ArrayList<String>();
                        for (int j = 0; j < marks.length(); ++j) {
                            marklist.add(marks.getString(j));
                        }
                        part.setMarks(marklist);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        partMap.put(String.valueOf(part.getPart_no()), part);
                    }
                }


            }
//            partMap =  new HashMap<String, partItem>();
//            partMap.put("1", new partItem(1, "ZHYZB", "左后翼子板"));
//            partMap.put("2", new partItem(2, "ZHCM", "左后车门"));
//            partMap.put("3", new partItem(3, "ZQCM", "左前车门"));
//            partMap.put("4", new partItem(4, "ZQYZB", "左前翼子板"));
//            partMap.put("5", new partItem(5, "HBXG", "后保险杠"));
//            partMap.put("6", new partItem(6, "XLXG", "行李箱盖"));
//            partMap.put("7", new partItem(7, "CD_", "车顶"));
//            partMap.put("8", new partItem(8, "YQG", "引擎盖"));
//            partMap.put("9", new partItem(9, "QBXG", "前保险杠"));
//            partMap.put("10", new partItem(10, "YHYZB", "右后翼子板"));
//            partMap.put("11", new partItem(11, "YHCM", "右后车门"));
//            partMap.put("12", new partItem(12, "YQCM", "右前车门"));
//            partMap.put("13", new partItem(13, "YQYZB", "右前翼子板"));

            return partMap;
        }
    }

//    public ArrayList<CaptureItems> getCaptureItems() {
//        if (null != captureList) {
//            return captureList;
//        } else {
//            captureList = new ArrayList<CaptureItems>();
//            captureList.add(new CaptureItems("CAP1", "正前方", null));
//            captureList.add(new CaptureItems("CAP2", "发动机舱", null));
//            captureList.add(new CaptureItems("CAP3", "左前方45度", null));
//            captureList.add(new CaptureItems("CAP4", "左侧侧面", null));
//            captureList.add(new CaptureItems("CAP5", "铭牌", null));
//            captureList.add(new CaptureItems("CAP6", "驾驶室", null));
//            captureList.add(new CaptureItems("CAP7", "后排座位", null));
//            captureList.add(new CaptureItems("CAP8", "左后方45度", null));
//            captureList.add(new CaptureItems("CAP9", "正后方", null));
//            captureList.add(new CaptureItems("CAP10", "行李箱", null));
//            captureList.add(new CaptureItems("CAP11", "右后方45度", null));
//            captureList.add(new CaptureItems("CAP12", "右侧侧面", null));
//            captureList.add(new CaptureItems("CAP13", "右前方45度", null));
//            captureList.add(new CaptureItems("CAP14", "行驶证", null));
//            captureList.add(new CaptureItems("ADD", "点击添加", null));
//
//            menu_status[1] = true;
//            return captureList;
//        }
//    }

    public ArrayList<kZDBItem> getDB01Items() {
        if (null != items11list) {
            return items11list;
        } else {
            Cursor cursor;
            items11list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(10);
            if (null == cursor) {
                return null;
            }
            do {
                items11list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[0] = true;
            return items11list;
        }
    }

    public ArrayList<kZDBItem> getDB02Items() {
        if (null != items12list) {
            return items12list;
        } else {
            Cursor cursor;
            items12list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(20);
            if (null == cursor) {
                return null;
            }
            do {
                items12list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            return items12list;
        }
    }

    public ArrayList<kZDBItem> getDB03Items() {
        if (null != items13list) {
            return items13list;
        } else {
            Cursor cursor;
            items13list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(30);
            if (null == cursor) {
                return null;
            }
            do {
                items13list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();

            return items13list;
        }
    }

    public ArrayList<CaptureItems> getDB1Items() {
        if (null != captureList) {
            return captureList;
        } else {
            Cursor cursor;
            captureList = new ArrayList<CaptureItems>();
            cursor = database.getDB1Items();
            if (null == cursor) {
                return null;
            }
            do {
                captureList.add(new CaptureItems(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(5), cursor.getString(6)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[1] = true;
            return captureList;
        }
    }

    public ArrayList<CaptureItems> getDB11Items(String pic_collector_sub_cate) {
        Cursor cursor;
        captureList = new ArrayList<CaptureItems>();
        cursor = database.getDB1Items(pic_collector_sub_cate);
        if (null == cursor) {
            return null;
        }
        do {
            captureList.add(new CaptureItems(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(5)));
        } while (cursor.moveToNext());
        cursor.close();
        if (captureListMap == null) {
            captureListMap = new HashMap<String, ArrayList<CaptureItems>>();
        } else {
            if (captureListMap.get(pic_collector_sub_cate) != null) {
                captureListMap.remove(pic_collector_sub_cate);
            }
        }
        captureListMap.put(pic_collector_sub_cate, captureList);
        menu_status[1] = true;
        return captureList;
    }

    public ArrayList<kZDBItem> getDB2Items() {
        if (null != items2list) {
            return items2list;
        } else {
            Cursor cursor;
            items2list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(2);
            if (null == cursor) {
                return null;
            }
            do {
                items2list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[2] = true;
            return items2list;
        }
    }

    public ArrayList<kZDBItem> getDB3Items() {
        if (null != items3list) {
            return items3list;
        } else {
            Cursor cursor;
            items3list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(3);
            if (null == cursor) {
                return null;
            }
            do {
                items3list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[3] = true;
            return items3list;
        }
    }

    public ArrayList<kZDBItem> getDB4Items() {
        if (null != items4list) {
            return items4list;
        } else {
            Cursor cursor;
            items4list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(4);
            if (null == cursor) {
                return null;
            }
            do {
                items4list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[4] = true;
            return items4list;
        }
    }

    public ArrayList<kZDBItem> getDB5Items() {
        if (null != items5list) {
            return items5list;
        } else {
            Cursor cursor;
            items5list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(5);
            if (null == cursor) {
                return null;
            }
            do {
                items5list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[5] = true;
            return items5list;
        }
    }

    public ArrayList<kZDBItem> getDB6Items() {
        if (null != items6list) {
            return items6list;
        } else {
            Cursor cursor;
            items6list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(6);
            if (null == cursor) {
                return null;
            }
            do {
                items6list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[6] = true;
            return items6list;
        }
    }

    public ArrayList<kZDBItem> getDB7Items() {
        if (null != items7list) {
            return items7list;
        } else {
            Cursor cursor;
            items7list = new ArrayList<kZDBItem>();
            cursor = database.getDBItems(7);
            if (null == cursor) {
                return null;
            }
            do {
                items7list.add(new kZDBItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
            cursor.close();
            menu_status[7] = true;
            return items7list;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (menu.isMenuShowing()) {
            if (pressTag) {
                super.onBackPressed();
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出检测", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    super.onBackPressed();
                }
            }

        } else {
            pressTag = true;
            menu.showMenu();
            Toast.makeText(this, "再按一次退出检测", Toast.LENGTH_SHORT).show();
        }
    }

    public void showFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, fragment).commit();
        }
        menu.showContent(true);
    }

    public void showSelectDialog(final View v) {
        for (int i = 0; i < optionJsonArray.length(); ++i) {
            String id = null;
            String[] options = null;
            try {
                JSONObject item = optionJsonArray.getJSONObject(i);
                id = item.getString("id");
                if (id.equals((String) v.getTag())) {
                    JSONArray itemArray = item.getJSONArray("array");
                    options = new String[itemArray.length()];
                    for (int j = 0; j < itemArray.length(); ++j) {
                        options[j] = itemArray.getString(j);
                    }

                    if (null == options) {
                        Toast.makeText(MainActivity.this, "请先选择车型", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String[] text = options;
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_item, options);
                    AlertDialog dialog = new AlertDialog.Builder(this).
                            setNegativeButton("取消", null).
                            setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((TextView) v).setText(text[i]);
                                }
                            }).create();
                    dialog.show();
                    break;
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "暂无数据,请更换车型", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                options = null;
                continue;
            }
        }


    }

    public void showDateDialog(final View v, final Calendar curCal, final Calendar minCal,
                               final Calendar maxCal, boolean negButton) {
        final kZDatePickerDialog datePickerDialog =
                new kZDatePickerDialog(this, _datePickerDialogCallback, curCal, minCal, maxCal, null);
        datePickerDialog.hideDayOfMonth();

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "选择", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isPostivebutton = true;
                DatePicker datePicker = datePickerDialog.getDatePicker();
                _datePickerDialogCallback.onDateSet(datePicker, datePicker.getYear(),
                        datePicker.getMonth(), datePicker.getDayOfMonth());
                ((EditText) v).setText(dateTmp);
            }
        });

        if (negButton) {
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "已到期", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isPostivebutton = false;
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    _datePickerDialogCallback.onDateSet(datePicker, datePicker.getYear(),
                            datePicker.getMonth(), datePicker.getDayOfMonth());
                    ((EditText) v).setText(dateTmp);
                }
            });
        }
        datePickerDialog.show();
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (isPostivebutton) {
                dateTmp = year + "-" + (month + 1);
            } else {
                dateTmp = "已到期";
            }
        }
    }

    DateSetListener _datePickerDialogCallback = new DateSetListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isExit) {
            String model = database.getValue(Constant.getTableName(), "CX");
            if (null == model || model.equals("")) {
                database.deleteHistory(Constant.getTableName());
            }
        }
        if (netReceiver != null) {
            getApplicationContext().unregisterReceiver(netReceiver);
        }
        database.close();
    }

    @Background
    void updateDate(String id, ArrayList<kZDBItem> list, JSONObject jsonObject) {
        int index;
        index = searchIndex(id, list);
        try {
            if (-1 != index) {
                list.get(index).setValue(String.valueOf(jsonObject.getInt(id)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Background
    void updateDatewithString(String id, String value, ArrayList<kZDBItem> list) {
        int index;
        index = searchIndex(id, list);
        if (-1 != index) {
            list.get(index).setValue(value);
        }
    }

    @Background
    void add2OptionMap(JSONArray jsonArray, String id) {
        JSONObject itemJson = new JSONObject();
        try {
            itemJson.put("id", id);
            itemJson.put("array", jsonArray);
            optionJsonArray.put(itemJson);

            database.updateItem("OPTIONS", optionJsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        String[] options = new String[jsonArray.length()];
//        for (int i = 0; i < jsonArray.length(); ++i) {
//            try {
//                options[i] = jsonArray.getString(i);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        optionMap.put(id, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_MODEL:
                if (resultCode == Activity.RESULT_OK) {

                    Bundle bundle = data.getExtras();

                    String extraType = "kZextra";
                    database.insertItem("1000", "brandSlug", "brandSlug", bundle.getString("brandSlug"), extraType);
                    database.insertItem("1001", "brandName", "brandName", bundle.getString("brandName"), extraType);
                    database.insertItem("1002", "modelSlug", "modelSlug", bundle.getString("modelSlug"), extraType);
                    database.insertItem("1003", "modelName", "modelName", bundle.getString("modelName"), extraType);
                    database.insertItem("1004", "modelDetailSlug", "modelDetailSlug", bundle.getString("modelDetailSlug"), extraType);
                    database.insertItem("1005", "modelDetailName", "modelDetailName", bundle.getString("modelDetailName"), extraType);
                    database.insertItem("1006", "modelThumbnail", "modelThumbnail", bundle.getString("modelThumbnail"), extraType);


                    int index = searchIndex("CX", getDB01Items());
                    if (-1 == index) return;
                    getDB01Items().get(index).setValue(bundle.getString("modelName") + bundle.getString("modelDetailName"));
                    String modelDetailSlug = bundle.getString("modelDetailSlug");

                    requestUtils.getParams(modelDetailSlug, new RequestUtils.OngetParamsCallback() {
                        @Override

                        public void OngetParamsSuccess(JSONObject jsonObject) {
                            shangpai_time = new int[4];
                            try {
                                JSONArray jsonArray = jsonObject.getJSONObject("baseinfo").getJSONArray("SCSPSJ");
                                shangpai_time[0] = jsonArray.getInt(0);
                                shangpai_time[1] = jsonArray.getInt(1);
                                shangpai_time[2] = jsonArray.getInt(2);
                                shangpai_time[3] = jsonArray.getInt(3);


                                database.insertItem("4000", "SPTimeArray", "SPTimeArray", jsonArray.toString(), "option");

                                JSONObject config = jsonObject.getJSONObject("main_function");
                                Log.d("mag", config.toString());
                                updateDate("DCYX", getDB03Items(), config);
                                updateDate("DSXH", getDB03Items(), config);
                                updateDate("ESP", getDB03Items(), config);
                                updateDate("TC", getDB03Items(), config);
                                updateDate("ZDKT", getDB03Items(), config);
                                updateDate("ZYJR", getDB03Items(), config);
                                updateDate("ZPZY", getDB03Items(), config);
                                updateDate("JYZY", getDB03Items(), config);
                                updateDate("GPSDH", getDB03Items(), config);
                                updateDate("YJQD", getDB03Items(), config);
                                updateDate("DDZY", getDB03Items(), config);
                                updateDate("YKYS", getDB03Items(), config);
                                updateDate("DGNFXP", getDB03Items(), config);
                                try {
                                    optionJsonArray = new JSONArray("[]");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                JSONObject attribute = jsonObject.getJSONObject("attribute");
                                //车门数
//                                JSONArray attriArray = attribute.getJSONArray("CMS");
//                                updateDatewithString("CMS", attriArray.getString(0), getDB02Items());
//                                add2OptionMap(attriArray.getJSONArray(1), "CMS");
                                //排量
                                JSONArray attriArray = attribute.getJSONArray("PL");
                                updateDatewithString("PL", attriArray.getString(0), getDB02Items());
                                add2OptionMap(attriArray.getJSONArray(1), "PL");
                                //CSXS
//                                attriArray = attribute.getJSONArray("CSXS");
//                                updateDatewithString("CSXS", attriArray.getString(0), getDB02Items());
//                                add2OptionMap(attriArray.getJSONArray(1), "CSXS");
                                //DY
//                                attriArray = attribute.getJSONArray("DY");
//                                updateDatewithString("DY", attriArray.getString(0), getDB02Items());
//                                add2OptionMap(attriArray.getJSONArray(1), "DY");
                                //BSQ
                                attriArray = attribute.getJSONArray("BSQ");
                                updateDatewithString("BSQ", attriArray.getString(0), getDB02Items());
                                add2OptionMap(attriArray.getJSONArray(1), "BSQ");
                                //QDFS
                                attriArray = attribute.getJSONArray("QDFS");
                                updateDatewithString("QDFS", attriArray.getString(0), getDB02Items());
                                add2OptionMap(attriArray.getJSONArray(1), "QDFS");
//                                //CLLX
//                                attriArray = attribute.getJSONArray("CLLX");
//                                updateDatewithString("CLLX", attriArray.getString(0), getDB02Items());
//                                add2OptionMap(attriArray.getJSONArray(1), "CLLX");
//                                //RLLX
//                                attriArray = attribute.getJSONArray("RLLX");
//                                updateDatewithString(
//                                        "RLLX", attriArray.getString(0), getDB02Items());
//                                add2OptionMap(attriArray.getJSONArray(1), "RLLX");


                                saveOptions();

                                fragment0.initView();
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void OngetParamsError(String errorMessage) {
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    @Background
    void saveOptions() {
        database.insertItem("3000", "OPTIONS", "optionArray", optionJsonArray.toString(), "option");
    }

    @UiThread
    void showToast(String content) {
        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
    }

    public JSONObject buildData() {
        JSONObject rootJson = new JSONObject();
        int index;
        try {
            //1-1(15)
            if (items11list != null) {
                for (index = 0; index < items11list.size(); ++index) {
                   /* if (items11list.get(index).getKey().equals("CXZK")) continue; //跳过出现状况检测
                    if (items11list.get(index).getKey().equals("ZJBGRQ")) {
                        if (items11list.get(index).getValue().equals("") || null == items11list.get(index).getValue()) {
                            rootJson.put("ZJBGRQ", "");
                        } else {
                            rootJson.put("ZJBGRQ", items11list.get(index).getValue());
                        }
                        continue;
                    }*/

                    String value = items11list.get(index).getValue();
                    if (items11list.get(index).getKey().equals("CJH") ||items11list.get(index).getKey().equals("CPH") ||items11list.get(index).getKey().equals("FDJH") ||items11list.get(index).getKey().equals("FPJG") || items11list.get(index).getKey().equals("CX") || items11list.get(index).getKey().equals("LCS")) {

                    } else {
                        if (null == value || value.equals("")) {
                            showToast("基本信息有未填写的项目，无法提交。");
                            return null;
                        }
                    }

                    rootJson.put(items11list.get(index).getKey(), value);
                }
            }
            rootJson.put("CX", database.getValue(Constant.getTableName(), "brandSlug")
                    + "," + database.getValue(Constant.getTableName(), "modelSlug")
                    + "," + database.getValue(Constant.getTableName(), "modelDetailSlug"));
            //1-2(10)
      /*      for (index = 0; index < items12list.size(); ++index) {
                String value = items12list.get(index).getValue();
                if (items12list.get(index).getKey().equals("YS")
                        || items12list.get(index).getKey().equals("LCS")
                        || items12list.get(index).getKey().equals("PL")
                        || items12list.get(index).getKey().equals("BSQ")) {

                    if (null == value || value.equals("")) {
                        showToast("<车辆属性>有未填写的项目，无法提交。");
                        return null;
                    }
                    rootJson.put(items12list.get(index).getKey(), value);
                }
            }*/
            //1-3(20)
        /*    for (index = 0; index < items13list.size(); ++index) {
                String value = items13list.get(index).getValue();
                value = null == value ? "" : value;
                rootJson.put(items13list.get(index).getKey(), value);
            }*/
            //2(13)
            if (Constant.CHECK_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())) {
                if (items2list != null) {
                    for (index = 0; index < items2list.size(); ++index) {
                        JSONObject itemJson = new JSONObject(items2list.get(index).getValue());
                        JSONArray marks = itemJson.getJSONArray("marks");
                        for (int i = 0; i < marks.length(); ++i) {
                            marks.put(i, getBase64Img(marks.getString(i)));
                        }
                        rootJson.put(items2list.get(index).getKey(), itemJson);
                    }
                }
                if (items3list != null) {
                    //3(12)
                    for (index = 0; index < items3list.size(); ++index) {
                        String value = items3list.get(index).getValue();
                        value = null == value ? "" : value;
                        rootJson.put(items3list.get(index).getKey(), value);
                    }
                }
                if (items4list != null) {
                    //4(33)
                    for (index = 0; index < items4list.size(); ++index) {
                        String value = items4list.get(index).getValue();
                        value = null == value ? "" : value;
                        rootJson.put(items4list.get(index).getKey(), value);
                    }
                }
                if (items5list != null) {
                    //5(57)
                    for (index = 0; index < items5list.size(); ++index) {
                        String value = items5list.get(index).getValue();
                        value = null == value ? "" : value;
                        rootJson.put(items5list.get(index).getKey(), value);
                    }
                }
                if (items6list != null) {
                    //6(27)
                    for (index = 0; index < items6list.size(); ++index) {
                        String value = items6list.get(index).getValue();
                        value = null == value ? "" : value;
                        rootJson.put(items6list.get(index).getKey(), value);
                    }
                }
                Cursor cursor;
                cursor = database.getDBItems(8);
                if (null != cursor) {
                    content = cursor.getString(2);

                }
                cursor.close();
                rootJson.put("comments", content);

                //添加三张图片Base64 编码字符串
                Bitmap[] bitmaps = drawPic();
                for (int i = 0; i < bitmaps.length; ++i) {
                    String value = Utils.bitmapToBase64(bitmaps[i]);
                    bitmaps[i].recycle();
                    value = null == value ? "" : value;
                    rootJson.put("extra_pic" + (i + 1), value);
                }
                //出险记录
                JSONArray jsonArray = new JSONArray();
                for (HashMap<String, String> map : chuxian_list) {
                    jsonArray.put(map.get("money") + "," + map.get("note"));
                }

                rootJson.put("CXZK", jsonArray);
            }


//            if (TextUtils.isEmpty(content)) {
//                showToast("检测说明未编写，无法提交。");
//                return null;
//            }

//            //7(30)
//            for (index = 0; index < items7list.size(); ++index) {
//                String value = items7list.get(index).getValue();
//                value = null == value ? "" : value;
//                rootJson.put(items7list.get(index).getKey(), value);
//            }


            //照片采集
            int count;
            if (Constant.CHECK_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())) {
                count = 14;
                for (int i = 0; i < count; ++i) {
                    CaptureItems item = captureList.get(i);
                    if (item.file_path == null || item.file_path.equals("null")) {
                        showToast("车辆照片采集中有未填写的项目，无法提交。");
                        return null;
                    }
                    rootJson.put(item.key, item.desc + "|"
                            + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                }
                JSONArray extraCaptureArray = new JSONArray();

                if (captureList.size() > count) {
                    for (int i = count; i < captureList.size(); ++i) {
                        CaptureItems item = captureList.get(i);
                        if (null != item.file_path) {
                            extraCaptureArray.put(item.desc + "|" + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                        }
                    }
                }
                rootJson.put("extra_capture", extraCaptureArray);
            } else {
                if(captureListMap == null){
                    showToast("车辆照片采集中有未填写的项目，无法提交。");
                    return null;
                }else{
                    ArrayList<CaptureItems> list1 = captureListMap.get("基本信息");
                    ArrayList<CaptureItems> list2 = captureListMap.get("发动机舱");
                    ArrayList<CaptureItems> list3 = captureListMap.get("细节");
                    ArrayList<CaptureItems> list4 = captureListMap.get("内饰");
                    ArrayList<CaptureItems> list5 = captureListMap.get("其他");
                    int size1 = list1.size();
                    for (int i = 0; i < size1; ++i) {
                        CaptureItems item = list1.get(i);
                        if (item.file_path == null || item.file_path.equals("null")) {
                            showToast("车辆照片采集中有未填写的项目，无法提交。");
                            return null;
                        }
                        rootJson.put(item.key, item.desc + "|"
                                + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                    }

                    int size2 = list2.size();
                    for (int i = 0; i < size2; ++i) {
                        CaptureItems item = list2.get(i);
                        if (item.file_path == null || item.file_path.equals("null")) {
                            showToast("车辆照片采集中有未填写的项目，无法提交。");
                            return null;
                        }
                        rootJson.put(item.key, item.desc + "|"
                                + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                    }

                    int size3 = list3.size();
                    for (int i = 0; i < size3; ++i) {
                        CaptureItems item = list3.get(i);
                        if (item.file_path == null || item.file_path.equals("null")) {
                            showToast("车辆照片采集中有未填写的项目，无法提交。");
                            return null;
                        }
                        rootJson.put(item.key, item.desc + "|"
                                + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                    }

                    int size4 = list1.size();
                    for (int i = 0; i < size4; ++i) {
                        CaptureItems item = list1.get(i);
                        if (item.file_path != null && !item.file_path.equals("null")) {
                            rootJson.put(item.key, item.desc + "|"
                                    + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                        }

                    }
                    int size5 = 6;
                    for (int i = 0; i < size5; ++i) {
                        CaptureItems item = list5.get(i);
                        if (item.file_path != null && !item.file_path.equals("null")) {
                            rootJson.put(item.key, item.desc + "|"
                                    + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                        }
                    }
                    JSONArray extraCaptureArray = new JSONArray();
                    if (list5.size() > size5) {
                        for (int i = size5; i < list5.size(); ++i) {
                            CaptureItems item = captureList.get(i);
                            if (null != item.file_path) {
                                extraCaptureArray.put(item.desc + "|" + Utils.bitmapToBase64(BitmapFactory.decodeFile(item.file_path)));
                            }
                        }
                    }
                    rootJson.put("extra_capture", extraCaptureArray);
                }
            }


            //总计 220个字段
            return rootJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    String getBase64Img(String path) {
        if (path == null) return null;
        String[] array = path.split(",");
        if (array.length >= 3) {
            return array[0] + "," + array[1] + "," + Utils.GetImageStr(array[2]);
        }
        return null;
    }

    Bitmap[] drawPic() {
        Bitmap[] bitmaps = new Bitmap[3];
        Bitmap drawBitmap = Bitmap.createBitmap(Constant.CANVAS_WIDTH,
                Constant.CANVAS_HEIGHT, Bitmap.Config.ARGB_4444);
        Canvas drawCanvas = new Canvas(drawBitmap);
        drawCanvas.drawColor(Color.WHITE);
        drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_bg), 0, 0, paint);
        if (partMap != null) {
            for (int i = 1; i <= 13; ++i) {
                ArrayList<String> list = partMap.get(String.valueOf(i)).getMarks();
                for (int j = 0; j < list.size(); ++j) {
                    String[] xy = list.get(j).split(",");
                    drawCanvas.drawCircle(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]), 12, paint);
                }
            }
        }


        bitmaps[0] = drawBitmap.copy(Bitmap.Config.ARGB_4444, false);
        //画做漆图
        drawBitmap.eraseColor(Color.WHITE);

        drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_bg), 0, 0, paint);

        if (-1 != partMap.get("1").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_1_b), 0, 0, paint);
        }
        if (-1 != partMap.get("2").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_2_b), 0, 0, paint);
        }
        if (-1 != partMap.get("3").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_3_b), 0, 0, paint);
        }
        if (-1 != partMap.get("4").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_4_b), 0, 0, paint);
        }
        if (-1 != partMap.get("5").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_5_b), 0, 0, paint);
        }
        if (-1 != partMap.get("6").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_6_b), 0, 0, paint);
        }
        if (-1 != partMap.get("7").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_7_b), 0, 0, paint);
        }
        if (-1 != partMap.get("8").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_8_b), 0, 0, paint);
        }
        if (-1 != partMap.get("9").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_9_b), 0, 0, paint);
        }
        if (-1 != partMap.get("10").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_10_b), 0, 0, paint);
        }
        if (-1 != partMap.get("11").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_11_b), 0, 0, paint);
        }
        if (-1 != partMap.get("12").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_12_b), 0, 0, paint);
        }
        if (-1 != partMap.get("13").getP_level()) {
            drawCanvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.car_part_13_b), 0, 0, paint);
        }
        bitmaps[1] = drawBitmap.copy(Bitmap.Config.ARGB_4444, false);
        drawCanvas.drawBitmap(baseBitmap, 0, 0, paint);
        bitmaps[2] = drawBitmap.copy(Bitmap.Config.ARGB_4444, false);
        drawBitmap.recycle();
        return bitmaps;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
