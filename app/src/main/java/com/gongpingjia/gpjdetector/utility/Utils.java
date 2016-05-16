package com.gongpingjia.gpjdetector.utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gongpingjia.gpjdetector.global.Constant;

/**
 * @description 常用工具方法类
 * @since 2014.1.7
 */
public class Utils {

	private static final String LOG_FILE_NAME = "debug.log";
	private static final boolean DEBUG = true;

	public static void debug(String message) {
		if (DEBUG) {
			System.out.println(message);
			output(message);
		}
	}

	public static void debug(String tag, String message) {
		if (DEBUG) {
			Log.v(tag, message);
			output(message);
		}
	}

	/**
	 * 获取图片缩略图地址 <br>
	 * 最大边长200像素
	 * 
	 * @param url
	 * @return
	 */
	public static String getThumbUrl(String url) {
		return url + "!200";
	}

	/**
	 * 获取图片缩略图地址 <br>
	 * 最大边长可变
	 * 
	 * @param url
	 * @param max
	 * @return
	 */
	public static String getThumbUrl(String url, int max) {
		return url + "!" + max;
	}

	private static synchronized void output(String message) {
		File file = new File(Environment.getExternalStorageDirectory(),
				LOG_FILE_NAME);
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			fos = new FileOutputStream(file, true);
			dos = new DataOutputStream(fos);
			SimpleDateFormat sdf = new SimpleDateFormat("MM.dd HH:mm:ss",
					Locale.US);
			String suffix = sdf.format(new Date());
			String content = suffix + "  " + message + "\n";
			dos.writeUTF(content);
		} catch (IOException e) {
			debug(e.toString());
		} catch (Exception e) {
			debug(e.toString());
		} finally {
			try {
				if (dos != null) {
					dos.flush();
					dos.close();
					dos = null;
				}
				if (fos != null) {
					fos.flush();
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				debug(e.toString());
			}
		}
	}

	/**
	 * 判断是否是合法的Email地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		Matcher mc = pattern.matcher(email);
		return mc.matches();
	}

	/**
	 * 判断是否是合法的手机号码
	 * 
	 * @note 如果运营商发布新号段，需要更新该方法
	 * @param phone
	 * @return
	 */
	public static boolean isValidMobilePhoneNumber(String phone) {
		Pattern pattern = Pattern
				.compile("^(13[0-9]|14[3|5|7|9]|15[0-9]|170|18[0-9])\\d{8}$");
		Matcher mc = pattern.matcher(phone);
		return mc.matches();
	}

	/**
	 * 判断是否是合法的固定电话号码
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isValidPhoneNumber(String phone) {
		Pattern pattern = Pattern
				.compile("^(\\(\\d{3,4}-)|(\\d{3,4}-)?\\d{7,8}$");
		Matcher mc = pattern.matcher(phone);
		return mc.matches();
	}

	/**
	 * 判断是否是合法的URL
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isValidURL(String url) {
		Pattern patterna = Patterns.WEB_URL;
		Matcher mca = patterna.matcher(url);
		return mca.matches();
	}

	/**
	 * 是否为空
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isNull(Object object) {
		boolean result;
		if (TextUtils.isEmpty((CharSequence) (object))) {
			result = true;
		} else {
			String str = String.valueOf(object);
			str = str.toLowerCase();
			result = ("null").equals(str);
		}
		return result;
	}

	/**
	 * 如果键盘没有收回 自动关闭键盘
	 * 
	 * @param activity
	 *            Activity
	 * @param v
	 *            控件View
	 */
	public static void autoCloseKeyboard(Activity activity, View v) {
		/** 收起键盘 */
		View view = activity.getWindow().peekDecorView();
		if (view != null && view.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	/**
	 * 判断当前应用是否在前台
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAppForground(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> l = mActivityManager
				.getRunningAppProcesses();
		Iterator<RunningAppProcessInfo> i = l.iterator();
		while (i.hasNext()) {
			RunningAppProcessInfo info = i.next();
			if (info.uid == context.getApplicationInfo().uid
					&& info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否为锁屏状态
	 * 
	 * @param c
	 * @return
	 */
	public final static boolean isScreenLocked(Context c) {
		KeyguardManager mKeyguardManager = (KeyguardManager) c
				.getSystemService(Context.KEYGUARD_SERVICE);
		return mKeyguardManager.inKeyguardRestrictedInputMode();
	}

	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	public static String getMD5(String str) {
		String resultString = null;
		try {
			resultString = new String(str);
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(str.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	/**
	 * 唤醒屏幕，如果当前锁屏了
	 */
	@SuppressLint("Wakelock")
	public static void notifyScreen(Context context) {
		if (!isScreenLocked(context)) {
			return;
		}
		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock kl = km.newKeyguardLock("unLock");
		kl.disableKeyguard(); // 解锁
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);// 获取电源管理器对象
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		wl.acquire(5000);// 点亮屏幕
		// wl.release();//释放
	}

    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
    public static String GetImageStr(String imgFilePath) {
        byte[] data = null;

        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
            Logger.d(Constant.TAG, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对字节数组Base64编码
        return Base64.encodeToString(data, Base64.DEFAULT);// 返回Base64编码过的字节数组字符串
    }

    public static boolean GenerateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        try {
            // Base64解码
            byte[] bytes = Base64.decode(imgStr, Base64.DEFAULT);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
