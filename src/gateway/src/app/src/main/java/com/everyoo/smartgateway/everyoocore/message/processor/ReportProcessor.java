package com.everyoo.smartgateway.everyoocore.message.processor;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.everyoo.smartgateway.everyoocore.bean.ReportBean;
import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.CtrlBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceLogBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceStatusBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.SceneDaoBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.CtrlDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DefineActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DefineAttriDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DeviceLogDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DeviceStatusDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.SceneDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.ScenePanelDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.everyoozwave.ZWAVE;
import com.everyoo.smartgateway.smartgateway.ActionId;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.BHDConverterUtils;
import com.everyoo.smartgateway.utils.IdRandomUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/23.
 */
public class ReportProcessor {
    private final String TAG = "ReportProcessor";
    private static int INITIALIZE_TIMES = 0;
    private int MAX_INITIALILZE_TIMES = 12;
    private DefineAttriDao defineAttriDao;
    private DeviceStatusDao deviceStatusDao;
    private DeviceLogDao deviceLogDao;
    private volatile DeviceLogBean deviceLogBean;
    private volatile DeviceStatusBean deviceStatusBean;
    private CtrlDao ctrlDao;
    private DefineActionDao actionDao;
    private SceneDao sceneDao;
    private ScenePanelDao panelDao;
    private static Context mContext;
    private final int UPLOAD_CLOUD = 0;
    private final int UPLOAD_CLIENT = 1;
    private final int UPLOAD_ALL = 2;
    private final int DEVICE_STATUS_UPLOAD = 1;
    private final int DEVICE_STATUS_UNUPLOAD = 0;
    private ArrayList<CtrlBean> ctrlBeans;
    private String currentTime;
    private EveryooHttp mHttp;

