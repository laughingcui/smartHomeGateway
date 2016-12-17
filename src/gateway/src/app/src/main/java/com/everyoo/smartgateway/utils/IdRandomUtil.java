package com.everyoo.smartgateway.utils;

import java.util.Random;
import java.util.UUID;

/**
 * Created by chaos on 2016/6/24.
 */
public class IdRandomUtil {
    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 生成deviceId
     * @param nodeId
     * @return
     */
    public static String generateDeviceId(int nodeId){
        return System.currentTimeMillis()+"";
        //    return nodeId+"";
    }

    /**
     * 生成ctrlId
     * @return
     */
    public static String generateCtrlId(){
        return UUID.randomUUID().toString();
    }

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length
     *            随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }
}
