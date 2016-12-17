package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.CtrlImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.CtrlBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.CtrlHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class CtrlDao implements CtrlImpl {
    private Context mContext;
    private Dao<CtrlBean,Integer> dao;
    private CtrlHelper mHelper;
    protected CtrlDao(Context context){
        this.mContext=context;
        try {
            mHelper= CtrlHelper.getInstance(mContext);
            dao= mHelper.getDao(CtrlBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void insert(ArrayList<CtrlBean> mList) {
                try {
                    dao.create(mList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
    }

    @Override
    public void delete(String deviceId) {
        List<CtrlBean> list=null;
            try {
                list=dao.queryForEq("device_id",deviceId);
                for(CtrlBean bean:list){
                    dao.delete(bean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


    }

    @Override
    public CtrlBean selectActionId(String ctrlId) {
        List<CtrlBean> list=null;
        CtrlBean ctrlBean=null;
            try {
                list=dao.queryForEq("ctrl_id",ctrlId);
                if(list!=null){
                    for(CtrlBean bean:list){
                        ctrlBean=new CtrlBean();
                        ctrlBean.setActionId(bean.getActionId());
                        ctrlBean.setNodeId(bean.getNodeId());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return ctrlBean;
    }

    @Override
    public String selectCtrlId(String actionId, int nodeId) {
        String ctrlId=null;
        List<CtrlBean> list=null;
            try {
                list=dao.queryBuilder().where().eq("node_id",nodeId).and().eq("action_id",actionId).query();
                if(list!=null){
                    for(CtrlBean bean:list){
                        ctrlId=bean.getCtrlId();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return ctrlId;
    }

    @Override
    public int selectType(int nodeId) {
        int deviceType=0;
        List<CtrlBean> list=null;

            try {
                list=dao.queryForEq("node_id",nodeId);
                if(list!=null){
                    for(CtrlBean bean:list){
                        deviceType=bean.getDeviceType();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return deviceType;
    }

    @Override
    public String selectDeviceId(int nodeId) {
        String deviceId=null;
        List<CtrlBean> list=null;
            try {
                list=dao.queryForEq("node_id",nodeId);
                if(list!=null){
                    for(CtrlBean bean:list){
                        deviceId=bean.getDeviceId();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return deviceId;
    }

    @Override
    public int selectCount() {
        int count=0;
        List<CtrlBean> list=null;
            try {
                list=dao.queryBuilder().query();
                if(list!=null){
                    count=list.size();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return count;
    }

    @Override
    public int selectType(String deviceId) {
        int deviceType=0;
        List<CtrlBean> list=null;
            try {
                list=dao.queryForEq("device_id",deviceId);
                if(list!=null){
                    for(CtrlBean bean:list){
                        deviceType=bean.getDeviceType();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return deviceType;
    }

    @Override
    public int selectDevTypeByCtrlId(String ctrlId) {
        int deviceType=0;
        List<CtrlBean> list=null;
            try {
                list=dao.queryForEq("ctrl_id",ctrlId);
                if(list!=null){
                    for(CtrlBean bean:list){
                        deviceType=bean.getDeviceType();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return deviceType;
    }

    @Override
    public int selectNodeId(String deviceId) {
        int nodeId = 0;
        List<CtrlBean> list=null;
            try {
                list=dao.queryForEq("device_id",deviceId);
                if(list!=null){
                    for(CtrlBean bean:list){
                        nodeId=bean.getNodeId();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return nodeId;
    }

    @Override
    public ArrayList<CtrlBean> selectByDeviceType(int deviceType) {
        ArrayList<CtrlBean> list=null;
            try {
                list= (ArrayList<CtrlBean>) dao.queryForEq("device_type",deviceType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return list;
    }

}
