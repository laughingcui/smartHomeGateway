package com.everyoo.smartgateway.everyoocore.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/6/16.
 */
public class UpgradeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.println("UpgradeReceiver onReceive","upgrade perform");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent = new Intent(context, UpgradeService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }
}
