package com.gongpingjia.gpjdetector.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class DhUtil {

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dipValue + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String delHtml(String str) {
        String info = str.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
        info = info.replaceAll("[(/>)<]", "");
        return info;
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 通过 uri 获取图片的文件
     *
     * @param context
     * @param uri
     * @return
     */
    public File uriToImageFile(Activity context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = context.managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);
        return file;
    }


    public static int getColor(String color) {
        int c = 0;
        if (color.equals("白色")) {
            c = 0xffffffff;
        } else if (color.equals("黑色")) {
            c = 0xff000000;
        } else if (color.equals("银色")) {
            c = 0xffEDECEA;
        } else if (color.equals("灰色")) {
            c = 0xffC5C2BC;
        } else if (color.equals("红色")) {
            c = 0xffff0000;
        } else if (color.equals("棕色")) {
            c = 0xffA87247;
        } else if (color.equals("褐色")) {
            c = 0xff7B422B;
        } else if (color.equals("蓝色")) {
            c = 0xff003C66;
        } else if (color.equals("栗色")) {
            c = 0xff6A2823;
        } else if (color.equals("金色")) {
            c = 0xffFF9A00;
        } else if (color.equals("橙色")) {
            c = 0xffFF8000;
        } else if (color.equals("米色")) {
            c = 0xffEFDFA3;
        } else if (color.equals("黄色")) {
            c = 0xffFEC757;
        } else if (color.equals("紫色")) {
            c = 0xffAB00C4;
        } else if (color.equals("青色")) {
            c = 0xff006354;
        } else if (color.equals("绿色")) {
            c = 0xff008830;
        } else if (color.equals("其他")) {
            c = -10000;
        }
        return c;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap =
                Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    public static float getRollPages(Context mContext, float heigt) {
        return 1 + heigt / mContext.getResources().getDisplayMetrics().heightPixels;
    }


    public static String Distance(double long1, double lat1, double long2,
                                  double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d/1000f+"km";
    }


}
