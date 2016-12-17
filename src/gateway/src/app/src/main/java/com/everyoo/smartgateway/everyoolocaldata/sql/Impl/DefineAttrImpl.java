package com.everyoo.smartgateway.everyoolocaldata.sql.Impl;


import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DefineAttriBean;

import java.util.ArrayList;


/**
 * Created by chaos on 2016/6/24.
 */
public interface DefineAttrImpl {
    void insert(ArrayList<DefineAttriBean> reportBeans);
    int select(int manufactureId, int productId, int productType);
    int selectCount();
    void delete();
    void delete(ArrayList<DefineAttriBean> defineAttriBeans);


}
