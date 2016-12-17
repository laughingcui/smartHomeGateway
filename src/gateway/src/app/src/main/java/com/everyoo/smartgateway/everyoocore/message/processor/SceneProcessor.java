package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.timer.RegisterTimingAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.SceneDaoBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.ScenePanelBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.SceneDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.ScenePanelDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.TimingDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/6/16.
 */
public class SceneProcessor {

    private final String TAG = "SceneProcessor";
    private EveryooHttp mHttp;
    private SceneDao sceneDao;
    private SceneBean sceneBean;
    private Context mContext;

    public SceneProcessor(Context context) {
        mHttp= InitApplication.mHttp;
        sceneDao = (SceneDao) InitApplication.mSql.getSqlDao(SceneDao.class);
        mContext = context;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CREATE_SUCCESS:
                    create(jsonToSceneBean(sceneBean.getPosition().toString(), sceneBean.getSceneId()));
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.CREATE_SUCCESS), sceneBean.getUserId());
                    break;
                case Constants.CREATE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.CREATE_FAILED), sceneBean.getUserId());
                    break;
                case Constants.MODIFY_SUCCESS:
                    update(jsonToSceneBean(sceneBean.getPosition().toString(), sceneBean.getSceneId()), Constants.SCENE_SCENEID, sceneBean.getSceneId());
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.MODIFY_SUCCESS), sceneBean.getUserId());
                    break;
                case Constants.MODIFY_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.MODIFY_FAILED), sceneBean.getUserId());
                    break;
                case Constants.DELETE_SUCCESS:
                    deleteSceneAlarm(sceneBean.getSceneId());
                    delete(Constants.SCENE_SCENEID, sceneBean.getSceneId());
                    deleteScenePanel(sceneBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.DELETE_SUCCESS), sceneBean.getUserId());
                    break;
                case Constants.DELETE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.DELETE_FAILED), sceneBean.getUserId());
                    break;
                case Constants.CREATE_SCENE_PANEL_SUCCESS:
                    deleteScenePanel(sceneBean);
                    createScenePanel(sceneBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.CREATE_SUCCESS), sceneBean.getUserId());
                    break;
                case Constants.CREATE_SCENE_PANEL_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.CREATE_FAILED), sceneBean.getUserId());
                    break;
                case Constants.DELETE_SCENE_PANEL_SUCCESS:
                    deleteScenePanel(sceneBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.DELETE_SUCCESS), sceneBean.getUserId());
                    break;
                case Constants.DELETE_SCENE_PANEL_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.scene(sceneBean, Constants.DELETE_FAILED), sceneBean.getUserId());
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 取消并删除场景定时闹钟
     *
     * @param sceneId
     */
    private void deleteSceneAlarm(String sceneId) {
        LogUtil.println(TAG + "deleteSceneAlarm", "deviceId value is= " + sceneId);
        TimingDao timingDao = (TimingDao) InitApplication.mSql.getSqlDao(TimingDao.class);
        if (timingDao.selectAlarmId(sceneId) != null) {
            RegisterTimingAction.unRegisteAlarm(mContext, timingDao.selectAlarmId(sceneId));
            timingDao.delete("ctrl_id", sceneId);
        } else {
            LogUtil.println(TAG + "deleteSceneAlarm", " scene does not timing");
        }

    }




    /**
     * 场景指令处理器
     *
     * @param sceneBean
     */
    public void processor(SceneBean sceneBean) {
        if (sceneBean != null) {
            this.sceneBean = sceneBean;
            int type = sceneBean.getType();
            if (type == Constants.VALUE_CREATE) {
                mHttp.createScene(mContext,sceneBean, handler);
            } else if (type == Constants.VALUE_MODIFY) {
                mHttp.updateScene(mContext,sceneBean, handler);
            } else if (type == Constants.VALUE_DELETE) {
                mHttp.deleteScene(mContext,sceneBean, handler);
            } else if (type == Constants.VALUE_START) {
                start(sceneBean);
            } else if (type == Constants.VALUE_CREATE_SCNE_PANEL){
                mHttp.createScenePanel(mContext,sceneBean,handler);
            }else if (type == Constants.VALUE_UPDATE_SCENE_PANEL){
                mHttp.updateScenePanel(mContext,sceneBean, handler);
            }else if (type == Constants.VALUE_DELETE_SCENE_PANEL){
                mHttp.deleteScenePanel(mContext,sceneBean, handler);
            } else if (type == Constants.VALUE_PERORM_SCENE_PANEL){
                startScenePanel(sceneBean);
            }
            else {
                LogUtil.sceneLog(TAG + "processor", "type is invalid and type = " + type);
            }
        } else {
            LogUtil.sceneLog(TAG + "processor", "sceneBean is null");
        }


    }


    /**
     * json转换成ArrayList<SceneBean>
     *
     * @param message
     * @return
     */
    public ArrayList<SceneBean> jsonToSceneBean(String message, String sceneId) {
        try {
            if (message != null && !message.equals("") && sceneId != null && !sceneId.equals("")) {
                JSONArray jsonArray = new JSONArray(message);
                LogUtil.println(TAG + "jsonToSceneBean", "jsonArray = " + jsonArray.toString() + "sceneId = " + sceneId);
                ArrayList<SceneBean> sceneBeans = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    SceneBean sceneBean = new SceneBean();
                    sceneBean.setSceneId(sceneId);
                    sceneBean.setDeviceId(jsonArray.optJSONObject(i).optString("deviceid"));
                    sceneBean.setCtrlId(jsonArray.optJSONObject(i).optString("ctrlid"));
                    sceneBean.setValue(jsonArray.optJSONObject(i).optString("value"));
                    sceneBeans.add(sceneBean);
                    LogUtil.println(TAG + "jsonToSceneBean", "sceneBean.getSceneId = " + sceneBean.getSceneId());
                }
                return sceneBeans;
            } else {
                LogUtil.println(TAG + "jsonToSceneBean", "message is null or sceneId is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建本地场景
     *
     * @param sceneBeans
     */
    public void create(final ArrayList<SceneBean> sceneBeans) {
        if (sceneBeans != null && sceneBeans.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sceneDao.create(SceneBeanToSceneDaoBean(sceneBeans));
                }
            }).start();
        } else {
            LogUtil.println(TAG + "create", "sceneBeans is null");
        }
    }

    /**
     * 创建场景面板keyid和场景sceneid的映射关系
     * @param sceneBean
     */
    public void createScenePanel(final SceneBean sceneBean){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScenePanelBean bean=new ScenePanelBean();
                bean.setKeyId(Integer.parseInt(sceneBean.getValue()));
                bean.setCtrlId(sceneBean.getCtrlId());
                bean.setSceneId(sceneBean.getSceneId());
                ((ScenePanelDao)InitApplication.mSql.getSqlDao(ScenePanelDao.class)).create(bean);
            }
        }).start();
    }
    /**
     * 删除本地场景
     *
     * @param key
     * @param value
     */
    public void delete(final String key, final String value) {
        if (key != null && !key.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sceneDao.delete(key, value);
                }
            }).start();
        }
    }

    public void deleteScenePanel(final SceneBean sceneBean){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScenePanelDao dao= (ScenePanelDao) InitApplication.mSql.getSqlDao(ScenePanelDao.class);
                if (sceneBean.getType() == Constants.VALUE_DELETE){
                    dao.delete(sceneBean.getSceneId());
                }else if (sceneBean.getType() == Constants.VALUE_DELETE_SCENE_PANEL){
                    ScenePanelBean bean=new ScenePanelBean();
                    bean.setSceneId(sceneBean.getSceneId());
                    bean.setCtrlId(sceneBean.getCtrlId());
                    bean.setKeyId(Integer.parseInt(sceneBean.getValue()));
                    dao.delete(bean);
                }

            }
        }).start();
    }

    /**
     * 修改本地场景
     *
     * @param sceneBeans
     * @param key
     * @param value
     */
    public void update(final ArrayList<SceneBean> sceneBeans, final String key, final String value) {
        if (sceneBeans != null && sceneBeans.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sceneDao.delete(key, value);
                    sceneDao.create(SceneBeanToSceneDaoBean(sceneBeans));
                }
            }).start();
        } else {
            LogUtil.println(TAG + "update", "sceneBeans is null");
        }
    }




    /**
     * 执行本地场景
     *
     * @param sceneBean
     */
    public void start(final SceneBean sceneBean) {
        if (sceneBean != null && !sceneBean.getSceneId().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.sceneLog(TAG + "start", "sceneId = " + sceneBean.getSceneId());
                    ArrayList<SceneDaoBean> sceneBeans = sceneDao.select(sceneBean.getSceneId());
                    LogUtil.println(TAG + "start", "sceneBeans.size = " + sceneBeans.size());
                    for (int i = 0; i < sceneBeans.size(); i++) {
                        BroadcastAction.sipToGatewayMsg(PjsipMsgAction.ctrl(sceneBeans.get(i).getCtrlId(), sceneBeans.get(i).getValue(), sceneBean.getUserId(), Constants.CONTROL), mContext);
                    }
                }
            }).start();
        } else {
            LogUtil.sceneLog(TAG + "start", "sceneBean is null or sceneId is ");
        }
    }

    public  void startScenePanel(SceneBean sceneBean){
        if (sceneBean != null){
            if (sceneBean.getSceneId().equals("")){
                sceneBean.setSceneId(((ScenePanelDao)InitApplication.mSql.getSqlDao(ScenePanelDao.class)).select(sceneBean.getCtrlId(),sceneBean.getValue()));
            }
            LogUtil.sceneLog(TAG + "startScenePanel","sceneId is " + sceneBean.getSceneId());
            ArrayList<SceneDaoBean> sceneBeans = sceneDao.select(sceneBean.getSceneId());
            for (int i = 0; i < sceneBeans.size(); i++) {
                BroadcastAction.sipToGatewayMsg(PjsipMsgAction.ctrl(sceneBeans.get(i).getCtrlId(), sceneBeans.get(i).getValue(), sceneBean.getUserId(), Constants.CONTROL), mContext);
            }
        }
    }

    private ArrayList<SceneDaoBean> SceneBeanToSceneDaoBean(ArrayList<SceneBean> list){
        ArrayList<SceneDaoBean> sList=new ArrayList<>();
        for(SceneBean bean:list){
            SceneDaoBean sBean=new SceneDaoBean();
            sBean.setDeviceId(bean.getDeviceId());
            sBean.setCtrlId(bean.getCtrlId());
            sBean.setValue(bean.getValue());
            sBean.setSceneId(bean.getSceneId());
            sList.add(sBean);
        }
        return sList;
    }
}
