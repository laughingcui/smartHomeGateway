package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageActionBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageEnableBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageTriggerBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageActionDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageEnableDao;
import com.everyoo.smartgateway.everyoolocaldata.sql.dao.LinkageTriggerDao;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/6/16.
 */
public class LinkageProcessor {

    private final String TAG = "LinkageProcessor ";
    private EveryooHttp mHttp;
    private LinkageBean linkageBean;
    private LinkageTriggerDao linkageTriggerDao;
    private LinkageActionDao linkageActionDao;
    private LinkageEnableDao linkageEnableDao;
    private Context mContext;

    public LinkageProcessor(Context context) {
        mContext = context;
        mHttp= InitApplication.mHttp;
        linkageTriggerDao = (LinkageTriggerDao) InitApplication.mSql.getSqlDao(LinkageTriggerDao.class);
        linkageActionDao = (LinkageActionDao)InitApplication.mSql.getSqlDao(LinkageActionDao.class);
        linkageEnableDao = (LinkageEnableDao)InitApplication.mSql.getSqlDao(LinkageEnableDao.class);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CREATE_SUCCESS:
                    create(linkageBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.CREATE_SUCCESS), linkageBean.getUserId());
                    break;
                case Constants.CREATE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.CREATE_FAILED), linkageBean.getUserId());
                    break;
                case Constants.DELETE_SUCCESS:
                    delete(linkageBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.DELETE_SUCCESS), linkageBean.getUserId());
                    break;
                case Constants.DELETE_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.DELETE_FAILED), linkageBean.getUserId());
                    break;
                case Constants.MODIFY_SUCCESS:
                    update(linkageBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.MODIFY_SUCCESS), linkageBean.getUserId());
                    break;
                case Constants.MODIFY_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.MODIFY_FAILED), linkageBean.getUserId());
                    break;
                case Constants.START_SUCCESS:
                    enable(linkageBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.START_SUCCESS), linkageBean.getUserId());
                    break;
                case Constants.START_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.START_FAILED), linkageBean.getUserId());
                    break;
                case Constants.STOP_SUCCESS:
                    enable(linkageBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.STOP_SUCCESS), linkageBean.getUserId());
                    break;
                case Constants.STOP_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean, Constants.STOP_FAILED), linkageBean.getUserId());
                    break;
                case Constants.UPDATE_INTELIGENT_SUCCESSFUL:
                    updateInteligent(linkageBean);
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean,Constants.UPDATE_INTELIGENT_SUCCESSFUL),linkageBean.getUserId());
                    break;
                case Constants.UPDATE_INTELIGENT_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.linkage(linkageBean,Constants.UPDATE_INTELIGENT_FAILED),linkageBean.getUserId());
                    break;
                default:
                    break;
            }
        }
    };

    public void processor(LinkageBean linkageBean) {
        if (linkageBean != null) {
            this.linkageBean = linkageBean;
            int type = linkageBean.getMsgType();
            if (type == Constants.VALUE_CREATE) {
                mHttp.createLinkage(mContext,linkageBean, handler);
            } else if (type == Constants.VALUE_DELETE) {
                mHttp.deleteLinkage(mContext,linkageBean, handler);
            } else if (type == Constants.VALUE_MODIFY) {
                mHttp.updateLinkage(mContext,linkageBean, handler);
            } else if (type == Constants.VALUE_START) {
                linkageBean.setEnable(Constants.ENABLE);
                mHttp.enableLinkage(mContext,linkageBean, handler);
            } else if (type == Constants.VALUE_STOP) {
                linkageBean.setEnable(Constants.UNENABLE);
                mHttp.enableLinkage(mContext,linkageBean, handler);
            } else {
                LogUtil.println(TAG + "processor", "type is invalid and type = " + type);
            }
        } else {
            LogUtil.println(TAG + "processor", "linkageBean is null");
        }
    }

    public void create(final LinkageBean linkageBean) {
        if (linkageBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    linkageTriggerDao.create(linkBeanToTriggerLinkageBean(linkageBean.getTriggerLinkageBeans()));
                    linkageActionDao.create(linkBeanToLinkageActionBean(linkageBean.getActionLinkageBeans()));
                    linkageEnableDao.create(linkBeanToLinkageEnableBean(linkageBean));
                }
            }).start();
        } else {
            LogUtil.println(TAG + "create", "linkageBean is null");
        }
    }

    public void delete(final LinkageBean linkageBean) {
        if (linkageBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    linkageTriggerDao.delete(Constants.LINKAGE_LINKAGEID, linkageBean.getLinkageId());
                    linkageActionDao.delete(Constants.LINKAGE_LINKAGEID, linkageBean.getLinkageId());
                    linkageEnableDao.delete(linkageBean.getLinkageId());
                }
            }).start();
        } else {
            LogUtil.println(TAG + "delete", "linkageBean is null");
        }
    }

    public void update(final LinkageBean linkageBean) {
        if (linkageBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    linkageTriggerDao.delete(Constants.LINKAGE_LINKAGEID, linkageBean.getLinkageId());
                    linkageActionDao.delete(Constants.LINKAGE_LINKAGEID, linkageBean.getLinkageId());

                    linkageTriggerDao.create(linkBeanToTriggerLinkageBean(linkageBean.getTriggerLinkageBeans()));
                    linkageActionDao.create(linkBeanToLinkageActionBean(linkageBean.getActionLinkageBeans()));
                }
            }).start();
        } else {
            LogUtil.println(TAG + "update", "linkageBean is null");
        }
    }

    public void enable(final LinkageBean linkageBean) {
        if (linkageBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LinkageEnableBean bean=new LinkageEnableBean();
                    bean.setLinkageId(linkageBean.getLinkageId());
                    bean.setEnable(linkageBean.getEnable());
                    linkageEnableDao.update(bean);
                }
            }).start();
        } else {
            LogUtil.println(TAG + "update", "linkageBean is null");
        }
    }


    public void updateInteligent(final LinkageBean linkageBean){
        if (linkageBean != null ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0;i < linkageBean.getTriggerLinkageBeans().size();i++){
                        linkageTriggerDao.delete(Constants.LINKAGE_LINKAGEID,linkageBean.getTriggerLinkageBeans().get(i).getLinkageId());
                    }
                    linkageTriggerDao.create(linkBeanToTriggerLinkageBean(linkageBean.getTriggerLinkageBeans()));
                }
            }).start();
        }
    }
    private ArrayList<LinkageTriggerBean> linkBeanToTriggerLinkageBean(ArrayList<LinkageBean> list){
        ArrayList<LinkageTriggerBean> tList=new ArrayList<>();
        for(LinkageBean bean:list){
            LinkageTriggerBean tBean=new LinkageTriggerBean();
            tBean.setValue(bean.getTriggerValue());
            tBean.setFlag(bean.getFlag());
            tBean.setLinkageId(bean.getLinkageId());
            tBean.setCtrlId(bean.getTriggerCtrlId());
            tBean.setDeviceId(bean.getTriggerDeviceId());
            tBean.setIsConform(bean.getIsConform());
            tBean.setRelationship(bean.getRelationship());
            tList.add(tBean);
        }
        return tList;
    }
    private ArrayList<LinkageActionBean>   linkBeanToLinkageActionBean(ArrayList<LinkageBean> list){
        ArrayList<LinkageActionBean> aList=new ArrayList<>();
        for(LinkageBean bean:list){
            LinkageActionBean aBean=new LinkageActionBean();
            aBean.setDeviceId(bean.getActionDeviceId());
            aBean.setCtrlId(bean.getActionCtrlId());
            aBean.setFlag(bean.getFlag());
            aBean.setLinkageId(bean.getLinkageId());
            aBean.setValue(bean.getActionValue());
            aList.add(aBean);
        }
        return aList;
    }
    private LinkageEnableBean linkBeanToLinkageEnableBean(LinkageBean bean) {

        LinkageEnableBean eBean = new LinkageEnableBean();
        eBean.setEnable(bean.getEnable());
        eBean.setLinkageId(bean.getLinkageId());
        return eBean;
    }
}
