package com.everyoo.smartgateway.smartgateway;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.everyoo.smartgateway.everyoocore.bugs.BugTagsAction;
import com.everyoo.smartgateway.everyoocore.bugs.CrashHandler;
import com.everyoo.smartgateway.everyoocore.networkobserver.IndicatorLigthAction;
import com.everyoo.smartgateway.everyoocore.timer.RegisterTimingAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sp.SPHelper;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.EveryooSql;
import com.everyoo.smartgateway.everyoozwave.zwavesdk.MessageManager;
import com.everyoo.smartgateway.utils.FileUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.TimeUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by chaos on 2016/6/16.
 */
public class InitApplication extends Application {

    private final String TAG = "InitApplication ";
    public static InitApplication instance = null;
    public static EveryooHttp mHttp;
    public static EveryooSql mSql;
    public static EventBus mEventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = InitApplication.this;
        LogUtil.initXlog(instance);
        mHttp = EveryooHttp.init(getApplicationContext());
        mSql = EveryooSql.init(getApplicationContext());
        mEventBus = EventBus.getDefault();
        initData();
        TimeUtil.setAutoTimeAndZone(this);// 设置自动获取日期和时间，时区设置成东八区，时间格式为24h
        if (Constants.isBind) {
            networkProcess(this);
            ServiceAction.startServices(this, true);
            //RegisterTimingAction.registeUpgradeAlarm(this);
        } else {
            ServiceAction.startServices(this, false);
        }
        CrashHandler.getInstance().init(this);
        DaemonAction.daemonAction(this);
        RegisterTimingAction.registeNetworkHeart(this, Constants.ACTION_NETWORK_HEART);
        MessageManager.getInstance(this);
    }


    private void initData() {
        Constants.gatewaySn = FileUtil.read();
        Constants.AP_SSID = Constants.apSsid + Constants.gatewaySn;
        /*new BindInfoAction().readBindInfo(this);*/
        SPHelper.getInstance().readBindInfo(this);
    }


    public void networkProcess(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkInfo networkInfo = WifiUtil.getActiveNetworkInfo(context);
                if (networkInfo != null) {
                    if (WifiUtil.isNetworkConnectedByPing()) {
                        LogUtil.toast(context, "网络连接并且有网");
                        //  ServiceAction.startPjService(context);
                        mHttp.updateAppVersion(context);
                        BugTagsAction.bugCollect(InitApplication.this);
                        TimeUtil.getNetworkTimeAndSetSystemTime();
                    } else {
                        IndicatorLigthAction.networkConnectedButNoData(InitApplication.this);
                        LogUtil.toast(context, "网络虽然连接但是没有网络数据");
                        int networkType = networkInfo.getType();
                        if (ConnectivityManager.TYPE_WIFI == networkType) {
                            LogUtil.toast(context, "wifi虽然连接但是没有网络数据");
                        } else if (ConnectivityManager.TYPE_ETHERNET == networkType) {
                            LogUtil.toast(context, "Ethernet虽然连接但是没有网络数据");
                        } else {
                            System.out.println("networkType = " + networkType);
                        }
                    }
                } else {
                    LogUtil.toast(context, "没有网络连接");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            IndicatorLigthAction.noNetworkConnected(InitApplication.this);
                        }
                    });
                    WifiUtil.getInstance(context).networkConnect(Constants.wifiSsid, Constants.wifiPwd, true);
                }
            }
        }).start();
    }

}
