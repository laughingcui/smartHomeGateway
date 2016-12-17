package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceLogBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/25.
 */
public interface DeviceLogDaoImpl {
    void create(DeviceLogBean deviceLogBean);
    void delete(String deviceId);
    ArrayList<DeviceLogBean> select();
}
