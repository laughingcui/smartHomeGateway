package com.everyoo.smartgateway.everyoocore.bean;

/**
 * Created by Administrator on 2016/11/14.
 */
public class EventMessage {
    private String messageType;
    private String message;
    private String usrId;
    public EventMessage(String messageType, String message, String usrId) {
        this.messageType = messageType;
        this.message = message;
        this.usrId=usrId;
    }
    public String getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getUsrId() {
        return usrId;
    }
}
