package com.everyoo.smartgateway.everyoocore.message.processor;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.android.volley.VolleyError;
import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.everyoohttp.core.NetCommApi;
import com.everyoo.smartgateway.everyoohttp.core.RequestListener;
import com.everyoo.smartgateway.everyoohttp.core.RequestManager;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineAttriBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DefineActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.DefineAttriDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.GwBindDao;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by chaos on 2016/1/5.
 */
public class HttpService extends Service {

    private final String TAG = "HttpService ";
    private final String TABLE_DEVTYPEATTR = "device_attr";
    private final String TABLE_DEVTYPEACTION = "device_action";
    private final String TABLE_USER = "user";
    private int devAttrVersion = 0;
    private int devActionVersion = 0;
    private int userInfoVersion = 0;
    private DefineActionDao mDao;
    private DefineAttriDao mAttriDao;
    private GwBindDao gwDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int[] num = (int[]) msg.obj;
            devAttrVersion = num[0];
            devActionVersion = num[1];
            userInfoVersion = num[2];
            compareVersion();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = (DefineActionDao) InitApplication.mSql.getSqlDao(DefineActionDao.class);
        mAttriDao = (DefineAttriDao) InitApplication.mSql.getSqlDao(DefineAttriDao.class);
        gwDao = (GwBindDao) InitApplication.mSql.getSqlDao(GwBindDao.class);
        if (isDictionaryEmpty()) {
            LogUtil.println(TAG + "onCreate", "dictionary is empty");
            InitApplication.mHttp.getDictionaryVersion(this, mHandler);
        } else {
            LogUtil.println(TAG + "onCreate", "dictionary is not empty");
        }
    }

    private boolean isDictionaryEmpty() {
        boolean isDefAttrEmpty = mDao.selectCount() <= 0;
        boolean isDefActionEmpty = mDao.selectCount() <= 0;
        return isDefActionEmpty || isDefAttrEmpty;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        InitApplication.mEventBus.register(this);
        return START_NOT_STICKY;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerMessage(EventMessage msg) {
        if (Constants.ACTION_DATA_SYNCHRONIZED.equals(msg.getMessageType())) {
            InitApplication.mHttp.getDictionaryVersion(this, mHandler);
        }
    }

    private void compareVersion() {
        SharedPreferences sp = getSharedPreferences("table_version", MODE_PRIVATE);
        int currentDevAttrVersion = sp.getInt("dev_attr_version", 0);
        int currentDevActionVersion = sp.getInt("dev_action_version", 0);
        int currentUserInfoVersion = sp.getInt("user_info_version", 0);
        SharedPreferences.Editor editor = sp.edit();
        if (currentDevAttrVersion < devAttrVersion) {
            editor.putInt("dev_attr_version", devAttrVersion);
            getDictionary(TABLE_DEVTYPEATTR, currentDevAttrVersion);
        }
        if (currentDevActionVersion < devActionVersion) {
            editor.putInt("dev_action_version", devActionVersion);
            getDictionary(TABLE_DEVTYPEACTION, currentDevActionVersion);
        }
        if (currentUserInfoVersion < userInfoVersion) {
            editor.putInt("user_info_version", userInfoVersion);
            getDictionary(TABLE_USER, currentUserInfoVersion);
        }

        editor.commit();
    }

    private void getDictionary(final String tableName, final int version) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(Constants.gatewaySn)) {
            map.put("gatewayid", Constants.gatewaySn);
        }
        if (!StringUtils.isNullOrEmpty(Constants.sipAccount)) {
            map.put("sipaccount", Constants.sipAccount);
        }
        if (!StringUtils.isNullOrEmpty(Constants.sipPwd)) {
            map.put("sippwd", Constants.sipPwd);
        }
        if (!StringUtils.isNullOrEmpty(tableName)) {
            map.put("about", tableName);
        }
        if (!StringUtils.isNullOrEmpty(version + "")) {
            map.put("version", version);
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(HttpService.this, NetCommApi.HOST + NetCommApi.GET_NEWEST_DICTIONARY, jsonObject, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println(TAG + "getDictionary", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        JSONObject infoObject = jsonObject.optJSONObject("info");
                        JSONArray addArray = infoObject.optJSONArray("add");
                        JSONArray delArray = infoObject.optJSONArray("delete");
                        if (addArray != null && delArray != null) {
                            if (tableName.equals(TABLE_DEVTYPEATTR)) {
                                if (version > 0) {
                                    mAttriDao.delete(defAttrProcess(delArray));
                                }
                                mAttriDao.insert(defAttrProcess(addArray));
                            } else if (tableName.equals(TABLE_DEVTYPEACTION)) {
                                if (version > 0) {
                                    mDao.delete(defActionProcess(delArray));
                                }
                                LogUtil.println(TAG + "getDictionary", "insert device type action");
                                mDao.create(defActionProcess(addArray));
                            } else if (tableName.equals(TABLE_USER)) {
                                if (version > 0) {
                                    gwDao.delete(userInfoProcess(delArray));
                                } else if (version == 0) {
                                    gwDao.delete(Constants.userId);
                                }
                                gwDao.create(userInfoProcess(addArray));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println(TAG + "getDevTypeAttrs", "connect time out");
            }
        });
    }

    private ArrayList<DefineAttriBean> defAttrProcess(JSONArray jsonArray) {
        ArrayList<DefineAttriBean> defineAttriBeens = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            DefineAttriBean defineAttriBean = new DefineAttriBean();
            defineAttriBean.setManufactureId(jsonArray.optJSONObject(i).optInt("manufacturerid"));
            defineAttriBean.setProductId(jsonArray.optJSONObject(i).optInt("productid"));
            defineAttriBean.setProductType(jsonArray.optJSONObject(i).optInt("producttype"));
            defineAttriBean.setDeviceType(jsonArray.optJSONObject(i).optInt("devicetype"));
            defineAttriBeens.add(defineAttriBean);
        }
        return defineAttriBeens;
    }

    private ArrayList<DefineActionBean> defActionProcess(JSONArray jsonArray) {
        ArrayList<DefineActionBean> defActionBeans = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            DefineActionBean defineActionBean = new DefineActionBean();
            defineActionBean.setDeviceType(jsonArray.optJSONObject(i).optInt("devicetype"));
            defineActionBean.setActionId(jsonArray.optJSONObject(i).optString("actionid"));
            defActionBeans.add(defineActionBean);
        }
        return defActionBeans;
    }

    private ArrayList<BindBean> userInfoProcess(JSONArray jsonArray) {
        ArrayList<BindBean> bindBeens = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            BindBean bindBean = new BindBean();
            bindBean.setUserId(jsonArray.optJSONObject(i).optString("userid"));
            bindBean.setUserSip(jsonArray.optJSONObject(i).optString("usersip"));
            bindBean.setRole(jsonArray.optJSONObject(i).optInt("role"));
            bindBean.setBindTime(jsonArray.optJSONObject(i).optString("date"));
            bindBeens.add(bindBean);
        }
        return bindBeens;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        InitApplication.mEventBus.unregister(this);
    }
}
