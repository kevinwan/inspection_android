package com.gongpingjia.gpjdetector.utility;

import java.lang.reflect.Field;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * @description SharedPreferences封装工具类。
 *              可以序列化对象的所有属性，包括private。但是不能序列化父类属性。这就是getFields和getDeclaredFields的区别
 *              。
 * @since 2014.1.8
 */
public class PreferenceUtils {
	private static final int INT = 104431;
	private static final int LONG = 3327612;
	private static final int FLOAT = 97526364;
	private static final int BOOLEAN = 64711720;
	private static final int STRING = 1195259493;
	
	private Context context;

	public PreferenceUtils(Context context) {
		this.context = context;
	}

	public void saveObject(Object o) {
		SharedPreferences preference = context.getSharedPreferences(o
				.getClass().toString(), 0);
		SharedPreferences.Editor editor = preference.edit();
		Field[] fields = o.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				String key = field.getType() + ";" + field.getName();
				switch (field.getType().getName().hashCode()) {
				case INT:
					int intVal = field.getInt(o);
					editor.putInt(key, intVal);
					break;
				case LONG:
					long longVal = field.getLong(o);
					editor.putLong(key, longVal);
					break;
				case FLOAT:
					float floatVal = field.getFloat(o);
					editor.putFloat(key, floatVal);
					break;
				case BOOLEAN:
					boolean boolVal = field.getBoolean(o);
					editor.putBoolean(key, boolVal);
					break;
				case STRING:
					String strVal = field.get(o).toString();
					editor.putString(key, strVal);
					break;
				}
			} catch (IllegalArgumentException e) {
				Utils.debug(e.toString());
			} catch (IllegalAccessException e) {
				Utils.debug(e.toString());
			}
		}
		editor.commit();
		editor = null;
		preference = null;
	}

	public <T> T loadObject(Class<T> cls) {
		SharedPreferences preference = context.getSharedPreferences(
				cls.toString(), 0);
		Map<String, ?> all = (Map<String, ?>) preference.getAll();
		preference = null;
		try {
			T object = cls.newInstance();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String key = field.getType() + ";" + field.getName();
				if (all.containsKey(key)) {
					// 原来是这么处理：case INT:field.setInt(object, (Integer)
					// all.get(key));break;
					// 后来发现可以统一处理，不用区分类型
					switch (field.getType().getName().hashCode()) {
					case INT:
					case LONG:
					case FLOAT:
					case BOOLEAN:
					case STRING:
						field.set(object, all.get(key));
						break;
					}
				}
			}
			return object;
		} catch (SecurityException e) {
			Utils.debug(e.toString());
		} catch (IllegalArgumentException e) {
			Utils.debug(e.toString());
		} catch (InstantiationException e) {
			Utils.debug(e.toString());
		} catch (IllegalAccessException e) {
			Utils.debug(e.toString());
		}
		return null;
	}
	
	public <T> void clearObject(Class<T> cls) {
		SharedPreferences preference = context.getSharedPreferences(
				cls.toString(), 0);
		SharedPreferences.Editor editor = preference.edit();
		editor.clear().commit();
	}
}
