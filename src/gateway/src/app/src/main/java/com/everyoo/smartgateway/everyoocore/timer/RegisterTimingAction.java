package com.everyoo.smartgateway.everyoocore.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by chaos on 2016/4/7.
 */
public class RegisterTimingAction {

    public static final String ALARM_ACTION = "intent.action.register.alarm";
    private static final String TAG = "RegisterTimingAction ";

    /**
     * 注册闹钟
     *
     * @param mlist
     */
    public static void registeAlarm(ArrayList<TimingBean> mlist, Context context) {
        for (int j = 0; j < mlist.size(); j++) {
            if (mlist.get(j).getEnable() == (Constants.ENABLE)) {
                parseAlarm(mlist.get(j), context);
            } else {
                LogUtil.println(TAG + "registeAlarm", " alarm is closed");
            }
        }

    }

    public static void registeAlarm(TimingBean timingBean, Context context) {
        if (timingBean != null && timingBean.getEnable() == Constants.ENABLE) {
            parseAlarm(timingBean, context);
        } else {
            LogUtil.println(TAG + "registeAlarm", "timingBean is null");
        }
    }


    public static void parseAlarm(TimingBean alarmBean, Context context) {
        try {

            JSONArray jsonArray = new JSONArray(alarmBean.getLoop());
            String alarmTime = alarmBean.getAlarmTime();
            LogUtil.println(TAG + "parseAlarm", " alarmBean alarmTime = " + alarmTime);
            int[] loop = bubbleSort(jsonArray);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long triggerTime = simpleDateFormat.parse(alarmTime).getTime();// 定时触发时间
            LogUtil.println(TAG + "registeAlarm", " alarm time = " + simpleDateFormat.format(new Date(triggerTime)));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(triggerTime);

            // 考虑到存在app端和网关端时间不一致的情况，定时一概以星期计算，只去app端的hour和minute及second
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar1.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
            triggerTime = calendar1.getTimeInMillis();

            LogUtil.println(TAG + "registeAlarm", " trigger time = " + simpleDateFormat.format(new Date(triggerTime)));
            if (loop != null && loop.length > 0) {
                int nowWeek = calendar1.get(Calendar.DAY_OF_WEEK); // 计算今天是周几
                System.out.println("nowWeek = " + (nowWeek - 1));

                // 为方便计算，周一到周日，以1到7标识，和系统有些出入
                if (nowWeek == 1) {
                    nowWeek = 7;
                } else {
                    nowWeek = nowWeek - 1;
                }

                if (loop[0] > nowWeek) {
                    int interval = (loop[0] - nowWeek) * 24 * 60 * 60 * 1000;
                    long trigger = triggerTime + interval;
                    System.out.println(TAG + "registeAlarm alarm time more than current time" + simpleDateFormat.format(new Date(trigger)));
                    registeAlarm(trigger, alarmBean, context);
                } else if (loop[loop.length - 1] < nowWeek) {
                    int interal = (7 - nowWeek + loop[0]) * 24 * 60 * 60 * 1000;
                    long trigger = triggerTime + interal;
                    System.out.println(TAG + "+registeAlarm the largest value of alarm time is less than current time" + simpleDateFormat.format(new Date(trigger)));
                    registeAlarm(trigger, alarmBean, context);
                } else if (nowWeek < loop[loop.length - 1]) {
                    System.out.println(TAG + "registeAlarm current time is between the maximum and minimum");
                    for (int i = 0; i < loop.length; i++) {
                        if (loop[i] > nowWeek) {
                            long trigger = triggerTime + (loop[i] - nowWeek) * 24 * 60 * 60 * 1000;
                            System.out.println(TAG + "registeAlarm middle trigger time" + simpleDateFormat.format(new Date(trigger)));
                            registeAlarm(trigger, alarmBean, context);
                            break;
                        } else if (loop[i] == nowWeek) {
                            if (triggerTime > System.currentTimeMillis()) {
                                System.out.println(TAG + "registeAlarm today trigger " + simpleDateFormat.format(new Date(triggerTime)));
                                registeAlarm(triggerTime, alarmBean, context);
                                break;
                            }

                        }
                    }

                } else if (nowWeek == loop[loop.length - 1]) {
                    if (triggerTime > System.currentTimeMillis()) {
                        System.out.println(TAG + "registeAlarm today trigger " + simpleDateFormat.format(new Date(triggerTime)));
                        registeAlarm(triggerTime, alarmBean, context);
                    } else {
                        int interal = (7 - nowWeek + loop[0]) * 24 * 60 * 60 * 1000;
                        long trigger = triggerTime + interal;
                        System.out.println(TAG + "+registeAlarm the largest value of alarm time is less than current time" + simpleDateFormat.format(new Date(trigger)));
                        registeAlarm(trigger, alarmBean, context);
                    }

                }
            } else {
                if (calendar.getTimeInMillis() < calendar1.getTimeInMillis()) {
                    LogUtil.println(TAG + "registeAlarm", " single register and triggleTime is less than current time so alarm invalid");
                } else {
                    LogUtil.println(TAG + "registeAlarm", " single register and triggelTime is more than current time so alarm valid");
                    registeAlarm(calendar.getTimeInMillis(), alarmBean, context);

                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册定时闹钟
     *
     * @param triggerTime
     */
    public static void registeAlarm(long triggerTime, TimingBean alarmBean, Context mContext) {
        LogUtil.println(TAG + "registeAlarm", " alarmId = " + alarmBean.getAlarmId());
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        //   Intent intent = new Intent(ALARM_ACTION);
        Intent intent = new Intent(alarmBean.getAlarmId());
        intent.putExtra("alarm_bean", alarmBean);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    /**
     * 冒泡排序，从小到大
     *
     * @param jsonArray
     * @return
     */
    public static int[] bubbleSort(JSONArray jsonArray) {
        if (jsonArray != null && !jsonArray.equals("")) {
            int[] list = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                list[i] = jsonArray.optInt(i);
            }
            for (int i = 0; i < list.length - 1; i++) {
                for (int j = i + 1; j < list.length; j++) {
                    if (list[i] > list[j]) {
                        int temr = list[i];
                        list[i] = list[j];
                        list[j] = temr;
                    }
                }
            }
            return list;
        } else {
            LogUtil.println(TAG + "bubbleSort ", "jsonArray is null or equals null");
            return null;
        }
    }

    /**
     * 取消定时
     */
    public static void unRegisteAlarm(Context mContext, String alarmId) {
        LogUtil.println(TAG + "unRegisteAlarm ", "alarmId = " + alarmId);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        //    Intent intent = new Intent(ALARM_ACTION);
        Intent intent = new Intent(alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 注册升级广播
     *
     * @param mContext
     */
    public static void registeUpgradeAlarm(Context mContext) {
        int INTERVAL = 1000 * 60 * 60 * 24;
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        long repeatTime = calendar.getTimeInMillis();
        long currentTime = System.currentTimeMillis();
        if (repeatTime < currentTime) {
            repeatTime = repeatTime + INTERVAL;
        }
        LogUtil.println(TAG + "registeUpgradeAlarm", "repeatTime = " + repeatTime + "currentTime = " + currentTime);
        Intent intent = new Intent(Constants.ACTION_START_UPDATE);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, repeatTime, INTERVAL, sender);
    }


    public static void registeNetworkHeart(Context mContext, String action) {
        int INTERVAL = 1000 * 60 * 3; // 网络心跳周期是5分钟
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        LogUtil.println(TAG + "registeNetworkHeart", "currentTime = " + currentTime);
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime, INTERVAL, sender);
    }


    // 注册升级心跳，每隔1个小时检测一下网关版本
    public static void registeUpgradeHeart(Context mContext, String action) {
        int INTERVAL = 1000 * 60 * 60; // 升级检测心跳周期是1h
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        LogUtil.println(TAG + "registeUpgradeHeart", "registeUpgradeHeart = " + currentTime);
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime, INTERVAL, sender);
    }
}
