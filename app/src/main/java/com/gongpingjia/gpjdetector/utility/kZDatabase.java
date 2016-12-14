package com.gongpingjia.gpjdetector.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.format.DateFormat;
import android.util.Log;

import com.gongpingjia.gpjdetector.global.Constant;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.Calendar;
import java.util.Locale;

public class kZDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "detector.db";
    private static final int DATABASE_VERSION = 57;

    public kZDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }

    public Cursor getDBItems(int type) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String sqlSelection = "type = ?";
        String[] sqlSelectionArgs;
        switch (type) {
            case 10:
                sqlSelectionArgs = new String[]{"基本信息"};
                break;
            case 20:
                sqlSelectionArgs = new String[]{"车辆属性"};
                break;
            case 30:
                sqlSelectionArgs = new String[]{"主要功能"};
                break;
            case 1:
                if("3".equals(SharedPreUtil.getInstance().getUser().getUser_type())) {
                    sqlSelection = "type = ? AND checker = ?";

                }else{
                    sqlSelection = "type = ? AND pic_collector = ?";
                }
                sqlSelectionArgs = new String[]{"cap","1"};
                break;
            case 2:
                sqlSelectionArgs = new String[]{"钣金"};
                break;
            case 3:
                sqlSelectionArgs = new String[]{"车架"};
                break;
            case 4:
                sqlSelectionArgs = new String[]{"外观"};
                break;
            case 5:
                sqlSelectionArgs = new String[]{"内饰"};
                break;
            case 6:
                sqlSelectionArgs = new String[]{"发动机舱"};
                break;
            case 7:
                sqlSelectionArgs = new String[]{"底盘"};
                break;

            case 8:
                sqlSelectionArgs = new String[]{"检测报告"};
                break;
            default:
                sqlSelectionArgs = null;
                break;
        }

        String[] sqlSelect = {"key", "name", "value", "priority", "option"};
        String sqlTables = Constant.getTableName();


        qb.setTables(sqlTables);

            Cursor c = qb.query(db, sqlSelect, sqlSelection, sqlSelectionArgs,
                null, null, "_id", null);
        c.moveToFirst();
        return c;

    }

    public Cursor getBrandList() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"slug", "name", "first_lett", "logo_img"};
        String sqlTables = "brand";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null, null);
        c.moveToFirst();
        return c;
    }


    public Cursor getModelDetail(String[] global_slug) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String sqlSelection = "global_slug = ?";


        String[] sqlSelect = {"year", "detail_model_slug", "url", "detail_model"};
        String sqlTables = "open_model_detail";
        qb.setTables(sqlTables);

        Cursor c = qb.query(db, sqlSelect, sqlSelection, global_slug,
                null, null, "id", null);

//        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null, null);
        c.moveToFirst();
        return c;
    }


    public Cursor getModelList(String parentBrand) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"slug", "name", "parent", "keywords", "thumbnail", "logo_img"};
        String sqlSelection = "parent=?";
        String[] sqlSelectionArgs = new String[]{parentBrand};
        String sqlTables = "model";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, sqlSelection, sqlSelectionArgs, null, null, null, null);
        c.moveToFirst();
        return c;
    }

    public Cursor getModelThumbnail(String modelName) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"thumbnail"};
        String sqlSelection = "name=?";
        String[] sqlSelectionArgs = new String[]{modelName};
        String sqlTables = "model";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, sqlSelection, sqlSelectionArgs, null, null, null, null);
        c.moveToFirst();
        return c;
    }

    public String createTable() {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = "detection_" + new DateFormat().format("yyyyMMddhhmmss", Calendar.getInstance(Locale.CHINA));
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName +
                " (_id INTEGER PRIMARY KEY," +
                "key TEXT," +
                "name TEXT," +
                "value TEXT," +
                "type TEXT," +
                "priority TEXT," +
                "option TEXT," +
                "description TEXT," +
                "price_scale TEXT," +
                "checker TEXT," +
                "pic_collector TEXT)");

        db.execSQL("INSERT INTO " + tableName + " SELECT * FROM detection");

        db.execSQL("CREATE TABLE IF NOT EXISTS history" +
                " (tableName TEXT PRIMARY KEY, date TEXT, isFinish INTEGER)");
        db.execSQL("INSERT INTO history values('" + tableName + "', '" + new DateFormat().format("yyyy-MM-dd kk:mm:ss", Calendar.getInstance(Locale.CHINA)) + "'," + "'0')");
        return tableName;
    }

    public void updateItem(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("UPDATE" + " " + Constant.getTableName() + " " + "SET value = '" + value + "' " + "WHERE key='" + key + "'");
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.toString());
        }
    }

    public void setIsFinish(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("UPDATE history SET isFinish = 1 WHERE tableName='" + tableName + "'");
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.toString());
        }
    }

    public void insertItem(String _id, String key, String name, String value, String type) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteQueryBuilder dq = new SQLiteQueryBuilder();
        String[] sqlSelect = {"_id"};
        String sqlSelection = "_id=?";
        String[] sqlSelectionArgs = new String[]{_id};
        dq.setTables(Constant.getTableName());
        Cursor c = dq.query(db, sqlSelect, sqlSelection, sqlSelectionArgs, null, null, null, null);
        if (0 == c.getCount()) {
            db.execSQL("INSERT INTO " + Constant.getTableName() + "(_id, key, name, value, type)" +
                    " values('" + _id + "', '" + key + "', '" + name + "', '" + value + "', '" + type + "')");
        } else {
            ContentValues cv = new ContentValues();
            cv.put("key", key);
            cv.put("name", name);
            cv.put("value", value);
            cv.put("type", type);

            db.update(Constant.getTableName(), cv, "_id=?", new String[]{_id});

        }

    }

    public String getValue(String tableName, String key) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"value"};
        String sqlSelection = "key=?";
        String[] sqlSelectionArgs = new String[]{key};
        qb.setTables(tableName);
        Cursor c = qb.query(db, sqlSelect, sqlSelection, sqlSelectionArgs, null, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            return c.getString(0);
        } else {
            return null;
        }
    }

    public Cursor getHistoryList() {

//        if (tabbleIsExist("history")) {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String[] sqlSelect = {"tableName", "date", "isFinish"};
            String sqlTables = "history";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, sqlSelect, "isFinish=?", new String[]{"0"}, null, null, null, null);
            c.moveToFirst();
            return c;
//        } else {
//            return null;
//        } } else {
//            return null;
//        }

    }

    public void deleteHistory(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM history WHERE tableName='" + tableName + "'");
            db.execSQL("DROP TABLE " + tableName);
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.toString());
        }
    }

    public int getHistorySize() {
        Cursor c = getHistoryList();
        if (c != null) {
            return c.getCount();
        } else {
            return 0;
        }
    }


    public boolean tabbleIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from " + "detector.db" + " where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

}
