package com.everyoo.smartgateway.everyoocore.message.filter;

import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoocore.message.impl.FilterMessageImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.CtrlBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.CtrlDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DeviceStatusDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageTriggerDao;
import com.everyoo.smartgateway.smartgateway.ActionId;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Administrator on 2016/11/17.
 */
public class FilterMessage implements FilterMessageImpl {
    private LinkageTriggerDao linkageTriggerDao;
    private LinkageActionDao linkageActionDao;
    private DeviceStatusDao devStatusDao;
    private String ctrlId = null;
    private String value = null;
    private CtrlDao ctrlDao;
    private CtrlBean ctrlBean;
    private int enable = 1;
    private String sceneId = "";
    public static final String LINKAGE_DAO = "linkage";
    public static final String CTRL_DAO = "ctrl";

    protected FilterMessage() {
    }

    protected FilterMessage(String CreateDaoType) {
        switch (CreateDaoType) {
            case LINKAGE_DAO:
                linkageTriggerDao = (LinkageTriggerDao) InitApplication.mSql.getSqlDao(LinkageTriggerDao.class);
                linkageActionDao = (LinkageActionDao) InitApplication.mSql.getSqlDao(LinkageActionDao.class);
                devStatusDao = (DeviceStatusDao) InitApplication.mSql.getSqlDao(DeviceStatusDao.class);
                break;
            case CTRL_DAO:
                ctrlDao = (CtrlDao) InitApplication.mSql.getSqlDao(CtrlDao.class);
                ctrlBean = new CtrlBean();
                break;
        }
    }

