package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageActionBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/22.
 */
public interface LinkageActionDaoImpl {

    void create(LinkageActionBean linkageBean);
    void create(ArrayList<LinkageActionBean> linkageBeans);
    void delete(String key, String value);
    ArrayList<LinkageActionBean> select(String key, String value);
    String isExisted(String ctrlId, String value);
    String selectLinkageId(int flag);
}
