package com.everyoo.smartgateway.everyoocore.message.filter;

import android.content.Context;

import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.CtrlBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageTriggerBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DeviceStatusDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageEnableDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageTriggerDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
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
public class Helper {
    protected static void triggerJudge(LinkageBean bean, String deviceValue, ArrayList<LinkageBean> triggerBeanArrayList, ArrayList<String> linkageIdList) {
        LogUtil.linkageLog("LinkageReceiver triggerJudge", " rule = " + bean.getRelationship());
        String value = bean.getTriggerValue();
        double maxValue = 0;
        double minValue = 0;
        double devValue = Double.parseDouble(deviceValue);
        try {
            JSONArray jsonArray = new JSONArray(value);
            if (jsonArray != null && jsonArray.length() == 2) {
                minValue = jsonArray.optDouble(0);
                maxValue = jsonArray.optDouble(1);
                LogUtil.linkageLog(" LinkageReceiver triggerJudge ", "jsonArray = " + jsonArray.toString() + " minValue = " + minValue + " maxValue = " + maxValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (devValue <= maxValue && devValue >= minValue){
            bean.setIsConform(1);
            triggerBeanArrayList.add(bean);
            linkageIdList.add(bean.getLinkageId());
        }else {
            bean.setIsConform(0);
            triggerBeanArrayList.add(bean);
        }

    }
    protected static void triggerJudge(ArrayList<String> linkageIdList, Context context, LinkageTriggerDao linkageTriggerDao) {

        for (int i = 0; i < linkageIdList.size(); i++) {
            LogUtil.linkageLog("triggerJudge", " linkageId = " + linkageIdList.get(i));
            int enable = ((LinkageEnableDao) InitApplication.mSql.getSqlDao(LinkageEnableDao.class)).select(linkageIdList.get(i));
            LogUtil.linkageLog("triggerJudge", " enable = " + enable);
            if (enable == 0) {
                continue;
            }
            LogUtil.linkageLog("LinkageReceiver triggerJudge", " list.size = " + linkageIdList.size() + "linkageId = " + linkageIdList.get(i));
            ArrayList<LinkageTriggerBean> mlist = linkageTriggerDao.select(Constants.LINKAGE_LINKAGEID, linkageIdList.get(i));
            int conform = 0;
            if (mlist.size() > 0) {
                for (int j = 0; j < mlist.size(); j++) {
                    LogUtil.linkageLog("LinkageReceiver triggerJudge", " conform = " + mlist.get(j).getIsConform());
                    conform += mlist.get(j).getIsConform();
                }
                LogUtil.linkageLog("LinkageReceiver triggerJudge ", "conform = " + conform + "mlist.size = " + mlist.size());
                if (conform == mlist.size()) {
                    for (int j = 0; j < mlist.size(); j++) {
                        ArrayList<LinkageActionBean> ctrlBeans = ((LinkageActionDao)InitApplication.mSql.getSqlDao(LinkageActionDao.class)).select(Constants.LINKAGE_LINKAGEID, linkageIdList.get(i));
                        for (int x = 0; x < ctrlBeans.size(); x++) {
                            LogUtil.linkageLog("LinkageReceiver triggerJudge", " trigger match");
                            BroadcastAction.sipToGatewayMsg(PjsipMsgAction.ctrl(ctrlBeans.get(x).getCtrlId(), ctrlBeans.get(x).getValue(),null, Constants.CONTROL), context);
                        }
                    }
                } else {
                    LogUtil.linkageLog("LinkageReceiver triggerJudge ", "trigger mismatch");
                }
            } else {
                LogUtil.linkageLog("LinkageReceiver triggerJudge", " trigger mismatch");
            }
        }


    }
    protected static void reChecking(ArrayList<String> mlist) {
        LogUtil.linkageLog("LinkageReceiver reChecking", " begin mlist.size = " + mlist.size());
        for (int i = 0; i < mlist.size() - 1; i++) {
            for (int j = i + 1; j < mlist.size(); j++) {
                if (mlist.get(i).equals(mlist.get(j))) {
                    mlist.remove(j);
                }
            }
        }
        LogUtil.linkageLog("LinkageReceiver reChecking ", "end mlist.size = " + mlist.size());

    }
    protected static  boolean isExistedSelf(ArrayList<LinkageBean> triggerList, ArrayList<LinkageBean> actionList) {
        if (triggerList != null && triggerList.size() > 0 && actionList != null && actionList.size() > 0) {
            for (int i = 0; i < triggerList.size(); i++) {
                for (int j = 0; j < actionList.size(); j++) {
                    if (triggerList.get(i).getTriggerCtrlId().equals(actionList.get(j).getActionCtrlId())) {
                        LogUtil.linkageLog("isExistedSelf", "trigger and action is repeat");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    protected static  boolean isExistedOthers(ArrayList<LinkageBean> triggerBeanArrayList, ArrayList<LinkageBean> ctrlBeanArrayList, String linkageId,LinkageTriggerDao linkageTriggerDao,
            LinkageActionDao dao) {
        // 联动设置条件是否存在于已创建成功的联动动作中
        for (int i = 0; i < triggerBeanArrayList.size(); i++) {
            String existedLinkageId = dao.isExisted(triggerBeanArrayList.get(i).getTriggerCtrlId(), parseValue(triggerBeanArrayList.get(i).getTriggerValue().toString(), true));
            if (existedLinkageId == null || existedLinkageId.equals("")) {
                continue;
            } else {
                if (existedLinkageId.equals(linkageId)) {
                    continue;
                } else {
                    LogUtil.linkageLog( "isExistedOthers", "trigger condition existed perform action" + " ctrlId = " + triggerBeanArrayList.get(i).getTriggerCtrlId() + " value = " + triggerBeanArrayList.get(i).getTriggerValue());
                    return true;
                }
            }
        }

        // 联动设置动作是否存在于已创建成功的联动条件中
        for (int i = 0; i < ctrlBeanArrayList.size(); i++) {
            String existedLinkageId = linkageTriggerDao.isExisted(ctrlBeanArrayList.get(i).getActionCtrlId(), parseValue(ctrlBeanArrayList.get(i).getActionValue(), false));
            if (existedLinkageId == null || existedLinkageId.equals("")) {
                continue;
            } else {
                if (existedLinkageId.equals(linkageId)) {
                    continue;
                } else {
                    LogUtil.linkageLog( "isExistedOthers", "perform action existed trigger condition" + " ctrlId = " + ctrlBeanArrayList.get(i).getActionCtrlId() + " value = " + ctrlBeanArrayList.get(i).getActionValue() + "relationship = " + 1);
                    return true;
                }
            }
        }
        return false;

    }
    protected static int getConform(String ctrlId, JSONArray valueArray,DeviceStatusDao devStatusDao) {
        if (valueArray != null && valueArray.length() == 2) {
            String devCurrentValue = devStatusDao.select(ctrlId);
            if (devCurrentValue != null && devCurrentValue.equals("")) {
                Double currentValue = Double.parseDouble(devCurrentValue);
                if (currentValue >= valueArray.optDouble(0) && currentValue <= valueArray.optDouble(1)) {
                    return 1;
                }
            }
        } else {
            LogUtil.linkageLog( "getConform", "valueArray is null or length is not equals 2");
        }
        return 0;
    }

    protected static String parseValue(String value, boolean isDowncast) {
        if (isDowncast) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                if (jsonArray != null && jsonArray.length() == 2) {
                    return jsonArray.optString(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return value;

        } else {
            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray.put(0, Double.parseDouble(value));
                jsonArray.put(1, Double.parseDouble(value));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray.toString();
        }

    }

    protected static JSONArray positionGenerate(JSONArray jsonArray){
        if (jsonArray != null){
            JSONArray position = new JSONArray();
            for (int i = 0;i < jsonArray.length();i++){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("deviceid",jsonArray.optJSONObject(i).optString("deviceid"));
                    jsonObject.put("ctrlid",jsonArray.optJSONObject(i).optString("ctrlid"));
                    jsonObject.put("value",jsonArray.optJSONObject(i).optString("value"));
                    jsonObject.put("position",i);
                    position.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return position;
        }
        return null;
    }

    public static String generateMsg(CtrlBean ctrlBean, String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action_id", ctrlBean.getActionId());
            jsonObject.put("node_id", ctrlBean.getNodeId());
            jsonObject.put("value", ctrlBean.getValue());
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
