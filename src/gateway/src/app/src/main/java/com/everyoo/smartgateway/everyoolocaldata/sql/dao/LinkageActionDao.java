package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.LinkageActionDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.LinkageActionHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/1.
 */
public class LinkageActionDao implements LinkageActionDaoImpl {
    private Context mContext;
    private Dao<LinkageActionBean,Integer> dao;
    protected LinkageActionDao(Context context){
        this.mContext=context;
        try {
            dao= LinkageActionHelper.getInstance(mContext).getDao(LinkageActionBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(LinkageActionBean linkageBean) {
        try {
            dao.create(linkageBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(ArrayList<LinkageActionBean> linkageBeans) {
        try {
            dao.create(linkageBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String key, String value) {
        ArrayList<LinkageActionBean> list=null;
        try {
            list= (ArrayList<LinkageActionBean>) dao.queryBuilder().where().eq(key,value).query();
            if(list!=null){
                for(LinkageActionBean bean:list){
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<LinkageActionBean> select(String key, String value) {
        ArrayList<LinkageActionBean> list=null;
        try {
            list= (ArrayList<LinkageActionBean>) dao.queryForEq(key,value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String isExisted(String ctrlId, String value) {
        ArrayList<LinkageActionBean> list=null;
        String linkageId=null;
        try {
            list= (ArrayList<LinkageActionBean>) dao.queryBuilder().where().eq("ctrl_id",ctrlId).and().eq("value",value).query();
            if(list!=null){
                for(LinkageActionBean bean:list){
                    linkageId=bean.getLinkageId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return linkageId;
    }

    @Override
    public String selectLinkageId(int flag) {
        ArrayList<LinkageActionBean> list=null;
        String linkageId=null;
        try {
            list= (ArrayList<LinkageActionBean>) dao.queryForEq("flag",flag);
            if(list!=null){
                for(LinkageActionBean bean:list){
                    linkageId=bean.getLinkageId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return linkageId;
    }
}
