package com.everyoo.smartgateway.everyoohttp.core;

/**
 * Created by chaos on 2016/6/17.
 */
public class NetCommApi {
     //public static final String HOST = "http://alitest.api.everyoo.com/";
    //public static final String HOST = "http://smarthome.api.everyoo.com/";
    public static final String HOST = "http://smarthomeali.api.everyoo.com/";
    //   public static final String HOST = "http://smarthomeali.api.everyoo.com:82/";// 祥子测试
//  public static final String HOST = "http://101.201.107.97:83/";// 祥子测试
    public static final String CREATE_SCENE = "define/robot";
    public static final String UPDATE_SCENE = "edit/robot";
    public static final String DELETE_SCENE = "delete/robot";
    public static final String UPDATE_SCENE_PANEL = "api/gateway/robot/mapping/update";
    public static final String DELETE_SCENE_PANEL = "api/gateway/robot/mapping/empty";

    public static final String CREATE_TIMING = "define/timer";
    public static final String CREATE_SCENE_TIMING = "api/gateway/timing/robot/update";
    public static final String UPDATE_TIMING = "edit/timer";
    public static final String DELETE_TIMING = "delete/timer";
    public static final String DELETE_SCENE_TIMING = "api/gateway/timing/robot/delete";

    public static final String ENABLE_TIMING = "gateway/timer/enable";

    public static final String NETWORK_STATUS = "gateway/search/status";
    public static final String SDK_STATUS = "api/gateway/SDK/enable/update";
    public static final String UPLOAD_FILE = "api/gateway/upload/gatewaylog";

    public static final String GET_SIP_ACCOUNT = "gateway/register";
    public static final String BIND_GATEWAY = "api/gateway/bind/user";
    public static final String UNBIND_GATEWAY = "gateway/remove/bind";

    public static final String REPORT_IP_ADDRESS = "gateway/sync/ip";
    public static final String MODE_SWITCH = "api/gateway/model/status";

    public static final String CREATE_LINKAGE = "gateway/sync/linkAge";
    public static final String DELETE_LINKAGE = "gateway/delete/linkAge";
    public static final String UPDATE_LINKAGE = "gateway/edit/linkAge";
    public static final String ENABLE_LINKAGE = "gateway/edit/linkAge/enable";
    public static final String UPDATE_INTELIGENT = "api/gateway/model/smart/define";

    public static final String BIND_USER_INFO = "/gateway/user/info";

    public static final String DELETE_DEVICE = "gateway/remove/device";
    public static final String ADD_DEVICE = "gateway/added/device";

    public static final String ADD_CTRLID = "sync/ctrl";
    public static final String UPLOAD_DEVICE_LOG = "gateway/device/log";
    public static final String UPLOAD_DEVICE_STATUS = "gateway/upload";

    public static final String PULL_DEFINE_TIME = "api/gateway/info/lastest/time";
    public static final String PULL_DEFINE_ATTRIBUTES = "gateway/search/device/type";
    public static final String PULL_DEFINE_ACTION = "search/device/action";
    public static final String PULL_ACTION_DETAIL = "search/action";
    public static final String PULL_DEVICE_CTRL = "gateway/search/ctrl";

    public static final String GET_NEWEST_DICTIONARY = "api/gateway/get/data/about";
    public static final String CHECK_DICTIONARY_VERSION = "api/gateway/check/data/version";

}
