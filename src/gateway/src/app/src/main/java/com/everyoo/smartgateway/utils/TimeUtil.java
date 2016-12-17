package com.everyoo.smartgateway.utils;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by chaos on 2016/1/28.
 * 功能描述：获取当前系统时间
 */
public class TimeUtil {

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public static String currentTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(date);
    }

    // 将时区、时间、24h制设置封装到一个方法中
    public static void setAutoTimeAndZone(Context mContext) {
        if (!isTimeZoneChina()) {
            setChinaTimeZone(mContext);
        }
        if (!isTimeZoneAuto(mContext)) {
            setAutoTimeZone(mContext, 1);
        }
        if (!isDateTimeAuto(mContext)) {
            setAutoDateTime(mContext, 1);
        }
        if (!is24Hour(mContext)) {
            set24Hour(mContext);
        }
    }

    public static boolean is24Hour(Context mContext) {
        ContentResolver cv = mContext.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat != null && strTimeFormat.equals("24")) {
            return true;
        }
        return false;
    }

    public static void set24Hour(Context mContext) {
        ContentResolver cv = mContext.getContentResolver();
        android.provider.Settings.System.putString(cv, Settings.System.TIME_12_24, "24");
    }

    public static boolean isTimeZoneAuto(Context mContext) {
        try {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isTimeZoneChina() {
        TimeZone defaultZone = TimeZone.getDefault();
        if (defaultZone.getDisplayName(false, TimeZone.SHORT).equals("GMT+08:00")) {
            return true;
        }

        return false;

    }

    public static void setChinaTimeZone(Context context) {
       /* TimeZone.getTimeZone("GMT+8");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));*/
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone("Asia/Shanghai");// Asia/Taipei//GMT+08:00
    }

    public static void setAutoTimeZone(Context mContext, int checked) {
        android.provider.Settings.Global.putInt(mContext.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
    }

    public static boolean isDateTimeAuto(Context mContext) {
        try {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setAutoDateTime(Context mContext, int checked) {
        android.provider.Settings.Global.putInt(mContext.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME, checked);
    }


    // 先请求服务器时间，如果失败的话，再请求国家授时网服务器时间，如果再失败的话，就请求淘宝时间
    public static void getNetworkTimeAndSetSystemTime() {
        String everyoo = "http://common.api.everyoo.com/ntp/time";
        String nstc = "http://www.nstc.com/";
        String taobao = "http://www.taobao.com/";
        boolean isSuccess = false;
        isSuccess = TimeUtil.requestServerTime(everyoo);
        if (!isSuccess) {
            System.out.println("begin request nstc");
            isSuccess = TimeUtil.requestNetworkTime(nstc);
            if (!isSuccess) {
                System.out.println("begin request tabao");
                TimeUtil.requestNetworkTime(taobao);
            } else {
                System.out.println("nstc success");
            }
        }

    }

    public static boolean requestServerTime(String value) {
        String path = value;
        URL url;
        try {
            url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((len = inputStream.read(buffer)) != -1) {
                stringBuffer.append(new String(buffer, 0, len));
            }
            inputStream.close();
            System.out.println("serverTime = " + stringBuffer.toString());
            JSONObject jsonObject = new JSONObject(stringBuffer.toString());
            if (jsonObject.optInt("code") == 200 && jsonObject.optInt("result") == 2003) {
                JSONObject infoObject = jsonObject.optJSONObject("info");
                if (infoObject != null) {
                    System.out.println("begin set system time");
                    return setSystemTime(infoObject.optLong("time") * 1000);// 服务器端返回的时间精确到秒，系统要求毫秒级别
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 中国科学院国家授时中心：http://www.ntsc.ac.cn/
    // 淘宝：http://www.taobao.com/
    public static boolean requestNetworkTime(String value) {
        String path = value;
        URL url = null;
        try {
            url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            long networkTime = urlConnection.getDate();
            // 默认东八区
            System.out.println("path = " + path);
            System.out.println("networkTime = " + networkTime);
            System.out.println("currentTime = " + System.currentTimeMillis());
            return setSystemTime(networkTime);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setSystemTime(long value) {
        long networkTime = value;
        if (Math.abs(networkTime - System.currentTimeMillis()) > 5 * 60 * 1000) {
            System.out.println("采用网络时间");
            return SystemClock.setCurrentTimeMillis(networkTime);
        }
        return true;
    }


}
