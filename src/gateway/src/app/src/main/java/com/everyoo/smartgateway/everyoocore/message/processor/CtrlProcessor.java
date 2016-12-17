package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;

import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/6/22.
 */
public class CtrlProcessor {

    private final String TAG = "CtrlProcessor ";
    private Context mContext;
    public CtrlProcessor(Context context){
        mContext = context;
    }
    public void processor(String message,String userId){
        if (message != null && !message.equals("")){
            LogUtil.controlLog(TAG + "processor","message is Unnull");
            BroadcastAction.gatewayToSdkMsg(message, mContext);
        }else {
            LogUtil.controlLog(TAG + "processor","message is null");
        }
    }
}
