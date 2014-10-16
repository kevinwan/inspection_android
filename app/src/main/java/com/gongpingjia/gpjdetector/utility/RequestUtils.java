package com.gongpingjia.gpjdetector.utility;

import android.content.Context;
import android.content.Intent;

import com.gongpingjia.gpjdetector.global.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kooze on 14-9-4.
 */
public class RequestUtils {

    Context context;
    public RequestUtils (Context context) {
        this.context = context;
    }

    public void Login(final String account, final String password, final OnLoginCallback onLoginCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                onLoginCallback.OnLoginError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                onLoginCallback.OnLoginSuccess(json);
            }
        });
        String url = "/inspection/account/login/";
        String params = "?account=" + account
                + "&password=" + password;
        netDataJson.requestData(url + params, "post");
    }

    public void getParams(final String modelDetailSlug, final OngetParamsCallback ongetParamsCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                ongetParamsCallback.OngetParamsError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                ongetParamsCallback.OngetParamsSuccess(json);
            }
        });
        String url = "/inspection/category/parameters/";
        String params = "?d_model=" + modelDetailSlug;
        netDataJson.requestData(url + params, "get");
    }

    public void PostItems (final JSONObject jsonObject, final OnPostItemsCallback onPostItemsCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                onPostItemsCallback.OnPostError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                onPostItemsCallback.OnPostSuccess(json);
            }
        });
        String url = "/inspection/record/save/";
        netDataJson.post(url, jsonObject);
    }

    public void ModifyPassword(final String oldPassword, final String newPassword,
                               final OnModifyPasswordCallback onModifyPasswordCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                onModifyPasswordCallback.OnModifyError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                onModifyPasswordCallback.OnModifySuccess(json);
            }
        });
        String url = "/inspection/account/modify-password/";
        String params = "?old_password=" + oldPassword
                + "&new_password1=" + newPassword
                + "&new_password2=" + newPassword;
        netDataJson.requestData(url + params, "post");
    }

    public void Logout (final OnLogoutCallback onLogoutCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                onLogoutCallback.OnLogoutError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                onLogoutCallback.OnLogoutSuccess(json);
            }
        });
        String url = "/inspection/account/logout/";
        netDataJson.requestData(url, "get");
    }

    public void getHistory (final String page, final OngetHistoryCallback ongetHistoryCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                ongetHistoryCallback.OnHistoryError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                ongetHistoryCallback.OnHistorySuccess(json);
            }
        });
        String url = "/inspection/record/list/";
        String params = "?page=" + page;
        netDataJson.requestData(url + params, "get");
    }

    public void getUpdateInfo(final OngetUpdateInfoCallback ongetUpdateInfoCallback) {
        NetDataJson netDataJson = new NetDataJson(new NetDataJson.OnNetDataJsonListener() {
            @Override
            public void onDataJsonError(String errorMessage) {
                ongetUpdateInfoCallback.OnUpdateError(errorMessage);
            }

            @Override
            public void onDataJsonUpdate(JSONObject json) {
                JSONObject jsonObject = getJsonbyName("android", json);
                ongetUpdateInfoCallback.OnUpdateSuccess(jsonObject);
            }
        });
        String url = "/inspection/meta-data/";
        netDataJson.requestData(url, "get");


//        //test
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("version", "2.0");
//            jsonObject.put("download_url", "http://www.gongpingjia.com/static/app/gongpingjia_1.9.95.apk");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ongetUpdateInfoCallback.OnUpdateSuccess(jsonObject);
    }

    JSONObject getJsonbyName(String name, JSONObject meta_data) {
        if (null == name || null == meta_data) {
            return null;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = meta_data.getJSONArray("meta_data");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("name").equals(name)) {
                    return jsonObject;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public interface OnLoginCallback {
        void OnLoginSuccess(JSONObject jsonObject);
        void OnLoginError(String errorMessage);
    }

    public interface OngetParamsCallback {
        void OngetParamsSuccess(JSONObject jsonObject);
        void OngetParamsError(String errorMessage);
    }

    public interface OnPostItemsCallback {
        void OnPostSuccess(JSONObject jsonObject);
        void OnPostError(String errorMessage);
    }

    public interface OnModifyPasswordCallback {
        void OnModifySuccess(JSONObject jsonObject);
        void OnModifyError(String errorMessage);
    }

    public interface OnLogoutCallback {
        void OnLogoutSuccess(JSONObject jsonObject);
        void OnLogoutError(String errorMessage);
    }

    public interface OngetHistoryCallback {
        void OnHistorySuccess (JSONObject jsonObject);
        void OnHistoryError(String errorMessage);
    }

    public interface OngetUpdateInfoCallback {
        void OnUpdateSuccess (JSONObject jsonObject);
        void OnUpdateError (String errorMessage);
    }
}
