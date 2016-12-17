package com.everyoo.smartgateway.everyoocore.upgrade;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.AppInfoUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.UpdateUtil;

/**
 * Created by chaos on 2016/7/4.
 */
public class UpgradeService extends Service {

    private final String TAG = "UpgradeService ";
    private EveryooHttp mHttp;
    private String packageName;
    private int localAppVersion;
    private UpdateUtil updateUtil;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (localAppVersion < msg.arg1) {
                upgrade(msg.obj.toString());
            } else {
                LogUtil.println(TAG + "handler", "localAppVersion is not less than web app and localAppVersion = " + localAppVersion + " and web version = " + msg.arg1);
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.println(TAG + "onStartCommand", "service has been started");
        mHttp= InitApplication.mHttp;
        updateUtil = new UpdateUtil(this);
        packageName = AppInfoUtil.getPackageName(this);
        localAppVersion = AppInfoUtil.getLocalVersion(this);
        mHttp.getAppVersion(UpgradeService.this,packageName, handler);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 升级并下载
     *
     * @param appPath
     */
    public void upgrade(final String appPath) {
        LogUtil.println(TAG + "upgrade", "appPath = " + appPath);
        if (!TextUtils.isEmpty(appPath)) {
            updateUtil.downloadApp(appPath);
        }
    }
}



