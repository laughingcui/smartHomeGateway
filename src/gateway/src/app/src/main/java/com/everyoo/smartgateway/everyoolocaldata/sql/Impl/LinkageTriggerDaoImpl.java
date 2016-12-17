package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageTriggerBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/20.
 */
public interface LinkageTriggerDaoImpl {
    void create(LinkageTriggerBean linkageBean);
    void create(ArrayList<LinkageTriggerBean> linkageBeans);
    void delete(String key, String value);
    void update(ArrayList<LinkageTriggerBean> linkageBeans);
    ArrayList<LinkageTriggerBean> select(String key, String value);
    String isExisted(String ctrlId, String value);
    String selectLinkageId(int flag);
    ArrayList<LinkageTriggerBean> selectByCtrlId(String ctrlId);
    void updateByCtrlIdAndLinkageId(ArrayList<LinkageTriggerBean> linkageBeans);

}
