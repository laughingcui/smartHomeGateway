package com.everyoo.smartgateway.everyoohttp.core;

import android.content.Context;
import android.text.TextUtils;

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
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.StringUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/10/27.
 */
public class HttpHelper implements HttpHelperImpl {
    /**
     * 删除设备
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void deleteDevice(Context context, DeleteDeviceBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("deviceid", bean.getDeviceId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.DELETE_DEVICE, jsonObject, requestListener);
    }

    /**
     * 创建设备
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void createDevice(Context context, CreateDeviceBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("deviceid", bean.getDeviceId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceType() + "")) {
            map.put("devicetype", bean.getDeviceType());
        }
        if (!StringUtils.isNullOrEmpty(bean.getFirmVersion() + "")) {
            map.put("firmversion", bean.getFirmVersion());
        }
        if (!StringUtils.isNullOrEmpty(bean.getInclusionTime())) {
            map.put("inclusiontime", bean.getInclusionTime());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.ADD_DEVICE, jsonObject, requestListener);
    }

    /**
     * 上传CtrlId
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void uploadCtrlId(Context context, UploadCtrlIdBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        map.put("list", bean.getList());
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.ADD_CTRLID, jsonObject, requestListener);
    }

    /**
     * 上传设备日志
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void uploadDeviceLog(Context context, UploadDeviceLogBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        map.put("list", bean.getList());
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPLOAD_DEVICE_LOG, jsonObject, requestListener);
    }

    /**
     * 上传设备状态
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void uploadDeviceStatus(Context context, UploadDeviceStatusBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("deviceid", bean.getDeviceId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("ctrlid", bean.getCtrlId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getValue())) {
            map.put("value", bean.getValue());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEventTime())) {
            map.put("eventtime", bean.getEventTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getFlag() + "")) {
            map.put("flag", bean.getFlag());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPLOAD_DEVICE_STATUS, jsonObject, requestListener);
    }


    /**
     * 上传SDK状态
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void uploadSdkStatus(Context context, UploadSdkStatusBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.SDK_STATUS, jsonObject, requestListener);

    }


    /**
     * 上传APP版本信息
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void updateAppVersion(Context context, UpdateAppVersionBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getDeviceType() + "")) {
            map.put("device_type", bean.getDeviceType());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("device_id", bean.getDeviceId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAppVersionName())) {
            map.put("device_name", bean.getDeviceName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("device_model", bean.getDeviceModel());
        }
        if (!StringUtils.isNullOrEmpty(bean.getOsVersion())) {
            map.put("os_version", bean.getOsVersion());
        }
        if (!StringUtils.isNullOrEmpty(bean.getOsName())) {
            map.put("os_name", bean.getOsName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAppName())) {
            map.put("app_name", bean.getAppName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAppVersionName())) {
            map.put("app_version_name", bean.getAppVersionName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAppVersionCode() + "")) {
            map.put("app_version_code", bean.getAppVersionCode());
        }
        if (!StringUtils.isNullOrEmpty(bean.getChannel() + "")) {
            map.put("channel", bean.getChannel());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, "http://common.api.everyoo.com/device/register/v2", jsonObject, requestListener);
    }

    /**
     * 获取Sip账号
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void getSipAccountData(Context context, GetSipAccountBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.GET_SIP_ACCOUNT, jsonObject, requestListener);
    }

    /**
     * 网关绑定
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void gatewayBind(Context context, GatewayBindBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimesTamp())) {
            map.put("timestamp", bean.getTimesTamp());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSign())) {
            map.put("sign", bean.getSign());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.BIND_GATEWAY, jsonObject, requestListener);
    }

    /**
     * 网关解绑
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void gatewayUnbind(Context context, GatewayUnbindBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UNBIND_GATEWAY, jsonObject, requestListener);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void getUserInfo(Context context, GetUserInfoBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.BIND_USER_INFO, jsonObject, requestListener);
    }

    /**
     * 获取App版本
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void getAppVersion(Context context, GetAppVersionBean bean, RequestListener requestListener) {
        RequestManager.get(context, "http://common.api.everyoo.com/app/version?name=" + bean.getPackageName()+ "&gatewayid="+ bean.getDeviceId(), null, requestListener);
    }

    /**
     * 上报IP地址
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void reportIpHttp(Context context, ReportIpHttpBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getIp())) {
            map.put("ip", bean.getIp());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.REPORT_IP_ADDRESS, jsonObject, requestListener);
    }

    /**
     * 更新网关在线状态
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void updateGwOnlineStatus(Context context, UpdateGwOnlineStatusBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getStatus() + "")) {
            map.put("status", bean.getStatus() + "");
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + "api/gateway/sip/online/status", jsonObject, requestListener);
    }

    /**
     * 创建联动
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void createLinkage(Context context, CreateLinkageBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLinkageId())) {
            map.put("linkageid", bean.getLinkageId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLinkageName())) {
            map.put("linkagename", bean.getLinkageName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        if (!StringUtils.isNullOrEmpty(bean.getBegin())) {
            map.put("begin", bean.getBegin());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnd())) {
            map.put("end", bean.getEnd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLength() + "")) {
            map.put("length", bean.getLength());
        }
        map.put("order", bean.getOrder());
        map.put("trigger", bean.getTrigger());
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.CREATE_LINKAGE, jsonObject, requestListener);
    }

    /**
     * 删除联动
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void deleteLinkage(Context context, DeleteLinkageBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLinkageId())) {
            map.put("linkageid", bean.getLinkageId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.DELETE_LINKAGE, jsonObject, requestListener);
    }

    /**
     * 更新联动
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void updateLinkage(Context context, UpdateLinkageBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLinkageId())) {
            map.put("linkageid", bean.getLinkageId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLinkageName())) {
            map.put("linkagename", bean.getLinkageName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        if (!StringUtils.isNullOrEmpty(bean.getBegin())) {
            map.put("begin", bean.getBegin());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnd())) {
            map.put("end", bean.getEnd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLength() + "")) {
            map.put("length", bean.getLength());
        }
        map.put("order", bean.getOrder());
        map.put("trigger", bean.getTrigger());
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPDATE_LINKAGE, jsonObject, requestListener);
    }

    /**
     * 设置联动是否可用
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void enableLinkage(Context context, EnableLinkageBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLinkageId())) {
            map.put("linkageid", bean.getLinkageId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEventTime())) {
            map.put("eventtime", bean.getEventTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.ENABLE_LINKAGE, jsonObject, requestListener);
    }

    @Override
    public void updateInteligent(Context context, UpdateInteligentBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTrigger())) {
            map.put("trigger", bean.getTrigger());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPDATE_INTELIGENT, jsonObject, requestListener);
    }

    /**
     * 设置模式开关
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void modeSwitch(Context context, ModeSwitchBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getStatus() + "")) {
            map.put("status", bean.getStatus());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.MODE_SWITCH, jsonObject, requestListener);
    }

    /**
     * 创建情景
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void createScene(Context context, CreateSceneBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        if (!StringUtils.isNullOrEmpty(bean.getBegin())) {
            map.put("begin", bean.getBegin());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnd())) {
            map.put("end", bean.getEnd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getRobotId())) {
            map.put("robotid", bean.getRobotId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getRobotName())) {
            map.put("robotname", bean.getRobotName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLength() + "")) {
            map.put("length", bean.getLength());
        }
        map.put("info", bean.getInfo());
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.CREATE_SCENE, jsonObject, requestListener);
    }

    /**
     * 删除情景
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void deleteScene(Context context, DeleteSceneBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getRobotId())) {
            map.put("robotid", bean.getRobotId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.DELETE_SCENE, jsonObject, requestListener);
    }

    /**
     * 更新情景
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void updateScene(Context context, UpdateSceneBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        if (!StringUtils.isNullOrEmpty(bean.getBegin())) {
            map.put("begin", bean.getBegin());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnd())) {
            map.put("end", bean.getEnd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getRobotId())) {
            map.put("robotid", bean.getRobotId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getRobotName())) {
            map.put("robotname", bean.getRobotName());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLength() + "")) {
            map.put("length", bean.getLength());
        }
        map.put("info", bean.getInfo());
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPDATE_SCENE, jsonObject, requestListener);
    }

    /**
     * 创建ScenePanel
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void createScenePanel(Context context, CreateScenePanelBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getRobotId())) {
            map.put("robotid", bean.getRobotId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getKeyId())) {
            map.put("keyid", bean.getKeyId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("ctrlid", bean.getCtrlId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPDATE_SCENE_PANEL, jsonObject, requestListener);
    }

    /**
     * 与createScenePanel共用一个接口
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void updateScenePanel(Context context, UpdateScenePanelBean bean, RequestListener requestListener) {

    }

    /**
     * 删除ScenePanel
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void deleteScenePanel(Context context, DeleteScenePanelBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getKeyId())) {
            map.put("keyid", bean.getKeyId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("ctrlid", bean.getCtrlId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.DELETE_SCENE_PANEL, jsonObject, requestListener);
    }

    /**
     * 创建定时
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void createTiming(Context context, CreateTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("ctrlid", bean.getCtrlId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAlarmTime())) {
            map.put("alarmtime", bean.getAlarmTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable())) {
            map.put("enable", bean.getEnable());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLoop())) {
            map.put("loop", bean.getLoop());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getValue())) {
            map.put("value", bean.getValue());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("deviceid", bean.getDeviceId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.CREATE_TIMING, jsonObject, requestListener);
    }

    /**
     * 定时是否能用
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void enableTiming(Context context, EnableTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.ENABLE_TIMING, jsonObject, requestListener);
    }

    /**
     * 更新定时
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void updateTiming(Context context, UpdateTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("ctrlid", bean.getCtrlId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAlarmTime())) {
            map.put("alarmtime", bean.getAlarmTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLoop())) {
            map.put("loop", bean.getLoop());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getValue())) {
            map.put("value", bean.getValue());
        }
        if (!StringUtils.isNullOrEmpty(bean.getDeviceId())) {
            map.put("deviceid", bean.getDeviceId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPDATE_TIMING, jsonObject, requestListener);
    }

    /**
     * 删除定时
     *
     * @param context
     * @param bean
     * @param requestListener
     */
    @Override
    public void deleteTiming(Context context, DeleteTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.DELETE_TIMING, jsonObject, requestListener);
    }

    @Override
    public void createSceneTiming(Context context, CreateTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("robotid", bean.getCtrlId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAlarmTime())) {
            map.put("alarmtime", bean.getAlarmTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLoop())) {
            map.put("loop", bean.getLoop());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable())) {
            map.put("enable", bean.getEnable());
        }

        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.CREATE_SCENE_TIMING, jsonObject, requestListener);
    }

    @Override
    public void deleteSceneTiming(Context context, DeleteTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.DELETE_SCENE_TIMING, jsonObject, requestListener);
    }

    @Override
    public void updateSceneTiming(Context context, UpdateTimingBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }
        if (!StringUtils.isNullOrEmpty(bean.getGatewayId())) {
            map.put("gatewayid", bean.getGatewayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getUserId())) {
            map.put("userid", bean.getUserId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getTimerId())) {
            map.put("timerid", bean.getTimerId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCtrlId())) {
            map.put("robotid", bean.getCtrlId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getAlarmTime())) {
            map.put("alarmtime", bean.getAlarmTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getCreateTime())) {
            map.put("createtime", bean.getCreateTime());
        }
        if (!StringUtils.isNullOrEmpty(bean.getLoop())) {
            map.put("loop", bean.getLoop());
        }
        if (!StringUtils.isNullOrEmpty(bean.getEnable() + "")) {
            map.put("enable", bean.getEnable() + "");
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.CREATE_SCENE_TIMING, jsonObject, requestListener);
    }

    @Override
    public void uploadFile(Context context, UploadFileBean bean, RequestListener requestListener) {
        Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(bean.getGateWayId())) {
            map.put("gatewayid", bean.getGateWayId());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipPwd())) {
            map.put("sippwd", bean.getSipPwd());
        }
        if (!StringUtils.isNullOrEmpty(bean.getSipAccount())) {
            map.put("sipaccount", bean.getSipAccount());
        }

        List<File> files = new ArrayList<>();
        if (!TextUtils.isEmpty(Constants.fileName)) {
            files.add(new File("data/data/" + context.getPackageName() + "/xlogs/", Constants.fileName));
        } else {
            LogUtil.println("uploadFile", "file name is null");
        }
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.UPLOAD_FILE, "file[]", files, map, requestListener);

    }


}
