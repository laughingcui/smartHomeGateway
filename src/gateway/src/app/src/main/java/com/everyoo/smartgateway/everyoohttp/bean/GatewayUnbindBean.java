package com.everyoo.smartgateway.everyoohttp.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class GatewayUnbindBean {
    private String gateWayId;
    private String sipAccount;
    private String sipPwd;
    private String userId;

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
}