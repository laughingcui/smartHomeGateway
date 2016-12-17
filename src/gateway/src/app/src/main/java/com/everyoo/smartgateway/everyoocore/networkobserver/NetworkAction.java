package com.everyoo.smartgateway.everyoocore.networkobserver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chaos on 2016/7/5.
 */
public class NetworkAction {

    private final String TAG = "NetworkAction ";
    private static NetworkAction instance;
    private WifiUtil wifiUtil;
    private boolean isReceivedMessage = false;
    private long delay = 5 * 1000;
    private NetworkInfo.State currentWifiState;
    private NetworkInfo currentNetworkInfo;

    private Context mContext;
    private final int TYPE_WIFI_CONNECTED = 0;
    private final int TYPE_ETHERNET_CONNECTED = 1;
    private final int TYPE_NETWORK_DISCONNECTED = 2;
    private NetworkAction(Context context) {
        mContext = context;
        wifiUtil = WifiUtil.getInstance(context);
    }

    public static synchronized NetworkAction getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkAction(context);

        }
        return instance;
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TYPE_WIFI_CONNECTED){
                if (!Constants.isBind) {
                    InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_BIND,null,null));
                }else {
                    mContext.sendBroadcast(new Intent(Constants.ACTION_NETWORK_HEART));
                }
            }else if (msg.what == TYPE_ETHERNET_CONNECTED){
                mContext.sendBroadcast(new Intent(Constants.ACTION_NETWORK_HEART));
            }else if (msg.what == TYPE_NETWORK_DISCONNECTED){
                mContext.sendBroadcast(new Intent(Constants.ACTION_NETWORK_HEART));

            }

        }
    };


    public synchronized void receiveMessage(final NetworkInfo networkInfo) {
        if (networkInfo != null){
            currentNetworkInfo = networkInfo;
            LogUtil.println(TAG + "receiveMessage"," currentState = " + currentNetworkInfo.getState());
            if (!isReceivedMessage) {
                isReceivedMessage = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        LogUtil.println(TAG + "receiveMessage","currentWifiState = " + currentNetworkInfo.getState());
                        processorMessage(currentNetworkInfo);
                        isReceivedMessage = false;
                        cancel();
                    }
                }, delay);
            } else {
                LogUtil.println(TAG + "receiveMessage", "isReceiveMessage is true");
            }
        }else {
            LogUtil.println(TAG + "receiveMessage", "networkInfo is null");
            mHandler.sendEmptyMessage(TYPE_NETWORK_DISCONNECTED);
        }

    }

    public void processorMessage(NetworkInfo networkInfo) {
        LogUtil.println(TAG + "processorMessage","state = " + networkInfo.getState());
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                LogUtil.println(TAG + "processorMessage", "state is connected");
                mHandler.sendEmptyMessage(TYPE_WIFI_CONNECTED);
            } else {
                LogUtil.println(TAG + "processorMessage", "state is disconnected");
            }
        }else if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET){
            LogUtil.println(TAG + "processorMessage", "ethernet is connected");
            mHandler.sendEmptyMessage(TYPE_ETHERNET_CONNECTED);
        }

    }




}
