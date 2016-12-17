package com.everyoo.smartgateway.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * Created by chaos on 2016/3/11.
 * 清除应用本地数据
 */
public class CleanAppDataUtil {

    private static final String TAG = "CleanAppDataUtil";
    private static String DATA_PATH = null;

    // 清除应用本地数据
    public static void cleanLocalData(Context context) {
        System.out.println("clean local data");
        DATA_PATH = "data/data/" + context.getPackageName() + "/";
        deleteFilesByDirectory(new File(DATA_PATH + "shared_prefs"));
        deleteFilesByDirectory(new File(DATA_PATH + "databases"));
        deleteFilesByDirectory(new File(DATA_PATH + "cache"));
        deleteFilesByDirectory(new File(DATA_PATH + "files"));
        deleteFilesByDirectory(new File(DATA_PATH + "xlogs"));
        System.out.println("has clean local data");
    }


    // 删除文件
    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }
}
