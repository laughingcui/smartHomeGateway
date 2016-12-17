package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceStatusBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/22.
 */
public interface DevStatusDaoImpl {
    void create(DeviceStatusBean devStatusBean);
    void delete(String key, String value);
    void update(DeviceStatusBean devStatusBean);
    String select(String ctrlId);
    ArrayList<DeviceStatusBean> select(int isUpload);

}
