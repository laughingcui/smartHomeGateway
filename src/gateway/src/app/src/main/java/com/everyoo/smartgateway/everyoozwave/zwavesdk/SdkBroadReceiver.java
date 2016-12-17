package com.everyoo.smartgateway.everyoozwave.zwavesdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaos on 2016/6/23.
 */
public class SdkBroadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        LogUtil.controlLog("SdkBroadReceiver onReceiver","message = " + message);
        if (message != null && !message.equals("")){
            try {
                JSONObject jsonObject = new JSONObject(message);
                final String actionId = jsonObject.optString("action_id");
                final int nodeId = jsonObject.optInt("node_id");
                final int value = jsonObject.optInt("value");
                final String userId = jsonObject.optString("user_id");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MessageManager.dealMessageToSdk(nodeId,value,actionId,userId);
                    }
                }).start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            LogUtil.controlLog("SdkBroadReceiver onReceiver","message is null");
        }
    }



}
