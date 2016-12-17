package com.everyoo.smartgateway.everyoocore.networkobserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

/**
 * Created by chaos on 2016/6/16.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = "NetworkReceiver ";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("karla", "recevie network or wifi broadcast(" + WifiManager.WIFI_STATE_CHANGED_ACTION + ")");
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    LogUtil.println(TAG + "onReceive ", "wifi has been closed");
                    if (Constants.isBind){
                        WifiUtil.getInstance(context).openWifi();
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    LogUtil.println(TAG + "onReceive ", "wifi has been opened");
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            if (!Constants.isBind){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    LogUtil.println(TAG + "onReceive ", "wifi has been disconnected");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    LogUtil.println(TAG + "onReceive ", "wifi has been connected to" + wifiInfo.getSSID());
                }
                LogUtil.println(TAG + "onReceive", "info.getState = " + info.getState());
                NetworkAction.getInstance(context).receiveMessage(info);
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (Constants.isBind){
                NetworkInfo activeNetworkInfo = WifiUtil.getActiveNetworkInfo(context);
                if (activeNetworkInfo != null) {
                    if (ConnectivityManager.TYPE_WIFI == activeNetworkInfo.getType()) {
                        LogUtil.println(TAG + "onReceive ","activeNetworkInfo is wifi");
                        LogUtil.println(TAG + "onReceive ","更新sip");
                    } else if (ConnectivityManager.TYPE_ETHERNET == activeNetworkInfo.getType()) {
                        LogUtil.println(TAG + "onReceive ","activeNetworkInfo is ethernet");
                        LogUtil.println(TAG + "onReceive ","更新sip");
                    } else {
                        LogUtil.println(TAG + "onReceive ","activeNetworkInfo is others and type = " + activeNetworkInfo.getType());
                    }
                } else {
                    LogUtil.println(TAG + "onReceive ","activeNetworkInfo is null,need to connect wifi");
                    LogUtil.println(TAG + "onReceive ","连接wifi");
                }
                Constants.isPjSipEnabled = false;
                NetworkAction.getInstance(context).receiveMessage(activeNetworkInfo);
            }

        }

    }
}

