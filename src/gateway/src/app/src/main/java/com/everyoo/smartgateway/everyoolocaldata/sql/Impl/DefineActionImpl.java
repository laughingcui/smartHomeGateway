package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;

import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineActionBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/24.
 */
public interface DefineActionImpl {
    void create(ArrayList<DefineActionBean> defineActionBeans);
    ArrayList<DefineActionBean> select(int deviceType);
    int selectCount();
    void delete();
    void delete(ArrayList<DefineActionBean> defineActionBeans);
}
