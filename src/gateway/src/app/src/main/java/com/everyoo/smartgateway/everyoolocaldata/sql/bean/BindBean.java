package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chaos on 2016/6/21.
 */
@DatabaseTable(tableName = "gwBindInfo")
public class BindBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "user_id")
    private String userId;
    @DatabaseField(columnName = "user_sip")
    private String userSip;
    @DatabaseField(columnName = "role")
    private int role;
    @DatabaseField(columnName = "bind_time")
    private String bindTime;
    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUserSip() {
        return userSip;
    }

    public void setUserSip(String userSip) {
        this.userSip = userSip;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }


    public BindBean(String userId, String userSip, int role, String bindTime, int type) {
        this.userId = userId;
        this.userSip = userSip;
        this.role = role;
        this.bindTime = bindTime;
        this.type = type;
    }

    public BindBean(String userId, String userSip, int role, String bindTime) {
        this.userId = userId;
        this.userSip = userSip;
        this.role = role;
        this.bindTime = bindTime;
    }
    public BindBean(){

    }
}
