package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.DefineActionImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.DefineActionHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/31.
 */
public class DefineActionDao implements DefineActionImpl {
    private Context mContext;
    private Dao<DefineActionBean,Integer> dao;
    protected DefineActionDao(Context context){
        this.mContext=context;
        try {
            dao= DefineActionHelper.getInstance(mContext).getDao(DefineActionBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(ArrayList<DefineActionBean> defineActionBeans) {
        try {
            dao.create(defineActionBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<DefineActionBean> select(int deviceType) {
        ArrayList<DefineActionBean> list=null;
        try {
            list= (ArrayList<DefineActionBean>) dao.queryForEq("device_type",deviceType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int selectCount() {
        int count=0;
        List<DefineActionBean> list=null;
        try {
            list=dao.queryForAll();
            if(list!=null){
                count=list.size();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void delete() {
        try {
            dao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ArrayList<DefineActionBean> defineActionBeans) {
        try {
            dao.delete(defineActionBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
