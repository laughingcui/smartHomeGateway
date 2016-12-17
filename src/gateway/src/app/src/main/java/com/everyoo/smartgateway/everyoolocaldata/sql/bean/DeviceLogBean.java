package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chaos on 2016/6/25.
 */
@DatabaseTable(tableName = "deviceEventLogger")
public class DeviceLogBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "user_id")
    private String userId;
    @DatabaseField(columnName = "device_id")
    private String deviceId;
    @DatabaseField(columnName = "ctrl_id")
    private String ctrlId;
    @DatabaseField(columnName = "value")
    private String value;
    @DatabaseField(columnName = "event_time")
    private String eventTime;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCtrlId() {
        return ctrlId;
    }

    public void setCtrlId(String ctrlId) {
        this.ctrlId = ctrlId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public DeviceLogBean(String userId, String deviceId, String ctrlId, String value, String eventTime) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.ctrlId = ctrlId;
        this.value = value;
        this.eventTime = eventTime;
    }

    public DeviceLogBean() {}
}
