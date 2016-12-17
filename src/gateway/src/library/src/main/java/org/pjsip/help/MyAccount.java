package org.pjsip.help;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnInstantMessageStatusParam;
import org.pjsip.pjsua2.OnRegStateParam;

import java.util.ArrayList;

/**
 * Created by will on 2016/1/24.
 */
public class MyAccount extends Account {
    public ArrayList<MyBuddy> buddyList = new ArrayList<MyBuddy>();
    public AccountConfig cfg;

  /*  public MyAccount(AccountConfig config){
        super();
        cfg = config;
        myAccount = new MyAccount();
    }
*/
    private MyAccount(){};
    private static MyAccount myAccount;
    public static MyAccount getMyAccount(){
        if (myAccount == null){
            myAccount = new MyAccount();
        }
        return myAccount;
    }

    public MyBuddy addBuddy(BuddyConfig bud_cfg) {
	    /* Create Buddy */
        MyBuddy bud = new MyBuddy(bud_cfg);
        try {
            bud.create(this, bud_cfg);
        } catch (Exception e) {
            bud.delete();
            bud = null;
        }
        return bud;
    }

    public void delBuddy(MyBuddy buddy) {
        buddy.delete();
    }


    @Override
    public void onRegState(OnRegStateParam prm) {
        MyApp.observer.notifyRegState(prm.getCode(), prm.getReason(),
                prm.getExpiration());
    }


    @Override
    public void onInstantMessage(OnInstantMessageParam prm)
    {//接收pjsip message
        MyApp.observer.receiveMessage(prm);
    }

    @Override
    public void onInstantMessageStatus(OnInstantMessageStatusParam prm) {
        super.onInstantMessageStatus(prm);
        System.out.println("message body-" + prm.getMsgBody());
        if (onMessageStatus != null){
            onMessageStatus.onMessageStatusReceive(prm);
        }

    }

    OnMessageStatus onMessageStatus;
    public interface OnMessageStatus{
        void onMessageStatusReceive(OnInstantMessageStatusParam message);
    }

    public void setOnMessageStatus(OnMessageStatus onMessageStatus){
        this.onMessageStatus = onMessageStatus;
    }

}
