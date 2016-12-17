package com.everyoo.smartgateway.everyoozwave.tronico.kuju;

public class Events {
	public static final int INITIALIZE = 0;
	public static final int INITIALIZE_SUCCESS=1;
	public static final int INITIALIZE_FAILURE=2;
	public static final int USB_DONGLE_TACHED = 3;
	public static final int USB_DONGLE_ATTACHED = 4;
	public static final int USB_DONGLE_DETACHED = 5;
	public static final int INCLUSION = 6;
	public static final int INCLUSION_STARTED = 7;
	public static final int INCLUSION_STOPPED = 6;
	public static final int INCLUSION_SUCCESS = 8;
	public static final int INCLUSION_FAILURE = 9;
	public static final int DEVICE_LIST_UPDATE=7;
	public static final int EXCLUSION = 10;
	public static final int EXCLUSION_STARTED = 11;
	public static final int EXCLUSION_STOPPED = 12;
	public static final int EXCLUSION_SUCCESS = 13;
	public static final int EXCLUSION_FAILURE = 14;
	public static final int DATA_UPDATED = 15;
	public static final int DEVICE_ONLINE_STATUS = 16;
	public static final int DEVICE_ONLINE = 17;
	public static final int DEVICE_OFFLINE = 18;
	public static final int DEVICE_SEND_MESSAGE_RESULT = 19;
	public static final int SEND_SUCCESS = 20;
	public static final int SEND_FAIL = 21;
	public static final int SEND_REPORT = 25;
	public static final int INCLUSION_TIME_OUT = 22;
	public static final int EXCLUSION_TIME_OUT = 23;
	public static final int ZONE_RESULT=24;
	public static final int QUERY_CMD_VER=12;
	public static final int DEVICE_VERSION_REPORT=30;
	public static final int DONGLE_ERROR_REPORT=31;
	public static final int DONGLE_TEST_SEND_REPORT=32;
	//??????б?
	public static final int SUCCESS=0x00;
	public static final int CAN_ERROR=0x02;
	public static final int NAK_ERROR=0x03;
	public static final int NONE_RESPONSE=0x04;
	public static final int UNKNOWN_ERROR=0x05;
	public static final int OPERATION_FAILED=0x07;
	public static final int DEV_NOT_ONLINE=0x06;
	//网关与SDK通信指令格式（cmdcode)
	public static final int DEVICE_STATUS_TYP = 0x00;// 设备状态（在线、离线）
	public static final int POWER_TYP = 0x01; // 功率
	public static final int CONSUMPTION_TYP	= 0x02;// 电量
	public static final int TEMPERATURE_TYP	= 0x03;// 温度
	public static final int MOTION_TYP	= 0x04;// 移动
	public static final int LUX_TYP	= 0x05;// 光度
	public static final int DEVICE_INFOR_TYP= 0x06;// 设备信息
	public static final int DOOR_ONOFF_TYPE	= 0x07;// 门磁开/关
	public static final int BASIC_ONOFF_TYP		= 0x08;// 基本开关信息
	public static final int MULTIPLE_SWITCHES_TYP = 0x09;// 多路开关
	public static final int POSITION_TPY = 0x0A;// 位置(窗帘电机和调光灯用初期时候)
	public static final int SMOKER_STATE = 0x0B;// 烟雾
	public static final int SINGLE_FIRE_SWITCH = 0x0C;// 单火开关
	public static final int LOW_POWER_ALARM = 0X0D;// 低电量报警
	public static final int INITIALIZE_TYP = 0X0D;// 初始化
	public static final int INCLUSION_TYP = 0X0E;// 加网
	public static final int EXCLUSION_TYP = 0X0F;// 退网
//	public static final int USB_DONGLE_TACHED = 0X13;

	public static final int HUMIDITY = 0X0E; // 湿度
	public static final int WIND_SPEED = 0x0F;// 风速
	public static final int RAINFALL = 0X10;// 雨量
	public static final int ULTRAVIOLET_INTENSITY = 0X11;// 紫外线强度
	public static final int LIGHT_INTENSITY = 0X12;// 光强度
	public static final int CO2 = 0X13;// co2
	public static final int PM_TYPE = 0X15;// PM2.5
	public static final int SPEED_TYPE = 0x16; //风速获取
	public static final int SET_TYPE = 0x17; //设置总类
	public static final int WEIGHT = 0x18; //体重
	public static final int FRESH_AIR_MODE = 0x18; //模式
	public static final int SPEED_AIR_MODE = 0x19; //风速模式获取
	public static final int SCENE_PANEL = 0x20;//场景面板
	public static final int AIRCONDITIONER_TEMP = 0x21; //空调控制器 温度类
	public  static final int AIRCONDITIONER_SET = 0x22;//空调控制器 配置类
	public static final int AIRCONDITIONER_TEMP_REPORT = 0x23;//空调控制器温度上报
	public static final int BINARY_ONOFF_REPORT = 0x24;//binary 上报
	public static final int DOOR_REPORT = 0x25;//锁上报
	public static final int SOS_DEVICE_REPORT = 0x26;







}
