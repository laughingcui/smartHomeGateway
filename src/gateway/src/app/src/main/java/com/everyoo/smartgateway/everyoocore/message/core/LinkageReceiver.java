package com.everyoo.smartgateway.everyoocore.message.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.everyoocore.message.filter.ParseMessage;

/**
 * Created by chaos on 2016/6/16.
 */
public class LinkageReceiver extends BroadcastReceiver {

    private ParseMessage parseMessage=null;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        parseMessage=new ParseMessage();
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseMessage.parseLinkageReceiverMessage(intent, context);
            }
        }).start();
    }

}
