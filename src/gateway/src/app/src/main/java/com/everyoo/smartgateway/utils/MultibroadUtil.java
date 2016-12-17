package com.everyoo.smartgateway.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.everyoo.smartgateway.everyoocore.networkobserver.IndicatorLigthAction;
import com.everyoo.smartgateway.smartgateway.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by chaos on 2016/3/16.
 * 广播实体类
 */
public class MultibroadUtil {

    private final String TAG = "MultiBroadUtil ";
    private WifiManager wifiManager;
    private NetworkInterface networkInterface;
    private Context mContext;

    public MultibroadUtil(Context context) {
        mContext = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    /**
     * 判断是否存在网络接口，热点通信，只有在wlan0的网络接口下才能进行通信，而网络接口表系统会定时刷新
     *
     * @return
     */
    private boolean isGetWlan0Interface() {
        Enumeration<NetworkInterface> enumeration;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                networkInterface = enumeration.nextElement();
                if (networkInterface.getName().equals(Constants.WIFI_WLAN)) {
                    if (networkInterface.getInterfaceAddresses().size() != 0) {
                        LogUtil.println(TAG + "isInterfaceExisted", "there is exist wlan0");
                        return true;
                    } else {
                        LogUtil.println(TAG + "isInterfaceExisted", " there is inexist wlan0");
                        return false;
                    }
                } else {
                    LogUtil.println(TAG + "isInterfaceExisted", " the interface is " + networkInterface.getName());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isWlan0Existed() {
        int times = 0;
        while (times < 30) {
            if (isGetWlan0Interface()) {
                return true;
            } else {
                try {
                    Thread.sleep(2000);
                    times++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;

    }

    /**
     * 接收广播并发送指令
     */
    public boolean isReceivedMessage() {
        String message = null;
        try {
            byte[] buf = new byte[1024];
            DatagramSocket datagramSocket = new DatagramSocket(Constants.MULTI_BROADCAST_PORT);
        //  InetAddress inetAddress = InetAddress.getByName(Constants.MULTI_BROADCAST_IP);
            DatagramPacket datagramPacket ;
            while (true) {
                datagramPacket = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(datagramPacket);
                IndicatorLigthAction.binding(mContext);
                System.out.println("message id = " + datagramPacket.getAddress().getHostAddress());
                System.out.println("local ip = " + GetHostIpUtil.getLocalHostIp());
                if (datagramPacket.getAddress().getHostAddress().equals(GetHostIpUtil.getLocalHostIp())) {
                  //  WifiUtil.getInstance(mContext).closeWifi();
                    continue;
                }
                message = new String(datagramPacket.getData()).trim();
                LogUtil.println(TAG + "receiveAndSendMessage ", "message = " + message);
                if (isFormat(message)) {
                    byte[] result = getFeedbackMsg(true);
                    datagramPacket = new DatagramPacket(result, result.length, datagramPacket.getAddress(), Constants.MULTI_BROADCAST_PORT); // android反馈报文
                    datagramSocket.send(datagramPacket);
                    datagramSocket.close();
                    return true;
                } else {  // 数据不完整或数据格式有问题
                    LogUtil.println(TAG + "receiveAndSendMessage", " message's format is error");
                    byte[] result = getFeedbackMsg(false);
                    datagramPacket = new DatagramPacket(result, result.length, datagramPacket.getAddress(), Constants.MULTI_BROADCAST_PORT); // android反馈报文
                    datagramSocket.send(datagramPacket);
                    IndicatorLigthAction.waitingBind(mContext);
                    continue;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析组播数据包
     *
     * @param msg
     */
    private boolean isFormat(String msg) {
        LogUtil.println(TAG + "parseMessage", "开始解析数据");
        if (!TextUtils.isEmpty(msg)) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                Constants.wifiSsid = jsonObject.optString("wifi_ssid");
                Constants.wifiPwd = jsonObject.optString("wifi_pwd");
                Constants.userId = jsonObject.optString("user_id");
                //  if (!Constants.userId.equals("") && !Constants.wifiSsid.equals("") && !Constants.wifiPwd.equals("")) {
                if (!Constants.userId.equals("") && !Constants.wifiSsid.equals("")) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else {
        }

        return false;
    }

    /**
     * 封装返回数据
     *
     * @param isSuccess
     * @return
     */
    public byte[] getFeedbackMsg(boolean isSuccess) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (isSuccess) {
                jsonObject.put("result", 1);// 成功result返回1
            } else {
                jsonObject.put("result", 0); // 失败result返回0
            }
            jsonObject.put("gateway_sn", Constants.gatewaySn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString().getBytes();
    }
}
