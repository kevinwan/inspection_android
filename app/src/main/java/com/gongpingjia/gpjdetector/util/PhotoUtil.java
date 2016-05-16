package com.gongpingjia.gpjdetector.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/***
 * 头像上传工具类 调用 getPhoto 在onactivityResult 调用
 * <p/>
 * onPhotoFromCamera
 * <p/>
 * onPhotoFromPick
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class PhotoUtil {


    //保存照片为正方形
    public static void saveLocalImageSquare(Bitmap bm, File f) {
        if (bm == null)
            return;
        File file = f;
        try {
            file.createNewFile();

            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // // bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            // int options = 100;
            // while (baos.toByteArray().length / 1024 > 100)
            // { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            // baos.reset();// 重置baos即清空baos
            // bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//
            // 这里压缩options%，把压缩后的数据存放到baos中
            // options -= 10;// 每次都减少10
            // }
            // baos.reset();
            // baos.flush();
            // baos.close();
            Bitmap squareBitmap = ImageCrop(bm);

            OutputStream outStream = new FileOutputStream(file);
            compressImage(squareBitmap, outStream);
            outStream.flush();
            outStream.close();
            bm.recycle();
            squareBitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //将照片存到本地相册(保存的图片为正方形)
    public static String saveLocalImage(Bitmap bm, int degree, Context mContext) {
        if (bm == null)
            return null;
        try {

            File appDir = new File(Environment
                    .getExternalStorageDirectory(), "carplay");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);

            if (degree != 0) {
                bm = rotateBitmapByDegree(bm, degree);
            }
            Bitmap squareBitmap = ImageCrop(bm);
            OutputStream outStream = new FileOutputStream(file);
            compressImage(squareBitmap, outStream);
            outStream.flush();
            outStream.close();
            bm.recycle();
            squareBitmap.recycle();
            Intent intent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            mContext.sendBroadcast(intent);

            return file.getPath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //拍照存储照片
    public static void saveLocalImage(Bitmap bm, File f, int degree) {
        if (bm == null)
            return;
        File file = f;
        try {
//
//            File appDir = new File(Environment
//                    .getExternalStorageDirectory(), "carplay");
//            if (!appDir.exists()) {
//                appDir.mkdir();
//            }
//            String fileName = System.currentTimeMillis() + ".jpg";
//            File file1 = new File(appDir, fileName);


            file.createNewFile();


            if (degree != 0) {
                bm = rotateBitmapByDegree(bm, degree);
            }

            OutputStream outStream = new FileOutputStream(file);
            compressImage(bm, outStream);
            outStream.flush();
            outStream.close();
            bm.recycle();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //拍照保存正方形图片
    public static void saveLocalImageSquare(Bitmap bm, File f, int degree) {
        if (bm == null)
            return;
        File file = f;
        try {
//
//            File appDir = new File(Environment
//                    .getExternalStorageDirectory(), "carplay");
//            if (!appDir.exists()) {
//                appDir.mkdir();
//            }
//            String fileName = System.currentTimeMillis() + ".jpg";
//            File file1 = new File(appDir, fileName);


            file.createNewFile();


            if (degree != 0) {
                bm = rotateBitmapByDegree(bm, degree);
            }
            Bitmap squareBitmap = ImageCrop(bm);
            OutputStream outStream = new FileOutputStream(file);
            compressImage(squareBitmap, outStream);
            outStream.flush();
            outStream.close();
            bm.recycle();
            squareBitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressImage(Bitmap image, OutputStream outStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        baos.reset();
        image.compress(Bitmap.CompressFormat.JPEG, options, outStream);

        //
        // ByteArrayInputStream isBm = new
        // ByteArrayInputStream(baos.toByteArray());//
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        //
        // // 把ByteArrayInputStream数据生成图片
        // return new SoftReference<Bitmap>(BitmapFactory.decodeStream(isBm,
        // null, null)).get();
    }

    /**
     * 由本地获取图片
     *
     * @param f
     * @return
     */
    public static Bitmap getLocalImage(File f) {
        File file = f;
        if (file.exists()) {
            try {
                file.setLastModified(System.currentTimeMillis());
                FileInputStream in = new FileInputStream(file);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                int sWidth = 800;
                int sHeight = 800;
                int mWidth = options.outWidth;
                int mHeight = options.outHeight;
                int s = 1;
                while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2)) {
                    s *= 2;
                }
                options = new BitmapFactory.Options();
                options.inSampleSize = s;
                in.close();
                // 再次获取
                in = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * aspectY Y对于X的比例 outputX X 的宽
     **/
    public static void photoZoom(Activity activity, Uri uri, Uri outUri,
                                 int photoResoultCode, int aspectX, int aspectY, int outputX, Fragment fg) {
        Log.d("msg", "22222222222222");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        if (aspectY > 0) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }
        // outputX outputY 是裁剪图片宽高
        // intent.putExtra("outputX", outputX);
        // if (aspectY > 0) {
        // intent.putExtra("outputY", (int)(aspectY/(float)aspectX) * outputX);
        // }
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // 加入人脸识别
        fg.startActivityForResult(intent, photoResoultCode);
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }


    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = (w - 400) / 2;//基于原图，取正方形左上角x坐标
        int retY = (h - 600) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, 400, 600, null, false);
    }

}
