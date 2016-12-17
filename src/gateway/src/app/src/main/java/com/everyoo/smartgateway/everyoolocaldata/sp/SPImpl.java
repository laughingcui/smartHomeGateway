package com.everyoo.smartgateway.everyoolocaldata.sp;

import android.content.Context;

/**
 * Created by Administrator on 2016/11/16.
 */
public interface SPImpl {
    void saveBindInfo(final Context context);
    void readBindInfo(Context context);
    void modifyWifi(Context context);
    void saveMode(int currentMode, Context context);
    float powerPorcessor(String nodeId, String value, Context context);
    void deleteData(Context context, int nodeId);
}
