package com.everyoo.smartgateway.smartgateway;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/6/16.
 */
public class Constants {

    public static String gatewaySn = "";
    public static String sipAccount = "";
    public static String sipPwd = "";
    //public static String sipDomain = "101.201.107.97:5064";
    //public static String sipDomain = "smarthome.api.everyoo.com:5062";
    public static String sipDomain = "smarthomeali.api.everyoo.com:5064";
    //  public static String sipDomain = "changchunali.api.everyoo.com:5062";
    public static String masterSip = "";
    public static String userId = "";
    public static boolean isBind;
    public static int MASTER_ROLE = 1;
    public static int SUB_ROLE = 0;
    public static String ip = "";
    public static final String MULTI_BROADCAST_IP = "255.255.255.255";
    public static final int MULTI_BROADCAST_PORT = 8601;
    public static final String WIFI_WLAN = "wlan0";
    public static String wifiSsid = "";
    public static String wifiPwd = "";

    public static String modifyWifiSSID = "";
    public static String modifyWifiPwd = "";
    public static String fileName = "";


    public static int wifiEncription;
    public static final int WIFI_CONNECTED = -1;
    public static final int NETWORK_AVAILABLE = 0;
    public static final int NETWORK_UNAVAILABLE = 1;
    public static final int SIP_ACCOUNT_AVAILABLE = 2;
    public static final int SIP_ACCOUNT_UNAVAILABLE = 3;
    public static final int BIND_SUCCESS = 4;
    public static final int BIND_FAILED = 5;
    public static final int UNBIND_SUCCESS = 1;
    public static final int UNBIND_FAILED = 0;
    public static final int PULL_DATE_SUCCESSFUL = 2;
    public static final int PULL_DATE_FAILED = 3;

    public static final int UPLOAD_DEVICE_STATUS_SUCCESSFUL = 0;
    public static final int UPLOAD_DEVICE_STATUS_FAILED = 1;
    public static final int UPLOAD_DEVICE_LOG_SUCCESSFUL = 2;
    public static final int UPLOAD_DEVICE_LOG_FAILED = 3;
    public static final int UPLOAD_DEVICE_CTRLID_SUCCESSFUL = 4;
    public static final int UPLOAD_DEVICE_CTRLID_FAILED = 5;
    public static final int UPLOAD_DEVICE_INFO_SUCCESSFUL = 6;
    public static final int UPLOAD_DEVICE_INFO_FAILED = 7;


    public static ArrayList<String> userIdList = new ArrayList<>();

    public static boolean isContains(int value) {
        if (value >= 1 && value <= 16) {
            return true;
        }
        return false;
    }

    public static boolean isCurtainDevice(int deviceType) {
        if (deviceType == FLAG_INDOOR_SHUTTER || deviceType == FLAG_CLOTH_CURTAIN || deviceType == FLAG_SUNSHADE_MOTOR || deviceType == FLAG_OUTDOOR_FACADE_SHUTTER || deviceType == FLAG_CURTAIN_MOTOR) {
            return true;
        }
        return false;
    }

    public static boolean isTemDevice(int deviceType) {
        if (deviceType == FLAG_TRIAD_SENSOR || deviceType == FLAG_CO2_SENSOR || deviceType == FLAG_PM_SENSOR || deviceType == FLAG_AIR_CONDITION || deviceType == FLAG_WARM) {
            return true;
        }
        return false;
    }

    public static boolean isWindDevice(int deviceType) {
        if (deviceType == Constants.FLAG_OUTDOOR_TRIAD_SENSOR) {
            return true;
        }
        return false;
    }

    public static final ArrayList<Integer> windDevices = new ArrayList<>();

    public static ArrayList<Integer> getWindDevices() {
        if (windDevices.size() == 0) {
            windDevices.add(FLAG_OUTDOOR_TRIAD_SENSOR);
        }
        return windDevices;
    }

    public static final ArrayList<Integer> temDevices = new ArrayList<>();

