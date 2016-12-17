package com.everyoo.smartgateway.everyoohttp.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.android.volley.VolleyError;


import com.elvishew.xlog.XLog;
import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoocore.message.core.BindService;
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
import com.everyoo.smartgateway.everyoohttp.bean.GetSipAccountBean;
import com.everyoo.smartgateway.everyoohttp.bean.GetUserInfoBean;
import com.everyoo.smartgateway.everyoohttp.bean.ModeSwitchBean;
import com.everyoo.smartgateway.everyoohttp.bean.ReportIpHttpBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateAppVersionBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateGwOnlineStatusBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateInteligentBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateLinkageBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateSceneBean;
import com.everyoo.smartgateway.everyoohttp.bean.UpdateTimingBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadCtrlIdBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadDeviceLogBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadDeviceStatusBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadFileBean;
import com.everyoo.smartgateway.everyoohttp.bean.UploadSdkStatusBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceLogBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.smartgateway.Constants;

import com.everyoo.smartgateway.utils.DeviceUtil;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.StringUtils;
import com.everyoo.smartgateway.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bugtags.library.obfuscated.a.a;

/**
 * 数据交换类（初始化类）
 */
public class EveryooHttp implements HttpImpl {
    private HttpHelper helper = new HttpHelper();
    public static EveryooHttp everyooHttp = null;

    private EveryooHttp() {
    }

    public static EveryooHttp init(Context context) {
        if (everyooHttp == null) {
            everyooHttp = new EveryooHttp();
            RequestManager.init(context);
        }
        return everyooHttp;
    }

