package com.gongpingjia.gpjdetector.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.UserInfo;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kooze on 14-9-4.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById
    EditText account, password;
    @ViewById
    Button login;
    @ViewById
    TextView banner_title;

    @ViewById
    Button slidingmenu_toggler, extra;


    RequestUtils requestUtils;

    @Click
    void extra () {
        onBackPressed();
    }

    @AfterViews
    void afterViews() {
        requestUtils = new RequestUtils(LoginActivity.this);
        extra.setBackgroundResource(R.drawable.back);
        slidingmenu_toggler.setVisibility(View.INVISIBLE);
        banner_title.setText("登 录");
      /*  account: 15680709902 password: 123456 拍照师权限
        account: 15208324770 password: 123456  检测师权限*/
        account.setText("15680709902");
        password.setText("123456");
    }

    @Click
    public void login() {
        Utils.autoCloseKeyboard(LoginActivity.this, login);
        if (account.getText().toString().equals("") || password.getText().equals("")) {
            Toast.makeText(LoginActivity.this, "请填写用户名和密码。", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = Constant.showProgress(LoginActivity.this, null, "正在登录 ...");
        requestUtils.Login(account.getText().toString(), password.getText().toString(), new RequestUtils.OnLoginCallback() {
            @Override
            public void OnLoginSuccess(JSONObject jsonObject) {
                progressDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("json", jsonObject.toString());
                intent.putExtra("password", password.getText().toString());
                setResult(RESULT_OK, intent);
                LoginActivity.this.finish();
                JSONObject json1;
                String password;

                try {
                    json1 = new JSONObject(jsonObject.getString("json"));
                    password = jsonObject.getString("password");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                afterLogin(json1, password);
            }

            @Override
            public void OnLoginError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
    }
}
