package com.gongpingjia.gpjdetector.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class NetUtil
{
    
    public static boolean isSuccess(JSONObject json)
    {
        boolean isSuccess = false;
        try
        {
            String status = json.getString("status");
            if (status.equals("success"))
            {
                isSuccess = true;
            }
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isSuccess;
    }
    
    public static boolean getNetState(Context context)
    {
        ConnectivityManager connectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (activeNetInfo.isConnected())
        {
            return true;
        }
        if (mobNetInfo.isConnected())
        {
            return true;
        }
        if (!activeNetInfo.isConnected() && !mobNetInfo.isConnected())
        {
            return false;
        }
        return false;
    }
    
}
