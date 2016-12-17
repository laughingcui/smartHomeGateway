package com.everyoo.smartgateway.everyoohttp.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class CreateDeviceBean {
    private String gateWayId;
    private String sipAccount;
    private String sipPwd;
    private String deviceId;
    private int deviceType;
    private String inclusionTime;
    private String firmVersion;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int  deviceType) {
        this.deviceType = deviceType;
    }

    public String getInclusionTime() {
        return inclusionTime;
    }

    public void setInclusionTime(String inclusionTime) {
        this.inclusionTime = inclusionTime;
    }

    public void setFirmVersion(String firmVersion) {
        this.firmVersion = firmVersion;
    }

    public String getFirmVersion() {
        return firmVersion;
    }
}
