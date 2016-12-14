package com.gongpingjia.gpjdetector.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

/**
 * Created by Kooze on 14-9-4.
 */
@EActivity(R.layout.activity_usercenter)
public class UserCenterActivity extends Activity {


    @ViewById
    TextView banner_title;

    @ViewById
    Button slidingmenu_toggler, extra, logout;

    @ViewById
    LinearLayout modify_psw;

    @ViewById
    TextView nick, phone, company_name, user_type;

    RequestUtils requestUtils;

    @Click
    void extra() {
        onBackPressed();
    }

    @Click
    void modify_psw() {
        //修改密码
        startActivity(new Intent().setClass(UserCenterActivity.this, ModifyPasswordActivity_.class));
    }

    @Click
    void logout() {

        //退出登录
        final ProgressDialog progressDialog = Constant.showProgress(UserCenterActivity.this, null, "正在退出登录...");
        requestUtils.Logout(new RequestUtils.OnLogoutCallback() {
            @Override
            public void OnLogoutSuccess(JSONObject jsonObject) {
                progressDialog.dismiss();
                Toast.makeText(UserCenterActivity.this, "退出登录成功。", Toast.LENGTH_SHORT).show();
                GPJApplication.getInstance().setLogin(false);
                logout.setVisibility(View.GONE);
                SharedPreUtil.getInstance().DeleteUser();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void OnLogoutError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(UserCenterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterViews() {
        banner_title.setText("个人中心");
        nick.setText(SharedPreUtil.getInstance().getUser().getUser());
        phone.setText(SharedPreUtil.getInstance().getUser().getPhone());
        company_name.setText(SharedPreUtil.getInstance().getUser().getCompany());
        if("3".equals(SharedPreUtil.getInstance().getUser().getUser_type())){
            user_type.setText("车况检测");
        }else{
            user_type.setText("图片采集");
        }
        extra.setBackgroundResource(R.drawable.back);
        slidingmenu_toggler.setVisibility(View.INVISIBLE);
        requestUtils = new RequestUtils(UserCenterActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
