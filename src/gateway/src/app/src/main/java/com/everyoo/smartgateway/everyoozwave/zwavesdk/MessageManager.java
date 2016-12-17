package com.everyoo.smartgateway.everyoozwave.zwavesdk;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.everyoo.smartgateway.everyoocore.networkobserver.IndicatorLigthAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sp.SPHelper;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.CtrlDao;
import com.everyoo.smartgateway.everyoozwave.ZWAVE;
import com.everyoo.smartgateway.everyoozwave.tronico.kuju.Controller;
import com.everyoo.smartgateway.everyoozwave.tronico.kuju.DeviceInfo;
import com.everyoo.smartgateway.everyoozwave.tronico.kuju.Events;
import com.everyoo.smartgateway.smartgateway.ActionId;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;
import com.everyoo.smartgateway.utils.WifiUtil;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chaos on 2016/3/30.
 * 功能描述：网关和sdk交互的中间类
 */
public class MessageManager {

    private static final String TAG = "MessageManager ";
    public static final String ACTION_UPLOAD_MESSAGE = "action.upload.sdk.message";
    private int type;
    private int nodeId;
    private String value;
    private String keepField;

    private static int INITIALIZE_TIMES;
    private int MAX_INITIALILZE_TIMES = 12;

    private static Context mContext;
    public static Controller controller;

    public static MessageManager instance;
    private static volatile String userId;
    private static volatile String userIdProcessor;
    private static volatile String actionId;
    //private static volatile int ctrlNodeId;
    private boolean isFilter = false;
    private MessageProcessor messageProcessor;
    private static boolean isInitializeSDK = false;
    private EveryooHttp mHttp;
    private static CtrlDao mCtrlDao;

    public MessageManager(final Context context) {
        mContext = context;
        messageProcessor = new MessageProcessor(context);
        controller = new Controller(context, null);
        receiveMessageFromSdk();
        mHttp = InitApplication.mHttp;
        mCtrlDao = (CtrlDao) InitApplication.mSql.getSqlDao(CtrlDao.class);
        //initializeDongle();
    }

    public static MessageManager getInstance(Context context) {
        if (instance == null) {
            instance = new MessageManager(context);
        }
        LogUtil.controlLog(TAG + "getInstance", "initialize dongle");
        initializeDongle();
        return instance;
    }

