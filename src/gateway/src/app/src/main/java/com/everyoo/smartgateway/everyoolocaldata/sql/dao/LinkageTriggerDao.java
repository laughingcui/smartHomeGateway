package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.LinkageTriggerDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageTriggerBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.LinkageTriggerHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/1.
 */
public class LinkageTriggerDao implements LinkageTriggerDaoImpl {
    private Context mContext;
    private Dao<LinkageTriggerBean,Integer> dao;
    protected LinkageTriggerDao(Context context){
        this.mContext=context;
        try {
            dao= LinkageTriggerHelper.getInstance(mContext).getDao(LinkageTriggerBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(LinkageTriggerBean linkageBean) {
        try {
            dao.create(linkageBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(ArrayList<LinkageTriggerBean> linkageBeans) {
        try {
            dao.create(linkageBeans);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String key, String value) {
        ArrayList<LinkageTriggerBean> list=null;
        try {
            list= (ArrayList<LinkageTriggerBean>) dao.queryBuilder().where().eq(key,value).query();
            if(list!=null){
                for(LinkageTriggerBean bean:list){
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ArrayList<LinkageTriggerBean> linkageBeans) {
        ArrayList<LinkageTriggerBean> list=null;
        for(LinkageTriggerBean bean:linkageBeans){
            try {
                list= (ArrayList<LinkageTriggerBean>) dao.queryBuilder().where().eq("linkage_id",bean.getLinkageId()).and().eq("ctrl_id",bean.getCtrlId()).query();
                if(list!=null){
                    for(LinkageTriggerBean linkbean:list){
                        linkbean.setLinkageId(bean.getLinkageId());
                        linkbean.setCtrlId(bean.getCtrlId());
                        linkbean.setValue(bean.getValue());
                        linkbean.setRelationship(bean.getRelationship());
                        linkbean.setIsConform(bean.getIsConform());
                        linkbean.setDeviceId(bean.getDeviceId());
                        dao.update(linkbean);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<LinkageTriggerBean> select(String key, String value) {
        ArrayList<LinkageTriggerBean> list=null;
        try {
            list= (ArrayList<LinkageTriggerBean>) dao.queryForEq(key,value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String isExisted(String ctrlId, String value) {
        ArrayList<LinkageTriggerBean> list=null;
        String linkageId = null;
        try {
            list= (ArrayList<LinkageTriggerBean>) dao.queryBuilder().where().eq("ctrl_id",ctrlId).and().eq("value",value).query();
            if(list!=null){
                for(LinkageTriggerBean bean:list){
                    linkageId= bean.getLinkageId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return linkageId;
    }

    @Override
    public String selectLinkageId(int flag) {
        ArrayList<LinkageTriggerBean> list=null;
        String linkageId = null;
        try {
            list= (ArrayList<LinkageTriggerBean>) dao.queryForEq("flag",flag);
            for(LinkageTriggerBean bean:list){
                linkageId=bean.getLinkageId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return linkageId;
    }

    @Override
    public ArrayList<LinkageTriggerBean> selectByCtrlId(String ctrlId) {
        ArrayList<LinkageTriggerBean> list=null;
        try {
            list= (ArrayList<LinkageTriggerBean>) dao.queryForEq("ctrl_id",ctrlId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateByCtrlIdAndLinkageId(ArrayList<LinkageTriggerBean> linkageBeans) {
        ArrayList<LinkageTriggerBean> list=null;
        for(LinkageTriggerBean bean:linkageBeans){
            try {
                list= (ArrayList<LinkageTriggerBean>) dao.queryBuilder().where().eq("linkage_id",bean.getLinkageId()).and().eq("ctrl_id",bean.getCtrlId()).query();
                if(list!=null){
                    for(LinkageTriggerBean tbean:list){
                        tbean.setCtrlId(bean.getCtrlId());
                        tbean.setRelationship(bean.getRelationship());
                        tbean.setIsConform(bean.getIsConform());
                        tbean.setLinkageId(bean.getLinkageId());
                        tbean.setValue(bean.getValue());
                        tbean.setFlag(bean.getFlag());
                        tbean.setDeviceId(bean.getDeviceId());
                        dao.update(tbean);

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
