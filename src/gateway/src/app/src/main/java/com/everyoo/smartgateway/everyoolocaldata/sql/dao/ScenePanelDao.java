package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.ScenePanelDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.ScenePanelBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.ScenePanelHepler;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/1.
 */
public class ScenePanelDao implements ScenePanelDaoImpl {
    private Context mContext;
    private Dao<ScenePanelBean,Integer> dao;
    protected ScenePanelDao(Context context){
        this.mContext=context;
        try {
            dao= ScenePanelHepler.getInstance(mContext).getDao(ScenePanelBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(ScenePanelBean bean) {
        try {
            dao.create(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ScenePanelBean bean) {
        try {
            dao.delete(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String sceneId) {
        ArrayList<ScenePanelBean> list=null;
        try {
            list= (ArrayList<ScenePanelBean>) dao.queryForEq("scene_id",sceneId);
            if(list!=null){
                for(ScenePanelBean bean:list){
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ScenePanelBean bean) {
       ArrayList<ScenePanelBean> list=null;
        try {
            list= (ArrayList<ScenePanelBean>) dao.queryBuilder().where().eq("ctrl_id",bean.getCtrlId()).and().eq("key_id",bean.getKeyId()).query();
            if(list!=null){
                for(ScenePanelBean pBean:list){
                    pBean.setKeyId(bean.getKeyId());
                    pBean.setCtrlId(bean.getCtrlId());
                    pBean.setSceneId(bean.getSceneId());
                    dao.update(pBean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String select(String ctrlId, String keyId) {
        ArrayList<ScenePanelBean> list=null;
        String sceneId=null;
        try {
            list= (ArrayList<ScenePanelBean>) dao.queryBuilder().where().eq("ctrl_id",ctrlId).and().eq("key_id",keyId).query();
            if(list!=null){
                for(ScenePanelBean bean:list){
                    sceneId=bean.getSceneId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sceneId;
    }
}
