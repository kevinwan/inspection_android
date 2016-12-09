package com.gongpingjia.gpjdetector.global;

import android.app.Application;

import com.gongpingjia.gpjdetector.data.CityData;
import com.gongpingjia.gpjdetector.utility.DataManager;
import com.gongpingjia.gpjdetector.utility.FileUtils;
import com.gongpingjia.gpjdetector.utility.UserLocation;

import org.androidannotations.annotations.EApplication;
import org.json.JSONArray;


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

    private CityData mCityData = new CityData();

    private JSONArray mCityJson = null;



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
        UserLocation.getInstance().init(getApplicationContext());
        if (!FileUtils.isFileExist(Constant.sdcard + "/GPJImages")) {
            FileUtils.createSDDir(Constant.sdcard + "/GPJImages");
        }
        mCityJson = new FileUtils().readFile2JsonArray(getRootPath(), "city");
        if (null != mCityJson) {
            mCityData.LoadCityData(mCityJson);
            mCityData.LoadCityData1(mCityJson);
            DataManager.getInstance().setCitySuccess(true);
            DataManager.getInstance().setIsCitylaoding(false);
        }
        DataManager.getInstance().getCityData(mCityData,
                getRootPath(),
                getApplicationContext(),
                0);

    }

    public String getRootPath() {
        return getApplicationContext().getFilesDir().getAbsolutePath() + "/";
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


    public CityData getCityData() {
        return mCityData;
    }
}
