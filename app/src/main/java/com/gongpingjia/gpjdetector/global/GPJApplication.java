package com.gongpingjia.gpjdetector.global;

import android.app.Application;

import com.gongpingjia.gpjdetector.utility.FileUtils;

import org.androidannotations.annotations.EApplication;


/**
 * Created by kooze on 14-7-24.
 */
@EApplication
public class GPJApplication extends Application {

    private static GPJApplication instance;

    public static GPJApplication getInstance() {
        return instance;
    }

    private boolean isLogin = false;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if (!FileUtils.isFileExist(Constant.sdcard + "/GPJImages")) {
            FileUtils.createSDDir(Constant.sdcard + "/GPJImages");
        }
    }

    public String getApiUrlFromMeta(String name) {
        if (name.equals("brand_model_logo_img")) {
            return "/img/logo/";
        } else if (name.equals("detail_model")) {
            return "/inspection/category/detail-model-data/";
        } else if (name.equals("brand_model_logo_img")) {
            return "/img/logo/";
        }
        return null;
    }

}
