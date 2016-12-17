package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.LinkageEnableDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageEnableBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.LinkageEnableHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/1.
 */
public class LinkageEnableDao implements LinkageEnableDaoImpl {
    private Context mContext;
    private Dao<LinkageEnableBean,Integer> dao;
    private LinkageEnableHelper mHelper;
    protected LinkageEnableDao(Context context){
        this.mContext=context;
        try {
            mHelper= LinkageEnableHelper.getInstance(mContext);
            dao= mHelper.getDao(LinkageEnableBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(LinkageEnableBean linkageBean) {
        try {
            dao.create(linkageBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String linkageId) {
        ArrayList<LinkageEnableBean> list=null;
        try {
            list= (ArrayList<LinkageEnableBean>) dao.queryForEq("linkage_id",linkageId);
            if(list!=null){
                for(LinkageEnableBean bean:list){
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LinkageEnableBean linkageBean) {
        ArrayList<LinkageEnableBean> list=null;
        try {
            list= (ArrayList<LinkageEnableBean>) dao.queryForEq("linkage_id",linkageBean.getLinkageId());
            if(list!=null){
                for(LinkageEnableBean bean:list){
                    bean.setEnable(linkageBean.getEnable());
                    dao.update(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int select(String linkageId) {
        ArrayList<LinkageEnableBean> list=null;
        int enable=0;
        try {
            list= (ArrayList<LinkageEnableBean>) dao.queryForEq("linkage_id",linkageId);
            if(list!=null){
                for(LinkageEnableBean bean:list){
                    enable=bean.getEnable();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enable;
    }
}
