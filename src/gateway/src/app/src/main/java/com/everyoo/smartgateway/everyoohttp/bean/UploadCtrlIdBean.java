package com.everyoo.smartgateway.everyoohttp.bean;

import org.json.JSONArray;

/**
 * Created by Administrator on 2016/10/27.
 */
public class UploadCtrlIdBean {
    private String gateWayId;
    private String sipAccount;
    private String sipPwd;
    private JSONArray list;

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

    public JSONArray getList() {
        return list;
    }

    public void setList(JSONArray list) {
        this.list = list;
    }
}