    public ReportProcessor(Context context) {
        defineAttriDao = (DefineAttriDao) InitApplication.mSql.getSqlDao(DefineAttriDao.class);
        ctrlDao = (CtrlDao) InitApplication.mSql.getSqlDao(CtrlDao.class);
        deviceStatusDao = (DeviceStatusDao) InitApplication.mSql.getSqlDao(DeviceStatusDao.class);
        deviceLogDao = (DeviceLogDao) InitApplication.mSql.getSqlDao(DeviceLogDao.class);
        actionDao = (DefineActionDao) InitApplication.mSql.getSqlDao(DefineActionDao.class);
        sceneDao = (SceneDao) InitApplication.mSql.getSqlDao(SceneDao.class);
        panelDao = (ScenePanelDao) InitApplication.mSql.getSqlDao(ScenePanelDao.class);
        deviceStatusBean = new DeviceStatusBean();
        mHttp = InitApplication.mHttp;
        mContext = context;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.UPLOAD_DEVICE_STATUS_SUCCESSFUL:
                    saveDeviceStatus(DEVICE_STATUS_UPLOAD);
                    break;
                case Constants.UPLOAD_DEVICE_STATUS_FAILED:
                    saveDeviceStatus(DEVICE_STATUS_UNUPLOAD);
                    break;
                case Constants.UPLOAD_DEVICE_LOG_SUCCESSFUL:
                    break;
                case Constants.UPLOAD_DEVICE_LOG_FAILED:
                    saveDeviceLog();
                    break;
                case Constants.UPLOAD_DEVICE_CTRLID_SUCCESSFUL:
                    ctrlDao.insert(ctrlBeans);
                    break;
                case Constants.UPLOAD_DEVICE_CTRLID_FAILED:
                    break;
                case Constants.UPLOAD_DEVICE_INFO_SUCCESSFUL:
                    break;
                case Constants.UPLOAD_DEVICE_INFO_FAILED:
                    break;
            }
        }
    };


    public void processor(ReportBean reportBean) {
        LogUtil.controlLog(TAG + "processor", "userId = " + reportBean.getUserId());
        if (reportBean != null) {
            switch (reportBean.getActionId()) {
                case ActionId.DONGLE_INITIALIZE://SDK异常，绿灯快闪
                    LogUtil.controlLog(TAG + "processor", "dongle initialize and value = " + reportBean.getValue());
                    break;
                case ActionId.DEVICE_INCLUSION:
                    if (!reportBean.getValue().equals(ZWAVE.INCLUSION_SUCCESS + "")) {
                        clusionProcessor(reportBean.getNodeId(), null, reportBean.getValue(), reportBean.getActionId(), reportBean.getUserId());
                    }
                    break;
                case ActionId.DEVICE_EXCLUSION:
                    clusionProcessor(reportBean.getNodeId(), null, reportBean.getValue(), reportBean.getActionId(), reportBean.getUserId());
                    break;
                case ActionId.USB_DONGLE_TACHED:
                    break;
                case ActionId.DEVICE_INFOMATION:
                    deviceInfoProcessor(BHDConverterUtils.hexStringToByteArray(reportBean.getValue()), reportBean.getUserId());
                    break;
                case ActionId.DEVICE_STATE_REPORT:
                    deviceStatusProcessor(UPLOAD_ALL, reportBean);
                    break;

                default:
                    dealMessage(UPLOAD_ALL, reportBean);
                    break;
            }
        } else {
            LogUtil.controlLog(TAG + "processor", "reportBean is null");
        }

    }


    private void deviceStatusProcessor(int type, ReportBean reportBean) {
        if (reportBean != null) {
            String deviceId = ctrlDao.selectDeviceId(reportBean.getNodeId());
            if (!TextUtils.isEmpty(deviceId)) {
                String ctrlId = ctrlDao.selectCtrlId(reportBean.getActionId(), reportBean.getNodeId());
                if (!TextUtils.isEmpty(ctrlId)) {
                    if (reportBean.getActionId().equals(ActionId.PERCENT)) {
                        LogUtil.controlLog(TAG + "deviceStatusProcessor", "actionId is percent and value = " + reportBean.getValue());
                        reportBean.setValue(multiLevelSwitchRpt(reportBean));
                    } else if (reportBean.getActionId().equals(ActionId.SCENE_PANEL)) {
                        LogUtil.controlLog(TAG + "deviceStatusProcessor", "actionId is scene panel and value = " + reportBean.getValue());
                        scenePanelProcessor(ctrlId, reportBean.getValue());
                    }

                    LogUtil.controlLog(TAG + "deviceStatusProcessor", "value = " + reportBean.getValue());
                    currentTime = TimeUtil.currentTime();
                    BroadcastAction.gatewayToSip(PjsipMsgAction.report(ctrlId, reportBean.getValue(), reportBean.getUserId(), Constants.REPORT, currentTime), reportBean.getUserId());
                    mHttp.uploadDeviceLog(mContext, logProcessor(new DeviceLogBean(reportBean.getUserId(), deviceId, ctrlId, reportBean.getValue(), currentTime)));
                    mHttp.uploadDeviceStatus(mContext, new DeviceLogBean(reportBean.getUserId(), deviceId, ctrlId, reportBean.getValue(), currentTime));
                } else {
                    LogUtil.controlLog(TAG + "deviceStatusProcessor", "ctrlId is null");
                }
            } else {
                LogUtil.controlLog(TAG + "deviceStatusProcessor", "deviceId is null");
            }
        } else {
            LogUtil.controlLog(TAG + "deviceStatusProcessor", "reportBean is null");
        }

    }


    public void clusionProcessor(int nodeId, String deviceId, String value, String actionId, String userId) {
        if (actionId.equals(ActionId.DEVICE_INCLUSION)) {
            BroadcastAction.gatewayToSip(PjsipMsgAction.reportClusionStatus(userId, Constants.INCLUSION, deviceId, value), userId);
        } else if (actionId.equals(ActionId.DEVICE_EXCLUSION)) {
            BroadcastAction.gatewayToSip(PjsipMsgAction.reportClusionStatus(userId, Constants.EXCLUSION, deviceId, value), userId);
            if (ZWAVE.EXCLUSION_SUCCESS == Integer.parseInt(value)) {
                if (nodeId > 0) {
                    deviceId = ctrlDao.selectDeviceId(nodeId);
                    if (!TextUtils.isEmpty(deviceId)) {
                        JSONObject jsonObject = PjsipMsgAction.deleteDeviceJson(deviceId, "", TimeUtil.currentTime());
                        BroadcastAction.sipToGatewayMsg(jsonObject.toString(), mContext);
                        LogUtil.controlLog(TAG + "clusionProcessor", "jsonObject is " + jsonObject.toString());
                    } else {
                        LogUtil.controlLog(TAG + "clusionProcessor", "deviceId is null");
                    }
                } else {
                    LogUtil.controlLog(TAG + "clusionProcessor", "nodeId is 0");
                }

            }
        }
    }


    public void deviceInfoProcessor(byte[] deviceInfo, String userId) {
        LogUtil.controlLog(TAG + "deviceInfoProcessor", "userId = " + userId);
        if (deviceInfo.length > 0) {
            byte[] cmd = deviceInfo;
            int nodeId = cmd[3];
            String firmwareVersion = cmd[22] + "." + cmd[23];
            /*int firmwareVersion = cmd[22] & 0xff;
            firmwareVersion = firmwareVersion << 8;
            firmwareVersion = firmwareVersion | (cmd[23] & 0xff);*/

            int manufactureId = cmd[7] & 0xff;
            manufactureId = manufactureId << 8;
            manufactureId = manufactureId | (cmd[8] & 0xff);

            int productId = cmd[9] & 0xff;
            productId = productId << 8;
            productId = productId | (cmd[10] & 0xff);

            int productType = cmd[11] & 0xff;
            productType = productType << 8;
            productType = productType | (cmd[12] & 0xff);

            LogUtil.controlLog(TAG + "deviceInfoProcessor", " manufactureId = " + manufactureId + "productId =" + productId + "productType = " + productType + "firmware = " + firmwareVersion);
            int deviceType = defineAttriDao.select(manufactureId, productId, productType);
            String deviceId = ctrlDao.selectDeviceId(nodeId);
            if (!TextUtils.isEmpty(deviceId)) {
                return;
            }
            deviceId = IdRandomUtil.generateDeviceId(nodeId);
            LogUtil.controlLog(TAG + "uploadDeviceInfo", " deviceId = " + deviceId);

            if (deviceType > 0) {
                ArrayList<DefineActionBean> defineActionBeans = actionDao.select(deviceType);
                JSONArray ctrlIds = ctrlProcessor(defineActionBeans, deviceId, nodeId);
                ctrlBeans = ctrlProcessor(ctrlIds, deviceId, nodeId, deviceType);
                if (ctrlIds != null && ctrlIds.length() > 0) {
                    // devHttp.uploadCtrlId(ctrlIds, handler);
                    //devHttp.create(new DeviceBean(deviceId, deviceType, firmwareVersion, TimeUtil.currentTime()), handler);
                    mHttp.uploadCtrlId(mContext, ctrlIds, handler);
                    mHttp.createDevice(mContext, new DeviceBean(deviceId, deviceType, firmwareVersion, TimeUtil.currentTime()), handler);
                    LogUtil.controlLog(TAG + "deviceInfoProcessor", "userId = " + userId);
                    clusionProcessor(nodeId, deviceId, ZWAVE.INCLUSION_SUCCESS + "", ActionId.DEVICE_INCLUSION, userId);
                } else {
                    LogUtil.controlLog(TAG + "uploadDeviceInfo ", "ctrlIds is null");
                    clusionProcessor(nodeId, deviceId, ZWAVE.INCLUSION_FAILURE + "", ActionId.DEVICE_INCLUSION, userId);
                }
            } else {
                LogUtil.controlLog(TAG + "uploadDeviceInfo ", "deviceType is invalid and deviceType = " + deviceType);
                clusionProcessor(nodeId, deviceId, ZWAVE.INCLUSION_FAILURE + "", ActionId.DEVICE_INCLUSION, userId);
            }

        } else {
            LogUtil.controlLog(TAG + "uploadDeviceInfo", " instruction is null");
            clusionProcessor(0, null, ZWAVE.INCLUSION_FAILURE + "", ActionId.DEVICE_INCLUSION, userId);
        }
    }


    public JSONArray ctrlProcessor(ArrayList<DefineActionBean> defineActionBeans, String deviceId, int nodeId) {
        if (defineActionBeans != null && defineActionBeans.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < defineActionBeans.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", IdRandomUtil.generateCtrlId());
                    jsonObject.put("deviceid", deviceId);
                    jsonObject.put("nodeid", nodeId);
                    jsonObject.put("actionid", defineActionBeans.get(i).getActionId());
                    jsonObject.put("value", 0);
                    jsonObject.put("inittime", TimeUtil.currentTime());
                    jsonObject.put("enable", 1);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonArray;
        } else {
            LogUtil.controlLog(TAG + "ctrlProcessor", "defineActionBeans is null");
            return null;
        }
    }


    public ArrayList<CtrlBean> ctrlProcessor(JSONArray jsonArray, String deviceId, int nodeId, int deviceType) {
        if (jsonArray != null && jsonArray.length() > 0) {
            ArrayList<CtrlBean> ctrlBeans = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                CtrlBean ctrlBean = new CtrlBean();
                ctrlBean.setDeviceId(deviceId);
                ctrlBean.setNodeId(nodeId);
                ctrlBean.setCtrlId(jsonArray.optJSONObject(i).optString("id"));
                ctrlBean.setValue(jsonArray.optJSONObject(i).optString("value"));
                ctrlBean.setActionId(jsonArray.optJSONObject(i).optString("actionid"));
                ctrlBean.setDeviceType(deviceType);
                ctrlBeans.add(ctrlBean);
            }
            return ctrlBeans;
        }

        return null;
    }

    /**
     * 指令分类处理
     *
     * @param type
     */
    private void dealMessage(int type, ReportBean reportBean) {
        if (reportBean != null) {
            String deviceId = ctrlDao.selectDeviceId(reportBean.getNodeId());
            if (deviceId != null && !deviceId.equals("")) {
                String ctrlId = ctrlDao.selectCtrlId(reportBean.getActionId(), reportBean.getNodeId());
                if (ctrlId != null && !ctrlId.equals("")) {
                    if (reportBean.getActionId().equals(ActionId.PERCENT)) {
                        LogUtil.controlLog(TAG + "dealMessage", "actionId is percent and value = " + reportBean.getValue());
                        reportBean.setValue(multiLevelSwitchRpt(reportBean));
                    } else if (reportBean.getActionId().equals(ActionId.SCENE_PANEL)) {
                        LogUtil.controlLog(TAG + "dealMessage", "actionId is scene panel and value = " + reportBean.getValue());
                        scenePanelProcessor(ctrlId, reportBean.getValue());
                    }
                    BroadcastAction.sendStatusToLinkageReceiver(ctrlId, reportBean.getValue(), reportBean.getActionId(), mContext);
                    currentTime = TimeUtil.currentTime();
                    LogUtil.controlLog(TAG + "dealMessage", "ctrlId= " + ctrlId);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.report(ctrlId, reportBean.getValue(), reportBean.getUserId(), Constants.REPORT, currentTime), reportBean.getUserId());
                    mHttp.uploadDeviceLog(mContext, logProcessor(new DeviceLogBean(reportBean.getUserId(), deviceId, ctrlId, reportBean.getValue(), currentTime)));
                    mHttp.uploadDeviceStatus(mContext, new DeviceLogBean(reportBean.getUserId(), deviceId, ctrlId, reportBean.getValue(), currentTime));
                } else {
                    LogUtil.controlLog(TAG + "dealMessage", "ctrlId is null");
                }
            } else {
                LogUtil.controlLog(TAG + "dealMessage", "deviceId is null");
            }
        } else {
            LogUtil.controlLog(TAG + "dealMessage", "reportBean is null");
        }
    }


    private JSONArray logProcessor(DeviceLogBean deviceLogBean) {
        JSONArray jsonArray;
        if (deviceLogBean != null) {
            jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", deviceLogBean.getUserId() + "");
                jsonObject.put("deviceid", deviceLogBean.getDeviceId());
                jsonObject.put("eventtime", deviceLogBean.getEventTime());
                jsonObject.put("ctrlid", deviceLogBean.getCtrlId());
                jsonObject.put("value", deviceLogBean.getValue());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        } else {
            LogUtil.controlLog(TAG + "lgoProcessor", "deviceLogBeans is null");
        }
        return null;
    }

    private void saveDeviceLog() {
        deviceLogDao.create(this.deviceLogBean);
    }

    private void saveDeviceStatus(int upload) {
        this.deviceStatusBean.setIsUpload(upload);
        deviceStatusDao.update(this.deviceStatusBean);
    }


    private void scenePanelProcessor(String ctrlId, String value) {
        LogUtil.controlLog(TAG + "scenePanelProcessor", "ctrlI= " + ctrlId + "value = " + value);
        ArrayList<SceneDaoBean> sceneBeans = sceneDao.select(panelDao.select(ctrlId, value));
        for (int i = 0; i < sceneBeans.size(); i++) {
            BroadcastAction.sipToGatewayMsg(PjsipMsgAction.ctrl(sceneBeans.get(i).getCtrlId(), sceneBeans.get(i).getValue(), null, Constants.CONTROL), mContext);
        }
    }

    public String multiLevelSwitchRpt(ReportBean reportBean) {
        String deviceValue = "";
        int value;
        if (reportBean.getValue().equals("")) {
            return "";
        } else if (reportBean.getValue().indexOf(".") < 0) {
            value = Integer.parseInt(reportBean.getValue());
        } else {
            value = Integer.parseInt(reportBean.getValue().substring(0, reportBean.getValue().indexOf(".")));
        }
        switch (ctrlDao.selectType(reportBean.getNodeId())) {  // 0是全关，100是全开
            case Constants.FLAG_DIMMER_SWITCH:   // 调光开关 0是全关，99全开
                LogUtil.controlLog(TAG + "multiLevelSwitchRpt", "value = " + value);
                if (value <= 0) {
                    deviceValue = 0 + "";
                } else if (value >= 99) {
                    deviceValue = 100 + "";
                } else {
                    deviceValue = reportBean.getValue();
                }
                break;
            case ZWAVE.FLAG_SUNSHADE_MOTOR:  // 遮阳电机 1：全开；99：全关
                LogUtil.controlLog(TAG + "multiLevelSwitchRpt", "value = " + value);
                if (value <= 1) {
                    deviceValue = 100 + "";
                } else if (value >= 99) {
                    deviceValue = 0 + "";
                } else {
                    deviceValue = 99 - value + "";
                }
                break;
            case ZWAVE.FLAG_CURTAIN_MOTOR:   // 窗帘电机 0:全开；99：全关
                LogUtil.controlLog(TAG + "multiLevelSwitchRpt", "value = " + value);
                if (value <= 1) {
                    deviceValue = 100 + "";
                } else if (value >= 99) {
                    deviceValue = 0 + "";
                } else {
                    deviceValue = 99 - value + "";
                }

                break;
            case ZWAVE.FLAG_VERTICAL_WINDOW_MOTOR:  // 垂直窗电机  1:全开；99：全关
                LogUtil.controlLog(TAG + "multiLevelSwitchRpt", "value = " + value);
                if (value <= 1) {
                    deviceValue = 0 + "";
                } else if (value >= 99) {
                    deviceValue = 100 + "";
                } else {
                    deviceValue = value + "";
                }
                break;
            default:
                LogUtil.controlLog("ParseCtrlAction deviceType", "is not found");
                break;
        }
        LogUtil.controlLog(TAG + "multiLevelSwitchRpt", "deviceValue = " + deviceValue);
        return deviceValue;

    }

}
