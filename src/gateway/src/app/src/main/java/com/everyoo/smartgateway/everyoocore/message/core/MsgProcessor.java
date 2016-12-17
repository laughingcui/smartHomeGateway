package com.everyoo.smartgateway.everyoocore.message.core;

import android.content.Context;

import com.elvishew.xlog.XLog;
import com.everyoo.smartgateway.everyoocore.bean.ReportBean;
import com.everyoo.smartgateway.everyoocore.message.filter.ParseMessage;
import com.everyoo.smartgateway.everyoocore.message.impl.MsgImpl;
import com.everyoo.smartgateway.everyoocore.message.processor.CtrlProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.DevProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.GwProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.LinkageProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.ModeSwitchProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.ReportProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.SceneProcessor;
import com.everyoo.smartgateway.everyoocore.message.processor.TimingProcessor;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaos on 2016/6/16.
 * 功能描述：接收指令-指令解析及过滤-生成指令-发送指令
 */
public class MsgProcessor implements MsgImpl {

    private final String TAG = "MsgProcessor ";
    private Context mContext;

    private GwProcessor gwProcessor;
    private DevProcessor devProcessor;
    private CtrlProcessor ctrlProcessor;
    private SceneProcessor sceneProcessor;
    private TimingProcessor timingProcessor;
    private LinkageProcessor linkageProcessor;
    private ModeSwitchProcessor modeSwitchProcessor;
    private ReportProcessor reportProcessor;
    private ParseMessage parseMessage;


    public MsgProcessor(Context context) {
        mContext = context;
        parseMessage = new ParseMessage();
        gwProcessor = new GwProcessor(mContext);
        devProcessor = new DevProcessor(mContext);
        sceneProcessor = new SceneProcessor(mContext);
        modeSwitchProcessor = new ModeSwitchProcessor(mContext);
        timingProcessor = new TimingProcessor(mContext);
        ctrlProcessor = new CtrlProcessor(mContext);
        linkageProcessor = new LinkageProcessor(mContext);
        reportProcessor = new ReportProcessor(mContext);
    }

    @Override
    public void receiveUploadMsg(String message) {
        if (!StringUtils.isNullOrEmpty(message)) {
            LogUtil.controlLog(TAG + "receiveUploadMsg", message.toString());
            parseUploadMsg(message);
        } else {
            LogUtil.controlLog(TAG + "receiveUploadMsg", "message is null");
        }
    }

    @Override
    public void parseUploadMsg(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            int nodeId = jsonObject.optInt("node_id");
            String value = jsonObject.optString("value");
            String actionId = jsonObject.optString("action_id");
            String userId = jsonObject.optString("user_id");
            processUploadMsg(nodeId, value, actionId, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void processUploadMsg(int nodeId, String value, String actionId, String userId) {
        reportProcessor.processor(new ReportBean(value, nodeId, actionId, userId));

    }

    @Override
    public void sendUploadMsg(String message) {

    }

    @Override
    public void receiveDownloadMsg(String message) {
        if (message != null) {
            LogUtil.controlLog(TAG + "receiveDownloadMsg", message);
            parseDownloadMsg(message);
        } else {
            LogUtil.controlLog(TAG + "receiveDownloadMsg", "message is null");
        }
    }

    @Override
    public void parseDownloadMsg(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String mGatewayId = jsonObject.optString("gatewayid");
            String mUserId = jsonObject.optString("userid");
            String mMessage = jsonObject.optString("msg");
            int mType = jsonObject.optInt("type");
            LogUtil.controlLog(TAG + "parseDownloadMsg", "gatewayId = " + mGatewayId + " userId = " + mUserId + " mType = " + mType + " mMessage = " + mMessage);
            if (mGatewayId.equals(Constants.gatewaySn) && mType != Constants.WEB_SUBUSER) {
                processDownloadMsg(mMessage, mUserId, mType);
            } else if (mType == Constants.WEB_SUBUSER) {
                LogUtil.controlLog(TAG + "parseDownloadMsg", "message params is webSubUser");
                processDownloadMsg(mMessage, mUserId, mType);
            } else {
                LogUtil.controlLog(TAG + "parseDownloadMsg", "message params is incompletely");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processDownloadMsg(String message, String userId, int type) {
        if (type == Constants.CONTROL || type == Constants.WEB_CTRL) {
            ctrlProcessor.processor(parseMessage.parseCtrlMessage(message, userId, type), userId);
        } else if (type == Constants.SCENE) {
            sceneProcessor.processor(parseMessage.parseSceneMessage(message, userId));
        } else if (type == Constants.TIMING || type == Constants.SCENE_TIMING) {
            timingProcessor.processor(parseMessage.parseTimingMessage(message, userId, type));
        } else if (type == Constants.LINKAGE) {
            linkageProcessor.processor(parseMessage.parseLinkageMessage(message, userId));
        } else if (type == Constants.INCLUSION || type == Constants.EXCLUSION) {
            ctrlProcessor.processor(parseMessage.parseCtrlMessage(message, userId, type), userId);
        } else if (type == Constants.DELETE_DEVICE) {
            devProcessor.processor(parseMessage.deleteFilter(message, userId), Constants.DELETE_DEVICE);
        } else if (type == Constants.UNBIND) {
            gwProcessor.processor(parseMessage.parseGwMessage(null, userId, Constants.UNBIND));
        } else if (type == Constants.WEB_SUBUSER) {
            gwProcessor.processor(parseMessage.parseGwMessage(message, userId, Constants.WEB_SUBUSER));
        } else if (type == Constants.MODE_SWITCH) {
            modeSwitchProcessor.processor(parseMessage.parseModeSwitchMessage(message, userId));
        } else if (type == Constants.WIFI_MODIFY) {
            gwProcessor.processor(parseMessage.parseGwMessage(message, userId, Constants.WIFI_MODIFY));
        } else if (type == Constants.VERSION_UPDATE) {
            gwProcessor.processor(parseMessage.parseGwMessage(message, userId, Constants.VERSION_UPDATE));
        } else {
            LogUtil.controlLog(TAG + "generateDownloadMsg", "type is invalid and type = " + type);
        }

    }

    @Override
    public void sendDownloadMsg(String message) {
        BroadcastAction.gatewayToSdkMsg(message, mContext);
    }
}
