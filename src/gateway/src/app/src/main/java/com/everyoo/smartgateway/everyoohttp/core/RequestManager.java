package com.everyoo.smartgateway.everyoohttp.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.everyoo.smartgateway.utils.JsonUtils;
import com.everyoo.smartgateway.utils.StringUtils;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressLint("NewApi")
public class RequestManager {
    public static RequestQueue mRequestQueue;


    private RequestManager() {
    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static <T> void post(Object tag, String url, Class<T> classOfT, Object objectParams, HashMap<String, String> headers, RequestJsonListener<T> listener) {
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST, url, objectParams, headers, responseListener(listener, classOfT), responseError(listener));
        addRequest(request, tag);
    }

    public static <T> void post(Object tag, String url, Class<T> classOfT, Object objectParams, RequestJsonListener<T> listener) {
        post(tag, url, classOfT, objectParams, null, listener);
    }

    public static void post(Object tag, String url, Object objectParams, RequestListener listener) {
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST, url, objectParams, null, responseListener(listener), responseError(listener));
        addRequest(request, tag);
    }

    public static void get(Object tag, String url, Object objectParams, RequestListener listener) {
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.GET, url, objectParams, null, responseListener(listener), responseError(listener));
        addRequest(request, tag);
    }

    /**
     * 单个文件上传
     *
     * @param
     * @param
     * @param
     * @return
     */
    public static void post(Object tag, String url, String filePartName, File file, Map<String, String> params, RequestListener listener) {
        FileMultipartArrayRequest request = new FileMultipartArrayRequest(Request.Method.POST, url, filePartName, file, params, responseStringListener(listener), responseError(listener));
        addRequest(request, tag);
    }


    /**
     * 多个文件上传
     *
     * @param
     * @param
     * @param
     * @return
     */
    public static void post(Object tag, String url, String filePartName, List<File> files, Map<String, String> params, RequestListener listener) {
        FileMultipartArrayRequest request = new FileMultipartArrayRequest(Request.Method.POST, url, filePartName, files, params, responseStringListener(listener), responseError(listener));
        addRequest(request, tag);
    }

    protected static <T> Response.Listener<byte[]> responseListener(final RequestJsonListener<T> listener, final Class<T> classOfT) {
        return new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] arg0) {
                String data = null;
                try {
                    data = new String(arg0, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (!StringUtils.isNullOrEmpty(data) || data.length() > 3) {
                    listener.requestSuccess(JsonUtils.object(data, classOfT));
                }
            }
        };
    }

    protected static Response.Listener<byte[]> responseListener(final RequestListener listener) {
        return new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] arg0) {
                String data = null;
                try {
                    data = new String(arg0, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i("解析数据：", "数据：" + data);
                listener.requestSuccess(data);
            }
        };
    }

    protected static Response.Listener<String> responseStringListener(final RequestListener listener) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listener.requestSuccess(s);
            }
        };
    }

    protected static <T> Response.ErrorListener responseError(final RequestJsonListener<T> l) {
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                l.requestError(e);
            }
        };
    }

    protected static Response.ErrorListener responseError(final RequestListener listener) {
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                listener.requestError(e);
            }
        };
    }

    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

}
