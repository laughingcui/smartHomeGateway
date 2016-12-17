package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chaos on 2016/6/24.
 */
@DatabaseTable(tableName = "devTypeActionMap")
public class DefineActionBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "device_type")
    private int deviceType;
    @DatabaseField(columnName = "action_id")
    private String actionId;

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getActionId() {
        return actionId;
    }

    public DefineActionBean(int deviceType, String actionId){
        this.deviceType = deviceType;
        this.actionId = actionId;
    }
    public DefineActionBean(){}

}

