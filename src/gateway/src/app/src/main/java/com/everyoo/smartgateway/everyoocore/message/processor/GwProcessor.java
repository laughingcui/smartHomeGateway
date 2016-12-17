package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.message.filter.FilterMessage;
import com.everyoo.smartgateway.everyoocore.message.filter.Helper;
import com.everyoo.smartgateway.everyoocore.message.impl.GwProcessorImpl;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sp.SPHelper;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.CtrlBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.GwBindDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.ActionId;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.smartgateway.MainActivity;
import com.everyoo.smartgateway.utils.ApUtil;
import com.everyoo.smartgateway.utils.CleanAppDataUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.RestartAppUtil;
import com.everyoo.smartgateway.utils.UpdateUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/6/16.
 */
public class GwProcessor implements GwProcessorImpl {

    private final String TAG = "GwProcessor ";
    private Context mContext;
    private BindBean bindBean;
    private EveryooHttp mHttp;
    public static boolean isModifyWifi = false;
    private final int UPLOAD_FILE = 3;

    public GwProcessor(Context context) {
        mContext = context;
        mHttp = InitApplication.mHttp;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.UNBIND_SUCCESS:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.unbind(Constants.UNBIND_SUCCESS, bindBean.getUserId()), bindBean.getUserId());
                    reset();
                    break;
                case Constants.UNBIND_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.unbind(Constants.UNBIND_FAILED, bindBean.getUserId()), bindBean.getUserId());
                    break;
                case Constants.PULL_DATE_SUCCESSFUL:
                    saveUserInfo(msg.obj.toString());
                    break;
                case Constants.PULL_DATE_FAILED:

                    break;
            }
        }
    };

    @Override
    public void processor(BindBean bindBean) {
        if (bindBean != null) {
            this.bindBean = bindBean;
            int type = bindBean.getType();
            if (type == Constants.UNBIND) {
                mHttp.gatewayUnbind(mContext, bindBean.getUserId(), handler);
            } else if (type == Constants.WEB_SUBUSER) {
                //  gwHttp.getUserInfo(handler);
                InitApplication.mEventBus.post(new EventMessage(Constants.ACTION_DATA_SYNCHRONIZED, null, null));
            } else if (type == Constants.WIFI_MODIFY) {
                if (!TextUtils.isEmpty(Constants.wifiSsid) && !TextUtils.isEmpty(Constants.wifiPwd)) {
                    LogUtil.println(TAG + "processor", "begin modify wifi");
                    isModifyWifi = true;
                    modifyWifi();
                    modifyTiming();
                } else {
                    BroadcastAction.gatewayToSip(PjsipMsgAction.modifyWifi(bindBean.getUserId(), Constants.WIFI_MODIFY, Constants.WIFI_FORMAT_ERROR), bindBean.getUserId());
                }
            } else if (type == UPLOAD_FILE) {
                InitApplication.mHttp.uploadFile(mContext);
            } else if (type == Constants.VERSION_UPDATE) {
                //BroadcastAction.gatewayToSip(PjsipMsgAction.versionUpdate(bindBean.getUserId(), Constants.VERSION_UPDATE, 1), bindBean.getUserId());
                if (!Constants.isInstall) {
                    Constants.isInstall = true;
                    mContext.sendBroadcast(new Intent(Constants.ACTION_START_UPDATE));
                }
            }
        } else {
            LogUtil.println(TAG + "processor", "bindBean is null");
        }
    }

    @Override
    public void reset() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Constants.isBind = false;
                BroadcastAction.gatewayToSdkMsg(Helper.generateMsg(new CtrlBean(0, null, ActionId.USB_DONGLE_RESET, null, null), null), mContext);
                //   MessageManager.controller.clearDongle();
                CleanAppDataUtil.cleanLocalData(mContext);
                WifiUtil.getInstance(mContext).forgetWifi();
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RestartAppUtil.restartApp(mContext, 0);
            }
        }).start();
    }


    /**
     * 保存用户信息
     *
     * @param message
     */
    public void saveUserInfo(final String message) {
        final GwBindDao dao = (GwBindDao) InitApplication.mSql.getSqlDao(GwBindDao.class);
        if (message != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jsonArray = new JSONArray(message);
                        if (jsonArray != null && jsonArray.length() > 0) {
                            ArrayList<BindBean> bindBeans = new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                BindBean bindBean = new BindBean();
                                bindBean.setUserId(jsonArray.optJSONObject(j).optString("userid"));
                                bindBean.setUserSip(jsonArray.optJSONObject(j).optString("usersip"));
                                bindBean.setRole(jsonArray.optJSONObject(j).optInt("role"));
                                bindBean.setBindTime(jsonArray.optJSONObject(j).optString("date"));
                                if (!bindBean.getUserId().equals("") && !bindBean.getUserSip().equals("") && (bindBean.getRole() == Constants.MASTER_ROLE || bindBean.getRole() == Constants.SUB_ROLE)) {
                                    bindBeans.add(bindBean);
                                } else {
                                    LogUtil.println(TAG + "userInfoProcessor ", "bindBean is incompletely");
                                }
                            }
                            dao.delete();
                            dao.create(bindBeans);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            LogUtil.println(TAG + "userInfoProcessor", "message is null");
        }
    }


    private void modifyWifi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ApUtil(mContext).closeAp();
                WifiUtil.getInstance(mContext).openWifi();
                WifiUtil.getInstance(mContext).forgetWifi();
                //  mContext.sendBroadcast(new Intent(Constants.ACTION_NETWORK_HEART));
                WifiUtil.getInstance(mContext).networkConnect(Constants.modifyWifiSSID, Constants.modifyWifiPwd, false);
            }
        }).start();
    }

    private synchronized void modifyTiming() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 15; i++) {
                    try {
                        Thread.sleep(4 * 1000);
                        LogUtil.println(TAG + "modifyTiming", "wifi is connecting");
                        String ssid = WifiUtil.ssidConvert(WifiUtil.getInstance(mContext).getSSID());
                        boolean isSsidSame = (!TextUtils.isEmpty(ssid) && ssid.equals(Constants.modifyWifiSSID));
                        if (WifiUtil.getInstance(mContext).isWifiConnected(mContext) && isSsidSame && Constants.isPjSipEnabled) {
                            LogUtil.println(TAG + "modifyTiming", "wifi is connected");
                            /*new BindInfoAction().modifyWifi(mContext);*/
                            SPHelper.getInstance().modifyWifi(mContext);
                            BroadcastAction.gatewayToSip(PjsipMsgAction.modifyWifi(bindBean.getUserId(), Constants.WIFI_MODIFY, Constants.WIFI_CONNECT_SUCCESS), bindBean.getUserId());
                            Constants.wifiSsid = Constants.modifyWifiSSID;
                            Constants.wifiPwd = Constants.modifyWifiPwd;
                            isModifyWifi = false;
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isModifyWifi = false;
                WifiUtil.getInstance(mContext).forgetWifi(Constants.modifyWifiSSID);
                LogUtil.println(TAG + "modifyTiming", "wifi is disconnected");
                mContext.sendBroadcast(new Intent(Constants.ACTION_NETWORK_HEART));
                if (Constants.isPjSipEnabled) {
                    BroadcastAction.gatewayToSip(PjsipMsgAction.modifyWifi(bindBean.getUserId(), Constants.WIFI_MODIFY, Constants.WIFI_CONNECT_FAILED), bindBean.getUserId());
                }
            }
        }).start();
    }

}
