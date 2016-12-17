package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

/**
 * Created by chaos on 2016/6/23.
 */
public class DeviceBean {
    private String deviceId;
    private String userId;
    private int nodeId;

    private int deviceType;
    private String inclusionTime;
    private String firmwareVersion;

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public void setInclusionTime(String inclusionTime) {
        this.inclusionTime = inclusionTime;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getInclusionTime() {
        return inclusionTime;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public DeviceBean(String deviceId, String userId) {
        this.deviceId = deviceId;
        this.userId = userId;
    }

    public DeviceBean(String deviceId, int deviceType, String firmwareVersion, String inclusionTime){
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.firmwareVersion = firmwareVersion;
        this.inclusionTime = inclusionTime;
    }

}