    /**
     * 控制指令过滤器
     *
     * @param jsonObject
     * @param userId
     * @return
     */
    @Override
    public String controlFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            ctrlId = jsonObject.optString("ctrlid");
            value = jsonObject.optString("value");
            if (ctrlId != null && !ctrlId.equals("") && value != null && !value.equals("")) {
                ctrlBean = ctrlDao.selectActionId(ctrlId);
                if (ctrlBean != null && ctrlBean.getActionId() != null && !ctrlBean.getActionId().equals("") && ctrlBean.getNodeId() != 0) {
                    if (ctrlBean.getActionId().equals(ActionId.PERCENT)) {
                        ctrlBean.setValue(multiLevelSwitchSet(ctrlId, value));
                    } else {
                        ctrlBean.setValue(value);
                    }
                    return Helper.generateMsg(ctrlBean, userId);
                } else {
                    LogUtil.controlLog("parseMessage", "ctrlBean is incompletely");
                }
            } else {
                LogUtil.controlLog("parseMessage", "ctrl params is incompletely");
            }
        } else {
            LogUtil.controlLog("control", "jsonObject is null");
        }
        return null;
    }

    /**
     * 加网和退网事件过滤
     *
     * @param jsonObject
     * @param userId
     * @param actionId
     * @return
     */
    @Override
    public String clusionFilter(JSONObject jsonObject, String userId, String actionId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String value = jsonObject.optString("value");
            return Helper.generateMsg(new CtrlBean(0, null, actionId, value, null), userId);
        } else {
            LogUtil.controlLog("clusionFilter", "jsonObject is null");
        }
        return null;
    }

    /**
     * 根据ctrlId查询devicetype，根据devicetype确定value含义
     *
     * @param ctrlId
     * @param devValue
     * @return
     */
    @Override
    public String multiLevelSwitchSet(String ctrlId, String devValue) {
        int value = Integer.parseInt(devValue);
        LogUtil.println("multiLevelSwitchSet", "ctrlId = " + ctrlId + " value = " + devValue);
        switch (ctrlDao.selectDevTypeByCtrlId(ctrlId)) {
            case Constants.FLAG_DIMMER_SWITCH:   // 调光开关  -1全关，99全开
                LogUtil.println("ParseCtrlAction ", "调光开关 value = " + value);
                if (value <= 0) {
                    value = -1;
                } else if (value >= 99) {
                    value = 99;
                }
                break;
            case Constants.FLAG_SUNSHADE_MOTOR:  // 遮阳电机  1：全开；99：全关
                LogUtil.println("ParseCtrlAction", " 遮阳电机 value = " + value);
                if (value == 1 || value == 0) {
                    value = 99;
                } else if (value >= 99) {
                    value = 1;
                } else if (value == -1) {
                    value = -2;
                } else {
                    value = 99 - value;
                }
                break;
            case Constants.FLAG_CURTAIN_MOTOR:   // 窗帘电机  1:全开；99：全关
                LogUtil.println("ParseCtrlAction ", "窗帘电机 value = " + value);
                if (value == 1 || value == 0) {
                    value = 99;
                } else if (value >= 99) {
                    value = 1;
                } else if (value == -1) {
                    value = -2;
                } else {
                    value = 99 - value;
                }
                break;
            case Constants.FLAG_VERTICAL_WINDOW_MOTOR:  // 垂直窗电机  1:全开；100：全关
                LogUtil.println("ParseCtrlAction ", "垂直窗电机 value = " + value);
                if (value == 1 || value == 0) {
                    value = 1;
                } else if (value > 99) {
                    value = 100;
                } else if (value == -1) {
                    value = -2;
                }
                break;
            case Constants.FLAG_THREE_CONTROL:// 三路开关停
                LogUtil.println("ParseCtrlAction", "三路控制器 value = " + value);
                if (value == -1) {
                    value = -2;
                }
                break;
            default:
                LogUtil.println("multiLevelSwitchSet", "deviceType is invalid");
                break;
        }
        return value + "";
    }

    @Override
    public LinkageBean createLinkageFilter(JSONObject jsonObject, String userId) {
        String linkageId = UUID.randomUUID().toString();
        String linkageName = jsonObject.optString("linkagename");
        JSONArray triggerArray = jsonObject.optJSONArray("trigger");
        JSONArray actionArray = jsonObject.optJSONArray("position");
        ArrayList<LinkageBean> triggerLinkageBeans = new ArrayList<>();
        ArrayList<LinkageBean> actionLinkageBeans = new ArrayList<>();
        if (!linkageName.equals("") && triggerArray != null && !triggerArray.equals("") && actionArray != null && !actionArray.equals("")) {
            for (int i = 0; i < triggerArray.length(); i++) {
                String deviceId = triggerArray.optJSONObject(i).optString("deviceid");
                String ctrlId = triggerArray.optJSONObject(i).optString("ctrlid");
                JSONArray value = triggerArray.optJSONObject(i).optJSONArray("valueArray");
                LogUtil.linkageLog("createFilter", "deviceId = " + deviceId + "ctrlId = " + ctrlId);
                if (deviceId.equals("") || ctrlId.equals("") || value == null || value.length() != 2) {
                    LogUtil.linkageLog("createFilter", "params is incompletely");
                    return null;
                } else {
                    triggerLinkageBeans.add(new LinkageBean(linkageId, deviceId, ctrlId, value.toString(), 0, Helper.getConform(ctrlId, value, devStatusDao), Constants.FLAG_LINKAGE_USER_CREATE));
                }
            }
            for (int i = 0; i < actionArray.length(); i++) {
                String deviceId = actionArray.optJSONObject(i).optString("deviceid");
                String ctrlId = actionArray.optJSONObject(i).optString("ctrlid");
                String value = actionArray.optJSONObject(i).optString("value");

                if (deviceId.equals("") || ctrlId.equals("") || value.equals("") || value.equals("")) {
                    LogUtil.linkageLog("createFilter", "params is incompletely and actionArray = " + actionArray);
                    return null;
                } else {
                    actionLinkageBeans.add(new LinkageBean(linkageId, deviceId, ctrlId, value, Constants.VALUE_CREATE));
                }

            }
            if (!Helper.isExistedOthers(triggerLinkageBeans, actionLinkageBeans, linkageId, linkageTriggerDao, linkageActionDao)) {
                return new LinkageBean(linkageId, linkageName, triggerArray.toString(), actionArray.toString(), triggerArray.optJSONObject(0).optString("ctrlid"), triggerArray.optJSONObject(triggerArray.length() - 1).optString("ctrlid"), triggerArray.length(), Constants.ENABLE, userId, TimeUtil.currentTime(), triggerLinkageBeans, actionLinkageBeans, Constants.VALUE_CREATE);
            } else {
                return null;
            }
        } else {
            LogUtil.linkageLog("createFilter", "params is incompletely!");
        }
        return null;
    }

    @Override
    public LinkageBean updateLinkageFilter(JSONObject jsonObject, String userId) {
        String linkageId = jsonObject.optString("linkageid");
        String linkageName = jsonObject.optString("linkagename");
        JSONArray triggerArray = jsonObject.optJSONArray("trigger");
        JSONArray actionArray = jsonObject.optJSONArray("position");
        ArrayList<LinkageBean> triggerLinkageBeans = new ArrayList<>();
        ArrayList<LinkageBean> actionLinkageBeans = new ArrayList<>();
        if (!linkageId.equals("") && !linkageName.equals("") && triggerArray != null && !triggerArray.equals("") && actionArray != null && !actionArray.equals("")) {
            for (int i = 0; i < triggerArray.length(); i++) {
                String deviceId = triggerArray.optJSONObject(i).optString("deviceid");
                String ctrlId = triggerArray.optJSONObject(i).optString("ctrlid");
                JSONArray value = triggerArray.optJSONObject(i).optJSONArray("valueArray");

                if (deviceId.equals("") || ctrlId.equals("") || value == null && value.length() != 2) {
                    LogUtil.linkageLog("createFilter", "params is incompletely");
                    return null;
                } else {
                    triggerLinkageBeans.add(new LinkageBean(linkageId, deviceId, ctrlId, value.toString(), 0, Helper.getConform(ctrlId, value, devStatusDao), Constants.FLAG_LINKAGE_USER_CREATE));
                }
            }
            for (int i = 0; i < actionArray.length(); i++) {
                String deviceId = actionArray.optJSONObject(i).optString("deviceid");
                String ctrlId = actionArray.optJSONObject(i).optString("ctrlid");
                String value = actionArray.optJSONObject(i).optString("value");
                if (deviceId.equals("") || ctrlId.equals("") || value.equals("")) {
                    LogUtil.linkageLog("createFilter", "params is incompletely and actionArray = " + actionArray);
                    return null;
                } else {
                    actionLinkageBeans.add(new LinkageBean(linkageId, deviceId, ctrlId, value, Constants.VALUE_MODIFY));
                }
            }
            if (!Helper.isExistedSelf(triggerLinkageBeans, actionLinkageBeans)) {  // 校验联动自身条件和动作是否重复
                if (!Helper.isExistedOthers(triggerLinkageBeans, actionLinkageBeans, linkageId, linkageTriggerDao, linkageActionDao)) {
                    return new LinkageBean(linkageId, linkageName, triggerArray.toString(), actionArray.toString(), triggerArray.optJSONObject(0).optString("ctrlid"), triggerArray.optJSONObject(triggerArray.length() - 1).optString("ctrlid"), triggerArray.length(), Constants.ENABLE, userId, TimeUtil.currentTime(), triggerLinkageBeans, actionLinkageBeans, Constants.VALUE_MODIFY);
                } else {
                    return null;
                }
            } else {
                LogUtil.linkageLog("updateFilter", "linkage trigger and action is repeat");
                return null;
            }


        } else {
            LogUtil.linkageLog("createFilter", "params is incompletely!");
        }
        return null;
    }

    @Override
    public LinkageBean deleteLinkageFilter(JSONObject jsonObject, String userId) {
        String linkageId = jsonObject.optString("linkageid");
        if (!linkageId.equals("")) {
            return new LinkageBean(linkageId, userId, 0, Constants.VALUE_DELETE, TimeUtil.currentTime());
        } else {
            LogUtil.linkageLog("deleteFilter", "linkageId is null");
            return null;
        }
    }

    @Override
    public LinkageBean enableLinkageFilter(JSONObject jsonObject, String userId) {
        String linkageId = jsonObject.optString("linkageid");
        int msgType = jsonObject.optInt("type");
        if (!linkageId.equals("")) {
            if (msgType == Constants.VALUE_START) {
                return new LinkageBean(linkageId, userId, msgType, Constants.VALUE_START, TimeUtil.currentTime());
            } else if (msgType == Constants.VALUE_STOP) {
                return new LinkageBean(linkageId, userId, msgType, Constants.VALUE_STOP, TimeUtil.currentTime());
            } else {
                LogUtil.linkageLog("enableFilter", "msgType is invalid and msgType = " + msgType);
            }
        } else {
            LogUtil.linkageLog("enableFilter", " linkageId is null or msgType is invalid and msgType = " + msgType);

        }
        return null;
    }

    @Override
    public SceneBean createSceneFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneName = jsonObject.optString("scenename");
            JSONArray jsonArray = jsonObject.optJSONArray("position");
            if (!sceneName.equals("") && !jsonArray.equals("") && jsonArray.length() > 0) {
                String sceneId = UUID.randomUUID().toString();
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.optJSONObject(i).optString("deviceid").equals("") || jsonArray.optJSONObject(i).optString("ctrlid").equals("") || jsonArray.optJSONObject(i).optString("value").equals("")) {
                        LogUtil.sceneLog("createFilter", "position is not standard");
                        return null;
                    }

                }
                return new SceneBean(sceneId, sceneName, Helper.positionGenerate(jsonArray), jsonArray.optJSONObject(0).optString("ctrlid"), jsonArray.optJSONObject(jsonArray.length() - 1).optString("ctrlid"), jsonArray.length(), enable, TimeUtil.currentTime(), userId, Constants.VALUE_CREATE);
            } else {
                LogUtil.sceneLog("createFilter", "params is incompletely");
            }

        } else {
            LogUtil.sceneLog("createFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean updateSceneFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneId = jsonObject.optString("sceneid");
            String sceneName = jsonObject.optString("scenename");
            JSONArray jsonArray = jsonObject.optJSONArray("position");
            if (!sceneId.equals("") && !sceneName.equals("") && !jsonArray.equals("") && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.optJSONObject(i).optString("deviceid").equals("") || jsonArray.optJSONObject(i).optString("ctrlid").equals("") || jsonArray.optJSONObject(i).optString("value").equals("")) {
                        LogUtil.sceneLog("createFilter", "position is not standard");
                        return null;
                    }

                }
                return new SceneBean(sceneId, sceneName, Helper.positionGenerate(jsonArray), jsonArray.optJSONObject(0).optString("ctrlid"), jsonArray.optJSONObject(jsonArray.length() - 1).optString("ctrlid"), jsonArray.length(), enable, TimeUtil.currentTime(), userId, Constants.VALUE_MODIFY);
            } else {
                LogUtil.sceneLog("updateFilger", "params is incompletely");
            }
        } else {
            LogUtil.sceneLog("updateFilger", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean deleteSceneFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneId = jsonObject.optString("sceneid");
            if (!sceneId.equals("")) {
                return new SceneBean(sceneId, null, null, null, null, 0, enable, TimeUtil.currentTime(), userId, Constants.VALUE_DELETE);
            } else {
                LogUtil.sceneLog("startFilter", "sceneId is null");
            }
        } else {
            LogUtil.sceneLog("deleteFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean startFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneId = jsonObject.optString("sceneid");
            LogUtil.sceneLog("startFilter", "sceneId = " + sceneId);
            if (!sceneId.equals("")) {
                return new SceneBean(sceneId, null, null, null, null, 0, enable, TimeUtil.currentTime(), userId, Constants.VALUE_START);
            } else {
                LogUtil.sceneLog("startFilter", "sceneId is null");
            }
        } else {
            LogUtil.sceneLog("deleteFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean createScenePanelFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneId = jsonObject.optString("sceneid");
            String value = jsonObject.optString("mapid");
            String ctrlId = jsonObject.optString("ctrlid");
            if (!sceneId.equals("") && !value.equals("") && !ctrlId.equals("")) {
                return new SceneBean(ctrlId, sceneId, value, userId, TimeUtil.currentTime(), Constants.VALUE_CREATE_SCNE_PANEL);
            } else {
                LogUtil.sceneLog("createScenePanelFilter", "sceneId or mapId or deviceId is null");
            }
        } else {
            LogUtil.sceneLog("createScenePanelFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean updateScenePanelFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneId = jsonObject.optString("sceneid");
            String mapId = jsonObject.optString("mapid");
            String ctrlId = jsonObject.optString("ctrlid");
            if (!sceneId.equals("") && !mapId.equals("") && !ctrlId.equals("")) {
                return new SceneBean(ctrlId, sceneId, mapId, userId, TimeUtil.currentTime(), Constants.VALUE_UPDATE_SCENE_PANEL);
            } else {
                LogUtil.sceneLog("createScenePanelFilter", "sceneId or mapId or deviceId is null");
            }
        } else {
            LogUtil.sceneLog("createScenePanelFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean performScenePanelFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String sceneId = jsonObject.optString("sceneid");
            String mapId = jsonObject.optString("mapid");
            String ctrlId = jsonObject.optString("ctrlid");
            if (!mapId.equals("") && !ctrlId.equals("")) {
                return new SceneBean(ctrlId, sceneId, mapId, userId, TimeUtil.currentTime(), Constants.VALUE_PERORM_SCENE_PANEL);
            } else {
                LogUtil.sceneLog("createScenePanelFilter", "sceneId or mapId or deviceId is null");
            }
        } else {
            LogUtil.sceneLog("createScenePanelFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean deleteScenePanelFilter(JSONObject jsonObject, String userId) {
        if (jsonObject != null && !jsonObject.equals("")) {
            String mapId = jsonObject.optString("mapid");
            String ctrlId = jsonObject.optString("ctrlid");
            if (!mapId.equals("") && !ctrlId.equals("")) {
                return new SceneBean(ctrlId, sceneId, mapId, userId, TimeUtil.currentTime(), Constants.VALUE_DELETE_SCENE_PANEL);
            } else {
                LogUtil.sceneLog("createScenePanelFilter", "sceneId or mapId or deviceId is null");
            }
        } else {
            LogUtil.sceneLog("createScenePanelFilter", "jsonObject is null");
        }
        return null;
    }

    @Override
    public SceneBean controlAllDev(int value, String userId) {
        return new SceneBean(null, null, null, null, null, 0, enable, TimeUtil.currentTime(), userId, value);
    }

    @Override
    public TimingBean createTimingFilter(JSONObject jsonObject, String userId) {
        TimingBean timingBean = new TimingBean();
        timingBean.setUserId(userId);
        timingBean.setDeviceId(jsonObject.optString("deviceid"));
        timingBean.setAlarmId(UUID.randomUUID().toString());
        timingBean.setCtrlId(jsonObject.optString("ctrlid"));
        timingBean.setValue(jsonObject.optString("value"));
        timingBean.setAlarmTime(jsonObject.optString("alarmtime"));
        timingBean.setLoop(jsonObject.optJSONArray("loop").toString());
        timingBean.setType(Constants.VALUE_CREATE);
        timingBean.setTimerType(Constants.TIMING);
        if (timingBean.getDeviceId().equals("") || timingBean.getAlarmId().equals("") || timingBean.getCtrlId().equals("") || timingBean.getValue().equals("") || timingBean.getAlarmTime().equals("") || timingBean.getLoop().equals("")) {
            LogUtil.timingLog("createFilter", "timingBean is incompletely");
            return null;
        }
        return timingBean;
    }

    @Override
    public TimingBean createSceneTimeFilter(JSONObject jsonObject, String userId) {
        LogUtil.println("createSceneFilter", "jsonObject= " + jsonObject);
        TimingBean timingBean = new TimingBean();
        timingBean.setUserId(userId);
        timingBean.setAlarmId(UUID.randomUUID().toString());
        // timingBean.setSceneId(jsonObject.optString("robotid"));
        timingBean.setCtrlId(jsonObject.optString("robotid"));//此时的CtrlId代表SceneId
        timingBean.setAlarmTime(jsonObject.optString("alarmtime"));
        timingBean.setLoop(jsonObject.optJSONArray("loop").toString());
        timingBean.setType(Constants.VALUE_CREATE);
        timingBean.setTimerType(Constants.SCENE_TIMING);
        if (timingBean.getAlarmId().equals("") || timingBean.getCtrlId().equals("") || timingBean.getAlarmTime().equals("") || timingBean.getLoop().equals("")) {
            LogUtil.timingLog("createSceneFilter", "timingBean is incompletely");
            return null;
        }
        return timingBean;


    }

    @Override
    public TimingBean updateTimingFilter(JSONObject jsonObject, String userId) {
        TimingBean timingBean = new TimingBean();
        timingBean.setUserId(userId);
        timingBean.setDeviceId(jsonObject.optString("deviceid"));
        timingBean.setAlarmId(jsonObject.optString("timingid"));
        timingBean.setCtrlId(jsonObject.optString("ctrlid"));
        timingBean.setValue(jsonObject.optString("value"));
        timingBean.setAlarmTime(jsonObject.optString("alarmtime"));
        timingBean.setLoop(jsonObject.optJSONArray("loop").toString());
        timingBean.setType(Constants.VALUE_MODIFY);
        timingBean.setTimerType(Constants.TIMING);
        if (timingBean.getDeviceId().equals("") || timingBean.getAlarmId().equals("") || timingBean.getCtrlId().equals("") || timingBean.getValue().equals("") || timingBean.getAlarmTime().equals("") || timingBean.getLoop().equals("")) {
            LogUtil.timingLog("updateFilter", "timingBean is incompletely");
            return null;
        }
        return timingBean;
    }

    @Override
    public TimingBean updateSceneTimeFilter(JSONObject jsonObject, String userId) {
        LogUtil.println("updateSceneFilter", "jsonObject= " + jsonObject);
        TimingBean timingBean = new TimingBean();
        timingBean.setUserId(userId);
        timingBean.setAlarmId(jsonObject.optString("timingid"));
        //timingBean.setSceneId(jsonObject.optString("robotid"));
        timingBean.setCtrlId(jsonObject.optString("robotid"));//此时的CtrlId代表SceneId
        timingBean.setAlarmTime(jsonObject.optString("alarmtime"));
        timingBean.setLoop(jsonObject.optJSONArray("loop").toString());
        timingBean.setType(Constants.VALUE_MODIFY);
        timingBean.setTimerType(Constants.SCENE_TIMING);
        if (timingBean.getAlarmId().equals("") || timingBean.getCtrlId().equals("") || timingBean.getAlarmTime().equals("") || timingBean.getLoop().equals("")) {
            LogUtil.timingLog("updateSceneFilter", "timingBean is incompletely");
            return null;
        }
        return timingBean;
    }

    @Override
    public TimingBean deleteTimingFilter(JSONObject jsonObject, String userId, int timerType) {
        LogUtil.timingLog("deleteFilter", "jsonObject= " + jsonObject);
        TimingBean timingBean = new TimingBean();
        timingBean.setAlarmId(jsonObject.optString("timingid"));
        if (!timingBean.getAlarmId().equals("")) {
            timingBean.setUserId(userId);
            timingBean.setType(Constants.VALUE_DELETE);
            timingBean.setTimerType(timerType);
            return timingBean;
        } else {
            LogUtil.timingLog("deleteFilter", "timingBean.getAlarmId() is null");
            return null;
        }
    }

    @Override
    public TimingBean startTimingFilter(JSONObject jsonObject, String userId) {
        TimingBean timingBean = new TimingBean();
        timingBean.setAlarmId(jsonObject.optString("timingid"));
        if (!timingBean.getAlarmId().equals("")) {
            timingBean.setUserId(userId);
            timingBean.setType(Constants.VALUE_START);
            timingBean.setEnable(Constants.ENABLE);
            return timingBean;
        } else {
            LogUtil.timingLog("startFilter", "timingBean.getAlarmId() is null");
            return null;
        }
    }

    @Override
    public TimingBean closeTimingFilter(JSONObject jsonObject, String userId) {
        TimingBean timingBean = new TimingBean();
        timingBean.setAlarmId(jsonObject.optString("timingid"));
        if (!timingBean.getAlarmId().equals("")) {
            timingBean.setUserId(userId);
            timingBean.setType(Constants.VALUE_STOP);
            timingBean.setEnable(Constants.UNENABLE);
            return timingBean;
        } else {
            LogUtil.timingLog("startFilter", "timingBean.getAlarmId() is null");
            return null;
        }
    }

}
