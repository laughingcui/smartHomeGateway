package com.everyoo.smartgateway.everyoohttp.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class GetAppVersionBean {
    private String packageName;
    private String deviceId;

    public String getPackageName() {
        return packageName;
    }
    public String getDeviceId(){return deviceId;}

    public void setPackageName(String packageName,String deviceId) {
        this.packageName = packageName;
        this.deviceId = deviceId;
    }
}
