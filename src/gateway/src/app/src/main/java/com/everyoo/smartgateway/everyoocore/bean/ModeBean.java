package com.everyoo.smartgateway.everyoocore.bean;

/**
 * Created by chaos on 2016/6/21.
 */
public class ModeBean {

    private String userId;
    private int status;

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getUserId() {
        return userId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public ModeBean(int status,String userId){
        this.userId = userId;
        this.status = status;
    }
}
