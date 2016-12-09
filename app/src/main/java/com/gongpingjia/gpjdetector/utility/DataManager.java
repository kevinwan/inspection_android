package com.gongpingjia.gpjdetector.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.gongpingjia.gpjdetector.data.CityData;
import com.gongpingjia.gpjdetector.global.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {
    static DataManager dataManager;

    private boolean isCitySuccess;

    private boolean isCitylaoding;

    public void setCitySuccess(boolean isCitySuccess) {
        this.isCitySuccess = isCitySuccess;
    }

    public void setIsCitylaoding(boolean isCitylaoding) {
        this.isCitylaoding = isCitylaoding;
    }
    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }

        return dataManager;
    }



    public void getCityData(final CityData mCityData, final String path, final Context context, final int time) {
        isCitylaoding = true;
        NetDataJson mNetBrandData = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                try {
                    JSONArray Json = json.getJSONArray("result");

                    if (null != Json) {
                        mCityData.LoadCityData(Json);
                        mCityData.LoadCityData1(Json);
                    }

                    FileUtils file = new FileUtils();
                    file.write2SDFromBytes(path, "city", Json.toString().getBytes());
                    isCitySuccess = true;
                    SharedPreferences sharedPreferences = context.getSharedPreferences("db", Context.MODE_PRIVATE);
                    Editor edit = sharedPreferences.edit();
                    edit.putInt("local_city_time", time);
                    edit.commit();
                    isCitylaoding = false;
                } catch (JSONException e) {
                    isCitySuccess = false;
                    isCitylaoding = false;
                }
            }

            @Override
            public void onDataJsonError(String errorMessage) {
                isCitySuccess = false;
            }
        });
        String url = "/api/v1/common/provinceAndCities/";
        mNetBrandData.requestData(url, "get", Constant.CITY_SERVER_DOMAIN);
    }
}
