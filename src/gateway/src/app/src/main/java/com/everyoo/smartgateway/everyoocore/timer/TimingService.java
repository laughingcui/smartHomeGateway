package com.everyoo.smartgateway.everyoocore.timer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.SceneDaoBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.SceneDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.TimingDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/4/25.
 */
public class TimingService extends Service {
    private final String TAG = "TimingService";
    private ArrayList<TimingBean> mlist;
    private boolean isRegistered = false;
    private TimingDao timingDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.currentMode == Constants.CONTROL_MODE) {
                TimingBean alarmBean = (TimingBean) intent.getSerializableExtra("alarm_bean");
                LogUtil.println(TAG + "onReceive", " received alarm receive and alarmId = " + alarmBean.getAlarmId());

                LogUtil.println(TAG + "onReceive", " timerType value is = " + alarmBean.getTimerType());
                if (alarmBean.getTimerType() == Constants.TIMING) {
                    BroadcastAction.sipToGatewayMsg(PjsipMsgAction.ctrl(alarmBean.getCtrlId(), alarmBean.getValue(), alarmBean.getUserId(), Constants.CONTROL), context);
                } else if (alarmBean.getTimerType() == Constants.SCENE_TIMING) {
                    LogUtil.println(TAG + "start", "sceneId = " + alarmBean.getCtrlId());
                    ArrayList<SceneDaoBean> sceneBeans = ((SceneDao) InitApplication.mSql.getSqlDao(SceneDao.class)).select(alarmBean.getCtrlId());
                    LogUtil.println(TAG + "start", "sceneBeans.size = " + sceneBeans.size());
                    for (int i = 0; i < sceneBeans.size(); i++) {
                        BroadcastAction.sipToGatewayMsg(PjsipMsgAction.ctrl(sceneBeans.get(i).getCtrlId(), sceneBeans.get(i).getValue(), alarmBean.getUserId(), Constants.CONTROL), context);
                    }

                }
                ArrayList<TimingBean> arrayList = new ArrayList<>();
                arrayList.add(alarmBean);
                RegisterTimingAction.registeAlarm(arrayList, context);
            } else {
                LogUtil.println(TAG + "onReceive", "current mode is wisdom mode");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        timingDao = (TimingDao) InitApplication.mSql.getSqlDao(TimingDao.class);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mlist = timingDao.select();
                if (mlist != null && mlist.size() > 0) {
                    LogUtil.println(TAG + "onStartCommand", " alarm list is " + mlist.size());
                    RegisterTimingAction.registeAlarm(mlist, TimingService.this);
                    if (isRegistered == true) {
                        unregisterReceiver(broadcastReceiver);
                    }
                    registerReceiver(mlist);
                } else {
                    LogUtil.println(TAG + "onStartCommand", " alarm list is null");
                    stopSelf();
                }
            }
        }).start();
        return START_NOT_STICKY;
    }

    /**
     * 注册定时广播
     *
     * @param list
     */
    private void registerReceiver(ArrayList<TimingBean> list) {
        IntentFilter filter = new IntentFilter();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEnable() == Constants.ENABLE) {
                filter.addAction(list.get(i).getAlarmId());
                LogUtil.println(TAG + "registerReceiver", " alarmId = " + list.get(i).getAlarmId());
            } else {
                LogUtil.println(TAG + "registerReceiver", " alarm is closed");
            }
        }
        registerReceiver(broadcastReceiver, filter);
        isRegistered = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegistered == true) {
            unregisterReceiver(broadcastReceiver);
        }
    }

}
