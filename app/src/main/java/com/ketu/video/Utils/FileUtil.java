package com.ketu.video.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * <p/>
 * 功能 :文件路径管理
 *
 * @author ketu 时间 2017/8/7
 * @version 1.0
 */

public class FileUtil {

    public static final String VIDEODIR="videofeedlist";

    private FileUtil() {

    }

    /**
     * 获取缩略图的路径
     * @param context
     * @return
     */
    public static String getVideoThumbPicPath(Context context,String videoUrl){

        String filename = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());

        return getCacheFilesPath(context) + "/" + filename +".jpg";
    }

    /**
     * 获取外部files的路径
     * @param context
     * @return
     */
    public static String  getCacheFilesPath(Context context){
        File files=context.getExternalFilesDir("");
        String filespath = files.getPath();

        files = new File(filespath);
        if (!files.exists()){
            files.mkdir();
        }
        return  filespath;

    }

    /**
     * 获得视频的根地址
     * @param
     * @return
     */
    public static String getVideoPath(){
        return Environment.getExternalStorageDirectory() + "/"+VIDEODIR+"/" ;

    }

    /**
     * 删除文件
     *
     * @param path 文件所在路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除文件夹的内容
     *
     * @param root 根目录
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }




    /**
     * 获取照片旋转角度
     *
     * @param path 文件路径
     *
     * @return 旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            return orientation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * @param file
     * 返回文件大小 byte为单位
     * */
    public static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
            }
        } catch (Exception e) {

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

}
