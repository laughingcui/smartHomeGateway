package com.everyoo.smartgateway.everyoocore.bean;

/**
 * Created by chaos on 2016/6/24.
 */
public class ReportBean {

    private int nodeId;
    private String value;
    private String actionId;
    private String userId;
    private String ctrlId;

    public void setCtrlId(String ctrlId) {
        this.ctrlId = ctrlId;
    }

    public String getCtrlId() {
        return ctrlId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ReportBean(String value, int nodeId, String actionId, String userId) {
        this.value = value;
        this.nodeId = nodeId;
        this.actionId = actionId;
        this.userId = userId;
    }
}