    public static ArrayList<Integer> getTemDevices() {
        if (temDevices.size() == 0) {
            temDevices.add(FLAG_TRIAD_SENSOR);
            temDevices.add(FLAG_CO2_SENSOR);
            temDevices.add(FLAG_PM_SENSOR);
            temDevices.add(FLAG_AIR_CONDITION);
        }
        return temDevices;
    }

    public static final int CONTROL = 1;
    public static final int SCENE = 2;
    public static final int LINKAGE = 3;
    public static final int TIMING = 4;
    public static final int REPORT = 5;
    public static final int DEVICEINFO_GET = 6;
    public static final int INCLUSION = 7;
    public static final int EXCLUSION = 8;
    public static final int DEVICE_STATUS = 9;
    public static final int UNBIND = 10;
    public static final int DELETE_DEVICE = 11;
    public static final int WEB_CTRL = 12;
    public static final int COMPLETION = 13;
    public static final int WEB_SUBUSER = 13;
    public static final int MODE_SWITCH = 14;
    public static final int INTELIGENT_LINKAGE = 15;
    public static final int WIFI_MODIFY = 16;
    public static final int SCENE_TIMING = 17;
    public static final int VERSION_UPDATE = 18;


    public static final int LINKAGE_MAIN_FLAG = 1;
    public static final int LINKAGE_ASSISTANT_FLAG = -1;
    public static final int LINKAGE_COMMAN_FLAG = 0;

    public static final String LINKAGE_ID_WIND_GREATER = "be593c53-3f37-11e6-88e2-005056b56678";
    public static final String LINKAGE_ID_WIND_LESS = "d85fbee1-3f37-11e6-88e2-005056b56678";
    public static final String LINKAGE_ID_TEM_GREATER = "dbeb2799-3f37-11e6-88e2-005056b56678";
    public static final String LINKAGE_ID_TEM_LESS = "dfa3b04f-3f37-11e6-88e2-005056b56678";

    public static final String TEMPERATURE_VALUE = "[21,21]";
    public static final String WIND_VALUE = "[80,80]";

    public static long RESTART_DELAY = 5 * 1000;

    public static String AP_SSID = "EVERYOO";
    public static String apSsid = "EVERYOO-";

 /* public static String AP_SSID = "KuoEr";
  public static String apSsid = "KuoEr-";*/


    public static final String ACTION_UPLOAD_MESSAGE = "action.upload.sdk.message";  // sdk——》gateway(sdk——>ThreadPoolService)
    public static final String ACTION_UPLOAD_SIP_MESSAGE = "action.upload_sip.message";// gateway-->sip
    public static final String ACTION_DOWNLOAD_MESSAGE = "action.download.message";  // gateway——>gateway（MyPjsipService——》ThreadPoolService）
    public static final String ACTION_DOWNLOAD_SDK_MESSAGE = "action.download.sdk.message";// gatweay——>sdk
    public static final String ACTION_SEND_TO_LINAKGE = "action.sendto.linkagereceiver";//
    public static final String ACTION_BIND = "action.bind.gateway";
    public static final String ACTION_DATA_SYNCHRONIZED = "action.data.syncronized";

    public static final String ACTION_START_UPDATE = "com.everyoo.intent.action.START_UPDATE";// 升级action

    public static final String ACTION_NETWORK_LINKED_NO_DATA = "com.everyoo.intent.action.no.data";// 网络有物理连接，但是无网络数据
    public static final String ACTION_NETWORK_DISMISS = "com.everyoo.intent.action.no.network.found";// 网络连接不存在
    public static final String ACTION_WIFI_PWD_ERROR = "com.everyoo.intent.action.wifi.pwd.error";// wifi密码错误
    public static final String ACTION_NETWORK_HEART = "com.everyoo.intent.action.network.heart";// 网络心跳
    public static final String ACTION_UPGRADE = "com.everyoo.intent.action.upgrade";// 定时检测版本心跳

