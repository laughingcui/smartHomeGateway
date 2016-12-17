package com.everyoo.smartgateway.everyoohttp.core;

import com.android.volley.VolleyError;

/**
 * @param <T>
 * @ClassName: RequestJsonListener
 * @Description: TODO
 */
public interface RequestJsonListener<T> {
    /**
     * 网络请求成功
     *
     * @param result 返回数据对象
     */
    public void requestSuccess(T result);
    /**
     * 网络请求失败
     *
     * @param e 失败提示信息
     */
    public void requestError(VolleyError e);
}