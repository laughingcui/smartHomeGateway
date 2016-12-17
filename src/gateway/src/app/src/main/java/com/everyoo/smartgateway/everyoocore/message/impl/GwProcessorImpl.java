package com.everyoo.smartgateway.everyoocore.message.impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;

/**
 * Created by chaos on 2016/6/21.
 */
public interface GwProcessorImpl {
    void processor(BindBean bindBean);
    void reset();
}