    /**
     * 网关给sdk下发指令
     */
    public static void dealMessageToSdk(int nodeId, int value, String actionId, String userId) {
        UserProcessAction.userAdd(nodeId, value, userId);
        instance.userIdProcessor = userId;
        //ctrlNodeId = nodeId;
        switch (actionId) {
            case ActionId.USB_DONGLE_RESET:// 网关解绑，清除节点
                LogUtil.controlLog(TAG + "dealMessageToSdk clear dongle node");
                controller.clearDongle();
                break;
            case ActionId.DONGLE_INITIALIZE:
                isInitializeSDK = true;
                LogUtil.controlLog(TAG + "sendMessage initialize dongle");
                INITIALIZE_TIMES++;
                if (controller == null) {
                    controller = new Controller(mContext, null);
                }
                controller.closeDev(false);
                controller.initDevice();

                break;
            case ActionId.DEVICE_EXCLUSION:
                LogUtil.controlLog(TAG + "sendMessage exclusion device and value = " + value);
                if (value == 0) {// exclusion start
                    controller.startExclusion();
                } else { // vallue = 1;exclusion stop
                    controller.stopExclusion();
                }
                break;
            case ActionId.DEVICE_INCLUSION:
                LogUtil.controlLog(TAG + "sendMessage inclusion device and value = " + value);
                if (value == 0) {
                    controller.startInclusion();
                } else { // value = 1;inclusion stop
                    controller.stopInclusion();
                }
                break;
            case ActionId.DEVICE_SWITCH:
                LogUtil.controlLog(TAG + "sendMessage switch basic set and value = " + value + " nodeId = " + nodeId);
                if (value == 0x00 || value == 0x01) { // 00：设备关闭；01；设备打开；
                    if (mCtrlDao.selectType(nodeId) == Constants.FLAG_OPEN_DOOR) {
                        if (value == 0x00) {
                            value = -1;
                        }
                        LogUtil.controlLog(TAG + "sendMessage open door");
                        controller.openDoor(nodeId, value);
                    } else {
                        LogUtil.controlLog(TAG + "sendMessage switch basic set 1 road");
                        if (value != 0x00) {
                            controller.setOnOff(nodeId, 0xff);
                        } else {
                            controller.setOnOff(nodeId, value);
                        }
                    }

                }
                break;
            case ActionId.PERCENT://窗帘控制
                LogUtil.controlLog(TAG + "sendMessage curtain control and value = " + value + "ctrlNodeId = " + nodeId);
                if (value == -2) {
                    controller.StopDevChanging(nodeId);
                } else {
                    controller.CtrlDevByPercentage(nodeId, value);
                }
                break;
            case ActionId.BACKGROUND_MUSIC:// 背景音乐
                //value值关机 0x00 开机 0xFF 播放 0x01 暂停 0x02 停止 0x03  音量减0x04 音量加0x05 上一首0x06 下一首0x07 静音开0x08 静音关0x09 音量调节0x0A 全部0x0B 内存0x0C SD卡0x0D
                LogUtil.controlLog(TAG + "sendMessage background music");
                controller.executeCommond(nodeId, value, 0);
                break;
            case ActionId.HUMIDITY_REPORT:  // 湿度查询
                LogUtil.controlLog(TAG + "sendMessage humudity select and nodeId = " + nodeId);
                controller.setCommondForHum(nodeId);
                break;
            case ActionId.TEMPERATURE_REPORT:// 温度查询
                LogUtil.controlLog(TAG + "sendMessage temperature select and nodeId = " + nodeId + "value = " + value);
                if (mCtrlDao.selectType(nodeId) == Constants.FLAG_AC_CONTROLLER) {
                    controller.queryTemForAirConditioner(nodeId);// 查询空调红外室内温度
                } else {
                    controller.setCommondForTem(nodeId);
                }
                break;
            case ActionId.CO2_REPORT:  //value  0x00 co2  value 0x01 温度 value 0x02 湿度
                LogUtil.controlLog(TAG + "sendMessage co2 select and nodeId = " + nodeId);
                controller.setCommondForCO2(nodeId);
                break;
            case ActionId.PM_REPORT: //value  0x00 PM2.5  value 0x01 温度 value 0x02 湿度
                LogUtil.controlLog(TAG + "sendMessage pm2.5 select and nodeId = " + nodeId);
                controller.setCommondForPM(nodeId);
                break;
            case ActionId.WIND_SPEED_SET:// 风速设置 0x00 风扇低速 0x01 风扇中速 0x02 风扇高速 0x03 温控器风扇模式下模式请求 0x04:静音模式 0x05：标准模式 0x06：强力模式 0x07:自动
                LogUtil.controlLog(TAG + "sendMessage SPEED_SETTING and value = " + value + "nodeId = " + nodeId);
                //deviceType = DevInforTempTableDao.getInstance(mContext).select(nodeId);
                //   if (deviceType == ZWAVE.FLAG_FAN_SCOIL && value == 0x03) {
                if (value == 0x03) {//主动请求当前模式
                    if (mCtrlDao.selectType(nodeId) == Constants.FLAG_AC_CONTROLLER) {
                        controller.speedModeSearch(nodeId);// 空调红外风速查询
                    } else {
                        controller.getSpeedForFreshAir(nodeId);
                    }
                } else {
                    controller.setCommondForWindSet(nodeId, value);
                }

                break;
            case ActionId.AIR_CONDITION:// 模式设置 0x00 关机模式 0x01加热模式 0x02制冷模式 0x03 自动模式 0x04 只开风扇 0x05 当前模式查询 0x06：净化模式  0x07：新风模式
                LogUtil.controlLog(TAG + "sendMessage MODE_SETTING and value = " + value + "nodeId = " + nodeId + "value = " + value);
                // deviceType = DevInforTempTableDao.getInstance(mContext).select(nodeId);
                //    if (deviceType == ZWAVE.FLAG_FAN_SCOIL && value == 0x05) {
                if (value == 0x05) { //主动请求当前模式
                    if (mCtrlDao.selectType(nodeId) == Constants.FLAG_AC_CONTROLLER) {
                        controller.searchMode(nodeId);// 空调红外模式查询
                    } else {
                        controller.getModeForFreshAir(nodeId);
                    }
                } else {
                    controller.setCommondForActiveRequest(nodeId, value);
                }

                break;
            case ActionId.TEMPERATURE_SET:// 设定温度的查询和设置(加热）
                LogUtil.controlLog(TAG + "sendMessage TEMPERATURE_SETTING and value = " + value + "nodeId = " + nodeId);
                if (mCtrlDao.selectType(nodeId) == Constants.FLAG_AC_CONTROLLER) {
                    if (value == 0x00) {
                        controller.setTemForAirConditioner(nodeId, value, 2);// 0.加热设定 1.制冷设定 2加热查询 3.制冷查询 4 室温查询
                    } else {
                        controller.setTemForAirConditioner(nodeId, value, 0);
                    }

                } else {
                    if (value == 0x00) {// 温度查询
                        controller.setCommondForTemp(nodeId, 1, value);
                    } else {
                        controller.setCommondForTemp(nodeId, 0, value);
                    }
                }

                break;
            case ActionId.AC_REFRIGERATION:// 设定温度的查询和设置(制冷）
                LogUtil.controlLog(TAG + "sendMessage AC_REFRIGERATION and value = " + value + "nodeId = " + nodeId);
                if (mCtrlDao.selectType(nodeId) == Constants.FLAG_AC_CONTROLLER) {
                    if (value == 0x00) {
                        controller.setTemForAirConditioner(nodeId, value, 3);// 0.加热设定 1.制冷设定3加热查询 4.制冷查询5室温查询
                    } else {
                        controller.setTemForAirConditioner(nodeId, value, 1);
                    }

                } else {

                }
                break;
            case ActionId.SIGLE_FIRE_SWITCH:// 一路开关
                LogUtil.controlLog(TAG + "sendMessage single road switch ctrlNodeId = " + nodeId + " value = " + value);
                if (value == 0x00) {
                    controller.SetMutilSwitchOnOff(nodeId, 1, value);
                } else if (value == 0x01) {
                    controller.SetMutilSwitchOnOff(nodeId, 1, 0xff);
                } else {
                    LogUtil.controlLog(TAG + "sendMessage single road switch value invalid");
                }
                break;
            case ActionId.DOUBLE_FIRE_SWITCH://二路开关
                LogUtil.controlLog(TAG + "sendMessage two road switch ctrlNodeId = " + nodeId + " value = " + value);
                if (value == 0x00) {
                    controller.SetMutilSwitchOnOff(nodeId, 2, value);
                } else if (value == 0x01) {
                    controller.SetMutilSwitchOnOff(nodeId, 2, 0xff);
                } else {
                    LogUtil.controlLog(TAG + "sendMessage two road switch value invalid");
                }
                break;
            case ActionId.THREE_FIRE_SWITCH:// 三路开关
                LogUtil.controlLog(TAG + "sendMessage three road switch ctrlNodeId = " + nodeId + " value = " + value);
                if (value == 0x00) {
                    controller.SetMutilSwitchOnOff(nodeId, 3, value);
                } else if (value == 0x01) {
                    controller.SetMutilSwitchOnOff(nodeId, 3, 0xff);
                } else {
                    LogUtil.controlLog(TAG + "sendMessage three road switch value invalid");
                }
                break;
            case ActionId.IR_SETING:// 空调红外IR码设置
                controller.AirConditionerSet(nodeId, value, 0);
                break;
            default:
                LogUtil.controlLog(TAG + "sendMessage cmdCode is unExisted and cmdCode = ");

                break;
        }
    }

