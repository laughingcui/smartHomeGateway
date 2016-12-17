package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.GwBindDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.BindBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.GwBindHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/11/1.
 */
public class GwBindDao implements GwBindDaoImpl {
    private Context mContext;
    private Dao<BindBean,Integer> dao;
    protected GwBindDao(Context context){
        this.mContext=context;
        try {
            dao= GwBindHelper.getInstance(mContext).getDao(BindBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(ArrayList<BindBean> bindBeans) {
        try {
            dao.create(bindBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(BindBean bindBean) {
        try {
            dao.create(bindBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String userId) {
        List<BindBean> list=null;
        try {
            list=dao.queryForEq("user_id",userId);
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
    public void delete(ArrayList<BindBean> bindBeens) {
        try {
            dao.delete(bindBeens);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public String select(String userId) {
        String userSip=null;
        ArrayList<BindBean> list=null;
        try {
            list= (ArrayList<BindBean>) dao.queryForEq("user_id",userId);
            if(list!=null){
                for(int i=0;i<list.size();i++){
                    userSip=list.get(i).getUserSip();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userSip;
    }

    @Override
    public ArrayList<BindBean> select() {
        ArrayList<BindBean> list=null;
        try {
            list= (ArrayList<BindBean>) dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int selectCount() {
        int count=0;
        List<BindBean> list=null;
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
}
