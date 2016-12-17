package com.everyoo.smartgateway.everyoocore.message.filter;


import android.content.Context;
import android.content.Intent;

import com.elvishew.xlog.XLog;
import com.everyoo.smartgateway.everyoocore.bean.ModeBean;
import com.everyoo.smartgateway.everyoocore.bean.ReportBean;
import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.message.impl.ParseMessageImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageTriggerBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageEnableDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageTriggerDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.ActionId;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/17.
 */
public class ParseMessage implements ParseMessageImpl {
    private final int ADD_SUB_USER = 1;
    private final int DEL_SUB_USER = 2;
    private final int UPLOAD_FILE = 3;
    private int msgType = 0;
    private FilterMessage mFilter;

    public ParseMessage() {
        this.mFilter = new FilterMessage();
    }

    /**
     * 解析控制指令
     *
     * @param message
     * @param userId
     * @param type
     * @return
     */
    @Override
    public String parseCtrlMessage(String message, String userId, int type) {
        mFilter = new FilterMessage(FilterMessage.CTRL_DAO);
        try {
            if (message != null && !message.equals("")) {
                JSONObject jsonObject = new JSONObject(message);
                if (type == Constants.CONTROL || type == Constants.WEB_CTRL) {
                    return mFilter.controlFilter(jsonObject, userId);
                } else if (type == Constants.INCLUSION) {
                    return mFilter.clusionFilter(jsonObject, userId, ActionId.DEVICE_INCLUSION);
                } else if (type == Constants.EXCLUSION) {
                    return mFilter.clusionFilter(jsonObject, userId, ActionId.DEVICE_EXCLUSION);
                } else {
                    LogUtil.controlLog("parseMessage", "type is invalid and type = " + type);
                }
            } else {
                LogUtil.controlLog("parseMessage", "message is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析网关指令
     *
     * @param message
     * @param userId
     * @param type
     * @return
     */
    @Override
    public BindBean parseGwMessage(String message, String userId, int type) {
        if (type == Constants.UNBIND) {
            return new BindBean(userId, null, 0, null, Constants.UNBIND);
        } else if (type == Constants.WEB_SUBUSER) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                int msgType = jsonObject.optInt("type");
                if (msgType == ADD_SUB_USER || msgType == DEL_SUB_USER) {
                    return new BindBean(userId, null, 0, null, Constants.WEB_SUBUSER);
                } else if (msgType == UPLOAD_FILE) {
                    Constants.fileName = jsonObject.optJSONObject("info").optString("date");
                    XLog.i("filename=" + Constants.fileName);
                    return new BindBean(userId, null, 0, null, UPLOAD_FILE);
                } else {
                    LogUtil.println("parseMessage", "msgType is invalid and msgType = " + msgType);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type == Constants.WIFI_MODIFY) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                Constants.modifyWifiSSID = jsonObject.optString("wifi_ssid");
                Constants.modifyWifiPwd = jsonObject.optString("wifi_pwd");
                return new BindBean(userId, null, 0, null, Constants.WIFI_MODIFY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type == Constants.VERSION_UPDATE) {
            return new BindBean(userId, null, 0, null, Constants.VERSION_UPDATE);
        }
        return null;
    }

    /**
     * 解析联动指令
     *
     * @param message
     * @param userId
     * @return
     */
    @Override
    public LinkageBean parseLinkageMessage(String message, String userId) {
        mFilter = new FilterMessage(FilterMessage.LINKAGE_DAO);
        if (message != null && !message.equals("")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
                int msgType = jsonObject.optInt("type");
                if (msgType == Constants.VALUE_CREATE) {
                    return mFilter.createLinkageFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_MODIFY) {
                    return mFilter.updateLinkageFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_DELETE) {
                    return mFilter.deleteLinkageFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_START) {
                    return mFilter.enableLinkageFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_STOP) {
                    return mFilter.enableLinkageFilter(jsonObject, userId);
                } else {
                    LogUtil.linkageLog("parseMessage", "msgType is invalid and msgType = " + msgType);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            LogUtil.linkageLog("parseMessage", "message is null");
        }
        return null;
    }

    /**
     * 解析模式选择指令
     *
     * @param message
     * @param userId
     * @return
     */
    @Override
    public ModeBean parseModeSwitchMessage(String message, String userId) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (!jsonObject.equals("")) {
                int value = jsonObject.optInt("value");
                if (value == Constants.CONTROL_MODE || value == Constants.WISDOM_MODE) {
                    return new ModeBean(value, userId);
                } else {
                    LogUtil.println("parseMessage", "value is invalid and value = " + value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析上报指令
     *
     * @param nodeId
     * @param value
     * @param actionId
     * @param userId
     * @return
     */
    @Override
    public ReportBean parseReportMessage(int nodeId, String value, String actionId, String userId) {
        switch (actionId) {
            case ActionId.DONGLE_INITIALIZE:
                break;
            case ActionId.DEVICE_INCLUSION:
                return new ReportBean(value, nodeId, actionId, userId);
            case ActionId.DEVICE_EXCLUSION:
                return new ReportBean(value, nodeId, actionId, userId);
            case ActionId.USB_DONGLE_TACHED:
                break;
            case ActionId.DEVICE_INFOMATION:
                break;
            case ActionId.DEVICE_STATE_REPORT:
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * 解析场景指令
     *
     * @param message
     * @param userId
     * @return
     */
    @Override
    public SceneBean parseSceneMessage(String message, String userId) {
        if (message != null && !message.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                msgType = jsonObject.optInt("value");
                if (msgType == Constants.VALUE_CREATE) {
                    return mFilter.createSceneFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_MODIFY) {
                    return mFilter.updateSceneFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_DELETE) {
                    return mFilter.deleteSceneFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_START) {
                    return mFilter.startFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_CREATE_SCNE_PANEL) {
                    return mFilter.createScenePanelFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_UPDATE_SCENE_PANEL) {
                    return mFilter.updateScenePanelFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_DELETE_SCENE_PANEL) {
                    return mFilter.deleteScenePanelFilter(jsonObject, userId);
                } else if (msgType == Constants.VALUE_PERORM_SCENE_PANEL) {
                    return mFilter.performScenePanelFilter(jsonObject, userId);
                } else {
                    LogUtil.sceneLog("parseMessage", "msgType is invalid and msgType = " + msgType);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.sceneLog("parseMessage", "message is null");
        }
        return null;
    }

    /**
     * 解析定时指令
     *
     * @param message
     * @param userId
     * @param timerType
     * @return
     */
    @Override
    public TimingBean parseTimingMessage(String message, String userId, int timerType) {
        if (message != null && !message.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                int type = jsonObject.optInt("type");
                if (type == Constants.VALUE_CREATE) {
                    if (timerType == Constants.TIMING) {
                        return mFilter.createTimingFilter(jsonObject, userId);
                    } else {
                        return mFilter.createSceneTimeFilter(jsonObject, userId);
                    }
                } else if (type == Constants.VALUE_MODIFY) {
                    if (timerType == Constants.TIMING) {
                        return mFilter.updateTimingFilter(jsonObject, userId);
                    } else {
                        return mFilter.updateSceneTimeFilter(jsonObject, userId);
                    }
                } else if (type == Constants.VALUE_DELETE) {
                    return mFilter.deleteTimingFilter(jsonObject, userId, timerType);
                } else if (type == Constants.VALUE_START) {
                    return mFilter.startTimingFilter(jsonObject, userId);
                } else if (type == Constants.VALUE_STOP) {
                    return mFilter.closeTimingFilter(jsonObject, userId);
                } else {
                    LogUtil.timingLog("parseMessage", "type is invalid and type = " + type);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.timingLog(" parseMessage", "message is null");
        }
        return null;
    }

    @Override
    public DeviceBean deleteFilter(String message, String userId) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String deviceId = jsonObject.optString("ctrlid");
            if (!deviceId.equals("")) {
                return new DeviceBean(deviceId, userId);
            } else {
                LogUtil.println("deleteFilter", "deviceId is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void parseLinkageReceiverMessage(Intent intent, Context context) {
        String ctrlId = intent.getStringExtra("ctrl_id");
        String value = intent.getStringExtra("value");
        LogUtil.linkageLog("LinkageReceiver parseMessage", " ctrlId = " + ctrlId + "value = " + value);
        LinkageTriggerDao linkageTriggerDao = (LinkageTriggerDao) InitApplication.mSql.getSqlDao(LinkageTriggerDao.class);
        ArrayList<LinkageBean> triggerBeanArrayList = new ArrayList<>();
        ArrayList<String> linkageIdList = new ArrayList<>();
        if (!ctrlId.equals("")) {
            ArrayList<LinkageTriggerBean> tmpList = linkageTriggerDao.selectByCtrlId(ctrlId);
            ArrayList<LinkageBean> mlist = new ArrayList<>();
            for (LinkageTriggerBean bean : tmpList) {
                LinkageBean linkBean = new LinkageBean();
                linkBean.setTriggerValue(bean.getValue());
                linkBean.setTriggerCtrlId(bean.getCtrlId());
                linkBean.setTriggerDeviceId(bean.getDeviceId());
                linkBean.setFlag(bean.getFlag());
                linkBean.setIsConform(bean.getIsConform());
                linkBean.setRelationship(bean.getRelationship());
                linkBean.setLinkageId(bean.getLinkageId());
                mlist.add(linkBean);
            }
            if (mlist.size() > 0) {
                for (int i = 0; i < mlist.size(); i++) {
                    LinkageBean bean = mlist.get(i);
                    LogUtil.linkageLog("LinkageReceiver parseMessage", "trigger trlId = " + bean.getTriggerCtrlId() + "value = " + bean.getTriggerValue() + "rule = " + bean.getRelationship());
                    Helper.triggerJudge(bean, value, triggerBeanArrayList, linkageIdList);
                }
                ArrayList<LinkageTriggerBean> tList = new ArrayList<>();
                for (int i = 0; i < triggerBeanArrayList.size(); i++) {
                    LinkageTriggerBean tBean = new LinkageTriggerBean();
                    tBean.setLinkageId(triggerBeanArrayList.get(i).getLinkageId());
                    tBean.setDeviceId(triggerBeanArrayList.get(i).getTriggerDeviceId());
                    tBean.setRelationship(triggerBeanArrayList.get(i).getRelationship());
                    tBean.setIsConform(triggerBeanArrayList.get(i).getIsConform());
                    tBean.setCtrlId(triggerBeanArrayList.get(i).getTriggerCtrlId());
                    tBean.setFlag(triggerBeanArrayList.get(i).getFlag());
                    tBean.setValue(triggerBeanArrayList.get(i).getTriggerValue());
                    tList.add(tBean);
                }
                for (int i = 0; i < tList.size(); i++) {
                    linkageTriggerDao.updateByCtrlIdAndLinkageId(tList);
                }
                Helper.reChecking(linkageIdList);
                Helper.triggerJudge(linkageIdList, context, linkageTriggerDao);
            } else {
                LogUtil.linkageLog("LinkageReceiver parseMessage", " ctrlId is unExited in trigger");
            }
        } else {
            LogUtil.linkageLog("LinkageReceiver parseMessage ", "ctrlId is equals null");
        }

    }

}
