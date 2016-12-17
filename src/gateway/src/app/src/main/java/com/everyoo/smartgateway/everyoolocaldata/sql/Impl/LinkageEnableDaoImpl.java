package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageEnableBean;

/**
 * Created by chaos on 2016/6/22.
 */
public interface LinkageEnableDaoImpl {
    void create(LinkageEnableBean linkageBean);
    void delete(String linkageId);
    void update(LinkageEnableBean linkageBean);
    int select(String linkageId);
}
