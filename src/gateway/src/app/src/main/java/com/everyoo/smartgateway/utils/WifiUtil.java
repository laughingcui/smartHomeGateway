package com.everyoo.smartgateway.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.everyoo.smartgateway.smartgateway.Constants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

/**
 * Created by chaos on 2015/10/7.
 */
public class WifiUtil {

    private final String TAG = "WifiUtil ";
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;

    private static WifiUtil wifiUtil;

    private Context mContext;

    private ScanResult scanResult;

    // 构造器
    private WifiUtil(Context context) {
        mContext = context;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public static synchronized WifiUtil getInstance(Context context) {
        if (wifiUtil == null) {
            wifiUtil = new WifiUtil(context);
        }
        return wifiUtil;
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void forgetWifi() {
        List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
        if (configurations != null && configurations.size() > 0) {
            for (int i = 0; i < configurations.size(); i++) {
                System.out.println("configuration ssid = " + configurations.get(i).SSID);
                mWifiManager.removeNetwork(configurations.get(i).networkId);
            }
            mWifiManager.saveConfiguration();
        } else {
            System.out.println("configurations is null");
        }
    }

    public void forgetWifi(String ssid) {
        if (Build.VERSION.SDK_INT >= JELLY_BEAN_MR1) {
            ssid = "\"" + ssid + "\"";
        }
        List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
        if (configurations != null && configurations.size() > 0) {
            for (int i = 0; i < configurations.size(); i++) {
                if (configurations.get(i).SSID.equals(ssid)) {
                    mWifiManager.removeNetwork(configurations.get(i).networkId);
                }
            }
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock(int lockType, String tag) {
        mWifiLock = mWifiManager.createWifiLock(lockType, tag);
        mWifiLock.setReferenceCounted(false);// false：不计数锁，即无论调用多少次acquire()，只要调用一次release()即可解锁。
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    // 判断网络是否连接
    public boolean isConnecting(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTING) {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描wifi列表
     */
    public void startScan() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    /*public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }*/

    // 添加一个网络并连接
    public int addNetwork(WifiConfiguration wcg) {
        int networkId = mWifiManager.addNetwork(wcg);
        System.out.println("a--" + networkId);
        return networkId;
    }

    public boolean enableNetwork(int networkId, boolean disableOthers) {
        boolean enableNetwork = mWifiManager.enableNetwork(networkId, disableOthers);
        if (enableNetwork) {
            mWifiManager.saveConfiguration();
        }
        System.out.println("b--" + enableNetwork);
        return enableNetwork;
    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

//然后是一个实际应用方法，只验证过没有密码的情况：

    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    // 判断某个网络是否存在
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }

        }
        return null;
    }

    public boolean isExsistsWifiList(String SSID, List<ScanResult> mWifiList) {
        //List<ScanResult> mList = getWifiList();
        if (mWifiList != null) {
            for (ScanResult scanResult : mWifiList) {
                if (scanResult.SSID.equals(SSID)) {
                    this.scanResult = scanResult;
                    return true;
                }
            }
        }
        return false;
    }

    public int parseWifiEncryption(ScanResult scanResult) {
        String encryption = scanResult.capabilities;
        if (encryption != null) {
            if (encryption.contains("WPA") || encryption.contains("wpa")) {
                LogUtil.println(TAG + "judgeEncryption", " wap 方式加密");
                return 3;
            } else if (encryption.contains("WEB") || encryption.contains("web")) {
                LogUtil.println(TAG + "judgeEncryption", " web 方式加密");
                return 2;
            } else {
                return -1;
            }
        } else {
            LogUtil.println(TAG + "judgeEncryption", " 没有加密");
            return 1;
        }
    }


    // 判断当前是否有网络
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            if (mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean connectWifi(String ssid, String pwd, int encriptionType) {
        openWifi();
        WifiConfiguration configuration = createWifiInfo(ssid, pwd, encriptionType);
        boolean enable = enableNetwork(addNetwork(configuration), false);
        return enable;
    }

    public boolean isWifiConnectingOrConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mNetworkInfo != null) {
            return mNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    public boolean isWifiConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mNetworkInfo != null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
            System.out.println(TAG + "isWifiConnected wifi is connected");
            return true;
        }
        System.out.println(TAG + "isWifiConnected wifi is disconnected");
        return false;
    }


    public String getSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getSSID();
        }
        return null;
    }

    public static String ssidConvert(String ssid) {
        if (!TextUtils.isEmpty(ssid)) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN_MR1 && ssid.length() >= 2) {
                return ssid.substring(1, ssid.length() - 1);
            }
            return ssid;
        }
        return null;

    }

    /**
     * 判断wifi的加密方式
     */
    public void parseWifiEncryption() {
        int times = 0;
        int LAST_TIMES = 5;
        while (times < LAST_TIMES) {
            times++;
            openWifi();
            startScan();
            List<ScanResult> scanResultList = getWifiList();
            if (scanResultList != null) {
                for (ScanResult scanResult : scanResultList) {  // 如果没有扫描到，默认加密方式是WPA
                    LogUtil.println(TAG , scanResult.SSID+">>>>>>"+Constants.wifiSsid );
                    if (Constants.wifiSsid != null && Constants.wifiSsid.equals(scanResult.SSID)) {
                        String encryption = scanResult.capabilities;
                        if (encryption != null) {
                            if (encryption.contains("WPA") || encryption.contains("wpa")) {
                                LogUtil.println(TAG + "judgeEncryption", " wap 方式加密");
                                Constants.wifiEncription = 3;
                                return;
                            } else if (encryption.contains("WEB") || encryption.contains("web")) {
                                LogUtil.println(TAG + "judgeEncryption", " web 方式加密");
                                Constants.wifiEncription = 2;
                                return;
                            }
                        } else {
                            LogUtil.println(TAG + "judgeEncryption", " 没有加密");
                            Constants.wifiEncription = 1;
                            return;
                        }
                    } else {
                        LogUtil.println(TAG + "judgeEncryption", "用户传来的wifi信息不存在或wifi列表中没有发现");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                LogUtil.println(TAG + "judgeEncryption", " scanResult is null");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    /**
     * 连接wifi
     */
    public void connectWifi() {
        boolean isConnected = false;
        int connectTimes = 0;
        while (!isConnected && connectTimes < 10) {
            connectTimes++;
            isConnected = connectWifi(Constants.wifiSsid, Constants.wifiPwd, Constants.wifiEncription);
            if (!isConnected) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo;
        } else {
            LogUtil.println("WifiUtil activeNetworkInfo", "activeNetworkInfo is null");
            return null;
        }
    }

    public static NetworkInfo getWifiNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public static NetworkInfo getEthernetNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
    }

    static int exitCode = -1;

    public synchronized static boolean isNetworkConnectedByPing() {
        System.out.println("start ping");
        Runtime runtime = Runtime.getRuntime();
        try {
            // Process pingProcess = runtime.exec("/system/bin/ping -c 1 www.baidu1234556.com");
            final Process pingProcess = runtime.exec("/system/bin/ping -c 1 -w 2 14.215.177.38");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("exitCode = " + exitCode);
                    System.out.println("timer destroy");
                    pingProcess.destroy();
                    cancel();
                }
            }, 500);

            System.out.println("begin waitFor()");
            exitCode = pingProcess.waitFor();
            if (timer != null) {
                System.out.println("主动销毁timer");
                timer.cancel();
            }
            System.out.println("pingProcesser = " + pingProcess);
            if (pingProcess != null) {
                pingProcess.destroy();
            }
            System.out.println("end ping and exitCode = " + exitCode);
            return (exitCode == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public WifiConfiguration getConfiguration(String ssid) {
        if (Build.VERSION.SDK_INT >= JELLY_BEAN_MR1) {
            ssid = "\"" + ssid + "\"";
        }
        if (mWifiConfiguration != null && mWifiConfiguration.size() > 0) {
            for (int i = 0; i < mWifiConfiguration.size(); i++) {
                if (ssid.equals(mWifiConfiguration.get(i).SSID)) {
                    return mWifiConfiguration.get(i);
                }
            }
        }
        return null;
    }

    public synchronized void networkConnect(final String ssid, final String pwd, final boolean isEverConnected) {
        int scanTimes = 0;
        int enableTimes = 0;
        boolean isWifiExist = false;
        boolean isEnableNetwork = false;
        while (!isWifiExist && scanTimes < 5) {
            openWifi();
            startScan();
            isWifiExist = isExsistsWifiList(ssid, mWifiList);
            scanTimes++;
        }
        LogUtil.toast(mContext,TAG + "networkConnect isWifiExist = " + isWifiExist + " scanTimes = " + scanTimes);

        while (isWifiExist && !isEnableNetwork && enableTimes < 5) {
            WifiConfiguration wifiConfiguration = getConfiguration(ssid);
            if (isEverConnected && wifiConfiguration != null) {
                LogUtil.toast(mContext,TAG + "networkConnect wifiConfiguration != null");
                isEnableNetwork = enableNetwork(wifiConfiguration.networkId, false);
            } else {
                LogUtil.toast(mContext,TAG + "networkConnect wifiConfiguration == null");
                int encryption = parseWifiEncryption(scanResult);
                if (encryption != -1) {
                    wifiConfiguration = createWifiInfo(ssid, pwd, encryption);
                    isEnableNetwork = enableNetwork(addNetwork(wifiConfiguration), false);
                    LogUtil.toast(mContext,TAG + "networkConnect isEnableNetwork = " + isEnableNetwork);
                } else {
                    System.out.println("当前wifi加密方式网关不支持");
                }
            }
            LogUtil.println(TAG + "networkConnect", "isEnableNetwork = " + isEnableNetwork + "enableTimes = " + enableTimes);
            if (!isEnableNetwork) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            enableTimes++;
        }
        if (enableTimes >= 5) {
            LogUtil.toast(mContext,TAG + "networkConnect wifi pwd maybe error ");
            LogUtil.println(TAG + "networkConnect", "wifi密码可能错误");
        }

    }







    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }
}
