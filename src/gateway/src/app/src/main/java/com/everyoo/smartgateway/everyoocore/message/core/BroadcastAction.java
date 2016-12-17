package com.everyoo.smartgateway.everyoocore.message.core;

import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/6/20.
 */
public class BroadcastAction {

    private static final String TAG = "BroadcastAction ";
    /**
     * 将从云端接收到的指令发送给处理模块
     * @param message
     */
    public static void sipToGatewayMsg(String message, Context context) {
        if (!message.contains("[Offline message")) {
            InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_DOWNLOAD_MESSAGE,message,null));
        } else {
            LogUtil.controlLog(TAG + " sipToGatewayMsg", " offline message");

        }
    }

    /**
     * 网关发送给Zwave sdk处理
     * @param message
     * @param context
     */
    public static void gatewayToSdkMsg(String message,Context context){
        if (message != null && !message.equals("")){
            Intent intent = new Intent(Constants.ACTION_DOWNLOAD_SDK_MESSAGE);
            intent.putExtra("message",message);
            context.sendBroadcast(intent);
        }else {
            //LogUtil.println(TAG + " sendDownloadMsg","message = "+message);
            LogUtil.controlLog(TAG + " sendDownloadMsg","message = "+message);
        }
    }

    /**
     * 消息上报给sip模块处理
     * @param message
     * @param userId
     */
    public static void gatewayToSip(String message,String userId){
        if (message != null && !message.equals("")){
            InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_UPLOAD_SIP_MESSAGE,message,userId));
        }
    }

    public static void sendStatusToLinkageReceiver(String ctrlId,String value,String actionId,Context context){
        if (ctrlId != null && !ctrlId.equals("") && value != null && !value.equals("") && actionId != null && !actionId.equals("")){
            Intent intent = new Intent(Constants.ACTION_SEND_TO_LINAKGE);
            intent.putExtra("ctrl_id",ctrlId);
            intent.putExtra("value",value);
            intent.putExtra("action_id",actionId);
            context.sendBroadcast(intent);
        }
    }
}
