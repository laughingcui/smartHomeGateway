package com.everyoo.smartgateway.everyoocore.message.impl;

/**
 * Created by chaos on 2016/6/17.
 * 功能描述：接收指令-指令解析及过滤-生成指令-发送指令
 */
public interface MsgImpl {

    void receiveUploadMsg(String message);
    void parseUploadMsg(String message);
    void processUploadMsg(int nodeId, String value, String actionId, String userId);
    void sendUploadMsg(String message);

    void receiveDownloadMsg(String message);
    void parseDownloadMsg(String message);
    void processDownloadMsg(String message, String userId, int type);
    void sendDownloadMsg(String message);

}
