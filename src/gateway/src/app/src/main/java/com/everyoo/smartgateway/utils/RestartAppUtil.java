package com.everyoo.smartgateway.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.everyoocore.message.core.ThreadPoolService;
import com.everyoo.smartgateway.everyoocore.message.processor.HttpService;
import com.everyoo.smartgateway.everyoocore.timer.TimingService;
import com.everyoo.smartgateway.everyoocore.upgrade.UpgradeService;
import com.everyoo.smartgateway.everyoosip.PjsipService;
import com.everyoo.smartgateway.everyoozwave.zwavesdk.MessageManager;

/**
 * Created by chaos on 2016/3/11.
 * 重启应用
 */
public class RestartAppUtil {

    private static final long TRIGGERATMILLS = 5 * 1000;

    // 定时重启应用
    public static void restartApp(Context mContext, long delay) {
        stopSdk();
        stopService(mContext);
        if (delay == 0) {
            delay = TRIGGERATMILLS;
        }
        // System.out.println("perform restart app");
        LogUtil.println("RestartAppUtil", "perform restart app");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);

        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出;返回值为1表示异常退出
    }


    private static void stopSdk() {
        if (MessageManager.controller != null) {
            MessageManager.controller.closeDev(true);
        }
    }


    private static void stopService(Context context) {
        Intent intent = new Intent(context, HttpService.class);
        context.stopService(intent);

        intent = new Intent(context, PjsipService.class);
        context.stopService(intent);

        intent = new Intent(context, ThreadPoolService.class);
        context.stopService(intent);

        intent = new Intent(context, TimingService.class);
        context.stopService(intent);

        intent = new Intent(context, UpgradeService.class);
        context.stopService(intent);
    }

}

