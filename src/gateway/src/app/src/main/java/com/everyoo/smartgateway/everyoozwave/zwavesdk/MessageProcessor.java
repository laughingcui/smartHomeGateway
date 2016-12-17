package com.everyoo.smartgateway.everyoozwave.zwavesdk;

import android.content.Context;
import android.text.TextUtils;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaos on 2016/6/24.
 */
public class MessageProcessor {
    private static final String TAG = "MessageProcessor ";
    private static Context mContext;

    public MessageProcessor(Context context){
        mContext = context;
    }

    public synchronized static void sendToGateway(String message) {
        if (!TextUtils.isEmpty(message)){
            InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_UPLOAD_MESSAGE,message,null));
        }else {
            LogUtil.controlLog("MessageProcessor sendToGateway message is null");
        }

    }

    public synchronized static String generateMsg(int nodeId, String value,String actionId, String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("node_id", nodeId);
            jsonObject.put("value", value);
            jsonObject.put("action_id",actionId);
            jsonObject.put("user_id", userId);
            LogUtil.controlLog(TAG + "generateMsg","jsonObject = "+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
