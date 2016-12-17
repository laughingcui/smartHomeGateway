package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/21.
 */
public interface GwBindDaoImpl {
    void create(ArrayList<BindBean> bindBeans);
    void create(BindBean bindBean);
    void delete(String userId);
    void delete(ArrayList<BindBean> bindBeens);
    void delete();
    String select(String userId);
    ArrayList<BindBean> select();
    int selectCount();
}
