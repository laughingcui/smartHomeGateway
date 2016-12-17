package com.everyoo.smartgateway.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mac on 16/3/21.
 */
public class StringUtils {
    public static boolean isNullOrEmpty(String strParam) {
        if (strParam == null || strParam.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }
    public static boolean emailFormat(String emailString) {//邮箱判断正则表达式
        Pattern pattern = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher mc = pattern.matcher(emailString);
        return mc.matches();
    }
    public static boolean mobileFormat(String mobileString) {
        Pattern p = Pattern
                .compile("[1][358]\\d{9}");
        Matcher m = p.matcher(mobileString);
        return m.matches();
    }
}
