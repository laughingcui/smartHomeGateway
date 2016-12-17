package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.ScenePanelBean;

/**
 * Created by chaos on 2016/7/18.
 */
public interface ScenePanelDaoImpl {
    void create(ScenePanelBean bean);
    void delete(ScenePanelBean bean);
    void delete(String sceneId);
    void update(ScenePanelBean bean);
    String select(String ctrlId, String keyId);
}
