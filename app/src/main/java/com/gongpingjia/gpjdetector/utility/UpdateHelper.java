package com.gongpingjia.gpjdetector.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kooze on 14-10-13.
 */
public class UpdateHelper {

    RequestUtils requestUtils;
    Context context;

    public UpdateHelper(Context context) {
        this.context = context;
        requestUtils = new RequestUtils(context);
    }

    public static String getAppVersion() {
        String versionName = null;
        try {
            String pkName = Constant.appContext.getPackageName();
            versionName = Constant.appContext.getPackageManager().getPackageInfo(pkName, 0).versionName;

        } catch (Exception e) {
            return null;
        }
        return versionName;
    }

    public void checkUpdate() {
        final String currentVersion = getAppVersion();

        String updateVersion;
        requestUtils.getUpdateInfo(new RequestUtils.OngetUpdateInfoCallback() {
            @Override
            public void OnUpdateSuccess(JSONObject jsonObject) {
                String updateVersion;
                String url;
                try {
                    updateVersion = jsonObject.getString("version");
                    url = jsonObject.getString("download_url");
                } catch (JSONException e) {
                    Toast.makeText(context, "检测更新失败：数据格式不正确。", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (null == currentVersion || null == updateVersion) {
                    Toast.makeText(context, "检测更新失败：数据格式不正确。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (updateVersion.compareTo(currentVersion) > 0) {
                    showUpdateDialog(updateVersion, url);
                }

            }

            @Override
            public void OnUpdateError(String errorMessage) {
                Toast.makeText(context, "检测更新失败：" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showUpdateDialog(String updateVersion, final String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("发现新版本 V." + updateVersion);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(context, UpdateService.class);
                intent.putExtra("url", url);
                context.startService(intent);
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
