package com.everyoo.smartgateway.everyoocore.message.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.everyoocore.bugs.BugTagsAction;
import com.everyoo.smartgateway.everyoocore.networkobserver.IndicatorLigthAction;
import com.everyoo.smartgateway.everyoocore.timer.RegisterTimingAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sp.SPHelper;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.smartgateway.ServiceAction;
import com.everyoo.smartgateway.utils.ApUtil;
import com.everyoo.smartgateway.utils.GetHostIpUtil;
import com.everyoo.smartgateway.utils.IdRandomUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.MD5Util;
import com.everyoo.smartgateway.utils.MultibroadUtil;
import com.everyoo.smartgateway.utils.RestartAppUtil;
import com.everyoo.smartgateway.utils.TimeUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 绑定流程：创建热点-开启接收广播-数据验证-向云端发起绑定操作
 */
public class BindService extends Service {
    private final String TAG = "BindService ";
    private MultibroadUtil multibroadUtil;
    private ApUtil apUtil;
    private WifiUtil wifiUtil;
    private EveryooHttp mHttp;
    private final int BIND_GATEWAY = 1;
    public static final int BIND_SUCCESS = 2;
    public static final int BIND_FAILED = 3;
    private final int BIND_TIMING = 4;
    private boolean hasBound = false;
    private boolean isBinding = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BIND_GATEWAY:
                    String timestamp = IdRandomUtil.generateString(2);
                    String firstSign = MD5Util.get32MD5Str(Constants.gatewaySn + Constants.userId + timestamp);
                    String secondSign = MD5Util.get32MD5Str(firstSign + timestamp);
                    mHttp.gatewayBind(BindService.this, handler, timestamp, secondSign);
                    break;
                case BIND_SUCCESS:
                    hasBound = true;
                    bindResult(true);
                    break;
                case BIND_FAILED:
                    bindResult(false);
                    break;
                case BIND_TIMING:
                    bindTiming();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        register();
        mHttp = InitApplication.mHttp;
        apUtil = new ApUtil(this);
        multibroadUtil = new MultibroadUtil(this);
        wifiUtil = WifiUtil.getInstance(BindService.this);
        bindProcessor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    private void register() {
        InitApplication.mEventBus.register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerEvnetMessage(EventMessage msg) {
        if (Constants.ACTION_BIND.equals(msg.getMessageType())) {
            LogUtil.println(TAG + "onReceive", "begin bind gateway");
            if (!isBinding) {
                handler.sendEmptyMessage(BIND_GATEWAY);
            } else {
                LogUtil.println(TAG + "broadcastReceiver", "isBinding is true");
            }
        }
    }

    /**
     * 获取网络接口（wlan0）、开启热点、接收广播、关闭热点、wifi解析、绑定处理
     */
    private void bindProcessor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                apUtil.startAp(); // 打开热点
                IndicatorLigthAction.waitingBind(BindService.this);
                if (multibroadUtil.isReceivedMessage()) {    // 判断是否收到信息
                    apUtil.closeAp(); // 关闭热点
                    handler.sendEmptyMessage(BIND_TIMING);
                    wifiUtil.parseWifiEncryption(); // 解析wifi加密方式
                    wifiUtil.forgetWifi();
                    if (!TextUtils.isEmpty(Constants.wifiSsid) && !TextUtils.isEmpty(Constants.userId)) {
                        wifiUtil.connectWifi();
                    } else {
                        LogUtil.println(TAG + "connectWifi", " 用户信息不完整！");
                        RestartAppUtil.restartApp(BindService.this, 0);
                    }
                } else {
                    LogUtil.println(TAG + "initInstance", "multibroadUtil.receiveAndSendMessage() return false");
                }
            }
        }).start();

    }

    private void bindTiming() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (!hasBound && i < 60) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                if (!hasBound) {
                    WifiUtil.getInstance(BindService.this).forgetWifi();
                    RestartAppUtil.restartApp(BindService.this, 0);
                }
            }
        }).start();

    }

    /**
     * 绑定结果处理
     *
     * @param isSuccess
     */
    public void bindResult(boolean isSuccess) {
        if (isSuccess) {
            BugTagsAction.bugCollect(BindService.this.getApplication());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SPHelper.getInstance().saveBindInfo(BindService.this);
                    Constants.isBind = true;
                    mHttp.updateAppVersion(BindService.this);
                    String ip = GetHostIpUtil.getExternalNetIp();
                    if (!ip.equals("")) {
                        mHttp.reportIpHttp(BindService.this, ip);
                    } else {
                        LogUtil.println(TAG + "reportLocalHostIp", " ip is null");
                    }
                    ServiceAction.startServices(BindService.this, true);
                    ServiceAction.startPjService(BindService.this);
                    TimeUtil.getNetworkTimeAndSetSystemTime();//校对本地时间
                    //RegisterTimingAction.registeUpgradeAlarm(BindService.this);
                    BindService.this.stopSelf();
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RestartAppUtil.restartApp(BindService.this, 0);
                }
            }).start();

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        InitApplication.mEventBus.unregister(this);
    }
}
