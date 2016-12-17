package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.TimingDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.TimingHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/1.
 */
public class TimingDao implements TimingDaoImpl {
    private Context mContext;
    private Dao<TimingBean,Integer> dao;
    protected TimingDao(Context context){
        this.mContext=context;
        try {
            dao= TimingHelper.getInstance(mContext).getDao(TimingBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(TimingBean timingBean) {
        try {
            dao.create(timingBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String key, String value) {
        ArrayList<TimingBean> list=null;
        try {
            list= (ArrayList<TimingBean>) dao.queryForEq(key,value);
            if(list!=null){
                for(TimingBean bean:list){
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TimingBean timingBean) {
        ArrayList<TimingBean> list=null;
        try {
            list= (ArrayList<TimingBean>) dao.queryForEq("alarm_id",timingBean.getAlarmId());
            if(list!=null){
                for(TimingBean bean:list){
                    bean.setAlarmTime(timingBean.getAlarmTime());
                    bean.setAlarmId(timingBean.getAlarmId());
                    bean.setLoop(timingBean.getLoop());
                    bean.setValue(timingBean.getValue());
                    bean.setCtrlId(timingBean.getCtrlId());
                    bean.setDeviceId(timingBean.getDeviceId());
                    dao.update(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEnable(TimingBean timingBean) {
            ArrayList<TimingBean> list = null;
            try {
                list = (ArrayList<TimingBean>) dao.queryForEq("alarm_id", timingBean.getAlarmId());
                if (list != null) {
                    for (TimingBean bean : list) {
                        bean.setEnable(timingBean.getEnable());
                        dao.update(bean);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    @Override
    public ArrayList<TimingBean> select() {
        ArrayList<TimingBean> list=null;
        try {
            list= (ArrayList<TimingBean>) dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ArrayList<TimingBean> select(String deviceId) {
        ArrayList<TimingBean> list=null;
        try {
            list= (ArrayList<TimingBean>) dao.queryForEq("device_id",deviceId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int selectEnable(String timingId) {
        ArrayList<TimingBean> list=null;
        int enable=0;
        try {
            list= (ArrayList<TimingBean>) dao.queryForEq("alarm_id",timingId);
            if(list!=null){
                for(TimingBean bean:list){
                    enable=bean.getEnable();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enable;
    }

    @Override
    public String selectAlarmId(String ctrlId) {
        ArrayList<TimingBean> list=null;
        String alarmId = null;
        try {
            list= (ArrayList<TimingBean>) dao.queryForEq("ctrl_id",ctrlId);
            if(list!=null){
                for(TimingBean bean:list){
                    alarmId=bean.getAlarmId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmId;
    }
}
