package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/1.
 */
@DatabaseTable(tableName = "LinkTrigger")
public class LinkageTriggerBean {
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
    @DatabaseField(columnName = "relationship")
    private int relationship;
    @DatabaseField(columnName = "is_conform")
    private int isConform;
    @DatabaseField(columnName = "flag")
    private int flag;
    private ArrayList<LinkageTriggerBean> triggerLinkageBeans;
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

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    public int getIsConform() {
        return isConform;
    }

    public void setIsConform(int isConform) {
        this.isConform = isConform;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ArrayList<LinkageTriggerBean> getTriggerLinkageBeans() {
        return triggerLinkageBeans;
    }

    public void setTriggerLinkageBeans(ArrayList<LinkageTriggerBean> triggerLinkageBeans) {
        this.triggerLinkageBeans = triggerLinkageBeans;
    }
}
