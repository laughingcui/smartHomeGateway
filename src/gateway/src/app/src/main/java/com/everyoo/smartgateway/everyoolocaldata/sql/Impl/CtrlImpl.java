package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;

import com.everyoo.smartgateway.everyoolocaldata.sql.bean.CtrlBean;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/10/31.
 */
public interface CtrlImpl {
    void insert(ArrayList<CtrlBean> mList);
    void delete(String deviceId);
    CtrlBean selectActionId(String ctrlId);
    String selectCtrlId(String actionId, int nodeId);
    int selectType(int nodeId);
    String selectDeviceId(int nodeId);
    int selectCount();
    int selectType(String deviceId);
    int selectDevTypeByCtrlId(String ctrlId);
    int selectNodeId(String deviceId);
    ArrayList<CtrlBean> selectByDeviceType(int deviceType);
}