    @Override
    public void deleteDevice(Context context, DeviceBean bean, final Handler handler) {
        DeleteDeviceBean deleteDeviceBean = new DeleteDeviceBean();
        deleteDeviceBean.setDeviceId(bean.getDeviceId());
        deleteDeviceBean.setGateWayId(Constants.gatewaySn);
        deleteDeviceBean.setSipAccount(Constants.sipAccount);
        deleteDeviceBean.setSipPwd(Constants.sipPwd);
        helper.deleteDevice(context, deleteDeviceBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("delete", "jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.println("delete", "delete successful");
                            handler.sendEmptyMessage(Constants.DELETE_DEVICE_SUCCESS);
                        } else {
                            LogUtil.println("delete", "delete failed and result = " + result);
                            handler.sendEmptyMessage(Constants.DELETE_DEVICE_FAILED);
                        }
                    } else {
                        LogUtil.println("delete", "delete failed and code = " + code);
                        handler.sendEmptyMessage(Constants.DELETE_DEVICE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("delete", "connect time out");
                handler.sendEmptyMessage(Constants.DELETE_DEVICE_FAILED);
            }
        });
    }

    @Override
    public void createDevice(Context context, DeviceBean bean, Handler handler) {
        CreateDeviceBean createDeviceBean = new CreateDeviceBean();
        createDeviceBean.setSipPwd(Constants.sipPwd);
        createDeviceBean.setSipAccount(Constants.sipAccount);
        createDeviceBean.setGateWayId(Constants.gatewaySn);
        createDeviceBean.setDeviceId(bean.getDeviceId());
        createDeviceBean.setDeviceType(bean.getDeviceType());
        createDeviceBean.setFirmVersion(bean.getFirmwareVersion());
        createDeviceBean.setInclusionTime(bean.getInclusionTime());
        helper.createDevice(context, createDeviceBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("create onSuccess", "jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.println("create onSuccess", "add device successful");
                        } else {
                            LogUtil.println("create onSuccess", "add device failed and result = " + result);
                        }
                    } else {
                        LogUtil.println("create onSuccess", "add device failed and code = " + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("create onSuccess", "connect time out");
            }
        });
    }

    @Override
    public void uploadCtrlId(Context context, JSONArray jsonArray, final Handler handler) {
        UploadCtrlIdBean uploadCtrlIdBean = new UploadCtrlIdBean();
        uploadCtrlIdBean.setGateWayId(Constants.gatewaySn);
        uploadCtrlIdBean.setSipAccount(Constants.sipAccount);
        uploadCtrlIdBean.setSipPwd(Constants.sipPwd);
        uploadCtrlIdBean.setList(jsonArray);
        helper.uploadCtrlId(context, uploadCtrlIdBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("uploadCtrlId", " jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.println("uploadCtrlId", "upload ctrlId successful");
                            handler.sendEmptyMessage(Constants.UPLOAD_DEVICE_CTRLID_SUCCESSFUL);
                        } else {
                            LogUtil.println("uploadCtrlId", "upload ctrlId failed and result = " + result);
                            handler.sendEmptyMessage(Constants.UPLOAD_DEVICE_CTRLID_FAILED);
                        }
                    } else {
                        LogUtil.println("uploadCtrlId", "upload ctrlId failed and code = " + code);
                        handler.sendEmptyMessage(Constants.UPLOAD_DEVICE_CTRLID_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("uploadCtrlId", "connect time out");
                handler.sendEmptyMessage(Constants.UPLOAD_DEVICE_CTRLID_FAILED);
            }
        });
    }

    @Override
    public void uploadDeviceLog(Context context, JSONArray jsonArray) {
        UploadDeviceLogBean uploadDeviceLogBean = new UploadDeviceLogBean();
        uploadDeviceLogBean.setList(jsonArray);
        uploadDeviceLogBean.setSipPwd(Constants.sipPwd);
        uploadDeviceLogBean.setSipAccount(Constants.sipAccount);
        uploadDeviceLogBean.setGateWayId(Constants.gatewaySn);
        helper.uploadDeviceLog(context, uploadDeviceLogBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.controlLog("uploadDeviceLog", " jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.getInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.controlLog("uploadDeviceLog", "upload device log successful");
                        } else {
                            LogUtil.controlLog("uploadDeviceLog", "upload device log failed and result = " + result);
                        }
                    } else {
                        LogUtil.controlLog("uploadDeviceLog", "upload device log failed and result = ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.controlLog("uploadDeviceLog", "connect time out");
            }
        });
    }

    @Override
    public void uploadDeviceStatus(Context context, DeviceLogBean bean) {
        UploadDeviceStatusBean uploadDeviceStatusBean = new UploadDeviceStatusBean();
        uploadDeviceStatusBean.setGateWayId(Constants.gatewaySn);
        uploadDeviceStatusBean.setSipAccount(Constants.sipAccount);
        uploadDeviceStatusBean.setSipPwd(Constants.sipPwd);
        uploadDeviceStatusBean.setDeviceId(bean.getDeviceId());
        uploadDeviceStatusBean.setCtrlId(bean.getCtrlId());
        uploadDeviceStatusBean.setValue(bean.getValue());
        uploadDeviceStatusBean.setEventTime(bean.getEventTime());
        uploadDeviceStatusBean.setFlag(0);
        helper.uploadDeviceStatus(context, uploadDeviceStatusBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.controlLog("uploadDeviceStatus", " result = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.controlLog("uploadDeviceStatus", " upload device status successful");
                        } else {
                            LogUtil.controlLog("uploadDeviceStatus", "upload device status failed and result = " + result);
                        }
                    } else {
                        LogUtil.controlLog("uploadDeviceStatus", "upload device status failed and code = " + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.controlLog("uploadDeviceStatus", "connect time out");
            }
        });
    }

    @Override
    public void reportedSdkStates(Context context, int enable) {
        UploadSdkStatusBean uploadSdkStatusBean = new UploadSdkStatusBean();
        uploadSdkStatusBean.setGateWayId(Constants.gatewaySn);
        uploadSdkStatusBean.setSipAccount(Constants.sipAccount);
        uploadSdkStatusBean.setSipPwd(Constants.sipPwd);
        uploadSdkStatusBean.setEnable(enable);
        helper.uploadSdkStatus(context, uploadSdkStatusBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("reportedSdkStates", " result = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        LogUtil.println("reportedSdkStates", "SDK stated reported successful!");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("reportedSdkStates", "SDK stated reported failed!");
            }
        });


    }

    @Override
    public void updateAppVersion(Context context) {
        UpdateAppVersionBean bean = new UpdateAppVersionBean();
        bean.setDeviceType(0);
        bean.setDeviceId(Constants.gatewaySn);
        bean.setDeviceName(DeviceUtil.getApplicationId(context));
        bean.setDeviceModel(DeviceUtil.getDeviceModel());
        bean.setOsVersion(DeviceUtil.getOSVersion());
        bean.setOsName(DeviceUtil.getOSName());
        bean.setAppName(DeviceUtil.getApplicationId(context));
        bean.setAppVersionName(DeviceUtil.getAppVersionName(context));
        bean.setAppVersionCode(DeviceUtil.getAppVersionCode(context));
        bean.setChannel(0 + "");
        helper.updateAppVersion(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("code") == Constants.CODE) {
                        LogUtil.println("updateVersion", "update app version successful!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("updateVersion", "update app version failed!");
            }
        });
    }

    @Override
    public void getSipAccountData(Context context, final Handler handler) {
        GetSipAccountBean bean = new GetSipAccountBean();
        bean.setGatewayId(Constants.gatewaySn);
        helper.getSipAccountData(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("getSipAccount onSuccess", "jsonObject = " + jsonObject.toString());
                    int code = jsonObject.getInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.getInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            JSONObject object = jsonObject.optJSONObject("info");
                            if (object != null && !object.optString("sip_account").equals("") && !object.optString("sip_pwd").equals("")) {
                                Constants.sipAccount = object.optString("sip_account");
                                Constants.sipPwd = object.optString("sip_pwd");
                                handler.sendEmptyMessage(Constants.SIP_ACCOUNT_AVAILABLE);
                            } else {
                                handler.sendEmptyMessage(Constants.SIP_ACCOUNT_UNAVAILABLE);
                                LogUtil.println("getSipAccount onSuccess", "params is incompletely");
                            }
                        } else {
                            LogUtil.println("getSipAccount onSuccess", "getSipAccount failed and result = " + result);
                            handler.sendEmptyMessage(Constants.SIP_ACCOUNT_UNAVAILABLE);
                        }
                    } else {
                        LogUtil.println("getSipAccount onSuccess", "getSipAccount failed and code = " + code);
                        handler.sendEmptyMessage(Constants.SIP_ACCOUNT_UNAVAILABLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("getSipAccount onFailure", "getSipAccount connect time out");
                handler.sendEmptyMessage(Constants.SIP_ACCOUNT_UNAVAILABLE);
            }
        });
    }

    @Override
    public void gatewayBind(Context context, final Handler handler, String timestamp, String sign) {
        GatewayBindBean bean = new GatewayBindBean();
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSign(sign);
        bean.setTimesTamp(timestamp);
        bean.setUserId(Constants.userId);
        helper.gatewayBind(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("bindGateway ", " jsonOjbect = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.getInt("result") == Constants.RESULT_SUCCESS) {
                        JSONObject object = jsonObject.optJSONObject("info");
                        Constants.sipAccount = object.optString("sipusername");
                        Constants.sipPwd = object.optString("sippassword");
                        Constants.sipDomain = object.optString("sipdomain");
                        Constants.masterSip = object.getString("usersip");
                        handler.sendEmptyMessage(BindService.BIND_SUCCESS);
                    } else {
                        LogUtil.println("bindGateway ", " bind failed");
                        handler.sendEmptyMessage(BindService.BIND_FAILED);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("bindGateway ", " bind connect time out");
                handler.sendEmptyMessage(Constants.BIND_FAILED);
            }
        });
    }

    @Override
    public void gatewayUnbind(Context context, String userId, final Handler handler) {
        GatewayUnbindBean bean = new GatewayUnbindBean();
        bean.setUserId(userId);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setSipPwd(Constants.sipPwd);
        helper.gatewayUnbind(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("unBind", " jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            handler.sendEmptyMessage(Constants.UNBIND_SUCCESS);
                            LogUtil.println("unBind", "unbind successful ");
                        } else {
                            handler.sendEmptyMessage(Constants.UNBIND_FAILED);
                            LogUtil.println("unBind", "unbind failed and result = " + result);
                        }
                    } else {
                        handler.sendEmptyMessage(Constants.UNBIND_FAILED);
                        LogUtil.println("unBind", "unbind failed and code = " + code);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("unBind", "unbind connect time out");
                handler.sendEmptyMessage(Constants.UNBIND_FAILED);
            }
        });
    }

    @Override
    public void getUserInfo(Context context, final Handler handler) {
        GetUserInfoBean bean = new GetUserInfoBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setSipAccount(Constants.sipAccount);
        bean.setGateWayId(Constants.gatewaySn);
        helper.getUserInfo(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("getUserInfo", "jsonObject = " + jsonObject);
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            Message message = handler.obtainMessage();
                            message.obj = jsonObject.optString("info");
                            message.what = Constants.PULL_DATE_SUCCESSFUL;
                            handler.sendMessage(message);
                        } else {
                            LogUtil.println("getUserInfo", "get user info failed and result = " + result);
                            handler.sendEmptyMessage(Constants.PULL_DATE_FAILED);
                        }
                    } else {
                        LogUtil.println("getUserInfo", "get user info failed and code = " + code);
                        handler.sendEmptyMessage(Constants.PULL_DATE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("getUserInfo", "connect time out");
                handler.sendEmptyMessage(Constants.PULL_DATE_FAILED);
            }
        });
    }

    @Override
    public void getAppVersion(Context context, String packageName, final Handler handler) {
        GetAppVersionBean bean = new GetAppVersionBean();
        bean.setPackageName(packageName,Constants.gatewaySn);
        helper.getAppVersion(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("getAppVersion", " jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == 1) {
                            JSONObject infoObject = jsonObject.optJSONObject("info");
                            Message message = handler.obtainMessage();
                            message.obj = infoObject.getString("url");
                            message.arg1 = infoObject.optInt("version");
                            handler.sendMessage(message);
                        } else {
                            LogUtil.println("getAppVersion", " failed and result = " + result);
                        }
                    } else {
                        LogUtil.println("getAppVersion", " failed and code = " + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("getAppVersion", " failed connect time out");
            }
        });
    }

    /**
     * 上报IP地址
     *
     * @param context
     * @param ip
     */
    @Override
    public void reportIpHttp(Context context, String ip) {
        ReportIpHttpBean bean = new ReportIpHttpBean();
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setSipPwd(Constants.sipPwd);
        bean.setIp(ip);
        helper.reportIpHttp(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("reportIpHttp", " jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.println("reportIpHttp", "report local host ip successful");
                        } else {
                            LogUtil.println("reportIpHttp", "report local host ip failed and result = " + result);
                        }
                    } else {
                        LogUtil.println("reportIpHttp", "report local host ip failed and code  = " + code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("reportIpHttp", "connect time out");
            }
        });
    }

    /**
     * 更新网关在线状态
     *
     * @param context
     * @param status
     */
    @Override
    public void updateGwOnlineStatus(Context context, int status) {
        UpdateGwOnlineStatusBean bean = new UpdateGwOnlineStatusBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setSipAccount(Constants.sipAccount);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setStatus(status);
        helper.updateGwOnlineStatus(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("code") == 200 && jsonObject.optInt("result") == 2003) {
                        LogUtil.println("updateGwOnlineStatus", "upload gateway online state successul!");
                    } else {
                        LogUtil.println("updateGwOnlineStatus", "upload gateway online state failed!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("updateGwOnlineStatus", " connect time out!");
            }
        });
    }


    /**
     * 创建联动
     *
     * @param context
     * @param linkageBean
     * @param handler
     */
    @Override
    public void createLinkage(Context context, LinkageBean linkageBean, final Handler handler) {
        CreateLinkageBean createLinkageBean = new CreateLinkageBean();
        createLinkageBean.setGateWayId(Constants.gatewaySn);
        createLinkageBean.setSipAccount(Constants.sipAccount);
        createLinkageBean.setSipPwd(Constants.sipPwd);
        createLinkageBean.setBegin(linkageBean.getBegin());
        createLinkageBean.setCreateTime(linkageBean.getCreateTime());
        createLinkageBean.setEnable(linkageBean.getEnable());
        createLinkageBean.setLength(linkageBean.getLength());
        createLinkageBean.setEnd(linkageBean.getEnd());
        createLinkageBean.setLinkageId(linkageBean.getLinkageId());
        createLinkageBean.setLinkageName(linkageBean.getLinkageName());
        createLinkageBean.setUserId(linkageBean.getUserId());
        try {
            createLinkageBean.setOrder(new JSONArray(linkageBean.getActionArray()));
            createLinkageBean.setTrigger(new JSONArray(linkageBean.getTriggerArray()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        helper.createLinkage(context, createLinkageBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.linkageLog("create onSuccess", " jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.linkageLog("oncreate onSuccess", "uploadCreateLinkageInfo create success");
                            handler.sendEmptyMessage(Constants.CREATE_SUCCESS);
                        } else {
                            LogUtil.linkageLog("oncreate onSuccess", " create failed and result = " + result);
                            handler.sendEmptyMessage(Constants.CREATE_FAILED);
                        }
                    } else {
                        LogUtil.linkageLog("oncreate onSuccess", " create failed and code = " + code);
                        handler.sendEmptyMessage(Constants.CREATE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.linkageLog("onCreate onFailure", " connect time out");
                handler.sendEmptyMessage(Constants.CREATE_FAILED);
            }
        });
    }

    /**
     * 删除联动
     *
     * @param context
     * @param linkageBean
     * @param handler
     */
    @Override
    public void deleteLinkage(Context context, LinkageBean linkageBean, final Handler handler) {
        DeleteLinkageBean bean = new DeleteLinkageBean();
        bean.setUserId(Constants.userId);
        bean.setSipPwd(Constants.sipPwd);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setLinkageId(linkageBean.getLinkageId());
        helper.deleteLinkage(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.linkageLog("delete onSuccess", "jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.linkageLog("delete onSuccess", " delete successful");
                            handler.sendEmptyMessage(Constants.DELETE_SUCCESS);
                        } else {
                            LogUtil.linkageLog("delete onSuccess", " delete failed and result = " + result);
                            handler.sendEmptyMessage(Constants.DELETE_FAILED);
                        }
                    } else {
                        LogUtil.linkageLog("delete onSuccess", " delete failed and code = " + code);
                        handler.sendEmptyMessage(Constants.DELETE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.linkageLog("delete onSuccess", "connect time out");
                handler.sendEmptyMessage(Constants.DELETE_FAILED);
            }
        });
    }

    /**
     * 更新联动
     *
     * @param context
     * @param linkageBean
     * @param handler
     */
    @Override
    public void updateLinkage(Context context, LinkageBean linkageBean, final Handler handler) {
        UpdateLinkageBean bean = new UpdateLinkageBean();
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setSipPwd(Constants.sipPwd);
        bean.setBegin(linkageBean.getBegin());
        bean.setCreateTime(linkageBean.getCreateTime());
        bean.setEnable(linkageBean.getEnable());
        bean.setLength(linkageBean.getLength());
        bean.setEnd(linkageBean.getEnd());
        bean.setLinkageId(linkageBean.getLinkageId());
        bean.setLinkageName(linkageBean.getLinkageName());
        bean.setUserId(linkageBean.getUserId());
        try {
            bean.setOrder(new JSONArray(linkageBean.getActionArray()));
            bean.setTrigger(new JSONArray(linkageBean.getTriggerArray()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        helper.updateLinkage(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.linkageLog("update onSuccess", "jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.linkageLog("update onSuccess", "create successful");
                            handler.sendEmptyMessage(Constants.MODIFY_SUCCESS);
                        } else {
                            LogUtil.println("update onSuccess", "crate failed and result = " + result);
                            handler.sendEmptyMessage(Constants.MODIFY_FAILED);
                        }
                    } else {
                        LogUtil.linkageLog("update onSuccess", "crate failed and code = " + code);
                        handler.sendEmptyMessage(Constants.MODIFY_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("update onSuccess", "connect time out");
                handler.sendEmptyMessage(Constants.MODIFY_FAILED);
            }
        });
    }

    /**
     * 设置联动是否可用
     *
     * @param context
     * @param linkageBean
     * @param handler
     */
    @Override
    public void enableLinkage(Context context, final LinkageBean linkageBean, final Handler handler) {
        EnableLinkageBean bean = new EnableLinkageBean();
        bean.setSipAccount(Constants.sipAccount);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipPwd(Constants.sipPwd);
        bean.setEnable(Constants.ENABLE);
        bean.setEventTime(linkageBean.getCreateTime());
        bean.setLinkageId(linkageBean.getLinkageId());
        helper.enableLinkage(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.linkageLog("enable onSuccess", "  jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.println("enable onSuccess", " modify linkage enable successful!");
                            if (linkageBean.getEnable() == Constants.ENABLE) {
                                handler.sendEmptyMessage(Constants.START_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(Constants.STOP_SUCCESS);
                            }
                        } else {
                            LogUtil.linkageLog("enable onSuccess", " modify linkage enable failed and result = " + result);
                            if (linkageBean.getEnable() == Constants.ENABLE) {
                                handler.sendEmptyMessage(Constants.START_FAILED);
                            } else {
                                handler.sendEmptyMessage(Constants.STOP_FAILED);
                            }
                        }
                    } else {
                        LogUtil.linkageLog("enable onSuccess", " modify linkage enable failed and code  = " + code);
                        if (linkageBean.getEnable() == Constants.ENABLE) {
                            handler.sendEmptyMessage(Constants.START_FAILED);
                        } else {
                            handler.sendEmptyMessage(Constants.STOP_FAILED);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.linkageLog("enable onSuccess", "connect time out");
                if (linkageBean.getEnable() == Constants.ENABLE) {
                    handler.sendEmptyMessage(Constants.START_FAILED);
                } else {
                    handler.sendEmptyMessage(Constants.STOP_FAILED);
                }
            }
        });
    }

    @Override
    public void updateInteligent(Context context, LinkageBean linkageBean, final Handler handler) {
        UpdateInteligentBean bean = new UpdateInteligentBean();
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setSipPwd(Constants.sipPwd);
        bean.setTrigger(linkageBean.getTriggerArray());
        bean.setUserId(linkageBean.getUserId());
        helper.updateInteligent(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("updateInteligent", "jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            handler.sendEmptyMessage(Constants.UPDATE_INTELIGENT_SUCCESSFUL);
                        } else {
                            handler.sendEmptyMessage(Constants.UPDATE_INTELIGENT_FAILED);
                        }
                    } else {
                        handler.sendEmptyMessage(Constants.UPDATE_INTELIGENT_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("updateInteligent", "connect time out");
                handler.sendEmptyMessage(Constants.UPDATE_INTELIGENT_FAILED);
            }
        });
    }

    /**
     * 设置模式开关
     *
     * @param context
     * @param status
     * @param handler
     */
    @Override
    public void modeSwitch(Context context, int status, final Handler handler) {
        ModeSwitchBean bean = new ModeSwitchBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setSipAccount(Constants.sipAccount);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setStatus(status);
        helper.modeSwitch(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("modeSwitch", "jsonObject = " + jsonObject);
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            handler.sendEmptyMessage(Constants.MODE_SWITCH_SUCCESS);
                        } else {
                            LogUtil.println("modeSwitch", "mode switch failed and result = " + result);
                            handler.sendEmptyMessage(Constants.MODE_SWITCH_FAILED);
                        }
                    } else {
                        LogUtil.println("modeSwitch", "mode switch failed and code = " + code);
                        handler.sendEmptyMessage(Constants.MODE_SWITCH_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("modeSwitch", "mode switch connect time out");
                handler.sendEmptyMessage(Constants.MODE_SWITCH_FAILED);
            }
        });
    }

    /**
     * 创建情景
     *
     * @param context
     * @param sceneBean
     * @param handler
     */
    @Override
    public void createScene(Context context, SceneBean sceneBean, final Handler handler) {
        CreateSceneBean bean = new CreateSceneBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setEnd(sceneBean.getEnd());
        bean.setUserId(sceneBean.getUserId());
        bean.setCreateTime(sceneBean.getCreateTime());
        bean.setBegin(sceneBean.getBegin());
        bean.setEnable(sceneBean.getEnable());
        bean.setInfo(sceneBean.getPosition());
        bean.setLength(sceneBean.getLength());
        bean.setRobotId(sceneBean.getSceneId());
        bean.setRobotName(sceneBean.getSceneName());
        helper.createScene(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.sceneLog("createScene onSuccess", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.CREATE_SUCCESS);
                    } else {
                        LogUtil.sceneLog("createScene onSuccess", "createScene failed");
                        handler.sendEmptyMessage(Constants.CREATE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(Constants.CREATE_FAILED);
                }

            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("createScene onFailure", "connect time out");
                handler.sendEmptyMessage(Constants.CREATE_FAILED);
            }
        });
    }

    /**
     * 删除情景
     *
     * @param context
     * @param sceneBean
     * @param handler
     */
    @Override
    public void deleteScene(Context context, SceneBean sceneBean, final Handler handler) {
        DeleteSceneBean bean = new DeleteSceneBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setSipAccount(Constants.sipAccount);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setRobotId(sceneBean.getSceneId());
        bean.setCreateTime(sceneBean.getCreateTime());
        bean.setUserId(sceneBean.getUserId());
        helper.deleteScene(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                    LogUtil.sceneLog("deleteScene onSuccess", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.DELETE_SUCCESS);
                    } else {
                        LogUtil.sceneLog("deleteScene onSuccess", "deleteScene failed");
                        handler.sendEmptyMessage(Constants.DELETE_FAILED);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    handler.sendEmptyMessage(Constants.DELETE_FAILED);
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.sceneLog("deleteScene onFailure", "delete scene time out");
                handler.sendEmptyMessage(Constants.DELETE_FAILED);
            }
        });
    }

    /**
     * 更新情景
     *
     * @param context
     * @param sceneBean
     * @param handler
     */
    @Override
    public void updateScene(Context context, SceneBean sceneBean, final Handler handler) {
        UpdateSceneBean bean = new UpdateSceneBean();
        bean.setRobotName(sceneBean.getSceneName());
        bean.setSipPwd(Constants.sipPwd);
        bean.setSipAccount(Constants.sipAccount);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setEnd(sceneBean.getEnd());
        bean.setRobotId(sceneBean.getSceneId());
        bean.setBegin(sceneBean.getBegin());
        bean.setCreateTime(sceneBean.getCreateTime());
        bean.setInfo(sceneBean.getPosition());
        bean.setEnable(sceneBean.getEnable());
        bean.setLength(sceneBean.getLength());
        bean.setUserId(sceneBean.getUserId());
        helper.updateScene(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.sceneLog("updateScene onSuccess", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.MODIFY_SUCCESS);
                    } else {
                        LogUtil.sceneLog("updateScene onSuccess", "createScene failed");
                        handler.sendEmptyMessage(Constants.MODIFY_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(Constants.MODIFY_FAILED);
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.sceneLog("updateScene onFailure", "connect time out");
                handler.sendEmptyMessage(Constants.MODIFY_FAILED);
            }
        });
    }

    /**
     * 创建ScenePanel
     *
     * @param context
     * @param sceneBean
     * @param handler
     */
    @Override
    public void createScenePanel(Context context, SceneBean sceneBean, final Handler handler) {
        CreateScenePanelBean bean = new CreateScenePanelBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGateWayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(sceneBean.getUserId());
        bean.setCtrlId(sceneBean.getCtrlId());
        bean.setKeyId(sceneBean.getValue());
        bean.setRobotId(sceneBean.getSceneId());
        helper.createScenePanel(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.sceneLog("createScenePanel", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.CREATE_SCENE_PANEL_SUCCESS);
                    } else {
                        LogUtil.sceneLog("createScenePanel", "code or result is invalid");
                        handler.sendEmptyMessage(Constants.CREATE_SCENE_PANEL_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.sceneLog("createScenePanel", "connect time out");
                handler.sendEmptyMessage(Constants.CREATE_SCENE_PANEL_FAILED);
            }
        });
    }

    /**
     * 与createScenePanel共用一个接口
     *
     * @param context
     * @param sceneBean
     * @param handler
     */
    @Override
    public void updateScenePanel(Context context, SceneBean sceneBean, Handler handler) {
        createScenePanel(context, sceneBean, handler);
    }

    /**
     * 删除ScenePanel
     *
     * @param context
     * @param sceneBean
     * @param handler
     */
    @Override
    public void deleteScenePanel(Context context, SceneBean sceneBean, final Handler handler) {
        DeleteScenePanelBean bean = new DeleteScenePanelBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setSipAccount(Constants.sipAccount);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setKeyId(sceneBean.getValue());
        bean.setCtrlId(sceneBean.getCtrlId());
        bean.setUserId(sceneBean.getUserId());
        helper.deleteScenePanel(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.sceneLog("deleteScenePanel", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.DELETE_SCENE_PANEL_SUCCESS);
                    } else {
                        LogUtil.sceneLog("deleteScenePanel", "code or result is invalid");
                        handler.sendEmptyMessage(Constants.DELETE_SCENE_PANEL_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.sceneLog("createScenePanel", "connect time out");
                handler.sendEmptyMessage(Constants.DELETE_SCENE_PANEL_FAILED);
            }
        });
    }

    /**
     * 创建定时
     *
     * @param context
     * @param timingBean
     * @param handler
     */
    @Override
    public void createTiming(Context context, TimingBean timingBean, final Handler handler) {
        CreateTimingBean bean = new CreateTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(timingBean.getUserId());
        bean.setCtrlId(timingBean.getCtrlId());
        bean.setAlarmTime(timingBean.getAlarmTime());
        bean.setCreateTime(TimeUtil.currentTime());
        bean.setDeviceId(timingBean.getDeviceId());
        bean.setEnable("1");
        bean.setLoop(timingBean.getLoop());
        bean.setTimerId(timingBean.getAlarmId());
        bean.setValue(timingBean.getValue());
        helper.createTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.timingLog("createTiming onSuccess", "create timing successful!");
                            handler.sendEmptyMessage(Constants.CREATE_SUCCESS);
                        } else {
                            LogUtil.timingLog("createTiming onSuccess", "create timing failed and result = " + result);
                            handler.sendEmptyMessage(Constants.CREATE_FAILED);
                        }
                    } else {
                        LogUtil.timingLog("createTiming onSuccess", "create timing failed and code = " + code);
                        handler.sendEmptyMessage(Constants.CREATE_FAILED);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("createTiming onFailure", "connect time out");
                handler.sendEmptyMessage(Constants.CREATE_FAILED);
            }
        });
    }

    @Override
    public void enableTiming(Context context, final TimingBean timingBean, final Handler handler) {
        EnableTimingBean bean = new EnableTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setTimerId(timingBean.getAlarmId());
        bean.setEnable(timingBean.getEnable());
        helper.enableTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.timingLog("updateEnableStatus", "  jsonObject = " + jsonObject.toString());
                    int code = jsonObject.optInt("code");
                    if (code == Constants.CODE) {
                        int result = jsonObject.optInt("result");
                        if (result == Constants.RESULT_SUCCESS) {
                            LogUtil.timingLog("EnableTiming onSuccess", "updateEnableStatus successful");
                            if (timingBean.getEnable() == Constants.ENABLE) {
                                handler.sendEmptyMessage(Constants.START_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(Constants.STOP_SUCCESS);
                            }
                        } else {
                            LogUtil.timingLog("EnableTiming onSuccess", "updateEnableStatus is failed and result = " + result);
                            if (timingBean.getEnable() == Constants.ENABLE) {
                                handler.sendEmptyMessage(Constants.START_FAILED);
                            } else {
                                handler.sendEmptyMessage(Constants.STOP_FAILED);
                            }
                        }
                    } else {
                        LogUtil.timingLog("EnableTiming onSuccess", "updateEnableStatus is failed and code = " + code);
                        if (timingBean.getEnable() == Constants.ENABLE) {
                            handler.sendEmptyMessage(Constants.START_FAILED);
                        } else {
                            handler.sendEmptyMessage(Constants.STOP_FAILED);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("EnableTiming onSuccess", "updateEnableStatus is connect time out");
                if (timingBean.getEnable() == Constants.ENABLE) {
                    handler.sendEmptyMessage(Constants.START_FAILED);
                } else {
                    handler.sendEmptyMessage(Constants.STOP_FAILED);
                }
            }
        });
    }

    /**
     * 更新定时
     *
     * @param context
     * @param timingBean
     * @param handler
     */
    @Override
    public void updateTiming(Context context, TimingBean timingBean, final Handler handler) {
        UpdateTimingBean bean = new UpdateTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(timingBean.getUserId());
        bean.setCtrlId(timingBean.getCtrlId());
        bean.setAlarmTime(timingBean.getAlarmTime());
        bean.setCreateTime(TimeUtil.currentTime());
        bean.setDeviceId(timingBean.getDeviceId());
        bean.setEnable(timingBean.getEnable());
        bean.setLoop(timingBean.getLoop());
        bean.setTimerId(timingBean.getAlarmId());
        bean.setValue(timingBean.getValue());
        helper.updateTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    LogUtil.timingLog("updateTiming", "data = " + data);
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        LogUtil.timingLog("updateTiming", " modify successful");
                        handler.sendEmptyMessage(Constants.MODIFY_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(Constants.MODIFY_FAILED);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("updateTiming", "connect time out");
                handler.sendEmptyMessage(Constants.MODIFY_FAILED);
            }
        });
    }

    /**
     * 删除定时
     *
     * @param context
     * @param timingBean
     * @param handler
     */
    @Override
    public void deleteTiming(Context context, TimingBean timingBean, final Handler handler) {
        DeleteTimingBean bean = new DeleteTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(timingBean.getUserId());
        bean.setTimerId(timingBean.getAlarmId());
        helper.deleteTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.timingLog("deleteTiming", " jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        LogUtil.timingLog("deleteTiming", " delete successful");
                        handler.sendEmptyMessage(Constants.DELETE_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(Constants.DELETE_FAILED);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("deleteTiming", "connect time out");
                handler.sendEmptyMessage(Constants.DELETE_FAILED);
            }
        });
    }

    @Override
    public void createSceneTiming(Context context, TimingBean timingBean, final Handler handler) {
        CreateTimingBean bean = new CreateTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(timingBean.getUserId());
        bean.setTimerId(timingBean.getAlarmId());
        //bean.setCtrlId(timingBean.getSceneId());// 场景id
        bean.setCtrlId(timingBean.getCtrlId());// 场景id
        bean.setAlarmTime(timingBean.getAlarmTime());
        bean.setLoop(timingBean.getLoop());
        bean.setCreateTime(TimeUtil.currentTime());
        bean.setEnable("1");
        helper.createSceneTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    LogUtil.timingLog("createSceneTiming", "data = " + data);
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.CREATE_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(Constants.CREATE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("createSceneTiming", "connect time out");
                handler.sendEmptyMessage(Constants.CREATE_FAILED);
            }
        });


    }

    @Override
    public void deleteSceneTiming(Context context, TimingBean timingBean, final Handler handler) {
        DeleteTimingBean bean = new DeleteTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(timingBean.getUserId());
        bean.setTimerId(timingBean.getAlarmId());
        helper.deleteSceneTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.timingLog("deleteSceneTiming", " jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        LogUtil.println("deleteTiming", " delete successful");
                        handler.sendEmptyMessage(Constants.DELETE_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(Constants.DELETE_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("deleteSceneTiming", "connect time out");
                handler.sendEmptyMessage(Constants.DELETE_FAILED);
            }
        });
    }

    @Override
    public void updateSceneTiming(Context context, TimingBean timingBean, final Handler handler) {
        UpdateTimingBean bean = new UpdateTimingBean();
        bean.setSipPwd(Constants.sipPwd);
        bean.setGatewayId(Constants.gatewaySn);
        bean.setSipAccount(Constants.sipAccount);
        bean.setUserId(timingBean.getUserId());
        bean.setTimerId(timingBean.getAlarmId());
       // bean.setCtrlId(timingBean.getSceneId()); // 场景id
        bean.setCtrlId(timingBean.getCtrlId());// 场景id
        bean.setAlarmTime(timingBean.getAlarmTime());
        bean.setCreateTime(TimeUtil.currentTime());
        bean.setEnable(timingBean.getEnable());
        bean.setLoop(timingBean.getLoop());
        helper.updateSceneTiming(context, bean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    LogUtil.timingLog("updateSceneTiming", "data = " + data);
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        handler.sendEmptyMessage(Constants.MODIFY_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(Constants.MODIFY_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.timingLog("updateSceneTiming", "connect time out");
                handler.sendEmptyMessage(Constants.MODIFY_FAILED);
            }
        });
    }

    @Override
    public void getDictionaryVersion(Context context, final Handler handler) {
        final int[] version = new int[3];
        final Map map = new HashMap();
        if (!StringUtils.isNullOrEmpty(Constants.gatewaySn)) {
            map.put("gatewayid", Constants.gatewaySn);
        }
        if (!StringUtils.isNullOrEmpty(Constants.sipAccount)) {
            map.put("sipaccount", Constants.sipAccount);
        }
        if (!StringUtils.isNullOrEmpty(Constants.sipPwd)) {
            map.put("sippwd", Constants.sipPwd);
        }
        JSONObject jsonObject = new JSONObject(map);
        RequestManager.post(context, NetCommApi.HOST + NetCommApi.CHECK_DICTIONARY_VERSION, jsonObject, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    LogUtil.println("getDictionaryVersion", "jsonObject = " + jsonObject.toString());
                    if (jsonObject.optInt("code") == Constants.CODE && jsonObject.optInt("result") == Constants.RESULT_SUCCESS) {
                        JSONObject infoObject = jsonObject.optJSONObject("info");
                        version[0] = infoObject.optInt("deviceattrversion");
                        version[1] = infoObject.optInt("deviceactionversion");
                        version[2] = infoObject.optInt("userversion");
                        Message message = Message.obtain();
                        message.obj = version;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("getDictionaryVersion", "connect time out");
            }
        });
    }

    @Override
    public void uploadFile(Context context) {
        UploadFileBean uploadFileBean = new UploadFileBean();
        uploadFileBean.setGateWayId(Constants.gatewaySn);
        uploadFileBean.setSipAccount(Constants.sipAccount);
        uploadFileBean.setSipPwd(Constants.sipPwd);
        helper.uploadFile(context, uploadFileBean, new RequestListener() {
            @Override
            public void requestSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("code") == Constants.CODE) {
                        JSONArray jsonArray = jsonObject.optJSONObject("info").optJSONArray("success");
                        JSONObject fileObject = jsonArray.getJSONObject(0);
                        String fileName = fileObject.optString("file");
                        XLog.i("fileName=" + fileName);
                        LogUtil.println("uploadFile", "uploadFile successful!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                LogUtil.println("uploadFile", "uploadFile failed!");
            }
        });

    }


}
