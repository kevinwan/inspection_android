package com.gongpingjia.gpjdetector.activity;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.UserInfo;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

/**
 * Created by Kooze on 14-9-4.
 */
@EActivity(R.layout.activity_modifypassword)
public class ModifyPasswordActivity extends BaseActivity {

    @ViewById
    EditText oldpassword, newpassword1, newpassword2;
    @ViewById
    Button modify,extra;
    @ViewById
    TextView banner_title;

    @ViewById
    Button slidingmenu_toggler;



    RequestUtils requestUtils;


    @Click
    void extra() {
        onBackPressed();
    }


    @AfterViews
    void afterViews() {
        requestUtils = new RequestUtils(ModifyPasswordActivity.this);
        extra.setBackgroundResource(R.drawable.back);
        slidingmenu_toggler.setVisibility(View.INVISIBLE);
        banner_title.setText("修改密码");
        SharedPreUtil.initSharedPreference(ModifyPasswordActivity.this);
    }

    @Click
    public void modify() {
        Utils.autoCloseKeyboard(ModifyPasswordActivity.this, modify);
        if (oldpassword.getText().toString().equals("") || newpassword1.getText().equals("")
                || newpassword2.getText().toString().equals("")) {
            Toast.makeText(ModifyPasswordActivity.this, "请填写旧密码和新密码。", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newpassword1.getText().toString().equals(newpassword2.getText().toString())) {
            Toast.makeText(ModifyPasswordActivity.this, "两次输入的新密码不同，请重新输入。", Toast.LENGTH_SHORT).show();
            newpassword1.setText("");
            newpassword2.setText("");
            return;
        }
        final ProgressDialog progressDialog = Constant.showProgress(ModifyPasswordActivity.this, null, "正在请求修改密码...");
        requestUtils.ModifyPassword(oldpassword.getText().toString(),
                newpassword1.getText().toString(), new RequestUtils.OnModifyPasswordCallback() {
            @Override
            public void OnModifyError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(ModifyPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnModifySuccess(JSONObject jsonObject) {
                progressDialog.dismiss();
                Toast.makeText(ModifyPasswordActivity.this, "密码修改成功。", Toast.LENGTH_SHORT).show();
                UserInfo loaclInfo = SharedPreUtil.getInstance().getUser();
                loaclInfo.setPassword(newpassword1.getText().toString());
                SharedPreUtil.getInstance().putUser(loaclInfo);
                ModifyPasswordActivity.this.finish();
            }
        });
    }
}
