package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/1.
 */
@DatabaseTable(tableName = "LinkCtrl")
public class LinkageActionBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "linkage_id")
    private String linkageId;
    @DatabaseField(columnName = "device_id")
    private String deviceId;
    @DatabaseField(columnName = "ctrl_id")
    private String ctrlId;
    @DatabaseField(columnName = "value")
    private String value;
    @DatabaseField(columnName = "flag")
    private int flag;
    private ArrayList<LinkageActionBean> actionLinkageBeans;

    public String getLinkageId() {
        return linkageId;
    }

    public void setLinkageId(String linkageId) {
        this.linkageId = linkageId;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ArrayList<LinkageActionBean> getActionLinkageBeans() {
        return actionLinkageBeans;
    }

    public void setActionLinkageBeans(ArrayList<LinkageActionBean> actionLinkageBeans) {
        this.actionLinkageBeans = actionLinkageBeans;
    }
}
