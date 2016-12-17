package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chaos on 2016/6/22.
 */
@DatabaseTable(tableName = "device_status")
public class DeviceStatusBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "device_id")
    private String deviceId;
    @DatabaseField(columnName = "ctrl_id")
    private String ctrlId;
    @DatabaseField(columnName = "value")
    private String value;
    @DatabaseField(columnName = "event_time")
    private String time;
    @DatabaseField(columnName = "is_upload")
    private int isUpload;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public DeviceStatusBean(String deviceId, String ctrlId, String value, String time, int isUpload) {
        this.deviceId = deviceId;
        this.ctrlId = ctrlId;
        this.value = value;
        this.time = time;
        this.isUpload = isUpload;
    }

    public DeviceStatusBean() {
    }
}
