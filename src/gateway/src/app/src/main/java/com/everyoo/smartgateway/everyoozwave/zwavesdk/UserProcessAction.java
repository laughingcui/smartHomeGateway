package com.everyoo.smartgateway.everyoozwave.zwavesdk;


import android.text.TextUtils;

import com.everyoo.smartgateway.everyoocore.bean.UserBean;
import com.everyoo.smartgateway.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by abc on 2016/8/19.
 */
public class UserProcessAction {

    public static final String TAG = "UserProcessAction ";
    public static ArrayList<UserBean> userBeanArrayList = new ArrayList<>();

    /**
     * 添加
     * @param userId
     * @param nodeId
     * @param value
     * @return
     */
    public synchronized static void userAdd(int nodeId, int value, String userId) {
        if (!TextUtils.isEmpty(userId)){
            userBeanArrayList.add(new UserBean(nodeId, value, userId));
        }else {
            LogUtil.println(TAG + "userAdd","userId is null");
        }

    }


    /**
     * 查找
     * @param nodeId
     * @param value
     * @return
     */
    public synchronized static String userSelect(int nodeId, String value) {
        String userId = null;
        if (userBeanArrayList != null && userBeanArrayList.size() > 0) {
            for (int i = 0; i < userBeanArrayList.size(); i++) {
                if (nodeId == userBeanArrayList.get(i).getNodeId()) {  //&& value==userBeanArrayList.get(i).getValue()
                    userId = userBeanArrayList.get(i).getUserId();
                    userBeanArrayList.remove(i);
                } else {
                    LogUtil.println(TAG + "userSelect ", "NodeId does not exist");
                }
            }
        }
        return userId;
    }

}