    public static final int RESULT_WITHOUD_USER_INFO = 1000;
    public static final int RESULT_ERROR_USER_INFO = 1001;
    public static final int RESULT_ERROR_TOKEN = 1004;
    public static final int RESULT_NO_PERMISSION = 2000;
    public static final int RESULT_UNEXIST_INFO = 2001;
    public static final int RESULT_WEB_WRITE_ERROR = 2002;
    public static final int RESULT_SUCCESS = 2003;
    public static final int RESULT_SUCCESS_FILE_UPLOAD_FAILED = 2004;
    public static final int RESULT_RESOURCE_EXISTED = 2005;
    public static final int RESULT_USER_UNEXIST = 2006;
    public static final int RESULT_ERROR_FORMAT = 3000;
    public static final int RESULT_ERROR_CODE = 3001;
    public static final int RESULT_TIME_OUT_CODE = 3002;
    public static final int RESULT_PARAMETER_MISS = 4000;
    public static final int RESULT_PARAMETER_UNEXPECT = 4001;
    public static final int RESULT_EXITED_USER_INFO = 5000;
    public static final int RESULT_PUSH_FAILED = 6000;

    public static final int CODE = 200;


    //定时场景联动sip执行反馈状态
    public static final int CREATE_SUCCESS = 1;// 定时,场景,联动,创建成功
    public static final int CREATE_FAILED = 2;//定时，场景，联动，创建失败
    public static final int MODIFY_SUCCESS = 3;//定时，场景，联动，修改成功
    public static final int MODIFY_FAILED = 4;//定时，场景，联动，修改失败
    public static final int DELETE_SUCCESS = 5;//定时，场景，联动，删除成功
    public static final int DELETE_FAILED = 6;//定时，场景，联动，删除失败
    public static final int START_SUCCESS = 7;//场景，联动，定时启动成功
    public static final int START_FAILED = 8;//场景，联动，定时启动失败
    public static final int STOP_SUCCESS = 9;//联动，定时停止成功
    public static final int STOP_FAILED = 10;//联动，定时停止失败
    public static final int SET_REPEAT_FAILED = 11;//重复设置
    public static final int CREATE_SCENE_PANEL_SUCCESS = 12;
    public static final int CREATE_SCENE_PANEL_FAILED = 13;
    public static final int UPDATE_SCENE_PANEL_SUCCESS = 14;
    public static final int UPDATE_SCENE_PANEL_FAILED = 15;
    public static final int DELETE_SCENE_PANEL_SUCCESS = 16;
    public static final int DELETE_SCENE_PANEL_FAILED = 17;

    public static final int WIFI_CONNECT_SUCCESS = 0;
    public static final int WIFI_FORMAT_ERROR = 1;
    public static final int WIFI_CONNECT_FAILED = 2;

    public static final int UPDATE_INTELIGENT_SUCCESSFUL = 21;
    public static final int UPDATE_INTELIGENT_FAILED = 22;


    public static final int VALUE_CREATE = 1;
    public static final int VALUE_MODIFY = 2;
    public static final int VALUE_DELETE = 3;
    public static final int VALUE_START = 4;
    public static final int VALUE_STOP = 5;
    public static final int VALUE_CREATE_SCNE_PANEL = 6;
    public static final int VALUE_UPDATE_SCENE_PANEL = 7;
    public static final int VALUE_DELETE_SCENE_PANEL = 8;
    public static final int VALUE_PERORM_SCENE_PANEL = 9;

    public static final int VALUE_INTELIGENT_UPDATE = 11;
    public static final int VALUE_CONTROL_ALL_CLOSE = 12;
    public static final int VALUE_CONTROL_ALL_OPEN = 13;

    public static final int ENABLE = 1;
    public static final int UNENABLE = 0;


    public static final String SCENE_SCENEID = "scene_id";
    public static final String SCENE_DEVICEID = "device_id";

    public static final String TIMING_TIMINGID = "alarm_id";
    public static final String TIMING_DEVICEID = "device_id";

    public static final String LINKAGE_LINKAGEID = "linkage_id";
    public static final String LINKAGE_DEVICEID = "device_id";
    public static final String LINKAGE_CTRLID = "linkage_id";


    public static final int CONTROL_MODE = 0;
    public static final int WISDOM_MODE = 1;
    public static final int MODE_SWITCH_SUCCESS = 0;
    public static final int MODE_SWITCH_FAILED = 1;
    public static int currentMode = 0; // 默认自控模式

