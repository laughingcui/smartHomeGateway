package org.pjsip.help;

import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.pjsip_status_code;

/**
 * Created by will on 2016/1/24.
 */
public interface MyAppOberver
{
    void notifyRegState(pjsip_status_code code, String reason,
                        int expiration);
    void notifyBuddyState(MyBuddy buddy);
    void notifyEpState(Endpoint ep);
    void receiveMessage(OnInstantMessageParam prm);
}