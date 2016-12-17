package com.everyoo.smartgateway.everyoohttp.bean;

import org.json.JSONArray;

/**
 * Created by Administrator on 2016/10/27.
 */
public class CreateLinkageBean {
    private String gateWayId;
    private String sipAccount;
    private String sipPwd;
    private String userId;
    private String linkageId;
    private String linkageName;
    private String createTime;
    private int enable;
    private String begin;
    private String end;
    private int length;
    private JSONArray order;
    private JSONArray trigger;

    public JSONArray getOrder() {
        return order;
    }

    public void setOrder(JSONArray order) {
        this.order = order;
    }

    public JSONArray getTrigger() {
        return trigger;
    }

    public void setTrigger(JSONArray trigger) {
        this.trigger = trigger;
    }

    public String getGateWayId() {
        return gateWayId;
    }

    public void setGateWayId(String gateWayId) {
        this.gateWayId = gateWayId;
    }

    public String getSipAccount() {
        return sipAccount;
    }

    public void setSipAccount(String sipAccount) {
        this.sipAccount = sipAccount;
    }

    public String getSipPwd() {
        return sipPwd;
    }

    public void setSipPwd(String sipPwd) {
        this.sipPwd = sipPwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLinkageId() {
        return linkageId;
    }

    public void setLinkageId(String linkageId) {
        this.linkageId = linkageId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLinkageName() {
        return linkageName;
    }

    public void setLinkageName(String linkageName) {
        this.linkageName = linkageName;
    }
}
