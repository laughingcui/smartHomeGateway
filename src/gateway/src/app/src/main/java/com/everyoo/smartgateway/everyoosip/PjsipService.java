package com.everyoo.smartgateway.everyoosip;

import android.app.Service;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.networkobserver.IndicatorLigthAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.GwBindDao;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pjsip.help.MyAccount;
import org.pjsip.help.MyApp;
import org.pjsip.help.MyAppOberver;
import org.pjsip.help.MyBuddy;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.SendInstantMessageParam;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_state;

import java.util.ArrayList;

public class PjsipService extends Service implements MyAppOberver {
    private final static String TAG = "MyPjsipService ";
    public static MyApp app = null;
    public static MyAccount account = null;
    public static AccountConfig accCfg = null;
    public static PjsipService instance = null;
    private EveryooHttp mHttp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = PjsipService.this;
        mHttp = InitApplication.mHttp;
        sipConfig();
        pjsipLogin(Constants.sipAccount, Constants.sipPwd, Constants.sipDomain);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        InitApplication.mEventBus.register(this);
        return START_NOT_STICKY;
    }

    public static void setRegistration(final boolean isRegistration) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    try {
                        account.setRegistration(isRegistration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * configure sip running environment
     */
    public void sipConfig() {
        if (app == null) {
            app = new MyApp();
            app.init(this, getFilesDir().getAbsolutePath());
            if (app.accList.size() == 0) {
                accCfg = new AccountConfig();
                accCfg.setIdUri("sip:localhost");
                accCfg.getNatConfig().setIceEnabled(true);
                accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
                accCfg.getVideoConfig().setAutoShowIncoming(true);
                account = app.addAcc(accCfg);
            } else {
                account = app.accList.get(0);
                accCfg = account.cfg;
            }
        }
    }

    /**
     * sip account login
     *
     * @param sipName
     * @param sipPassword
     * @param sipDomain
     */
    public static void pjsipLogin(String sipName, String sipPassword, String sipDomain) {
        AccountConfig accCfg = new AccountConfig();
        String acc_id = "sip:" + sipName + "@" + sipDomain + ";transport=tcp";
        String registrar = "sip:" + sipDomain + ";transport=tcp";
        String proxy = "";
        String username = sipName;
        String password = sipPassword;
        accCfg.setIdUri(acc_id);
        accCfg.getRegConfig().setRegistrarUri(registrar);
        AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
        creds.clear();
        if (username.length() != 0) {
            creds.add(new AuthCredInfo("digest", "*", username, 0, password));
        }
        StringVector proxies = accCfg.getSipConfig().getProxies();
        proxies.clear();
        if (proxy.length() != 0) {
            proxies.add(proxy);
        }
        accCfg.getNatConfig().setIceEnabled(true);
        try {
            account.modify(accCfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * send pjsip message
     *
     * @param content
     * @param userId
     */
    public void pjsipSendMsg(final String content, String userId) {
        if (userId == null || userId.equals("")) {
            userId = Constants.userId;
        }
        String buddy_uri = "<sip:" + userId + "@" + Constants.sipDomain + ";transport=tcp" + ">";
        LogUtil.println(TAG + "pjsipSendMsg", " buddy_uri = " + buddy_uri);
        BuddyConfig bCfg = new BuddyConfig();
        bCfg.setUri(buddy_uri);
        bCfg.setSubscribe(false);

        MyBuddy myBuddy = account.addBuddy(bCfg);
        SendInstantMessageParam prm = new SendInstantMessageParam();
        prm.setContent(content);
        try {
            //    myBuddy.create(account, bCfg);
            myBuddy.sendInstantMessage(prm);
            myBuddy.delete();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    //注册状态改名
    @Override
    public void notifyRegState(pjsip_status_code code, String reason, int expiration) {
        Log.e("will", "code: " + code + "reason: " + reason + "expiration: " + expiration);
        regStateProcess(code, reason, expiration);
    }


    private void regStateProcess(pjsip_status_code code, String reason, int expiration) {
        if (reason.equals("OK") && expiration == 300 && code == pjsip_status_code.PJSIP_SC_OK) {
            Constants.isPjSipEnabled = true;
            InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_DATA_SYNCHRONIZED, null, null));
            if (Constants.isSdkEnabled) {
                IndicatorLigthAction.runNormally(PjsipService.this);
            } else {
                IndicatorLigthAction.sdkException(PjsipService.this);
            }
            mHttp.updateGwOnlineStatus(PjsipService.this, Constants.GATEWAY_ONLINE);

        } else {
            Constants.isPjSipEnabled = false;
            mHttp.updateGwOnlineStatus(PjsipService.this, Constants.GATEWAY_OFFLINE);
            sendBroadcast(new Intent(Constants.ACTION_NETWORK_HEART));
            NetworkInfo networkInfo = WifiUtil.getActiveNetworkInfo(PjsipService.this);
            if (networkInfo != null) {
                if (WifiUtil.getInstance(PjsipService.this).isNetworkConnectedByPing()) {
                    IndicatorLigthAction.networkConnectedButNoData(PjsipService.this);
                } else {
                    IndicatorLigthAction.networkConnectedButNoData(PjsipService.this);
                }
            } else {
                IndicatorLigthAction.noNetworkConnected(PjsipService.this);
            }
        }

    }

    //好友状态改变
    @Override
    public void notifyBuddyState(MyBuddy buddy) {
        Log.e("will", "buddy state: " + buddy.getStatusText());

    }

    //加载库的状态改变
    @Override
    public void notifyEpState(Endpoint ep) {
        if (ep.libGetState().equals(pjsua_state.PJSUA_STATE_RUNNING)) {//pjsip already

        }
    }

    /**
     * receive pjsip message
     *
     * @param prm
     */
    @Override
    public void receiveMessage(final OnInstantMessageParam prm) {
        if (prm != null) {
            LogUtil.controlLog(TAG + " receiverMessage ", prm.getMsgBody());
            // LogUtil.println(TAG + " receiverMessage ", prm.getMsgBody());
            //  sendMessage(prm.getMsgBody());
            BroadcastAction.sipToGatewayMsg(prm.getMsgBody(), this);
            IndicatorLigthAction.cmdTransfer(PjsipService.this);
            //   backMessage(prm.getMsgBody());
        } else {
            //LogUtil.println(TAG + "receiveMessage", " prm is equals null");
            LogUtil.controlLog(TAG + "receiveMessage", " prm is equals null");
        }
    }

    /**
     * 上报数据接收广播
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerEventMessage(EventMessage msg) {
        if (Constants.ACTION_UPLOAD_SIP_MESSAGE.equals(msg.getMessageType())) {
            if (!Constants.isPjSipEnabled) {
                return;
            }
            IndicatorLigthAction.cmdTransfer(PjsipService.this);
            LogUtil.controlLog(TAG + "uploadReceiver", "message is " + msg.getMessage());
            ArrayList<BindBean> bindBeens = ((GwBindDao) InitApplication.mSql.getSqlDao(GwBindDao.class)).select();
            if (bindBeens != null) {
                if (TextUtils.isEmpty(msg.getUsrId())) {
                    for (int i = 0; i < bindBeens.size(); i++) {
                        pjsipSendMsg(msg.getMessage(), bindBeens.get(i).getUserId());
                    }
                } else {
                    pjsipSendMsg(msg.getMessage(), msg.getUsrId());
                    for (int i = 0; i < bindBeens.size(); i++) {
                        boolean isExisted = bindBeens.get(i).getUserId().equals(msg.getUsrId());
                        if (isExisted) {
                            bindBeens.remove(i);
                        }
                    }
                    for (int i = 0; i < bindBeens.size(); i++) {
                        LogUtil.controlLog(TAG + "uploadReceiver", "userId is " + bindBeens.get(i).getUserId());
                        pjsipSendMsg(msg.getMessage(), bindBeens.get(i).getUserId());
                    }
                }

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            app.ep.libDestroy_();
        } catch (Exception e) {
            e.printStackTrace();
        }
        InitApplication.mEventBus.unregister(this);
    }

}