    /**
     * 网关接收sdk上报指令
     *
     * @param message
     */
    public void dealMessageToGateway(Message message) {
        parseSdkMessage(message);
        switch (message.what) {
            case Events.INITIALIZE:
                initializeProcessor(type, nodeId);
                break;
            case Events.USB_DONGLE_TACHED:
                usbTachedProcessor(type, nodeId);
                break;
            case Events.INCLUSION:
                inclusionProcessor(type, nodeId);
                break;
            case Events.EXCLUSION:
                exclusionProcessor(type, nodeId);
                break;
            case Events.DEVICE_ONLINE_STATUS:
                deviceStatusProcessor(type);
                break;
            case Events.DATA_UPDATED:
                updateProcessor(type, nodeId, value, keepField);
                break;
            case Events.DEVICE_SEND_MESSAGE_RESULT:
                LogUtil.controlLog(TAG + "dealMessageToGateway nodeId = " + nodeId);
                controlCompletion(type, nodeId);
                break;

            case Events.DEVICE_VERSION_REPORT:
                Log.d("karl", "nodeId" + nodeId + " version==>" + value + ", firmwarever=" + keepField);

                break;

            case Events.DONGLE_ERROR_REPORT:
                Log.d("karl", "dongle error report, now reset dongle");
                if (!isInitializeSDK) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mHttp.reportedSdkStates(mContext, Constants.SDK_UNUSABLE);
                        }
                    });
                    initializeDongle_ErrorHandler();
                } else {
                    // LogUtil.println(TAG + "dealMessageToGateway", "initializing sdk");
                    LogUtil.controlLog(TAG + "dealMessageToGateway", "initializing sdk");
                }
                break;
            default:
                Log.i(TAG, "receiveMessage message.what is default and what = " + message.what);
                break;
        }
    }


    /**
     * 解析sdk上报的数据
     *
     * @param message
     */
    public void parseSdkMessage(Message message) {
        if (!message.equals("")) {
            Bundle bundle = message.getData();
            type = bundle.getInt("type");
            nodeId = bundle.getInt("nodeId");
            value = bundle.getString("value");
            keepField = bundle.getString("keepField");
        } else {
            LogUtil.controlLog(TAG + "parseSdkMessage message is equals null");
        }
    }

    /**
     * 初始化dongle方法
     */
    public static void initializeDongle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.controlLog(TAG + "initializeDongle", "begin initialize dongle");
                dealMessageToSdk(0, 0, ActionId.DONGLE_INITIALIZE, null);
            }
        }).start();
    }

    public static void initializeDongle_ErrorHandler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.println(TAG + "initializeDongle_ErrorHandler", "begin initialize dongle");
                dealMessageToSdk(0, 0, ActionId.DONGLE_INITIALIZE, null);
            }
        }).start();
    }

    /**
     * 初始化事件处理器
     */
    private void initializeProcessor(int type, int nodeId) {
        if (type == Events.INITIALIZE_SUCCESS) {
            isInitializeSDK = false;
            Constants.isSdkEnabled = true;
            LogUtil.controlLog(TAG + "initializeProcessor initialize success");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mHttp.reportedSdkStates(mContext, Constants.SDK_USABLE);
                }
            });
            INITIALIZE_TIMES = 0;
            if (!Constants.isBind) {
                return;
            }
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(0, ZWAVE.INITIALIZE_SUCCESS + "", ActionId.ZWAVE_SDK_ENABLE, null));
            if (Constants.isPjSipEnabled) {
                IndicatorLigthAction.runNormally(mContext);
            } else {
                NetworkInfo networkInfo = WifiUtil.getActiveNetworkInfo(mContext);
                if (networkInfo != null) {
                    if (WifiUtil.isNetworkConnectedByPing()) {
                        LogUtil.controlLog("虽然有网，但是sip无连接");
                        IndicatorLigthAction.networkConnectedButNoData(mContext);
                    } else {
                        LogUtil.println(TAG + "processor", "wifi connected but no data");
                        IndicatorLigthAction.networkConnectedButNoData(mContext);
                    }
                } else {
                    IndicatorLigthAction.noNetworkConnected(mContext);
                }
            }
        } else {
            LogUtil.controlLog(TAG + "initializeProcessor initialize failure");
            if (INITIALIZE_TIMES <= MAX_INITIALILZE_TIMES) {
                try {
                    Thread.sleep(5 * 1000);
                    INITIALIZE_TIMES++;
                    initializeDongle();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isInitializeSDK = false;
                Constants.isSdkEnabled = false;
                if (!Constants.isBind) {
                    return;
                }
                LogUtil.controlLog(TAG + "initializeProcessor initialize failure and times is over");
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(0, ZWAVE.INITIALIZE_FAILURE + "", ActionId.ZWAVE_SDK_ENABLE, null));
                if (Constants.isPjSipEnabled) {
                    IndicatorLigthAction.sdkException(mContext);
                } else {
                    NetworkInfo networkInfo = WifiUtil.getActiveNetworkInfo(mContext);
                    if (networkInfo != null) {
                        if (WifiUtil.isNetworkConnectedByPing()) {
                            LogUtil.controlLog("虽然有网，但是sip无连接");
                            IndicatorLigthAction.networkConnectedButNoData(mContext);
                        } else {
                            LogUtil.println(TAG + "processor", "wifi connected but no data");
                            IndicatorLigthAction.networkConnectedButNoData(mContext);
                        }
                    } else {
                        IndicatorLigthAction.noNetworkConnected(mContext);
                    }
                }
            }
        }


    }

    /**
     * usb插拔事件处理器
     */
    public void usbTachedProcessor(int type, int nodeId) {
        if (type == Events.USB_DONGLE_ATTACHED) {
            LogUtil.controlLog(TAG + "usbTachedProcessor usb attached");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.USB_DONGLE_ATTACHED + "", ActionId.USB_DONGLE_TACHED, null));
        } else {
            LogUtil.controlLog(TAG + "usbTachedProcessor usb detached");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.USB_DONGLE_DETACHED + "", ActionId.USB_DONGLE_TACHED, null));
        }
    }

    int addDeviceNodeId = -1;

    /**
     * 加网事件处理器
     */
    public void inclusionProcessor(int type, int nodeId) {
        if (type == Events.INCLUSION_STARTED) {
            LogUtil.controlLog(TAG + "inclusionProcessor inclusion start ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.INCLUSION_STARTED + "", ActionId.DEVICE_INCLUSION, userIdProcessor));
        } else if (type == Events.INCLUSION_STOPPED) {
            LogUtil.controlLog(TAG + "inclusionProcessor inclusion stop ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.INCLUSION_STOPPED + "", ActionId.DEVICE_INCLUSION, userIdProcessor));
        } else if (type == Events.INCLUSION_SUCCESS) {
            LogUtil.controlLog(TAG + "inclusionProcessor inclusion success and nodeId = " + nodeId);
            if (nodeId >= 1 && nodeId <= 232 && nodeId != addDeviceNodeId) {
                addDeviceNodeId = nodeId;
                new ParseDeviceInfoAction(mContext).parseDeviceInfo(controller.GetNodeByNodeid(nodeId), userIdProcessor);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.INCLUSION_SUCCESS + "", ActionId.DEVICE_INCLUSION, userIdProcessor));
            } else {
                LogUtil.println(TAG + "inclusionProcessor", "nodeId is unFormat");
            }
        } else if (type == Events.INCLUSION_FAILURE) {
            LogUtil.controlLog(TAG + "inclusionProcessor inclusion failure ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.INCLUSION_FAILURE + "", ActionId.DEVICE_INCLUSION, userIdProcessor));
        } else {
        }
    }

    /**
     * 退网事件处理器
     */
    public void exclusionProcessor(int type, int nodeId) {
        if (type == Events.EXCLUSION_STARTED) {
            LogUtil.controlLog(TAG + "exclusionProcessor exclusion start ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.EXCLUSION_STARTED + "", ActionId.DEVICE_EXCLUSION, userIdProcessor));
        } else if (type == Events.EXCLUSION_STOPPED) {
            LogUtil.controlLog(TAG + "exclusionProcessor exclusion stop ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.EXCLUSION_STOPPED + "", ActionId.DEVICE_EXCLUSION, userIdProcessor));
        } else if (type == Events.EXCLUSION_SUCCESS) {
            LogUtil.controlLog(TAG + "exclusionProcessor exclusion success ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.EXCLUSION_SUCCESS + "", ActionId.DEVICE_EXCLUSION, userIdProcessor));
        } else if (type == Events.EXCLUSION_FAILURE) {
            LogUtil.controlLog(TAG + "exclusionProcessor exclusion failure ");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.EXCLUSION_FAILURE + "", ActionId.DEVICE_EXCLUSION, userIdProcessor));
        } else {

        }
    }

    /**
     * 设备上报状态事件处理器
     */
    public void updateProcessor(int type, int nodeId, String value, String keepField) {
        // LogUtil.controlLog(TAG + "updateProcessor nodeId = " + nodeId + " userId = " + userId);
        LogUtil.controlLog(TAG, "updateProcessor nodeId = " + nodeId);
        switch (type) {
            case Events.POWER_TYP:// 瞬时功率
                LogUtil.controlLog(TAG + "updateProcessor type equals instant power and value is " + value);
                //  sendMessageToRecSdkInfoService(messageGenerateAction.ctrMessageGenerator(null, nodeId, ZWAVE.POWER_TYP, value, null));
                break;
            case Events.CONSUMPTION_TYP:// 累计功率
                LogUtil.controlLog(TAG + "updateProcessor type equals consumption power and value is " + value);
                //   value = PowerAction.processPower(value, String.valueOf(nodeId), mContext);//处理功率的累计
                // float power = PowerAction.powerPorcessor(String.valueOf(nodeId), value, mContext);
                float power = SPHelper.getInstance().powerPorcessor(String.valueOf(nodeId), value, mContext);
                if (power > 0) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, new DecimalFormat("###,###,##0.0000").format(power), ActionId.POWER_REPORT, null));
                }
                LogUtil.controlLog(TAG + "updateProcessor type equals consumption process power and value is " + value);
                break;
            case Events.TEMPERATURE_TYP:// 温度
                LogUtil.controlLog(TAG + "updateProcessor type equals temperature and value is " + value + "keepField = " + keepField);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.TEMPERATURE_REPORT, null));
                break;
            case Events.MOTION_TYP:// 移动、震动、人体移动侦测
                LogUtil.controlLog(TAG + "updateProcessor type equals motion and value is " + value);
                if (Integer.parseInt(value) == 0xff) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.MOTION_ON + "", ActionId.MOVE_REPORT, null));
                } else {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.MOTION_OFF + "", ActionId.MOVE_REPORT, null));
                }
                break;
            case Events.LUX_TYP:// 光度
                LogUtil.controlLog(TAG + "updateProcessor type equals brightness and value is " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.LIGHT_REPORT, null));
                break;
            case Events.DEVICE_INFOR_TYP:// 设备信息
                LogUtil.controlLog(TAG + "updateProcessor type equals device info and value is " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.DEVICE_INFOMATION, null));
                break;
            case Events.DOOR_ONOFF_TYPE:// 门磁开关
                int deviceType = mCtrlDao.selectType(nodeId);
                Log.d("karl", "nodeid = " + nodeId + ", deviceType= " + deviceType + ", value = " + value);
                if (deviceType == ZWAVE.FLAG_DIMMER_SWITCH) {
                    LogUtil.controlLog(TAG + "updateProcessor type equals dimmer off and value is " + value);
                    doorDecelerator(Integer.parseInt(value), nodeId, ZWAVE.FLAG_DIMMER_SWITCH);
                } else if (deviceType == ZWAVE.FLAG_SMOKE_ALARM) {
                    LogUtil.controlLog(TAG + "updateProcessor type equals smoke alarm and value = " + value);
                    doorDecelerator(Integer.parseInt(value), nodeId, ZWAVE.FLAG_SMOKE_ALARM);
                } else if (deviceType == ZWAVE.FLAG_WATER_SENSOR) {
                    LogUtil.controlLog(TAG + "updateProcessor type equals water alarm and value = " + value);
                    doorDecelerator(Integer.parseInt(value), nodeId, ZWAVE.FLAG_WATER_SENSOR);
                } else if (deviceType == ZWAVE.FLAG_GAS_ALARM) {
                    LogUtil.controlLog(TAG + "updateProcessor type equals gas alarm and value = " + value);
                    doorDecelerator(Integer.parseInt(value), nodeId, ZWAVE.FLAG_GAS_ALARM);
                } else if (deviceType == ZWAVE.FLAG_MAGNETIC_DOOR || deviceType == ZWAVE.FLAG_HAIMAN_MAGNETIC_DOOR) {
                    doorDecelerator(Integer.parseInt(value), nodeId, ZWAVE.FLAG_MAGNETIC_DOOR);
                }
                break;
            case Events.BASIC_ONOFF_TYP:// 基本开关
                userId = UserProcessAction.userSelect(nodeId, value);
                LogUtil.controlLog(TAG + "updateProcessor type equals basic on or off and value is " + value + "mUserId = " + userId + "nodeId = " + nodeId);
                if (Integer.parseInt(value) == ZWAVE.BASIC_OFF) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.DEVICE_SWITCH, userId));
                } else {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.DEVICE_SWITCH, userId));
                }

                break;
            case Events.MULTIPLE_SWITCHES_TYP:// 多路开关
                LogUtil.controlLog(TAG + "updateProcessor type equals multiple switch and value is " + value);
                userId = UserProcessAction.userSelect(nodeId, value);
                if (keepField.equals("1")) {
                    if (Integer.parseInt(value) == 0x00) {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.SIGLE_FIRE_SWITCH, userId));
                    } else {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.SIGLE_FIRE_SWITCH, userId));
                    }
                } else if (keepField.equals("2")) {
                    if (Integer.parseInt(value) == 0x00) {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.DOUBLE_FIRE_SWITCH, userId));
                    } else {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.DOUBLE_FIRE_SWITCH, userId));
                    }
                } else if (keepField.equals("3")) {
                    if (Integer.parseInt(value) == 0x00) {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.THREE_FIRE_SWITCH, userId));
                    } else {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.THREE_FIRE_SWITCH, userId));
                    }
                }

                break;
            case Events.POSITION_TPY:// 百分比控制类设备的开关比
                LogUtil.controlLog(TAG + "updateProcessor type equals position and value is " + value);
                userId = UserProcessAction.userSelect(nodeId, value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.PERCENT, userId));
                break;
            case Events.SMOKER_STATE:// 烟雾报警
                LogUtil.controlLog(TAG + "updateProcessor type equals smoke and value is " + value);
                if (Integer.parseInt(value) == 0xff) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.SMOKE_ON + "", ActionId.SMOKER_REPORT, null));
                } else {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.SMOKE_OFF + "", ActionId.SMOKER_REPORT, null));
                }
                break;
            case Events.SINGLE_FIRE_SWITCH:// 单火开关
                LogUtil.controlLog(TAG + "updateProcessor type equals single fire and value is " + value);
                userId = UserProcessAction.userSelect(nodeId, value);
                if (keepField.equals("1")) {
                    if (Integer.parseInt(value) == 0x00) {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.SIGLE_FIRE_SWITCH, userId));
                    } else {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.SIGLE_FIRE_SWITCH, userId));
                    }
                } else if (keepField.equals("2")) {
                    if (Integer.parseInt(value) == 0x00) {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.DOUBLE_FIRE_SWITCH, userId));
                    } else {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.DOUBLE_FIRE_SWITCH, userId));
                    }
                } else if (keepField.equals("3")) {
                    if (Integer.parseInt(value) == 0x00) {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.THREE_FIRE_SWITCH, userId));
                    } else {
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.THREE_FIRE_SWITCH, userId));
                    }
                }

                break;
            case Events.HUMIDITY:// 风光雨,二氧化碳，PM2.5 湿度
                LogUtil.controlLog(TAG + "updateProcessor type equals humidity and value is " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.HUMIDITY_REPORT, null));
                break;
            case Events.WIND_SPEED:// 风光雨风度
                LogUtil.controlLog(TAG + "updateProcessor type equals wind and value is " + value);
                if (value.contains("-1")) {
                    value = "1";
                }
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.WIND_SPEED_REPORT, null));
                break;
            case Events.RAINFALL:// 风光雨雨量
                LogUtil.controlLog(TAG + "updateProcessor type equals rainfall and value is " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.RAINFALL_REPORT, null));
                break;
            case Events.ULTRAVIOLET_INTENSITY:// 风光雨紫外线强度
                LogUtil.controlLog(TAG + "updateProcessor type equals ultraviolet and value is " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.ULTRAVIOLET_REPORT, null));
                break;
            case Events.LIGHT_INTENSITY:// 风光雨亮度
                LogUtil.controlLog(TAG + "updateProcessor type equals light intensity and value is " + value);
                if (value.equals("-1")) {
                    value = "1";
                }
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.LIGHT_PERCENT_REPORT, null));
                break;
            case Events.CO2://二氧化碳浓度
                LogUtil.controlLog(TAG + "updateProcessor type equals co2 and value is " + value);
                userId = UserProcessAction.userSelect(nodeId, value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.CO2_REPORT, userId));
                break;
            case Events.PM_TYPE://PM2.5浓度
                LogUtil.controlLog(TAG + "updateProcessor type equals pm2.5 and value is " + value);
                userId = UserProcessAction.userSelect(nodeId, value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.PM_REPORT, userId));
                break;
            case Events.SET_TYPE:
                LogUtil.controlLog(TAG + "updateProcessor type equals set type and value =  " + value + "keepField = " + keepField);
                userId = UserProcessAction.userSelect(nodeId, value);
                // 0 当前模式返回 1 风速获取 2 设定温度获取(加热）3 设定温度获取（制冷）
                if (keepField.equals("0")) {
                    //value 0 关机模式 1加热 2 制冷 3自动模式  4只开风扇 0x06:净化模式  0x07：新风模式
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.AIR_CONDITION, userId));
                } else if (keepField.equals("1")) {
                    //value 0 低速 1中速 2 高速 0x04:静音模式 0x05：标准模式 0x06：强力模式 0x07:自动
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.WIND_SPEED_SET, userId));
                } else if (keepField.equals("2")) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.TEMPERATURE_SET, userId));
                } else if (keepField.equals("3")) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.AC_REFRIGERATION, userId));
                }
                break;
            case Events.WEIGHT:
                LogUtil.controlLog(TAG + "updateProcessor type equals weight and value = " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.WEIGHT_REPORT, null));
                break;
            case Events.SCENE_PANEL:
                LogUtil.controlLog(TAG + "updateProcessor type equals scene panel and value = " + value);
                MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, Integer.parseInt(value) - 1 + "", ActionId.SCENE_PANEL, null));
                break;
            case Events.DOOR_REPORT://智能云锁上报
                userId = UserProcessAction.userSelect(nodeId, value);
                LogUtil.controlLog(TAG + "updateProcessor type equals open door and value is " + value + "mUserId = " + userId + "nodeId = " + nodeId);
                if (Integer.parseInt(value) == ZWAVE.BASIC_ON) {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_ON + "", ActionId.DEVICE_SWITCH, userId));
                } else {
                    MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.BASIC_OFF + "", ActionId.DEVICE_SWITCH, userId));
                }
                break;
            default:
                break;
        }

    }

    /**
     * 门磁延时处理器
     *
     * @param state
     * @param nodeId
     */
    private void doorDecelerator(final int state, final int nodeId, final int msgType) {
        LogUtil.controlLog("received door data");
        if (!isFilter) {
            LogUtil.controlLog("performed door data and value = " + state);
            isFilter = true;
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (msgType == ZWAVE.FLAG_MAGNETIC_DOOR) {
                        if (state == 0x00) {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.DOOR_ELECTRIC_OFF + "", ActionId.DOOR_SWITCH_REPORT, null));
                        } else if (state == 0xff) {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.DOOR_ELECTRIC_ON + "", ActionId.DOOR_SWITCH_REPORT, null));
                        }
                    } else if (msgType == ZWAVE.FLAG_SMOKE_ALARM) {
                        if (state == 0xff) {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.SMOKE_ON + "", ActionId.SMOKER_REPORT, null));
                        } else {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.SMOKE_OFF + "", ActionId.SMOKER_REPORT, null));
                        }
                    } else if (msgType == ZWAVE.FLAG_GAS_ALARM) {
                        if (state == 0xff) {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.GAS_ON + "", ActionId.GAS_REPORT, null));
                        } else {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.GAS_OFF + "", ActionId.GAS_REPORT, null));
                        }
                    } else if (msgType == ZWAVE.FLAG_WATER_SENSOR) {
                        if (state == 0xff) {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.WATER_ON + "", ActionId.WATER_REPORT, null));
                        } else {
                            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.WATER_OFF + "", ActionId.WATER_REPORT, null));
                        }
                    } else if (msgType == ZWAVE.FLAG_DIMMER_SWITCH) {
                        userId = UserProcessAction.userSelect(nodeId, value);
                        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, value, ActionId.PERCENT, userId));
                    }

                    isFilter = false;
                    timer.cancel();
                }
            }, 500);
        }
    }


    /**
     * 设备离线事件处理器
     */
    public void deviceStatusProcessor(int type) {
        if (type == Events.DEVICE_ONLINE) {
            LogUtil.controlLog(TAG + "deviceStatusProcessor type is equals online");
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.DEVICE_ONLINE + "", ActionId.DEVICE_STATE_REPORT, null));
        } else {
            MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.DEVICE_OFFLINE + "", ActionId.DEVICE_STATE_REPORT, null));
            LogUtil.controlLog(TAG + "deviceStatusProcessor type is equals offline");
        }
    }

    /**
     * 控制设备指令发送结果处理器
     */
    public void controlCompletion(int type, int nodeId) {
        LogUtil.controlLog(TAG + "controlCompletion type = " + type + "nodeId = " + nodeId);
        if (type == Events.SEND_SUCCESS) {
            //  MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeId, ZWAVE.DEVICE_OFFLINE + "", ActionId.DEVICE_ONLINE, null));
            //  sendMessageToRecSdkInfoService(messageGenerateAction.ctrMessageGenerator(userId, nodeId, ZWAVE.DEVICE_SEND_MESSAGE_RESULT, ZWAVE.OPERATION_SEND_SUCCESS + "", null));
            LogUtil.controlLog(TAG + "deviceSendMessageResultProcessor type is equals success");
        } else if (type == Events.SEND_FAIL) {
            //   sendMessageToRecSdkInfoService(messageGenerateAction.ctrMessageGenerator(userId, nodeId, ZWAVE.DEVICE_SEND_MESSAGE_RESULT, ZWAVE.OPERATION_SEND_FAIL + "", null));
            LogUtil.controlLog(TAG + "deviceSendMessageResultProcessor type is equals failure");
        }
    }

    /**
     * 接收并处理sdk上报的指令
     */
    public void receiveMessageFromSdk() {
        controller.setOnMessageSendListener(new Controller.SendMessageToGatewayListener() {
            @Override
            public void sendMessageToGateway(Message message) {
                dealMessageToGateway(message);
            }
        });
    }


}
