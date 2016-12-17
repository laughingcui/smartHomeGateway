package com.everyoo.smartgateway.everyoohttp.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class UploadSdkStatusBean {
    private String gateWayId;
    private String sipAccount;
    private String sipPwd;
    private int enable;

    public String getGateWayId() {
        return gateWayId;
    }

    public void setGateWayId(String gateWayId) {
        this.gateWayId = gateWayId;
    }

    public String getSipPwd() {
        return sipPwd;
    }

    public void setSipPwd(String sipPwd) {
        this.sipPwd = sipPwd;
    }

    public String getSipAccount() {
        return sipAccount;
    }

    public void setSipAccount(String sipAccount) {
        this.sipAccount = sipAccount;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
