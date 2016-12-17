package com.everyoo.smartgateway.smartgateway;

/**
 * Created by chaos on 2016/6/17.
 */
public class ActionId {

    public static final String ZWAVE_SDK_ENABLE = "35a082fb-64c7-44fa-9c4e-10784092e0b1";//ZWAVE SDK可用性
    public static final String USB_DONGLE_RESET = "245498ce-53ac-11e6-9071-00163e0006e2";// D
    public static final String USB_DONGLE_TACHED = "026c91d5-2b81-11e6-920c-005056b543f4";// Dongle插拔
    public static final String DONGLE_INITIALIZE = "026c91d5-2b81-11e6-920c-005056b543f6"; //dongle初始化
    public static final String DEVICE_EXCLUSION = "026c81d5-2b81-11e6-920c-005056b543f7";// 设备退网
    public static final String DEVICE_INCLUSION = "026c91d5-2b81-11e6-920c-005056b543f5";// 设备加网
    public static final String DEVICE_INFOMATION = "026c91d5-2b81-11e6-920c-005056b543f3";// 设备信息
    public static final String DEVICE_STATE_REPORT = "97e72359-a7b2-11e5-b360-b8975ab8b52a";//设备在线不在线检测

    public static final String SIGLE_FIRE_SWITCH = "026b01d5-2b81-11e6-920c-005056b543f6"; //单火一路开关控制
    public static final String DOUBLE_FIRE_SWITCH = "3d524b6c-2b81-11e6-920c-005056b543f6";//二路开关控制
    public static final String THREE_FIRE_SWITCH = "59c6fb42-2b81-11e6-920c-005056b543f6";//三路开关控制

    public static final String DEVICE_SWITCH = "c30da48a-a7b2-11e5-b360-b8975ab8b52a";//设备开关，控制
    public static final String POWER_REPORT = "a1748f8e-a7b3-11e5-b360-b8975ab8b52a";//电量上报, 累积功率
    public static final String PERCENT = "8321dc5c-a7b3-11e5-b360-b8975ab8b52a";//上报百分比 _REPORT
    public static final String LIGHT_REPORT = "bc39ae07-a7b3-11e5-b360-b8975ab8b52a";//光感上报 三合一
    public static final String TEMPERATURE_REPORT = "b1e9dd4f-a7b3-11e5-b360-b8975ab8b52a";//温度上报， 三合一和风光雨，空调
    public static final String MOVE_REPORT = "b6899590-a7b3-11e5-b360-b8975ab8b52a";//震动,移动上报，三合一和门磁适用
    public static final String DOOR_SWITCH_REPORT = "c0d677e8-a7b3-11e5-b360-b8975ab8b52a";//门磁开关上报

    public static final String SMOKER_REPORT = "c7bc1ed2-a7b3-11e5-b360-b8975ab8b52a";//烟感上报,烟雾报警

    public static final String WIND_SPEED_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b53a";//风光雨 风速上报
    public static final String LIGHT_PERCENT_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b53b";//风光雨 光度百分比上报
    public static final String RAINFALL_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b53c";//风光雨 雨量上报
    public static final String HUMIDITY_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b53d";//风光雨 湿度上报
    public static final String ULTRAVIOLET_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b53e";//风光雨 紫外线上报


    public static final String INSTANTANEOUS_POWER = "9c3ccd50-a7b3-11e5-b360-b8975ab8b52a";//瞬时功率

    public static final String BACKGROUND_MUSIC = "f9d0ce27-a7b2-11e5-b360-b8975ab8b53f";//背景音乐
    public static final String CO2_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b540";//二氧化碳
    public static final String PM_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b541";//PM2.5
    public static final String AIR_CONDITION = "26e4607b-0848-11e6-920c-005056b543f6";//风机盘管温控器（模式设定）
    public static final String WIND_SPEED_SET = "20050d96-085e-11e6-920c-005056b543f6";//风速设定，空调
    public static final String TEMPERATURE_SET = "1b036c3a-085e-11e6-920c-005056b543f6";//温度设定，空调（加热）

    public static final String GAS_REPORT = "b2eab897-0cda-11e6-920c-005056b543f6";//燃气上报
    public static final String WATER_REPORT = "c0563f30-0cda-11e6-920c-005056b543f6";//水浸上报

    public static final String WEIGHT_REPORT = "f9d0ce27-a7b2-11e5-b360-b8975ab8b542";//体重上报
    public static final String SCENE_PANEL = "2448d4a8-4d54-11e6-a31f-005056b543f6";// 情景面板
    public static final String IR_SETING = "69fd1f7d-4e20-11e6-a31f-005056b543f6";// IR码设置
    public static final String AC_REFRIGERATION = "7d0c018c-4e3f-11e6-a31f-005056b543f6";// 空调制冷温度设定
}
