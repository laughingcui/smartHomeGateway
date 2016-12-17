package com.everyoo.smartgateway.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.everyoo.smartgateway.smartgateway.Constants;
import java.lang.reflect.Method;

/**
 * Created by chaos on 2016/6/16.
 * 热点实体类
 */
public class ApUtil {

    private final String TAG = "ApUtil ";
    private WifiUtil wifiUtil;
    private WifiManager wifiManager;

    public ApUtil(Context context){
        wifiUtil = WifiUtil.getInstance(context);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 创建热点
     */
    public void startAp() {
        wifiUtil.closeWifi();
        closeAp(); // 如果默认打开热点模式，先关闭
        LogUtil.println(TAG + "startAp","open ap ");
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = Constants.AP_SSID;
            netConfig.preSharedKey = "";
            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.NONE);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);// 密码长度为8-63
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            method.invoke(wifiManager, netConfig, true);
            LogUtil.println(TAG + "startAp ", "start access point");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭热点模式
     */
    public void closeAp() {
        if (isWifiApEnabled()) {
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(wifiManager, config, false);
                LogUtil.println(TAG + "closeAp", "关闭ap模式");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 调用反射方法，判断热点是否开启
     * @return
     */
    private  boolean isWifiApEnabled() {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
