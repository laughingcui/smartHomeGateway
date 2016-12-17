package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by chaos on 2016/6/20.
 */
@DatabaseTable(tableName = "timing")
public class TimingBean implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "alarm_id")
    private String alarmId;
    @DatabaseField(columnName = "ctrl_id")
    private String ctrlId;
    @DatabaseField(columnName = "value")
    private String value;
    @DatabaseField(columnName = "alarm_time")
    private String alarmTime;
    @DatabaseField(columnName = "loop")
    private String loop;
    private String userId;
    private String gatewayId;
    @DatabaseField(columnName = "device_id")
    private String deviceId;
    @DatabaseField(columnName = "enable")
    private int enable;
    private int type;
    @DatabaseField(columnName = "timer_type")
    private int timerType;
    /*private String sceneId;

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }*/

    public int getTimerType() {
        return timerType;
    }

    public void setTimerType(int timerType) {
        this.timerType = timerType;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getEnable() {
        return enable;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setCtrlId(String ctrlId) {
        this.ctrlId = ctrlId;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public String getCtrlId() {
        return ctrlId;
    }

    public String getLoop() {
        return loop;
    }

    public String getUserId() {
        return userId;
    }

    public String getValue() {
        return value;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
