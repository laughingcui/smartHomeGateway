package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.SceneDaoBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/20.
 */
public interface SceneDaoImpl {

    void create(ArrayList<SceneDaoBean> sceneBeanArrayList);

    void delete(String key, String value);

    ArrayList<SceneDaoBean> select(String sceneId);
    ArrayList<String> selectSceneId(String deviceId);
}
