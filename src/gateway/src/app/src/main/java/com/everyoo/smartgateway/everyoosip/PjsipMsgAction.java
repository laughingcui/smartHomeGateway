package com.everyoo.smartgateway.everyoosip;

import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaos on 2016/6/20.
 */
public class PjsipMsgAction {
    private static final String TAG = "PjsipMsgAction";

  // 场景创建、执行、删除、修改反馈
    public synchronized static String scene(SceneBean sceneBean, int msgType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", sceneBean.getUserId());
            jsonObject.put("type", Constants.SCENE);
            JSONObject msgObject = new JSONObject();
            msgObject.put("type", msgType);
            msgObject.put("sceneid", sceneBean.getSceneId());
            jsonObject.put("msg", msgObject);
            LogUtil.sceneLog(TAG + "reportSceneMessage", " params = " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    // 定时创建、删除、修改、开关反馈
    public synchronized static String timing(TimingBean timingBean, int msgType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", timingBean.getUserId());
            // jsonObject.put("type", Constants.TIMING);
            jsonObject.put("type",timingBean.getTimerType());
            JSONObject msgObject = new JSONObject();
            msgObject.put("type", msgType);
            msgObject.put("alarmid", timingBean.getAlarmId());
            jsonObject.put("msg", msgObject);
            LogUtil.timingLog(TAG + "timing", " params = " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    // 设备状态上报下发
    public synchronized static String ctrl(String ctrlId, String value, String userId,int type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            jsonObject.put("type", type);
            JSONObject msg = new JSONObject();
            msg.put("ctrlid", ctrlId);
            msg.put("value", value);
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    // 设备状态上报反馈
    public synchronized static String report(String ctrlId, String value, String userId,int type,String time) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            jsonObject.put("type", type);
            JSONObject msg = new JSONObject();
            msg.put("ctrlid", ctrlId);
            msg.put("value", value);
            msg.put("time",time);
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    // 模式切换反馈
    public synchronized static String modeSwitch(String userId, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            jsonObject.put("type", Constants.MODE_SWITCH);
            JSONObject msg = new JSONObject();
            msg.put("value", status);
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    // 解绑反馈
    public synchronized static String unbind(int value, String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", Constants.UNBIND);
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            JSONObject msg = new JSONObject();
            msg.put("ctrlid", "");
            msg.put("value", value);
            msg.put("time", TimeUtil.currentTime());
            jsonObject.put("msg", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    // 联动反馈
    public synchronized static String linkage(LinkageBean linkageBean, int msgType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", linkageBean.getUserId());
            jsonObject.put("type", Constants.LINKAGE);
            JSONObject msgObject = new JSONObject();
            msgObject.put("type", msgType);
            msgObject.put("linkageid", linkageBean.getLinkageId());
            jsonObject.put("msg", msgObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    // 设备删除反馈
    public synchronized static String device(DeviceBean deviceBean, int value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", Constants.DELETE_DEVICE);
            jsonObject.put("userid", deviceBean.getUserId());
            jsonObject.put("gatewayid", Constants.gatewaySn);
            JSONObject msg = new JSONObject();
            msg.put("ctrlid", deviceBean.getDeviceId());
            msg.put("value", value);  // 1：删除成功；0：删除失败
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


   // 加网和退网反馈
    public synchronized static String reportClusionStatus(String userId, int type, String deviceId, String value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            jsonObject.put("type", type);
            JSONObject msgObject = new JSONObject();
            msgObject.put("ctrlid", deviceId + "");
            msgObject.put("value", value);
            msgObject.put("time", TimeUtil.currentTime());
            jsonObject.put("msg", msgObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public static String modifyWifi(String userId,int type,int value){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            jsonObject.put("type", type);
            JSONObject msgObject = new JSONObject();
            msgObject.put("value", value);
            jsonObject.put("msg", msgObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public static String versionUpdate(String userId,int type,int value){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayid", Constants.gatewaySn);
            jsonObject.put("userid", userId);
            jsonObject.put("type", type);
            JSONObject msgObject = new JSONObject();
            msgObject.put("value", value);
            jsonObject.put("msg", msgObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }


    //解绑设备
    public static JSONObject deleteDeviceJson(String deviceId, String value, String time){
        JSONObject object = new JSONObject();
        try {
            object.put("type", Constants.DELETE_DEVICE);
            object.put("gatewayid", Constants.gatewaySn);
            object.put("userid", Constants.userId);
            JSONObject msg = new JSONObject();
            msg.put("ctrlid", deviceId);
            msg.put("value", value);
            msg.put("time", time);
            object.put("msg", msg);
            LogUtil.println("will", "delete device json:" + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
