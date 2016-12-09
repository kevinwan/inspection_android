package com.gongpingjia.gpjdetector.utility;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;


public class UserLocation implements AMapLocationListener {

    private LocationManagerProxy mLocationManagerProxy;

    private String city = "";
    private String provice, withshi_city, address;

    public double latitude, longitude;

    public boolean islocation;

    static UserLocation instance;

    private Context mContext;

    public static UserLocation getInstance() {
        if (instance == null) {
            instance = new UserLocation();
        }
        return instance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void cancleLocation() {
        mLocationManagerProxy.removeUpdates(this);
    }

    public void init(Context context) {
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        mLocationManagerProxy.setGpsEnable(false);
        mContext = context;
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用destroy()方法
        // 其中如果间隔时间为-1，则定位只定一次,
        // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, this);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isIslocation() {
        return islocation;
    }

    public void setIslocation(boolean islocation) {
        this.islocation = islocation;
    }


    //带市的城市
    public String getWithshi_city() {
        return withshi_city;
    }

    public void setWithshi_city(String withshi_city) {
        this.withshi_city = withshi_city;
    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
            islocation = true;
            // 定位成功回调信息，设置相关消息
            withshi_city = amapLocation.getCity();
            city = amapLocation.getCity();
            if (city != null && city.contains("市")) {
                city = city.replace("市", "");
            }
            provice = amapLocation.getProvince();

            if (provice != null && provice.contains("省")) {
                provice = provice.replace("省", "");
            }
            address = amapLocation.getAddress();
//            latitude = amapLocation.getLatitude();
//            longitude = amapLocation.getLongitude();
            double[] latlongs = GPSUtil.gcj02_To_Bd09(amapLocation.getLatitude(), amapLocation.getLongitude());
            latitude = latlongs[0];
            longitude = latlongs[1];
            mContext.sendBroadcast(new Intent("com.gongpingjia.city"));
        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Logger.e("AmapError", "location Error, ErrCode:"
                    + amapLocation.getAMapException().getErrorCode() + ", errInfo:"
                    + amapLocation.getAMapException().getErrorMessage());
            islocation = false;
        }
        mLocationManagerProxy.destroy();

    }

}
