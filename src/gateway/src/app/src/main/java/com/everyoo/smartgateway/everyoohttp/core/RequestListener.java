package com.everyoo.smartgateway.everyoohttp.core;

import com.android.volley.VolleyError;

/**
 * @ClassName: RequestListener
 * @Description: String 方式返回监听
 */
public interface RequestListener {

    /**
     * 网络请求成功
     *
     * @param data 返回数据字符串
     */
    public void requestSuccess(String data);

    /**
     * 网络请求失败
     *
     * @param e 失败提示信息
     */
    public void requestError(VolleyError e);
}
