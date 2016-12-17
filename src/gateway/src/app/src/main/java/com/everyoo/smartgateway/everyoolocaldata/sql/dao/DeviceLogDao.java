package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.DeviceLogDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceLogBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.DeviceLogHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/31.
 */
public class DeviceLogDao implements DeviceLogDaoImpl {
    private Context mContext;
    private Dao<DeviceLogBean,Integer> dao;
    protected DeviceLogDao(Context context){
        this.mContext=context;
        try {
            dao= DeviceLogHelper.getInstance(mContext).getDao(DeviceLogBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(DeviceLogBean deviceLogBean) {
        try {
            dao.create(deviceLogBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String deviceId) {
        List<DeviceLogBean> list=null;
        try {
            list=dao.queryForEq("device_id",deviceId);
            if(list!=null){
                for(DeviceLogBean bean:list){
                    dao.delete(bean);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<DeviceLogBean> select() {
        ArrayList<DeviceLogBean> list=null;
        try {
            list= (ArrayList<DeviceLogBean>)dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
