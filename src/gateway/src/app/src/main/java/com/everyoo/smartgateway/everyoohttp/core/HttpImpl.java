package com.everyoo.smartgateway.everyoohttp.core;

import android.content.Context;
import android.os.Handler;

import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceLogBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;

import org.json.JSONArray;


/**
 * Created by Administrator on 2016/10/27.
 */
public interface HttpImpl {
    void deleteDevice(Context context, DeviceBean bean, Handler handler);

    void createDevice(Context context, DeviceBean bean, Handler handler);

    void uploadCtrlId(Context context, JSONArray jsonArray, Handler handler);

    void uploadDeviceLog(Context context, JSONArray jsonArray);

    void uploadDeviceStatus(Context context, DeviceLogBean bean);

    void reportedSdkStates(Context context, int enable);

    void updateAppVersion(Context context);

    void getSipAccountData(Context context, Handler handler);

    void gatewayBind(Context context, Handler handler, String timestamp, String sign);

    void gatewayUnbind(Context context, String userId, Handler handler);

    void getUserInfo(Context context, Handler handler);

    void getAppVersion(Context context, String packageName, Handler handler);

    void reportIpHttp(Context context, String ip);

    void updateGwOnlineStatus(Context context, int status);

    void createLinkage(Context context, LinkageBean linkageBean, Handler handler);

    void deleteLinkage(Context context, LinkageBean linkageBean, Handler handler);

    void updateLinkage(Context context, LinkageBean linkageBean, Handler handler);

    void enableLinkage(Context context, LinkageBean linkageBean, Handler handler);

    void updateInteligent(Context context, LinkageBean linkageBean, Handler handler);

    void modeSwitch(Context context, int status, Handler handler);

    void createScene(Context context, SceneBean sceneBean, Handler handler);

    void deleteScene(Context context, SceneBean sceneBean, Handler handler);

    void updateScene(Context context, SceneBean sceneBean, Handler handler);

    void createScenePanel(Context context, SceneBean sceneBean, Handler handler);

    void updateScenePanel(Context context, SceneBean sceneBean, Handler handler);

    void deleteScenePanel(Context context, SceneBean sceneBean, Handler handler);

    void createTiming(Context context, TimingBean timingBean, Handler handler);

    void enableTiming(Context context, TimingBean timingBean, Handler handler);

    void updateTiming(Context context, TimingBean timingBean, Handler handler);

    void deleteTiming(Context context, TimingBean timingBean, Handler handler);

    void createSceneTiming(Context context, TimingBean timingBean, Handler handler);

    void deleteSceneTiming(Context context, TimingBean timingBean, Handler handler);

    void updateSceneTiming(Context context, TimingBean timingBean, Handler handler);

    void getDictionaryVersion(Context context, Handler handler);

    void uploadFile(Context context);
}
