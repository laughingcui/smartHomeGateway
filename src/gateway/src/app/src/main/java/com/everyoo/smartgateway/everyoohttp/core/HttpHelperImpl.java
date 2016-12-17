package com.everyoo.smartgateway.everyoohttp.core;

import android.content.Context;

import com.everyoo.smartgateway.everyoohttp.bean.CreateDeviceBean;
import com.everyoo.smartgateway.everyoohttp.bean.CreateLinkageBean;
import com.everyoo.smartgateway.everyoohttp.bean.CreateSceneBean;
import com.everyoo.smartgateway.everyoohttp.bean.CreateScenePanelBean;
import com.everyoo.smartgateway.everyoohttp.bean.CreateTimingBean;
import com.everyoo.smartgateway.everyoohttp.bean.DeleteDeviceBean;
import com.everyoo.smartgateway.everyoohttp.bean.DeleteLinkageBean;
import com.everyoo.smartgateway.everyoohttp.bean.DeleteSceneBean;
import com.everyoo.smartgateway.everyoohttp.bean.DeleteScenePanelBean;
import com.everyoo.smartgateway.everyoohttp.bean.DeleteTimingBean;
import com.everyoo.smartgateway.everyoohttp.bean.EnableLinkageBean;
import com.everyoo.smartgateway.everyoohttp.bean.EnableTimingBean;
import com.everyoo.smartgateway.everyoohttp.bean.GatewayBindBean;
import com.everyoo.smartgateway.everyoohttp.bean.GatewayUnbindBean;
import com.everyoo.smartgateway.everyoohttp.bean.GetAppVersionBean;
import com.everyoo.smartgateway.everyoohttp.bean.GetDictionaryBean;
import com.everyoo.smartgateway.everyoohttp.bean.GetDictionaryVersionBean;
import com.everyoo.smartgateway.everyoohttp.bean.GetSipAccountBean;
import com.everyoo.smartgateway.everyoohttp.bean.GetUserInfoBean;
import com.everyoo.smartgateway.everyoohttp.bean.ModeSwitchBean;
import com.everyoo.smartgateway.everyoohttp.bean.ReportIpHttpBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateAppVersionBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateGwOnlineStatusBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateInteligentBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateLinkageBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateSceneBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateScenePanelBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateTimingBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadCtrlIdBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadDeviceLogBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadDeviceStatusBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadFileBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadSdkStatusBean;


/**
 * Created by Administrator on 2016/10/27.
 */
public interface HttpHelperImpl {
    void deleteDevice(Context context, DeleteDeviceBean bean, RequestListener requestListener);

    void createDevice(Context context, CreateDeviceBean bean, RequestListener requestListener);

    void uploadCtrlId(Context context, UploadCtrlIdBean bean, RequestListener requestListener);

    void uploadDeviceLog(Context context, UploadDeviceLogBean bean, RequestListener requestListener);

    void uploadDeviceStatus(Context context, UploadDeviceStatusBean bean, RequestListener requestListener);

    void uploadSdkStatus(Context context, UploadSdkStatusBean bean, RequestListener requestListener);

    void updateAppVersion(Context context, UpdateAppVersionBean bean, RequestListener requestListener);

    void getSipAccountData(Context context, GetSipAccountBean bean, RequestListener requestListener);

    void gatewayBind(Context context, GatewayBindBean bean, RequestListener requestListener);

    void gatewayUnbind(Context context, GatewayUnbindBean bean, RequestListener requestListener);

    void getUserInfo(Context context, GetUserInfoBean bean, RequestListener requestListener);

    void getAppVersion(Context context, GetAppVersionBean bean, RequestListener requestListener);

    void reportIpHttp(Context context, ReportIpHttpBean bean, RequestListener requestListener);

    void updateGwOnlineStatus(Context context, UpdateGwOnlineStatusBean bean, RequestListener requestListener);

    void createLinkage(Context context, CreateLinkageBean bean, RequestListener requestListener);

    void deleteLinkage(Context context, DeleteLinkageBean bean, RequestListener requestListener);

    void updateLinkage(Context context, UpdateLinkageBean bean, RequestListener requestListener);

    void enableLinkage(Context context, EnableLinkageBean bean, RequestListener requestListener);

    void updateInteligent(Context context, UpdateInteligentBean bean, RequestListener requestListener);

    void modeSwitch(Context context, ModeSwitchBean bean, RequestListener requestListener);

    void createScene(Context context, CreateSceneBean bean, RequestListener requestListener);

    void deleteScene(Context context, DeleteSceneBean bean, RequestListener requestListener);

    void updateScene(Context context, UpdateSceneBean bean, RequestListener requestListener);

    void createScenePanel(Context context, CreateScenePanelBean bean, RequestListener requestListener);

    void updateScenePanel(Context context, UpdateScenePanelBean bean, RequestListener requestListener);

    void deleteScenePanel(Context context, DeleteScenePanelBean bean, RequestListener requestListener);

    void createTiming(Context context, CreateTimingBean bean, RequestListener requestListener);

    void enableTiming(Context context, EnableTimingBean bean, RequestListener requestListener);

    void updateTiming(Context context, UpdateTimingBean bean, RequestListener requestListener);

    void deleteTiming(Context context, DeleteTimingBean bean, RequestListener requestListener);

    void createSceneTiming(Context context, CreateTimingBean bean, RequestListener requestListener);

    void deleteSceneTiming(Context context, DeleteTimingBean bean, RequestListener requestListener);

    void updateSceneTiming(Context context, UpdateTimingBean bean, RequestListener requestListener);

    void uploadFile(Context context, UploadFileBean bean, RequestListener requestListener);

}
