package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2016/11/1.
 */
@DatabaseTable(tableName = "linkage")
public class LinkageEnableBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "linkage_id")
    private String linkageId;
    @DatabaseField(columnName = "enable")
    private int enable;

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
}
