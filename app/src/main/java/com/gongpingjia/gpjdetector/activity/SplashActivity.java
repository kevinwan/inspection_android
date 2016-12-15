package com.gongpingjia.gpjdetector.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.data.UserInfo;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.UpdateHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;


@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity {

    public static Boolean isFirstIn = false;
    SharedPreferences pref;

    @ViewById
    TextView version;

    @AfterViews
    void afterViews() {
        version.setText("Version:" + UpdateHelper.getAppVersion());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long startTime = Calendar.getInstance().getTimeInMillis();
        //if the first time running this app
        pref = this.getSharedPreferences("Base", 0);
        isFirstIn = pref.getBoolean("isFirstIn", true);
        SharedPreferences pref = this.getSharedPreferences("Base", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();

        //check login
        SharedPreUtil.initSharedPreference(SplashActivity.this);
        UserInfo userInfo = SharedPreUtil.getInstance().getUser();
        Date expires = userInfo.getExpiryDate();
        if (null != expires && null != userInfo.getPhone() && null != userInfo.getPassword()) {
            if (expires.after(new Date(Calendar.getInstance().getTimeInMillis()))) {
                GPJApplication.getInstance().setLogin(true);
            }
        }

        long endTime = Calendar.getInstance().getTimeInMillis();
        if (endTime - startTime > Constant.splashTime) {
            startActivity(new Intent().setClass(SplashActivity.this, MainActivity_.class));
            SplashActivity.this.finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent().setClass(SplashActivity.this, HomeActivity_.class));
                    SplashActivity.this.finish();
                }
            }, Constant.splashTime - (endTime - startTime));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
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
}
