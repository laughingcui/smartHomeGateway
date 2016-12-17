package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.DefineAttrImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineAttriBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.DefineAttriHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class DefineAttriDao implements DefineAttrImpl {
    private Context mContext;
    private Dao<DefineAttriBean,Integer> dao;
    protected DefineAttriDao(Context context){
        this.mContext=context;
        try {
            dao= DefineAttriHelper.getInstance(mContext).getDao(DefineAttriBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void insert(ArrayList<DefineAttriBean> reportBeans) {
        try {
            dao.create(reportBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int select(int manufactureId, int productId, int productType) {
        int deviceType=0;
        DefineAttriBean bean=null;
        ArrayList<DefineAttriBean> list=null;
        try {
            list= (ArrayList<DefineAttriBean>) dao.queryBuilder().where().eq("manufacture_id",manufactureId).and().eq("product_id",productId).and().eq("product_type",productType).query();
            if(list!=null){
                for(int i=0;i<list.size();i++){
                    deviceType=list.get(i).getDeviceType();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deviceType;
    }

    @Override
    public int selectCount() {
        int count=0;
        List<DefineAttriBean> list=null;
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
    public void delete(ArrayList<DefineAttriBean> defineAttriBeans) {
        try {
            dao.delete(defineAttriBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
