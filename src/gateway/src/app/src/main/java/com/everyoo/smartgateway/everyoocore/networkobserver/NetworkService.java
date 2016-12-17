package com.everyoo.smartgateway.everyoocore.networkobserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.everyoocore.bugs.BugTagsAction;
import com.everyoo.smartgateway.everyoocore.message.processor.GwProcessor;
import com.everyoo.smartgateway.everyoosip.PjsipService;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.smartgateway.ServiceAction;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.TimeUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

/**
 * Created by abc on 2016/7/26.
 */
public class NetworkService extends Service {
    private final String TAG = "NetworkService ";
    private boolean isReceivedMessage = false;
    private boolean isHeartReceived = false;
    private boolean isWifiConnected = false;
    private long delay = 5 * 1000;

    private WifiUtil wifiUtil;
    private NetworkInfo currentNetworkInfo;

    private final int TYPE_NETWORK_CHANGED = 0;
    private final int TYPE_WIFI_CONNECTED = 1;
    private final int TYPE_ETHERNET_CONNECTED = 2;
    private final int TYPE_NETWORK_DISCONNECTED = 3;
    private final int TYPE_NETWORK_HEART = 4;


    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "networkReceiver  接收到网络广播" + intent.getAction());
            if (Constants.ACTION_NETWORK_HEART.equals(intent.getAction())) {
                if (Constants.isBind) {
                    mHandler.sendEmptyMessage(TYPE_NETWORK_HEART);
                }
            } else {
                networkChangedReceiver(context, intent);
            }
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TYPE_NETWORK_CHANGED:
                    networkChanged();
                    break;
                case TYPE_WIFI_CONNECTED:
                    if (!Constants.isBind) {
                        InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_BIND, null, null));
                    } else {
                        networkHeartProcess(false);
                    }
                    break;
                case TYPE_ETHERNET_CONNECTED:
                    networkHeartProcess(false);
                    break;
                case TYPE_NETWORK_DISCONNECTED:
                    networkHeartProcess(false);
                    break;
                case TYPE_NETWORK_HEART:
                    networkHeartProcess(true);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // LogUtil.println(TAG + "onStartCommand", "网络服务创建");
        Log.v(TAG, "onStartCommand 网络服务创建");
        wifiUtil = WifiUtil.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(Constants.ACTION_NETWORK_HEART);
        registerReceiver(networkReceiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "onStartCommand 网络服务开始");
        // LogUtil.println(TAG + "onStartCommand", "网络服务开始");
        return START_NOT_STICKY;
    }


    private void networkChangedReceiver(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    LogUtil.println(TAG + "onReceive ", "wifi has been closed");
                    if (Constants.isBind) {
                        wifiUtil.openWifi();
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    LogUtil.println(TAG + "onReceive ", "wifi has been opened");
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            if (!Constants.isBind) {
                currentNetworkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (currentNetworkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    LogUtil.println(TAG + "onReceive ", "wifi has been disconnected");
                } else if (currentNetworkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    LogUtil.println(TAG + "onReceive ", "wifi has been connected to" + wifiUtil.getWifiInfo().getSSID());
                    mHandler.sendEmptyMessage(TYPE_WIFI_CONNECTED);
                }
                LogUtil.println(TAG + "onReceive", "info.getState = " + currentNetworkInfo.getState());

            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (Constants.isBind) {
                currentNetworkInfo = WifiUtil.getActiveNetworkInfo(context);
                if (currentNetworkInfo != null) {
                    if (ConnectivityManager.TYPE_WIFI == currentNetworkInfo.getType()) {
                        LogUtil.println(TAG + "onReceive ", "activeNetworkInfo is wifi");
                    } else if (ConnectivityManager.TYPE_ETHERNET == currentNetworkInfo.getType()) {
                        LogUtil.println(TAG + "onReceive ", "activeNetworkInfo is ethernet");
                    } else {
                        LogUtil.println(TAG + "onReceive ", "activeNetworkInfo is others and type = " + currentNetworkInfo.getType());
                    }
                } else {
                    LogUtil.println(TAG + "onReceive ", "activeNetworkInfo is null,need to connect wifi");
                }
                mHandler.sendEmptyMessage(TYPE_NETWORK_CHANGED);
            }

        }
    }


    private synchronized void networkChanged() {
        if (!isReceivedMessage) {
            Constants.isPjSipEnabled = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isReceivedMessage = true;
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (currentNetworkInfo != null) {
                        if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            if (currentNetworkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                                LogUtil.println(TAG + "processorMessage", "state is connected");
                                mHandler.sendEmptyMessage(TYPE_WIFI_CONNECTED);
                            } else {
                                LogUtil.println(TAG + "processorMessage", "state is disconnected");
                            }
                        } else if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                            LogUtil.println(TAG + "processorMessage", "ethernet is connected");
                            mHandler.sendEmptyMessage(TYPE_ETHERNET_CONNECTED);
                        }
                    } else {
                        mHandler.sendEmptyMessage(TYPE_NETWORK_DISCONNECTED);
                    }
                    isReceivedMessage = false;
                }
            }).start();
        }
    }

    private void networkHeartProcess(final boolean isNetworkHearted) {
        if (isHeartReceived) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                isHeartReceived = true;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NetworkInfo networkInfo = WifiUtil.getActiveNetworkInfo(NetworkService.this);
                if (networkInfo != null) {
                    int pingTimes = 0;
                    while (!WifiUtil.isNetworkConnectedByPing() && pingTimes < 3) {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pingTimes++;
                    }
                    if (pingTimes < 3) {
                        if (PjsipService.instance == null) {
                            ServiceAction.startPjService(NetworkService.this);
                        } else {
                            PjsipService.setRegistration(true);
                        }
                        if (!BugTagsAction.isBugTagsInitilized) {
                            BugTagsAction.bugCollect(InitApplication.instance);
                        }
                        TimeUtil.getNetworkTimeAndSetSystemTime();
                    } else {
                        IndicatorLigthAction.networkConnectedButNoData(NetworkService.this);
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            IndicatorLigthAction.noNetworkConnected(NetworkService.this);
                        }
                    });
                    System.out.println("启动网络连接模块");
                    if (!wifiUtil.isWifiConnectingOrConnected(NetworkService.this)) {
                        if (GwProcessor.isModifyWifi) {
                            wifiUtil.networkConnect(Constants.modifyWifiSSID, Constants.modifyWifiPwd, false);
                        } else {
                            wifiUtil.networkConnect(Constants.wifiSsid, Constants.wifiPwd, true);
                        }
                    }
                }
                isHeartReceived = false;
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.println(TAG + "onDestroy", "网络服务销毁");
        unregisterReceiver(networkReceiver);
    }
}
