package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/20.
 */
public interface TimingDaoImpl {

    void create(TimingBean timingBean);
    void delete(String key, String value);
    void update(TimingBean timingBean);
    void updateEnable(TimingBean timingBean);
   // TimingBean select(String timingId);
    ArrayList<TimingBean> select();
    ArrayList<TimingBean> select(String deviceId);
    int selectEnable(String timingId);
     String selectAlarmId(String ctrlId);
}
