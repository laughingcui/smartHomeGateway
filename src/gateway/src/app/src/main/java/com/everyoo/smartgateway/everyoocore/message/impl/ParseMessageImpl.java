package com.everyoo.smartgateway.everyoocore.message.impl;


import android.content.Context;
import android.content.Intent;

import com.everyoo.smartgateway.everyoocore.bean.ModeBean;
import com.everyoo.smartgateway.everyoocore.bean.ReportBean;
import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;

/**
 * Created by Administrator on 2016/11/17.
 */
public interface ParseMessageImpl {
    String parseCtrlMessage(String message, String userId, int type);
    BindBean parseGwMessage(String message, String userId, int type);
    LinkageBean parseLinkageMessage(String message, String userId);
    ModeBean parseModeSwitchMessage(String message, String userId);
    ReportBean parseReportMessage(int nodeId, String value, String actionId, String userId);
    SceneBean parseSceneMessage(String message, String userId);
    TimingBean parseTimingMessage(String message, String userId, int timerType);
    DeviceBean deleteFilter(String message, String userId);
    void parseLinkageReceiverMessage(Intent intent, Context context);
}
