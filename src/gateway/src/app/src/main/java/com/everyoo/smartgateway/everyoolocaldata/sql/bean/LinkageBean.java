package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/6/20.
 */
public class LinkageBean {

    private String linkageId;
    private int enable;
    private int isConform;
    private int relationship;
    private String triggerDeviceId;
    private String triggerCtrlId;
    private String triggerValue;
    private int triggerDeviceType;

    private String actionDeviceId;
    private String actionCtrlId;
    private String actionValue;

    private String linkageName;
    private String userId;
    private String createTime;
    private String begin;
    private String end;
    private int length;
    private String triggerArray;
    private String actionArray;

    private int msgType;

    private int flag ;// 1：标识智慧模式下全开联动；-1：标识智慧模式下全关联动；0：标识普通联动

    private ArrayList<LinkageBean> triggerLinkageBeans;
    private ArrayList<LinkageBean> actionLinkageBeans;

    public void setTriggerDeviceType(int triggerDeviceType) {
        this.triggerDeviceType = triggerDeviceType;
    }

    public int getTriggerDeviceType() {
        return triggerDeviceType;
    }

    public void setActionLinkageBeans(ArrayList<LinkageBean> actionLinkageBeans) {
        this.actionLinkageBeans = actionLinkageBeans;
    }

    public void setTriggerLinkageBeans(ArrayList<LinkageBean> triggerLinkageBeans) {
        this.triggerLinkageBeans = triggerLinkageBeans;
    }

    public ArrayList<LinkageBean> getActionLinkageBeans() {
        return actionLinkageBeans;
    }

    public ArrayList<LinkageBean> getTriggerLinkageBeans() {
        return triggerLinkageBeans;
    }

    public String getLinkageId() {
        return linkageId;
    }

    public void setLinkageId(String linkageId) {
        this.linkageId = linkageId;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getIsConform() {
        return isConform;
    }

    public void setIsConform(int isConform) {
        this.isConform = isConform;
    }

    public String getTriggerDeviceId() {
        return triggerDeviceId;
    }

    public void setTriggerDeviceId(String triggerDeviceId) {
        this.triggerDeviceId = triggerDeviceId;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    public String getTriggerCtrlId() {
        return triggerCtrlId;
    }

    public void setTriggerCtrlId(String triggerCtrlId) {
        this.triggerCtrlId = triggerCtrlId;
    }

    public String getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }

    public String getActionDeviceId() {
        return actionDeviceId;
    }

    public void setActionDeviceId(String actionDeviceId) {
        this.actionDeviceId = actionDeviceId;
    }

    public String getActionCtrlId() {
        return actionCtrlId;
    }

    public void setActionCtrlId(String actionCtrlId) {
        this.actionCtrlId = actionCtrlId;
    }

    public String getActionValue() {
        return actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    public String getLinkageName() {
        return linkageName;
    }

    public void setLinkageName(String linkageName) {
        this.linkageName = linkageName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getTriggerArray() {
        return triggerArray;
    }

    public void setTriggerArray(String triggerArray) {
        this.triggerArray = triggerArray;
    }

    public String getActionArray() {
        return actionArray;
    }

    public void setActionArray(String actionArray) {
        this.actionArray = actionArray;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public LinkageBean(String linkageId, String linkageName, String triggerArray, String actionArray, String begin, String end, int length, int enable, String userid, String createTime, ArrayList<LinkageBean> triggerLinkageBeans, ArrayList<LinkageBean> actionLinkageBeans, int msgType) {
        this.linkageId = linkageId;
        this.linkageName = linkageName;
        this.triggerArray = triggerArray;
        this.actionArray = actionArray;
        this.begin = begin;
        this.end = end;
        this.length = length;
        this.userId = userid;
        this.enable = enable;
        this.createTime = createTime;
        this.triggerLinkageBeans = triggerLinkageBeans;
        this.actionLinkageBeans = actionLinkageBeans;
        this.msgType = msgType;
    }



    public LinkageBean(String linkageId, String triggerDeviceId, String triggerCtrlId, String triggerValue, int relationship, int isConform, int flag) {
        this.linkageId = linkageId;
        this.triggerDeviceId = triggerDeviceId;
        this.triggerCtrlId = triggerCtrlId;
        this.triggerValue = triggerValue;
        this.relationship = relationship;
        this.isConform = isConform;
        this.flag = flag;
    }

    public LinkageBean(String linkageId, String deviceId, String ctrlId, String value, int msgType) {
        this.linkageId = linkageId;
        this.actionDeviceId = deviceId;
        this.actionCtrlId = ctrlId;
        this.actionValue = value;
        this.msgType = msgType;
    }
    public LinkageBean(String linkageId, String deviceId, String ctrlId, String value) {
        this.linkageId = linkageId;
        this.actionDeviceId = deviceId;
        this.actionCtrlId = ctrlId;
        this.actionValue = value;
    }


    public LinkageBean(String linkageId, String userId, int enable, int msgType, String createTime) {
        this.linkageId = linkageId;
        this.userId = userId;
        this.enable = enable;
        this.msgType = msgType;
        this.createTime = createTime;
    }

    public LinkageBean(ArrayList<LinkageBean> triggerLinkageBeans, ArrayList<LinkageBean> actionLinkageBeans, String trggerArray, String userId, String createTime, int msgType){
        this.triggerLinkageBeans = triggerLinkageBeans;
        this.actionLinkageBeans = actionLinkageBeans;
        this.triggerArray = trggerArray;
        this.userId = userId;
        this.createTime = createTime;
        this.msgType = msgType;
    }
    public LinkageBean(){

    }
}
