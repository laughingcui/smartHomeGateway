package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.DevStatusDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceStatusBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.DeviceStatusHepler;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/11/1.
 */
public class DeviceStatusDao implements DevStatusDaoImpl {
    private Context mContext;
    private Dao<DeviceStatusBean,Integer> dao;
    protected DeviceStatusDao(Context context){
        this.mContext=context;
        try {
            dao= DeviceStatusHepler.getInstance(mContext).getDao(DeviceStatusBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        @Override
    public void create(DeviceStatusBean devStatusBean) {
            try {
                dao.create(devStatusBean);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void delete(String key, String value) {
        List<DeviceStatusBean> list=null;
        try {
            list=dao.queryForEq("value",value);
            if(list!=null){
                for(int i=0;i<list.size();i++) {
                    dao.delete(list.get(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(DeviceStatusBean devStatusBean) {
        ArrayList<DeviceStatusBean> list=null;
        try {
            list= (ArrayList<DeviceStatusBean>) dao.queryForEq("ctrl_id",devStatusBean.getCtrlId());
            if(list!=null){
                for(DeviceStatusBean bean:list){
                    bean.setDeviceId(devStatusBean.getDeviceId());
                    bean.setValue(devStatusBean.getValue());
                    bean.setIsUpload(devStatusBean.getIsUpload());
                    bean.setTime(devStatusBean.getTime());
                    dao.update(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String select(String ctrlId) {
        ArrayList<DeviceStatusBean> list=null;
        String value=null;
        try {
            list= (ArrayList<DeviceStatusBean>) dao.queryForEq("ctrl_id",ctrlId);
            if(list!=null){
                for(int i=0;i<list.size();i++){
                    value= list.get(i).getValue();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public ArrayList<DeviceStatusBean> select(int isUpload) {
        ArrayList<DeviceStatusBean> list=null;
        try {
            list= (ArrayList<DeviceStatusBean>) dao.queryForEq("is_upload",isUpload);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
