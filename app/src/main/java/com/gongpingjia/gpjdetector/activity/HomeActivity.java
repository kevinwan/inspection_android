package com.gongpingjia.gpjdetector.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_home)
public class HomeActivity extends Activity {

    @ViewById
    LinearLayout act_start, act_history, action_login;

    @ViewById
    TextView user;

    RequestUtils requestUtils;

    static HomeActivity instance;

    kZDatabase db;

    UpdateHelper updateHelper;

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
                            Constant.setTableName(db.createTable());
                            startActivity(new Intent().setClass(HomeActivity.this, MainActivity_.class));
                        }
                    }).create();
            dialog.show();
        } else {
            Constant.setTableName(db.createTable());
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
        if (!GPJApplication.getInstance().isLogin()) {
            startActivityForResult(new Intent().setClass(HomeActivity.this, LoginActivity_.class), Constant.REQUEST_CODE_LOGIN);
            return;
        }
        showSelectDialog();
    }

    @AfterViews
    void afterViews () {

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
                final ProgressDialog progressDialog = Constant.showProgress(HomeActivity.this, null, "正在登录...");
                requestUtils.Login(userinfo.getPhone(), userinfo.getPassword(), new RequestUtils.OnLoginCallback() {
                    @Override
                    public void OnLoginSuccess(JSONObject jsonObject) {
                        progressDialog.dismiss();
                        afterLogin(jsonObject, null);
                    }

                    @Override
                    public void OnLoginError(String errorMessage) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, errorMessage + "，请点击头像重新登录。", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
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
            JSONObject json;
            String password;
            try {
                json = new JSONObject(data.getExtras().getString("json"));
                password = data.getExtras().getString("password");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            afterLogin(json, password);
        } else {
            return;
        }
        switch (requestCode) {
            case Constant.REQUEST_CODE_LOGIN:
                break;
            case Constant.REQUEST_CODE_LOGIN_TO_START:
                act_start.performClick();
                break;
            case Constant.REQUEST_CODE_LOGIN_TO_HISTORY:
                act_history.performClick();

        }
    }

    void afterLogin(JSONObject jsonObject, String password) {
        UserInfo userInfo = SharedPreUtil.getInstance().getUser();
        if (null == userInfo) userInfo = new UserInfo();
        if (null != password) {
            userInfo.setPassword(password);
        }
        if (jsonObject == null) {
            this.user.setText(userInfo.getUser());
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

        this.user.setText(userInfo.getUser());
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
                                    user.setText("点击登录");
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
}