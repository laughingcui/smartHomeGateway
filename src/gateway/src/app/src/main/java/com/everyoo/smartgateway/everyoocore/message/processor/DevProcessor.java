package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.timer.RegisterTimingAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sp.SPHelper;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageTriggerBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.CtrlDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageEnableDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageTriggerDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.SceneDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.TimingDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/6/16.
 */
public class DevProcessor {

    private final String TAG = "DevProcessor ";
    private final int ADD_DEVICE = 0;
    private final int DELETE_DEVICE = 1;
 // private DevHttp devHttp;
    private DeviceBean deviceBean;
    private Context mContext;
    private CtrlDao mCtrlDao;
    private EveryooHttp mHttp;
    private LinkageTriggerDao  mTriggerDao;
    private LinkageActionDao mActionDao;
    private LinkageEnableDao mEnableDao;
    public DevProcessor(Context context) {
        mContext = context;
        mHttp= InitApplication.mHttp;
        mCtrlDao= (CtrlDao) InitApplication.mSql.getSqlDao(CtrlDao.class);
        mTriggerDao=(LinkageTriggerDao) InitApplication.mSql.getSqlDao(LinkageTriggerDao.class);
        mActionDao=(LinkageActionDao) InitApplication.mSql.getSqlDao(LinkageActionDao.class);
        mEnableDao=(LinkageEnableDao) InitApplication.mSql.getSqlDao(LinkageEnableDao.class);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.DELETE_DEVICE_SUCCESS:
                    deleteLocalData(deviceBean.getDeviceId(), deviceBean.getUserId());
                    BroadcastAction.gatewayToSip(PjsipMsgAction.device(deviceBean, Constants.DELETE_DEVICE_SUCCESS), deviceBean.getUserId());
                    break;
                case Constants.DELETE_DEVICE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.device(deviceBean, Constants.DELETE_DEVICE_FAILED), deviceBean.getUserId());
                    break;
                default:
                    break;
            }
        }
    };


    public void processor(DeviceBean deviceBean, int type) {
        if (deviceBean != null) {
            this.deviceBean = deviceBean;
            if (type == ADD_DEVICE) {
            } else if (type == Constants.DELETE_DEVICE) {
                mHttp.deleteDevice(mContext,deviceBean, handler);
            } else {
                LogUtil.println(TAG + "processor", "type is invalid and type = " + type);
            }
        } else {
            LogUtil.println(TAG + "processor", "deviceBean is null");
        }

    }

    public void deleteLocalData(final String deviceId, final String userId) {
        if (!deviceId.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    deleteSharedPreferences(deviceId);
                    deleteLinkage(deviceId, userId);
                    deleteAlarm(deviceId);
                    deleteScene(deviceId);
                    deleteCtrl(deviceId);
                }
            }).start();
        } else {
            LogUtil.println(TAG + "deleteLocalData", "deviceId is null");
        }


    }

    public void deleteLinkage(String deviceId,String userId){
        ArrayList<LinkageTriggerBean> triggerLinkageBeans =  mTriggerDao.select(Constants.LINKAGE_DEVICEID,deviceId);
        ArrayList<LinkageActionBean> actionLinkageBeans = mActionDao.select(Constants.LINKAGE_DEVICEID,deviceId);
        for (int i = 0; i < triggerLinkageBeans.size();i++){
            mEnableDao.delete(triggerLinkageBeans.get(i).getLinkageId());
            mTriggerDao.delete(Constants.LINKAGE_LINKAGEID,triggerLinkageBeans.get(i).getLinkageId());
            mActionDao.delete(Constants.LINKAGE_LINKAGEID,triggerLinkageBeans.get(i).getLinkageId());
        }
        for (int i = 0;i < actionLinkageBeans.size();i++){
            mEnableDao.delete(actionLinkageBeans.get(i).getLinkageId());
            mTriggerDao.delete(Constants.LINKAGE_LINKAGEID,actionLinkageBeans.get(i).getLinkageId());
            mActionDao.delete(Constants.LINKAGE_LINKAGEID,actionLinkageBeans.get(i).getLinkageId());
        }


    }

    /**
     * 修改定时包括更新和删除
     */
    public void deleteAlarm(final String deviceId) {
        TimingDao timingDao = (TimingDao) InitApplication.mSql.getSqlDao(TimingDao.class);
        ArrayList<TimingBean> mlist = timingDao.select(deviceId);
        timingDao.delete(Constants.TIMING_DEVICEID, deviceId);
        if (mlist != null && mlist.size() > 0) {
            for (int i = 0; i < mlist.size(); i++) {
                RegisterTimingAction.unRegisteAlarm(mContext, mlist.get(i).getAlarmId());
            }
        }
    }

    public void deleteScene(String deviceId) {
        SceneDao sceneDao = (SceneDao) InitApplication.mSql.getSqlDao(SceneDao.class);
        deleteSceneAlarm(sceneDao.selectSceneId(deviceId));
        sceneDao.delete(Constants.SCENE_DEVICEID, deviceId);
    }

    private void deleteSceneAlarm(ArrayList<String> sceneIdList) {
        LogUtil.println(TAG + "deleteSceneAlarm", "sceneIdList value is= " + sceneIdList);
        TimingDao timingDao = (TimingDao) InitApplication.mSql.getSqlDao(TimingDao.class);
        if (sceneIdList != null && sceneIdList.size() > 0) {
            for (int i = 0; i < sceneIdList.size(); i++) {
                if (timingDao.selectAlarmId(sceneIdList.get(i)) != null) {
                    RegisterTimingAction.unRegisteAlarm(mContext, timingDao.selectAlarmId(sceneIdList.get(i)));
                    timingDao.delete("ctrl_id", sceneIdList.get(i));
                } else {
                    LogUtil.println(TAG + "deleteSceneAlarm", " scene does not timing");
                }

            }
        } else {
            LogUtil.println(TAG + "deleteSceneAlarm", "sceneIdList is null");
        }
    }


    public void deleteCtrl(String deviceId) {
        mCtrlDao.delete(deviceId);
    }

    public void deleteSharedPreferences(String deviceId) {
        SPHelper.getInstance().deleteData(mContext, mCtrlDao.selectNodeId(deviceId));
    }

}
