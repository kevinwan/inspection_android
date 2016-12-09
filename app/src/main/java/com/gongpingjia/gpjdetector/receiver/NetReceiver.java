package com.gongpingjia.gpjdetector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.UserLocation;

public class NetReceiver extends BroadcastReceiver {

    private static final String NET_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NET_CHANGE.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mobileInfo.isConnected() || wifiInfo.isConnected()) {

                if (!UserLocation.getInstance().isIslocation()) {
                    UserLocation.getInstance().init(GPJApplication.getInstance());
                }
            }
        }

    }

}