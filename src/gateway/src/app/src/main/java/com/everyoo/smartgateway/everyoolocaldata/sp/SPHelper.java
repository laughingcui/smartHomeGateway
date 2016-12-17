package com.everyoo.smartgateway.everyoolocaldata.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.GwBindDao;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.FileUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.TimeUtil;

import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/11/16.
 */
public class SPHelper implements SPImpl {
    private static volatile SPHelper mInstance=null;
    private SPHelper(){
    }
    public static SPHelper getInstance(){
        if(mInstance==null){
            synchronized (SPHelper.class){
                if(mInstance==null){
                    mInstance=new SPHelper();
                }
            }
        }
        return mInstance;
    }
    @Override
    public void saveBindInfo(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = context.getSharedPreferences("WIFISIPINFO", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("wifi_ssid", Constants.wifiSsid);
                editor.putString("wifi_pwd", Constants.wifiPwd);
                editor.putString("sip_account", Constants.sipAccount);
                editor.putString("sip_pwd", Constants.sipPwd);
                editor.putString("user_sip", Constants.masterSip);
                editor.putString("user_id",Constants.userId);
                editor.putInt("wifi_encryption", Constants.wifiEncription);
                editor.commit();
                ((GwBindDao) InitApplication.mSql.getSqlDao(GwBindDao.class)).create(new BindBean(Constants.userId, Constants.masterSip, Constants.MASTER_ROLE, TimeUtil.currentTime()));
                LogUtil.println(TAG + "saveBindInfoSharedPreferences", "sipAccount:" + Constants.sipAccount + "|" + "sipPwd:" + Constants.sipPwd + "|" + "wifiSsid:" + Constants.wifiSsid + "|" + "wifiPwd:" + Constants.wifiPwd + "|userSip = " + Constants.masterSip);
            }
        }).start();
    }

    @Override
    public void readBindInfo(Context context) {
        Constants.gatewaySn = FileUtil.read();
        Constants.AP_SSID = Constants.apSsid + Constants.gatewaySn;

        SharedPreferences sp = context.getSharedPreferences("WIFISIPINFO", Context.MODE_PRIVATE);
        Constants.isBind = !sp.getAll().isEmpty();
        if (Constants.isBind) {
            Constants.wifiSsid = sp.getString("wifi_ssid", null);
            Constants.wifiPwd = sp.getString("wifi_pwd", null);
            Constants.wifiEncription = sp.getInt("wifi_encryption", 3);
            Constants.sipAccount = sp.getString("sip_account", null);
            Constants.sipPwd = sp.getString("sip_pwd", null);
            Constants.masterSip = sp.getString("user_sip", null);
            Constants.userId = sp.getString("user_id", null);
            LogUtil.println(TAG + "readSharedPreferences", "userSip = " + Constants.masterSip + "userid = " + Constants.userId);
        }
    }

    @Override
    public void modifyWifi(Context context) {
        SharedPreferences sp = context.getSharedPreferences("WIFISIPINFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("wifi_ssid", Constants.wifiSsid);
        editor.putString("wifi_pwd", Constants.wifiPwd);
        editor.commit();
    }

    @Override
    public void saveMode(int currentMode,Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WIFISIPINFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mode", currentMode);
        editor.commit();
    }

    @Override
    public float powerPorcessor(String nodeId, String value, Context context) {
        if (TextUtils.isEmpty(nodeId)){
            System.out.println("nodeId is empty");
            return -1;
        }
        float currentValue = Float.parseFloat(value);
        if (currentValue >= 0){
            SharedPreferences preferences = context.getSharedPreferences(nodeId,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            float initValue = preferences.getFloat(nodeId,-1);
            System.out.println("initValue = " + initValue);
            if (initValue < 0){  // 第一次上报
                editor.putFloat(nodeId,currentValue);
                editor.putFloat(nodeId+"lastValue",currentValue);
                editor.commit();
                return -1;
            }else {
                float lastValue = preferences.getFloat(nodeId+"lastValue",0);
                if (currentValue <= lastValue){
                    System.out.println("插座本次上报的电量 <= 上次电量");
                    return -1;
                }else {
                    System.out.println("插座本次上报的电量 >  上次电量");
                    float usedPower = currentValue - initValue;
                    System.out.println("usedPower = " + new DecimalFormat("###,###,##0.0000").format(usedPower));
                    if (usedPower > 0){
                        System.out.println("userPower > 0");
                        editor.putFloat(nodeId+"lastValue",currentValue);
                        editor.commit();
                        return usedPower;
                    }else {
                        System.out.println("usedPower <= 0 ");
                        return -1;
                    }
                }
            }
        }else {
            System.out.println("currentValue < 0");
            return -1;
        }
    }

    @Override
    public void deleteData(Context context, int nodeId) {
        SharedPreferences preferences = context.getSharedPreferences("power_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(String.valueOf(nodeId).trim());
        editor.commit();
    }








}
