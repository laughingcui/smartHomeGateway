package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.timer.TimingService;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.TimingDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/6/16.
 */
public class TimingProcessor {
    private final String TAG = "TimingProcessor ";

    private EveryooHttp mHttp;
    private TimingDao timingDao;
    private TimingBean timingBean;
    private Context mContext;

    public TimingProcessor(Context context) {
        mHttp = InitApplication.mHttp;
        timingDao = (TimingDao) InitApplication.mSql.getSqlDao(TimingDao.class);
        mContext = context;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CREATE_SUCCESS:
                    create(timingBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.CREATE_SUCCESS), timingBean.getUserId());
                    registeAlarm();
                    break;
                case Constants.CREATE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.CREATE_FAILED), timingBean.getUserId());
                    break;
                case Constants.MODIFY_SUCCESS:
                    update(timingBean, Constants.TIMING_TIMINGID, timingBean.getAlarmId());
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.MODIFY_SUCCESS), timingBean.getUserId());
                    registeAlarm();
                    break;
                case Constants.MODIFY_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.MODIFY_FAILED), timingBean.getUserId());
                    break;
                case Constants.START_SUCCESS:
                    enable(timingBean, Constants.ENABLE);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.START_SUCCESS), timingBean.getUserId());
                    registeAlarm();
                    break;
                case Constants.START_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.START_FAILED), timingBean.getUserId());
                    break;
                case Constants.STOP_SUCCESS:
                    enable(timingBean, Constants.UNENABLE);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.STOP_SUCCESS), timingBean.getUserId());
                    registeAlarm();
                    break;
                case Constants.STOP_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.STOP_FAILED), timingBean.getUserId());
                    break;
                case Constants.DELETE_SUCCESS:
                    delete(Constants.TIMING_TIMINGID, timingBean.getAlarmId());
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.DELETE_SUCCESS), timingBean.getUserId());
                    registeAlarm();
                    break;
                case Constants.DELETE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.timing(timingBean, Constants.DELETE_FAILED), timingBean.getUserId());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 定时消息处理器
     *
     * @param timingBean
     */
    public void processor(TimingBean timingBean) {
        if (timingBean != null) {
            this.timingBean = timingBean;
            int type = timingBean.getType();
            if (type == Constants.VALUE_CREATE) {
                if (timingBean.getTimerType() == Constants.TIMING) {
                    mHttp.createTiming(mContext,timingBean, handler);
                } else {
                    mHttp.createSceneTiming(mContext,timingBean, handler);
                }
            } else if (type == Constants.VALUE_MODIFY) {
                timingBean.setEnable(timingDao.selectEnable(timingBean.getAlarmId()));
                if (timingBean.getTimerType() == Constants.TIMING) {
                    mHttp.updateTiming(mContext,timingBean, handler);
                } else {
                    mHttp.updateSceneTiming(mContext,timingBean, handler);
                }
            } else if (type == Constants.VALUE_START) {
                mHttp.enableTiming(mContext,timingBean, handler);
            } else if (type == Constants.VALUE_STOP) {
                mHttp.enableTiming(mContext,timingBean, handler);
            } else if (type == Constants.VALUE_DELETE) {
                if (timingBean.getTimerType() == Constants.TIMING) {
                    mHttp.deleteTiming(mContext,timingBean, handler);
                } else {
                    mHttp.deleteSceneTiming(mContext,timingBean, handler);
                }
            }
        } else {
            LogUtil.timingLog(TAG + "processor", "timingBean is null");
        }
    }

    /**
     * 创建本地定时
     *
     * @param timingBean
     */
    public void create(final TimingBean timingBean) {
        if (timingBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    timingBean.setEnable(Constants.ENABLE);
                    timingDao.create(timingBean);
                }
            }).start();
        } else {
            LogUtil.println(TAG + "create", "timingBean is null");
        }
    }

    /**
     * 删除本地定时
     *
     * @param key
     * @param value
     */
    public void delete(final String key, final String value) {
        if (key != null && !key.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    timingDao.delete(key, value);
                }
            }).start();
        } else {
            LogUtil.println(TAG + "delete", "timingBean is null");
        }

    }

    /**
     * 更新本地定时
     *
     * @param timingBean
     * @param key
     * @param value
     */
    public void update(final TimingBean timingBean, final String key, final String value) {
        if (timingBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                   /* timingDao.delete(key,value);
                    timingDao.create(timingBean);*/
                    timingDao.update(timingBean);
                }
            }).start();
        } else {
            LogUtil.println(TAG + "update", "timingBean is null");
        }
    }

    /**
     * 设置本地定时开关
     *
     * @param timingBean
     */
    public void enable(final TimingBean timingBean, final int enable) {
        if (timingBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    timingBean.setEnable(enable);
                    timingDao.updateEnable(timingBean);
                }
            }).start();
        } else {
            LogUtil.println(TAG + "enable", "timingBean is null");
        }
    }

    /**
     * 注册定时方法
     */
    public void registeAlarm() {
        Intent intent = new Intent(mContext, TimingService.class);
        mContext.startService(intent);
    }
}
