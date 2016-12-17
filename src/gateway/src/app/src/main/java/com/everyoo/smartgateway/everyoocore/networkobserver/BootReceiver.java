package com.everyoo.smartgateway.everyoocore.networkobserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.everyoo.smartgateway.utils.FileUtil;
import com.everyoo.smartgateway.utils.UpdateUtil;
import com.yyh.fork.NativeRuntime;

/**
 * Created by abc on 2016/12/13.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("BootReceiver", "onReceive action: " + intent.getAction());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("BootReceiver onReceiver", " gateway has been turn on");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        NativeRuntime.getInstance().startService(context.getPackageName() + "/com.everyoo.smartgateway.everyoocore.networkobserver.NetworkService", new FileUtil().createRootPath(context));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            System.out.println("BootReceiver.onReceive");
            UpdateUtil.execShell("reboot");
        }

    }
}
