package com.gongpingjia.gpjdetector.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.UserInfo;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.UpdateHelper;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity {

    @ViewById
    LinearLayout act_start, act_history, action_login,login_layout;

    @ViewById
    TextView user,username,company_name;

    RequestUtils requestUtils;

    static HomeActivity instance;

    kZDatabase db;

    UpdateHelper updateHelper;

    ProgressDialog progressDialog;

    TextView car_check_tv;



    @Click
    public void act_start() {

        if (!GPJApplication.getInstance().isLogin()) {
            Toast.makeText(HomeActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent().setClass(HomeActivity.this, LoginActivity_.class), Constant.REQUEST_CODE_LOGIN_TO_START);
            return;
        }

        int historySize = db.getHistorySize();
        if (historySize > 0) {
            AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                    .setMessage("本地仍有" + historySize + "条未提交的检测记录，是否现在处理？")
                    .setPositiveButton("现在去处理", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.putExtra("isFinish", 0);
                            intent.setClass(HomeActivity.this, HistoryActivity_.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("创建新检测", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog = ProgressDialog.show(HomeActivity.this, "提示", "数据初始化中...", true, false);
                            Constant.setTableName(db.createTable());
                            progressDialog.dismiss();
                            startActivity(new Intent().setClass(HomeActivity.this, MainActivity_.class));
                        }
                    }).create();
            dialog.show();
        } else {

            progressDialog = ProgressDialog.show(HomeActivity.this, "提示", "数据初始化中...", true, false);

            Constant.setTableName(db.createTable());
            progressDialog.dismiss();
            startActivity(new Intent().setClass(HomeActivity.this, MainActivity_.class));
        }

    }

    @Click
    public void act_history() {
        if (!GPJApplication.getInstance().isLogin()) {
            Toast.makeText(HomeActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent().setClass(HomeActivity.this, LoginActivity_.class), Constant.REQUEST_CODE_LOGIN_TO_HISTORY);
            return;
        }
        startActivity(new Intent().setClass(HomeActivity.this, HistoryActivity_.class));
    }

    @Click
    public void action_login() {
        if (GPJApplication.getInstance().isLogin()) {
            startActivityForResult(new Intent().setClass(HomeActivity.this, UserCenterActivity_.class), Constant.REQUEST_CODE_TO_USER);
        }else{
            startActivityForResult(new Intent().setClass(HomeActivity.this, LoginActivity_.class), Constant.REQUEST_CODE_LOGIN);
        }
    }

    @AfterViews
    void afterViews() {
        car_check_tv = (TextView) findViewById(R.id.car_check_tv);
        db = new kZDatabase(HomeActivity.this);
        updateHelper = new UpdateHelper(HomeActivity.this);
        updateHelper.checkUpdate();
        SharedPreUtil.initSharedPreference(HomeActivity.this);
        final UserInfo userinfo = SharedPreUtil.getInstance().getUser();
        requestUtils = new RequestUtils(HomeActivity.this);
        String account = userinfo.getPhone();
        String password = userinfo.getPassword();
        if (null != account && null != password) {
            if (GPJApplication.getInstance().isLogin()) {
                afterLogin(null, null);
            } else {
                login(userinfo);
            }
        }
        if(Constant.CHECK_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())){
            car_check_tv.setText("开始检测");
        }else{
            car_check_tv.setText("车辆信息采集");
        }
        initUserView();
    }

    private void login(UserInfo userinfo) {
        final ProgressDialog progressDialog = Constant.showProgress(HomeActivity.this, null, "正在登录...");
        requestUtils.Login(userinfo.getPhone(), userinfo.getPassword(), new RequestUtils.OnLoginCallback() {
            @Override
            public void OnLoginSuccess(JSONObject jsonObject) {
                progressDialog.dismiss();
                afterLogin(jsonObject, null);
                initUserView();
            }

            @Override
            public void OnLoginError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, errorMessage + "，请点击头像重新登录。", Toast.LENGTH_SHORT).show();
                initUserView();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    public static HomeActivity getInstance() {
        return instance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_CODE_LOGIN:
                    if (initLoginData(data)) return;
                    break;
                case Constant.REQUEST_CODE_TO_USER:
                    break;
                case Constant.REQUEST_CODE_LOGIN_TO_START:
                    if (initLoginData(data)) return;
                    act_start.performClick();
                    break;
                case Constant.REQUEST_CODE_LOGIN_TO_HISTORY:
                    if (initLoginData(data)) return;
                    act_history.performClick();
                    break;
                default:
                    break;
            }
            initUserView();
        }

    }

    private boolean initLoginData(Intent data) {
        JSONObject json;
        String password;
        try {
            json = new JSONObject(data.getExtras().getString("json"));
            password = data.getExtras().getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
        afterLogin(json, password);
        return false;
    }

    void afterLogin(JSONObject jsonObject, String password) {
        UserInfo userInfo = SharedPreUtil.getInstance().getUser();
        if (null == userInfo) userInfo = new UserInfo();
        if (null != password) {
            userInfo.setPassword(password);
        }
        if (jsonObject == null) {
            return;
        } else {
            try {
                userInfo.setUser(jsonObject.getString("user"));
                userInfo.setCompany(jsonObject.getString("company_name"));
                userInfo.setEmail(jsonObject.getString("email"));
                userInfo.setPhone(jsonObject.getString("phone"));
                userInfo.setUser_type(jsonObject.getString("user_type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        GPJApplication.getInstance().setLogin(true);
        Toast.makeText(HomeActivity.this, "登录成功。", Toast.LENGTH_SHORT).show();
    }

    public void showSelectDialog() {
        final String[] selection = new String[]{"修改密码", "退出登录"};
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_item, selection);
        AlertDialog dialog = new AlertDialog.Builder(this).
                setNegativeButton("取消", null).
                setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (0 == i) {
                            //修改密码
                            startActivity(new Intent().setClass(HomeActivity.this, ModifyPasswordActivity_.class));
                        } else if (1 == i) {
                            //退出登录
                            final ProgressDialog progressDialog = Constant.showProgress(HomeActivity.this, null, "正在退出登录...");
                            requestUtils.Logout(new RequestUtils.OnLogoutCallback() {
                                @Override
                                public void OnLogoutSuccess(JSONObject jsonObject) {
                                    progressDialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "退出登录成功。", Toast.LENGTH_SHORT).show();
                                    GPJApplication.getInstance().setLogin(false);
                                    SharedPreUtil.getInstance().DeleteUser();
                                }

                                @Override
                                public void OnLogoutError(String errorMessage) {
                                    progressDialog.dismiss();
                                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


    private void initUserView(){
        if(GPJApplication.getInstance().isLogin()){
            user.setVisibility(View.GONE);
            login_layout.setVisibility(View.VISIBLE);
            username.setText(SharedPreUtil.getInstance().getUser().getUser());
            company_name.setText(SharedPreUtil.getInstance().getUser().getCompany());
        }else{
            user.setVisibility(View.VISIBLE);
            login_layout.setVisibility(View.GONE);
        }
    }
}
