package com.gongpingjia.gpjdetector.global;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;






/**
 * Created by Kooze on 7/19/14.
 */
public class Constant {

    public static int RESULT_EMPTY = 0;

    //test SERVER_DOMAIN
    public static String SERVER_DOMAIN = "http://pad.eyelee.cn";

    //test 2
//   public static String SERVER_DOMAIN = "http://192.168.1.43:8000";

//    public static String SERVER_DOMAIN = "http://pad.gongpingjia.com";

    public static String CITY_SERVER_DOMAIN = "http://m.gongpingjia.com";
//
    public static  String TAG = "Detector_Logs";


    public static String value_POS = "-1";

    public static String value_NEG = "-3";

    public static String value_NEU = "-2";


    private static ProgressDialog progressDialog = null;

    public static final long splashTime = 2000;

    public static Context appContext = GPJApplication.getInstance().getApplicationContext();

    public static ProgressDialog showProgress(Context context, String title, String content) {
        progressDialog = ProgressDialog.show(context, title, content, true);
        return progressDialog;
    }

    public static int REQUEST_CODE_CAMERA = 1;

    public final static int REQUEST_CODE_MODEL = 2;

    public final static int REQUEST_CODE_LOGIN = 3;

    public final static int REQUEST_CODE_LOGIN_TO_START = 4;

    public final static int REQUEST_CODE_LOGIN_TO_HISTORY = 5;

    public final static int REQUEST_CODE_TO_USER = 6;

    public static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    public static int MAX_IMAGE_HEIGHT = 500;

    public static int CANVAS_WIDTH = 918;

    public static int CANVAS_HEIGHT = 550;

    public static int DEVICE_WIDTH = 1200;
    /**
     *照片采集人员权限
     */
    public static String PHOTO_USERTYPE = "5";
    /**
     *检测师权限
     */
    public static String CHECK_USERTYPE = "3";

    public static String tableName = "detection";

    public static int DEVICE_HEIGHT = 1920;

    public static String getTableName() {
        return tableName;
    }

    public static void setTableName(String tableName) {
        Constant.tableName = tableName;
    }
}
