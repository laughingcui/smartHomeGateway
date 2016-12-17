package com.everyoo.smartgateway.everyoolocaldata.sql.dao;

import android.content.Context;


/**
 * Created by Administrator on 2016/10/31.
 */
public class EveryooSql {
    public static EveryooSql everyooSql = null;
    private static Context mContext;
    private CtrlDao mCtrlDao;
    private DefineActionDao mDefineActionDao;
    private DefineAttriDao mDefineAttriDao;
    private DeviceLogDao mDeviceLogDao;
    private DeviceStatusDao mDeviceStatusDao;
    private GwBindDao mGwBindDao;
    private LinkageActionDao mLinkageActionDao;
    private LinkageEnableDao mLinkageEnableDao;
    private LinkageTriggerDao mLinkageTriggerDao;
    private SceneDao mSceneDao;
    private ScenePanelDao mScenePanelDao;
    private TimingDao mTimingDao;
    private EveryooSql() {
    }
    public static EveryooSql init(Context context){
        mContext=context;
        if(everyooSql == null){
            everyooSql = new EveryooSql();
        }
        return everyooSql;
    }
    public Object getSqlDao(Class clazz){
        String className=clazz.getSimpleName();
        Object obj=null;
        switch (className){
            case "CtrlDao":
                mCtrlDao=(mCtrlDao==null)?new CtrlDao(mContext):mCtrlDao;
                obj=mCtrlDao;
            break;
            case "DefineActionDao":
                mDefineActionDao=(mDefineActionDao==null)?new DefineActionDao(mContext):mDefineActionDao;
                obj=mDefineActionDao;
                break;
            case "DefineAttriDao":
                mDefineAttriDao=(mDefineAttriDao==null)?new DefineAttriDao(mContext):mDefineAttriDao;
                obj=mDefineAttriDao;
                break;
            case "DeviceLogDao":
                mDeviceLogDao=(mDeviceLogDao==null)?new DeviceLogDao(mContext):mDeviceLogDao;
                obj=mDeviceLogDao;
                break;
            case "DeviceStatusDao":
                mDeviceStatusDao=(mDeviceStatusDao==null)?new DeviceStatusDao(mContext):mDeviceStatusDao;
                obj=mDeviceStatusDao;
                break;
            case "GwBindDao":
                mGwBindDao=(mGwBindDao==null)?new GwBindDao(mContext):mGwBindDao;
                obj=mGwBindDao;
                break;
            case "LinkageActionDao":
                mLinkageActionDao=(mLinkageActionDao==null)?new LinkageActionDao(mContext):mLinkageActionDao;
                obj=mLinkageActionDao;
                break;
            case "LinkageTriggerDao":
                mLinkageTriggerDao=(mLinkageTriggerDao==null)?new LinkageTriggerDao(mContext):mLinkageTriggerDao;
                obj=mLinkageTriggerDao;
                break;
            case "LinkageEnableDao":
                mLinkageEnableDao=(mLinkageEnableDao==null)?new LinkageEnableDao(mContext):mLinkageEnableDao;
                obj=mLinkageEnableDao;
                break;
            case "SceneDao":
                mSceneDao=(mSceneDao==null)?new SceneDao(mContext):mSceneDao;
                obj=mSceneDao;
                break;
            case "ScenePanelDao":
                mScenePanelDao=(mScenePanelDao==null)?new ScenePanelDao(mContext):mScenePanelDao;
                obj=mScenePanelDao;
                break;
            case "TimingDao":
                mTimingDao=(mTimingDao==null)?new TimingDao(mContext):mTimingDao;
                obj=mTimingDao;
                break;
        }
        return obj;
    }
}
