package com.everyoo.smartgateway.everyoocore.bean;

/**
 * Created by abc on 2016/8/19.
 */
public class UserBean {
    private int nodeId;
    private int value;
    private String userId;

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public UserBean(int nodeId, int value, String userId) {
        this.nodeId = nodeId;
        this.value = value;
        this.userId = userId;

    }
}
