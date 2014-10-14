package com.gongpingjia.gpjdetector.utility;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static String SDPATH = "/mnt/sdcard/external_sd/";

    private int FILESIZE = 4 * 1024;

    public FileUtils() {
        //得到当前外部存储设备的目录( /SDCARD )
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    public String getSDPATH() {
        return SDPATH;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws java.io.IOException
     */
    public File createSDFile(String fileName) throws IOException {
//        File file = new File(SDPATH + fileName);
    	 File file = new File(fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public static File createSDDir(String dirName) {
//        File dir = new File(SDPATH + dirName);
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    public File getSDFile(String path, String filename) {
        File file = null;
        if (isFileExist(path + filename)) {
            file = new File(path, filename);
//            file = new File(SDPATH + path, filename);
        }
        return file;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
//        File file = new File(SDPATH + fileName);
        return file.exists();
    }
    
    /**
	 * 复制文件
	 * @param src
	 * @param dst
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("resource")
	public static void copyFile(File src, File dst) throws IOException
	{
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		finally
		{
			if(inChannel != null)
			{
				inChannel.close();
			}
			if(outChannel != null)
			{
				outChannel.close();
			}
		}
	}
	
	/**
	 * 删除文件
	 * @param file
	 */
	public static void deleteFile(String file)
	{
		if(Utils.isNull(file)){
			return;
		}
		File f = new File(file);
		if(null != f && !f.isDirectory())
		{
			f.delete();
		}
	}

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];

            int length;
            while ((length = (input.read(buffer))) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 将一个bytes里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param bytes
     * @return
     */
    public File write2SDFromBytes(String path, String fileName, byte[] bytes) {
        File file = null;
        OutputStream output = null;
        
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);
            
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];

            output.write(bytes);

            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public String readFile2String(String path, String fileName) {

        File file = getSDFile(path, fileName);
        InputStream input;
        InputStreamReader reader;
        String line;
        String result = "";

        if (null == file) {
            return null;
        }

        try {
            input = new FileInputStream(file);
            reader = new InputStreamReader(input);
            BufferedReader bufReader = new BufferedReader(reader);

            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public JSONObject readFile2JsonObject(String path, String fileName) {

        File file = getSDFile(path, fileName);
        InputStream input;
        InputStreamReader reader;
        String line;
        String result = "";
        JSONObject json = null;

        if (null == file) {
            return null;
        }

        try {
            input = new FileInputStream(file);
            reader = new InputStreamReader(input);
            BufferedReader bufReader = new BufferedReader(reader);

            while ((line = bufReader.readLine()) != null) {
                result += line;
            }

            reader.close();

            json = new JSONObject(result);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONArray readFile2JsonArray(String path, String fileName) {

        File file = getSDFile(path, fileName);
        
        InputStream input;
        InputStreamReader reader;
        String line;
        String result = "";
        JSONArray json = null;

        if (null == file) {
            return null;
        }

        try {
            input = new FileInputStream(file);
            reader = new InputStreamReader(input);
            BufferedReader bufReader = new BufferedReader(reader);

            while ((line = bufReader.readLine()) != null) {
                result += line;
            }

            reader.close();

            json = new JSONArray(result);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public Bitmap readFile2Bitmap(String path, String fileName) {

        File file = getSDFile(path, fileName);
        InputStream input;
        Bitmap bitmap;

        if (null == file) {
            return null;
        }

        try {
            input = new FileInputStream(file);

            bitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return bitmap;
    }

}

