package com.everyoo.smartgateway.everyoozwave;

/**
 * Created by chaos on 2016/1/8.
 */
public class ZWAVE {

    // 网关与云端通信指令
    public static final int INITIALIZE = 0X00;// dongle初始化
    public static final int EXCLUSION = 0X10;// 退网
    public static final int INCLUSION = 0X11;// 加网
    public static final int DEVICE_INFO = 0X12;// 设备信息
    public static final int DEVICE_STATUS = 0X13;// 设备状态（在线、离线）
    public static final int USB_DONGLE = 0x14;// dongle状态；value = 0：拔出；value = 1：插入

    public static final int SWITCH_STATU_REPORT = 0X21;// 开关状态
    public static final int MULTILEVEL_SWITCH_STATU_REPORT = 0X22;// 多级开关状态
    public static final int BACKGROUD_MUSIC_TYPE = 0X23;// 背景音乐控制
    public static final int INSTANT_POWER = 0X31;// 瞬时功率
    public static final int CUMULATIVE_POWER = 0X32;// 累积功率
    public static final int SENSOR_TEMPERATURE = 0X41;// 温度
    public static final int SENSOR_MOTION = 0X42;// 移动、震动（三合一，门磁等设备）
    public static final int SENSOR_LIGHT = 0X43;// 亮度
    public static final int SENSOR_DOOR_SWITCH = 0X44;// 门磁开关
    public static final int SENSOR_SMOKER_ALARM = 0X45;// 烟雾报警
    public static final int SENSOR_WIND_STRENGTH = 0X46;// 风度
    public static final int SENSOR_PHOTOMETRIC = 0X47;// 光度
    public static final int SENSOR_RAINFALL = 0X48;// 雨量
    public static final int SNESOR_HUMIDITY = 0X49;// 湿度
    public static final int SENSOR_ULTRAVIOLET_INTENSITY = 0X4A;// 紫外线强度
    public static final int CO2_CONCENTRATION_TYPE = 0X4B; // 二氧化碳浓度
    public static final int PM25_CONCENTRATION_TYPE = 0X4C; // PM2.5浓度
    public static final int GAS_ALARM = 0X4D;// 燃气报警
    public static final int WATER_ALARM = 0X4E;// 水浸报警
    public static final int WEIGHT_SCALE = 0X4F;// 重量
    public static final int FAN_COIL_TYPE = 0X24;
    public static final int TIME_OUT = 0XFF;

    public static final int SPEED_SETTING = 0X51;// 风速设定
    public static final int TEMPERATURE_SETTING = 0X50;// 温度设定
    public static final int TEMPERATURE_SELECT = 0X53;// 温度查询
    public static final int MODE_SETTING = 0X24;




    //网关与SDK通信指令格式（cmdcode)

    public static final int DEVICE_STATUS_TYP = 0x00;// 设备状态（在线、离线）
    public static final int POWER_TYP = 0x01; // 功率
    public static final int CONSUMPTION_TYP	= 0x02;// 电量
    public static final int TEMPERATURE_TYP	= 0x03;// 温度
    public static final int COMPARE_TEMPERATURE = 0X00; // 对比温度
    public static final int ENVIRONMENT_TEMPERATURE = 0X01;// 环境温度
    public static final int MOTION_TYP	= 0x04;// 移动
    public static final int LUX_TYP	= 0x05;// 光度
    public static final int DEVICE_INFOR_TYP= 0x06;// 设备信息
    public static final int DOOR_ONOFF_TYPE	= 0x07;// 门磁开/关
    public static final int BASIC_ONOFF_TYP		= 0x08;// 基本开关信息
    public static final int MULTIPLE_SWITCHES_TYP = 0x09;// 多路开关
    public static final int POSITION_TPY = 0x0A;// 位置(窗帘电机和调光灯用初期时候)
    public static final int SMOKER_STATE = 0x0B;// 烟雾
    public static final int SINGLE_FIRE_SWITCH = 0x0C;// 单火开关
    public static final int INITIALIZE_TYP = 0X0D;// 初始化
    public static final int INCLUSION_TYP = 0X0E;// 加网
    public static final int EXCLUSION_TYP = 0X0F;// 退网
    public static final int USB_DONGLE_TACHED = 0X11;
    public static final int BACKGROUD_MUSIC_CONTROL = 0X12;
    public static final int CO2_TYPE = 0X13;
    public static final int PM25_TYPE = 0X14;
 //   public static final int MODE_SETTING = 0X19 ;// 风机盘管模式
    public static final int SET_PARAMS = 0X20;// 设置
    public static final int SPEED_TYPE = 0X21;// 风速设置

    public static final int HUMIDITY = 0X12; // 湿度
    public static final int WIND_SPEED = 0x13;// 风速
    public static final int RAINFALL = 0X14;// 雨量
    public static final int ULTRAVIOLET_INTENSITY = 0X15;// 紫外线强度
    public static final int LIGHT_INTENSITY = 0X16;// 光强度
    public static final int CO2_CONCENTRATION = 0X17;// 二氧化碳浓度
    public static final int PM25_CONCENTRATION = 0X18;// PM25浓度




    public static final int INCLUSION_TIME_OUT = 0X18;
    public static final int EXCLUSION_TIME_OUT = 0X19;





