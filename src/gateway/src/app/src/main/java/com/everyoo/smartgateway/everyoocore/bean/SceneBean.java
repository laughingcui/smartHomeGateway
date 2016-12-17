package com.everyoo.smartgateway.everyoocore.bean;

import org.json.JSONArray;

/**
 * Created by chaos on 2016/4/11.
 */
public class SceneBean {

    private String sceneId;
    private String sceneName;
    private JSONArray position;
    private String begin;
    private String end;
    private int length;
    private int enable;
    private String createTime;
    private String userId;

    private String deviceId;
    private String ctrlId;
    private String value;

    private int type ;



    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCtrlId() {
        return ctrlId;
    }

    public String getDeviceId() {
        return deviceId;
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

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public JSONArray getPosition() {
        return position;
    }

    public void setPosition(JSONArray position) {
        this.position = position;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getEnable() {
        return enable;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public SceneBean(String sceneId, String sceneName, JSONArray position, String begin, String end, int length, int enable, String createTime, String userId,int type) {
        this.sceneId = sceneId;
        this.sceneName = sceneName;
        this.position = position;
        this.begin = begin;
        this.length = length;
        this.end = end;
        this.enable = enable;
        this.createTime = createTime;
        this.userId = userId;
        this.type = type;
    }

    public SceneBean(){

    }

    public SceneBean(String ctrlId,String sceneId,String value,String userId,String createTime,int type){
        this.ctrlId = ctrlId;
        this.sceneId = sceneId;
        this.value = value;
        this.userId = userId;
        this.createTime = createTime;
        this.type = type;
    }
}

