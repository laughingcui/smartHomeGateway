package com.everyoo.smartgateway.everyoohttp.bean;

/**
 * Created by abc on 2016/11/28.
 */

public class UploadFileBean {
    private String gateWayId;
    private String sipAccount;
    private String sipPwd;

    public String getSipPwd() {
        return sipPwd;
    }

    public void setSipPwd(String sipPwd) {
        this.sipPwd = sipPwd;
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
}