    // 网关与SDK通信指令格式 （value)
    public static final int DEVICE_OFFLINE = 0X01;      // 设备离线
    public static final int DEVICE_ONLINE = 0X00;       // 设备在线
    public static final int INITIALIZE_SUCCESS = 0x01;  // 初始化成功
    public static final int INITIALIZE_FAILURE = 0x00;     // 初始化失败
    public static final int TIMEOUT = 0x00;
    public static final int USB_DONGLE_ATTACHED = 0x01; // DONGLE拔出
    public static final int USB_DONGLE_DETACHED = 0x00; // DONGLE插入

    public static final int INCLUSION_STARTED = 0x00;  // 入网开始
    public static final int INCLUSION_STOPPED = 0x01;  // 入网中止
    public static final int INCLUSION_SUCCESS = 0x02;    // 入网成功
    public static final int INCLUSION_FAILURE = 0xFF;   // 入网失败（超时）

    public static final int EXCLUSION_STARTED = 0x00;  // 退网开始
    public static final int EXCLUSION_STOPPED = 0x01;  // 退网中止
    public static final int EXCLUSION_SUCCESS = 0x02;    // 退网成功
    public static final int EXCLUSION_FAILURE = 0xFF;   // 退网失败（超时）

    public static final int BASIC_OFF = 0X00;// 设备关闭
    public static final int BASIC_ON = 0X01;// 设备打开
    public static final int BASIC_SWITCH_OFF_ONE = 0X00;// 一路关闭
    public static final int BASIC_SWITCH_ON_ONE = 0X01;// 一路打开
    public static final int BASIC_SWITCH_OFF_TWO = 0X10;// 二路关闭
    public static final int BASIC_SWITCH_ON_TWO = 0X11;// 二路打开
    public static final int BASIC_SWITCH_OFF_THREE = 0x20;// 三路关闭
    public static final int BASIC_SWITCH_ON_THREEE = 0X21;// 三路打开

    public static final int DOOR_ELECTRIC_OFF = 0X00;// 门磁关闭
    public static final int DOOR_ELECTRIC_ON = 0X01;// 门磁打开
    public static final int MOTION_OFF = 0X00;// 震动取消
    public static final int MOTION_ON = 0X01;// 震动发生

    public static final int SMOKE_OFF = 0X00;// 烟雾报警取消
    public static final int SMOKE_ON = 0X01;// 烟雾报警

    public static final int WATER_OFF = 0X00;// 水浸报警取消
    public static final int WATER_ON = 0X01;// 水浸报警

    public static final int GAS_OFF = 0X00;// 燃气报警取消
    public static final int GAS_ON = 0X01;// 燃气报警

    public static final int DEVICE_SEND_MESSAGE_RESULT = 0X99;//设备控制指令结果
    public static final int OPERATION_SEND_SUCCESS = 0x01;  //网关给设备发送指令成功
    public static final int OPERATION_SEND_FAIL = 0x00;  //网关给设备发送指令失败








    public static final int DEVICE_LIST_UPDATE = 7;

    public static final int DATA_UPDATED = 11;

    public static final int QUERY_CMD_VER = 12;
    public static final int SUCCESS = 0;
    public static final int CAN_ERROR = 2;
    public static final int NAK_ERROR = 3;
    public static final int NONE_RESPONSE = 4;
    public static final int UNKNOWN_ERROR = 5;
    public static final int OPERATION_FAILED = 7;
    public static final int DEV_NOT_ONLINE = 6;
    public static final int NORMAL_ACTION = 0;
    public static final int INCLUSION_ACTION = 1;
    public static final int EXCLUSION_ACTION = 2;



    // 网关和云端指令分类
    public static final int CTRL = 1; // 控制类
    public static final int SCENE = 2;// 场景
    public static final int LINKAGE = 3;// 联动
    public static final int TIMING = 4;// 定时
    public static final int REPORT = 5;// 自动上报
    public static final int DEVICEINFO = 6;// 设备信息
    public static final int DEVICE_INCLUSION = 7;// 设备入网
    public static final int DEVICE_EXCLUSION = 8;// 设备退网
    public static final int DEVICE_STATU = 9;// 设备状态

    public static final int DONGLE_INITIALIZE = 11;// DONGLE初始化


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
    public static final int FLAG_GAS_ALARM =  11; // 燃气报警器
    public static final int FLAG_WATER_SENSOR = 12; // 水浸传感器
    public static final int FLAG_AC_CONTROLLER = 13; // 空调控制器
    public static final int FLAG_SIGLE_FIRE_SWITCH = 14; // 单火开关
    public static final int FLAG_DOUBLE_FIRE_SWITCH = 15;//二路开关
    public static final int FLAG_THREE_FIRE_SWITCH = 16;//三路开关
    public static final int FLAG_MASTER_OF_SCENE = 17;// 情景大师
    public static final int FLAG_FOUR_ONE_SENSOR = 18;// 四合一传感器

    public static final int FLAG_THREE_CONTROL = 19;//三路控制

    public static final int FLAG_FAN_SCOIL  = 27;// 新风系统
    public static final int FLAG_HAIMAN_MAGNETIC_DOOR = 37;// 海曼门磁


}
