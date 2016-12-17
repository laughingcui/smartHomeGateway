package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2016/10/31.
 */
@DatabaseTable(tableName = "device_ctrl")
public class CtrlBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "ctrl_id")
    private String ctrlId;
    @DatabaseField(columnName = "node_id")
    private int nodeId;
    @DatabaseField(columnName = "action_id")
    private String actionId;
    @DatabaseField(columnName = "value")
    private String value;
    @DatabaseField(columnName = "device_id")
    private String deviceId;
    @DatabaseField(columnName = "device_type")
    private int deviceType;

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setCtrlId(String ctrlId) {
        this.ctrlId = ctrlId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCtrlId() {
        return ctrlId;
    }

    public String getActionId() {
        return actionId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public String getValue() {
        return value;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public CtrlBean(int nodeId, String ctrlId, String actionId, String value, String deviceId) {
        this.nodeId = nodeId;
        this.ctrlId = ctrlId;
        this.actionId = actionId;
        this.value = value;
        this.deviceId = deviceId;

    }

    public CtrlBean(){
    }

    @Override
    public String toString() {
        return "CtrlBean{" +
                "ctrlId='" + ctrlId + '\'' +
                ", nodeId=" + nodeId +
                ", actionId='" + actionId + '\'' +
                ", value='" + value + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType=" + deviceType +
                '}';
    }
}