    public static int versionCode = 1;// 当前应用版本

    public static final int LESS_THAN = 0;
    public static final int EQUAL_WITH = 1;
    public static final int GREATER_THAN = 2;
    public static final int GREATER_AND_LESS = 3;  // x > y && x < z
    public static final int GREATER_AND_EQUAL_AND_LESSS = 4;  // x >= y && x < z
    public static final int GREATER_AND_LESS_AND_EQUAL = 5; // x > y && x <= z
    public static final int GREATER_AND_EQUAL_AND_LESS_AND_EQUAL = 6; // x >= y && x <= z

    public static final int DELETE_DEVICE_SUCCESS = 1;
    public static final int DELETE_DEVICE_FAILED = 0;

    public static String FULL_OPEN = "100";
    public static String FULL_CLOSE = "0";


    public static final int FLAG_ALL_DEVICES = 0;// 所有设备
    public static final int FLAG_INTEL_ELCTRIC_OUTLET = 1;  // 智能插座（带电）
    public static final int FLAG_DIMMER_SWITCH = 2; // 调光开关
    public static final int FLAG_PANEL_SWITCH = 3; // 面板开关
    public static final int FLAG_REPEATER = 4; // 中继器
    public static final int FLAG_SUNSHADE_MOTOR = 5; // 遮阳电机
    public static final int FLAG_CURTAIN_MOTOR = 6; // 窗帘电机
    public static final int FLAG_VERTICAL_WINDOW_MOTOR = 7; // 垂直窗电机
    public static final int FLAG_MAGNETIC_DOOR = 8; // 门磁
    public static final int FLAG_TRIAD_SENSOR = 9; // 三合一传感器
    public static final int FLAG_SMOKE_ALARM = 10; // 烟雾报警器
    public static final int FLAG_GAS_ALARM = 11; // 燃气报警器
    public static final int FLAG_WATER_SENSOR = 12; // 水浸传感器
    public static final int FLAG_AC_CONTROLLER = 13; // 空调控制器(红外)
    public static final int FLAG_SIGLE_FIRE_SWITCH = 14; // 单火开关
    public static final int FLAG_DOUBLE_FIRE_SWITCH = 15;//二路开关
    public static final int FLAG_THREE_FIRE_SWITCH = 16;//三路开关
    public static final int FLAG_MASTER_OF_SCENE = 17;// 情景大师
    public static final int FLAG_FOUR_ONE_SENSOR = 18;// 四合一传感器
    public static final int FLAG_THREE_CONTROL = 19;//三路控制
    public static final int FLAG_OUTDOOR_TRIAD_SENSOR = 20;//风光雨传感器
    public static final int FLAG_BACKGROUND_MUSIC = 21;//背景音乐
    public static final int FLAG_CO2_SENSOR = 22;//二氧化碳传感器
    public static final int FLAG_PM_SENSOR = 23;//PM2.5传感器
    public static final int FLAG_AIR_CONDITION = 24;//风机盘管温控器（空调）
    public static final int FLAG_WARM = 25;//地暖
    public static final int FLAG_SCALES_SENSOR = 26;//体重秤
    public static final int FLAG_FRESH_AIR = 27;//新风
    public static final int FLAG_CLOTH_CURTAIN = 28;// 布艺帘
    public static final int FLAG_INDOOR_SHUTTER = 29;// 户内百叶
    public static final int FLAG_OUTDOOR_FACADE_SHUTTER = 30;// 户外立面卷帘
    public static final int FLAG_OPEN_DOOR = 33;//智能云锁


    public static final int FLAG_LINKAGE_USER_CREATE = 1;
    public static final int FLAG_LINKAGE_GW_CREATE = 0;

    public static final int GATEWAY_ONLINE = 1;
    public static final int GATEWAY_OFFLINE = 0;

    public static final int SDK_USABLE = 1;
    public static final int SDK_UNUSABLE = 0;


    public static boolean isSdkEnabled = false;
    public static boolean isPjSipLogined = false;
    public static boolean isPjSipEnabled = false;
    public static boolean isInstall = false;
}
