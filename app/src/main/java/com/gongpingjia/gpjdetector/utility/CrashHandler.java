package com.gongpingjia.gpjdetector.utility;

import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

/**
 * 
 * @author 
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static final String OOM = "java.lang.OutOfMemoryError";
	private static final String HPROF_FILE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/data.hprof";

    
	private static CrashHandler sCrashHandler;

	private CrashHandler() {
	}

	public synchronized static CrashHandler getInstance() {
		if (sCrashHandler == null) {
			sCrashHandler = new CrashHandler();
		}
		return sCrashHandler;
	}

	public CrashHandler init() {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		return this;
	}



	public static boolean isOOM(Throwable throwable) {
		Log.d(TAG, "getName:" + throwable.getClass().getName());
		if (OOM.equals(throwable.getClass().getName())) {
			return true;
		} else {
			Throwable cause = throwable.getCause();
			if (cause != null) {
				return isOOM(cause);
			}
			return false;
		}
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
	    
	    if (!handleException(throwable) && mDefaultHandler != null) {  
            // 如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, throwable);  
	    }else{
	        android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
	    }
	}
	
	 private boolean handleException(Throwable throwable) {
	        if (throwable == null) {  
	            return false;  
	        }  
	        
	        if (isOOM(throwable)) {
	            try {
	                Debug.dumpHprofData(HPROF_FILE_PATH);
	            } catch (Exception e) {
	                Log.e(TAG, "couldn’t dump hprof", e);
	            }
	        }
	        String logdir;
	        if (Environment.getExternalStorageDirectory() != null) {
	            logdir = Environment.getExternalStorageDirectory()
	                    .getAbsolutePath()
	                    + File.separator
	                    + "gongpingjia"
	                    + File.separator + "error";
	            File file = new File(logdir);
	            boolean mkSuccess;
	            if (!file.isDirectory()) {
	                mkSuccess = file.mkdirs();
	                if (!mkSuccess) {
	                    mkSuccess = file.mkdirs();
	                }
	            }
	            try {
	                FileWriter fw = new FileWriter(logdir + File.separator
	                        + "error.txt", true);
	                fw.write(new Date() + "\n");
	                StackTraceElement[] stackTrace = throwable.getStackTrace();
	                fw.write(throwable.getMessage() + "\n");
	                for (int i = 0; i < stackTrace.length; i++) {
	                    fw.write("file:" + stackTrace[i].getFileName() + " class:"
	                            + stackTrace[i].getClassName() + " method:"
	                            + stackTrace[i].getMethodName() + " line:"
	                            + stackTrace[i].getLineNumber() + "\n");
	                }
	                fw.write("\n");
	                fw.flush();
	                fw.close();
	            } catch (IOException e) {
	                Log.e("crash handler", "load file failed...", e.getCause());
	            }
	        }

	        return true;  
	 }
}
