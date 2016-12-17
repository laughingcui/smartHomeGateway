package com.everyoo.smartgateway.smartgateway;

import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.everyoocore.message.core.BindService;
import com.everyoo.smartgateway.everyoocore.message.core.ThreadPoolService;
import com.everyoo.smartgateway.everyoocore.message.processor.HttpService;
import com.everyoo.smartgateway.everyoocore.networkobserver.NetworkService;
import com.everyoo.smartgateway.everyoocore.timer.TimingService;
import com.everyoo.smartgateway.everyoosip.PjsipService;
import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/5/23.
 */
public class ServiceAction {
    private static final String TAG = "ServiceAction ";

    public static void startServices(Context context,boolean isBind) {
        if (isBind) {
            Intent intent = new Intent(context, ThreadPoolService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);

            intent = new Intent(context, HttpService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);

            intent = new Intent(context, TimingService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);

            intent = new Intent(context, NetworkService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);

        } else {
            LogUtil.println(TAG + "startServices", "gateway is unbound");
            Intent intent = new Intent(context, BindService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);
        }
    }

    public static void startPjService(Context context){
        Intent intent = new Intent(context, PjsipService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    public static void stopPjService(Context context){
        Intent intent = new Intent(context,PjsipService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.stopService(intent);
    }

}
