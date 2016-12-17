package org.pjsip.help;

import org.pjsip.pjsua2.Buddy;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.BuddyInfo;
import org.pjsip.pjsua2.pjsip_evsub_state;
import org.pjsip.pjsua2.pjsua_buddy_status;

/**
 * Created by will on 2016/1/24.
 */
public class MyBuddy extends Buddy
{
    public BuddyConfig cfg;

    MyBuddy(BuddyConfig config)
    {
        super();
        cfg = config;
    }

    public String getStatusText()
    {
        BuddyInfo bi;

        try {
            bi = getInfo();
        } catch (Exception e) {
            return "?";
        }

        String status = "";
        if (bi.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_ACTIVE) {
            if (bi.getPresStatus().getStatus() ==
                    pjsua_buddy_status.PJSUA_BUDDY_STATUS_ONLINE)
            {
                status = bi.getPresStatus().getStatusText();
                if (status == null || status.length()==0) {
                    status = "Online";
                }
            } else if (bi.getPresStatus().getStatus() ==
                    pjsua_buddy_status.PJSUA_BUDDY_STATUS_OFFLINE)
            {
                status = "Offline";
            } else {
                status = "Unknown";
            }
        }
        return status;
    }

    @Override
    public void onBuddyState()
    {
        try {
            BuddyInfo buddyInfo = getInfo();
            System.out.println("MyBuddy subStateName = " + buddyInfo.getSubStateName() + "contact = " + buddyInfo.getContact() + "uri = " + buddyInfo.getUri() + "subStateName = " + buddyInfo.getSubStateName());
            System.out.println("statusText = " + buddyInfo.getPresStatus().getStatusText());
            if (buddyInfo.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_ONLINE){
                System.out.println("MyBuddy PJSUA_BUDDY_STATUS_ONLINE");
            }else if (buddyInfo.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_OFFLINE){
                System.out.println("MyBuddy PJSUA_BUDDY_STATUS_OFFLINE");
            }else if (buddyInfo.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_UNKNOWN){
                System.out.println("MyBuddy PJSUA_BUDDY_STATUS_UNKNOWN");
            } else {
                System.out.println("MyBuddy getPresStatus.getStatus unknow");

            }
            if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_ACCEPTED) {
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_ACCEPTED");
            }else if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_ACTIVE){
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_ACTIVE");
            }else if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_NULL){
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_NULL");
            }else if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_PENDING){
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_PENDING");
            }else if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_SENT){
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_SENT");
            }else if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_TERMINATED){
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_TERMINATED");
            }else if (buddyInfo.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_UNKNOWN){
                System.out.println("MyBuddy PJSIP_EVSUB_STATE_UNKNOWN");
            }else {
                System.out.println("MyBuddy unknow");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyApp.observer.notifyBuddyState(this);
    }


}