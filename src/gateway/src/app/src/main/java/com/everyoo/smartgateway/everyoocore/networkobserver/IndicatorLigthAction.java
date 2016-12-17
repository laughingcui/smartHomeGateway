package com.everyoo.smartgateway.everyoocore.networkobserver;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/5/23.
 * 网关指示灯：
 * 应用启动前 —橘灯常亮
 * 应用启动后（未绑定—橘灯常亮、绑定中—绿灯慢闪、已绑定(网络正常—绿灯常亮、网络异常—橘灯慢闪）)
 */
public class IndicatorLigthAction {
    private static final String TAG = "IndicatorLigthAction";

    public static final String GREEN_LIGHT_OFF = "0"; // 绿灯关
    public static final String GREEN_LIGHT_ON = "1"; // 绿灯亮
    public static final String GREEN_LIGHT_QUICK_5HZ = "2"; // 绿灯快闪
    public static final String GREEN_LIGHT_SLOW_1HZ = "3"; // 绿灯慢闪
    public static final String ORANGE_LIGHT_OFF = "4"; // 橘灯关
    public static final String ORANGE_LIGHT_ON = "5"; // 橘灯开
    public static final String ORANGE_LIGHT_QUICK_5HZ = "6"; // 橘灯快闪
    public static final String ORANGE_LIGHT_SLOW_1HZ = "7"; // 橘灯慢闪

    private static boolean isNetworkException = false;
    private static boolean isIndicatorEnable = true;

    public static void setIsIndicatorEnable(boolean isIndicatorEnable) {
        IndicatorLigthAction.isIndicatorEnable = isIndicatorEnable;
    }

    private static void sendIndicationBroad(Context context, String value) {
        Intent intent = new Intent("wisdom.led.setting");
        intent.putExtra("wisdom.led.data", value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.sendBroadcast(intent);
        //LogUtil.println(TAG + "ctrlIndicatorLight", " value = " + value)
        Log.i(TAG, "ctrlIndicatorLight value = " + value);
    }

    /**
     * 网关系统启动，网关应用未启动
     *
     * @param context
     */
    public static void beforeAppStartup(Context context) {
        networkException();
        sendIndicationBroad(context, GREEN_LIGHT_OFF);
        sendIndicationBroad(context, ORANGE_LIGHT_ON);
    }

    /**
     * 启动之后，等待绑定
     *
     * @param context
     */
    public static void waitingBind(Context context) {
        networkException();
        sendIndicationBroad(context, GREEN_LIGHT_OFF);
        sendIndicationBroad(context, ORANGE_LIGHT_SLOW_1HZ);
    }

    /**
     * 启动之后，正在绑定
     *
     * @param context
     */
    public static void binding(Context context) {
        //    networkException();
        sendIndicationBroad(context, GREEN_LIGHT_OFF);
        sendIndicationBroad(context, ORANGE_LIGHT_QUICK_5HZ);
    }

    /**
     * 绑定之后，运行正常（sdk，pjsip）
     *
     * @param context
     */
    public static void runNormally(Context context) {
        networkException();
        sendIndicationBroad(context, ORANGE_LIGHT_OFF);
        sendIndicationBroad(context, GREEN_LIGHT_ON);
    }

    /**
     * 绑定之后，没有物理的网络连接
     *
     * @param context
     */
    public static void noNetworkConnected(final Context context) {
        if (!isNetworkException) {
            isNetworkException = true;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            networkException(context);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("mainLooper and wifi exception is dismiss");
                    }
                }).start();
            } else {
                try {
                    networkException(context);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("subLooper and wifi exception is dismiss");
            }
        }

    }

    private static void networkException(Context context) throws InterruptedException {
        while (isNetworkException) {
            sendIndicationBroad(context, GREEN_LIGHT_ON);
            Thread.sleep(900);
            sendIndicationBroad(context, ORANGE_LIGHT_ON);
            Thread.sleep(100);
            sendIndicationBroad(context, GREEN_LIGHT_OFF);
            Thread.sleep(1000);
        }
    }

    /**
     * 绑定之后，网络虽有物理连接，但无网络数据
     *
     * @param context
     */
    public static void networkConnectedButNoData(Context context) {
        networkException();
        sendIndicationBroad(context, ORANGE_LIGHT_OFF);
        sendIndicationBroad(context, GREEN_LIGHT_SLOW_1HZ);

    }

    /**
     * 绑定之后，sdk异常
     *
     * @param context
     */
    public static void sdkException(Context context) {
        networkException();
        sendIndicationBroad(context, ORANGE_LIGHT_OFF);
        sendIndicationBroad(context, GREEN_LIGHT_QUICK_5HZ);
    }

    /**
     * 绑定之后，通信传输
     *
     * @param context
     */
    public synchronized static void cmdTransfer(Context context) {
        networkException();
        sendIndicationBroad(context, GREEN_LIGHT_QUICK_5HZ);
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendIndicationBroad(context, GREEN_LIGHT_ON);
    }


    private static void networkException() {
        if (isNetworkException) {
            isNetworkException = false;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
