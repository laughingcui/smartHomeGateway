package com.everyoo.smartgateway.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.elvishew.xlog.printer.flattener.LogFlattener;
import com.everyoo.gatewaylitedaemon.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chaos on 2016/6/16.
 */
public class LogUtil {
    public static void println(String key, String value) {
        Log.d(key, value);
       // XLog.d(key + " " + value);
    }

    public static void toast(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });

    }


    public static void controlLog(String key, String value) {
        Logger logger = XLog.tag("Control-LOG")
                // .st(3)   // 允许打印深度为3的调用栈信息
                .build();

        logger.d(key + " " + value);
    }

    public static void controlLog(String value) {
        Logger logger = XLog.tag("Control-LOG")
                .build();

        logger.i(value);
    }

    public static void sceneLog(String key, String value) {
        Logger logger = XLog.tag("Scene-LOG")
                //  .st(3)   // 允许打印深度为3的调用栈信息
                .build();

        logger.d(key + " " + value);
    }

    public static void timingLog(String key, String value) {
        Logger logger = XLog.tag("Timing-LOG")
                //  .st(3)   // 允许打印深度为3的调用栈信息
                .build();

        logger.d(key + " " + value);
    }

    public static void linkageLog(String key, String value) {
        Logger logger = XLog.tag("Linkage-LOG")
                // .st(3)   // 允许打印深度为3的调用栈信息
                .build();

        logger.d(key + " " + value);
    }


    public static Printer globalFilePrinter;

    /**
     * 初始化log工具
     */
    public static void initXlog(Context context) {
        LogConfiguration config = new LogConfiguration.Builder()
                .tag("X-LOG")                                         // 指定 TAG，默认为 "X-LOG"
                //.t()                                                   // 允许打印线程信息，默认禁止
                // .st(2)                                                 // 允许打印深度为2的调用栈信息，默认禁止
                // .b()                                                   // 允许打印日志边框，默认禁止
                // .jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
                // .xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
                //  .throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
                //.threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
                // .stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
                //  .borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
                .build();

        Printer androidPrinter = new AndroidPrinter();
        Printer filePrinter = new FilePrinter                       // 打印日志到文件的打印器
                //.Builder(new File(Environment.getExternalStorageDirectory(), "xlog").getPath())       // Specify the path to save log file
                // .Builder(FileUtils.getDiskCacheDir(mContext, "xlog").getPath())
                .Builder(new File("data/data/" + context.getPackageName() + "/xlogs/").getPath())
                .fileNameGenerator(new DateFileNameGenerator())
                //.fileNameGenerator(new LevelFileNameGenerator())
                .backupStrategy(new FileSizeBackupStrategy(9437184))//将文件大小指定为 9M=1024B*1024KB * 9=9437184B
                .logFlattener(new MyLogFlattener())
                .build();

        XLog.init(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE,
                config,
                androidPrinter,
                filePrinter);

        globalFilePrinter = filePrinter;


    }

    /**
     * 自定义LogFlattener
     */
    public static class MyLogFlattener implements LogFlattener {

        private ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {

            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            }

        };

        @Override
        public CharSequence flatten(int logLevel, String tag, String message) {
            return formatter.get().format(new Date())
                    + '|' + LogLevel.getShortLevelName(logLevel)
                    + '|' + tag
                    + '|' + message;
        }


    }


}
