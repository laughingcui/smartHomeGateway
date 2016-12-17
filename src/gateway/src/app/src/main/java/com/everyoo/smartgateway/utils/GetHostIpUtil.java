package com.everyoo.smartgateway.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by chaos on 2016/3/17.
 */
public class GetHostIpUtil {


    /**
     * 获取本机网络地址
     *
     * @return
     */
    public static String getExternalNetIp() {
        String IP = "";
        try {
            String address = "http://common.api.everyoo.com/ip";
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setUseCaches(false);

            //if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                // 将流转化为字符串
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                String tmpString = "";
                StringBuilder retJSON = new StringBuilder();
                while ((tmpString = reader.readLine()) != null) {
                    retJSON.append(tmpString + "\n");
                }

                JSONObject jsonObject = new JSONObject(retJSON.toString());
               // String code = jsonObject.getString("code");
                /*if (code.equals("0")) {*/
                  //  JSONObject data = jsonObject.getJSONObject("data");

                  /*  IP = data.getString("ip") + "(" + data.getString("country")
                            + data.getString("area") + "区"
                            + data.getString("region") + data.getString("city")
                            + data.getString("isp") + ")";*/
                    IP = jsonObject.getString("ip");
                    Log.e("提示", "您的IP地址是：" + IP);
                /*} else {*/
                   /* IP = "";
                    Log.e("提示", "IP接口异常，无法获取IP地址！");*/
                //}
           // } else {
               /* IP = "";
                Log.e("提示", "网络连接异常，无法获取IP地址！");*/
           // }
        } catch (Exception e) {
            IP = "";
            Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
        }
        return IP;
    }

    /*public String getExternalIp(){
        String url = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
        AsyncHttpClient client = new SyncHttpClient();
        client.setTimeout(10);
        RequestParams params = new RequestParams();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(bytes));
                    LogUtil.println("GetHostIpUtil getExternalIp","jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == 0){
                        JSONObject data = jsonObject.optJSONObject("data");
                        String ip = data.optString("ip");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }*/

    /**
     * 获取本机本地地址（支持以太网和wifi）
     *
     * @return
     */
    public static String getLocalHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses(); addressEnumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = addressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        System.out.println("local host is = " + inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "127.0.0.1";
    }

    /**
     * 获取本机本地wifi地址
     *
     * @param context
     * @return
     */
    public static String getWifiIp(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            //  wifiManager.setWifiEnabled(true);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            System.out.println("wifi ip address = " + intToIp(ipAddress));
            return intToIp(ipAddress);

        }
        return "127.0.0.1";
    }

    private static String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }
}
