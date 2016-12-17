package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;

import com.everyoo.smartgateway.everyoolocaldata.sql.Impl.SceneDaoImpl;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.SceneDaoBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.helper.SceneHepler;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/1.
 */
public class SceneDao implements SceneDaoImpl {
    private Context mContext;
    private Dao<SceneDaoBean,Integer> dao;
    protected SceneDao(Context context){
        this.mContext=context;
        try {
            dao= SceneHepler.getInstance(mContext).getDao(SceneDaoBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void create(ArrayList<SceneDaoBean> sceneBeanArrayList) {
        try {
            dao.create(sceneBeanArrayList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String key, String value) {
        ArrayList<SceneDaoBean> list=null;
        try {
            list= (ArrayList<SceneDaoBean>) dao.queryForEq(key,value);
            if(list!=null){
                for (SceneDaoBean bean:list){
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SceneDaoBean> select(String sceneId) {
        ArrayList<SceneDaoBean> list=null;
        try {
            list= (ArrayList<SceneDaoBean>) dao.queryForEq("scene_id",sceneId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ArrayList<String> selectSceneId(String deviceId) {
        ArrayList<String> sList=new ArrayList<>();
        ArrayList<SceneDaoBean> list=new ArrayList<>();
        try {
            list= (ArrayList<SceneDaoBean>) dao.queryForEq("device_id",deviceId);
            if(list!=null){
                for(SceneDaoBean bean:list){
                    sList.add(bean.getSceneId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sList;
    }
}
