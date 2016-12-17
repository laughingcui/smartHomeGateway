package com.everyoo.smartgateway.everyoocore.bugs;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.everyoo.smartgateway.smartgateway.Constants;

/**
 * Created by Administrator on 2016/10/25.
 */

public class BugTagsAction {
    public static boolean isBugTagsInitilized = false;
    public static void bugCollect(final Application application) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                BugtagsOptions options = new BugtagsOptions.Builder().
                        trackingLocation(true).       //是否获取位置，默认 true
                        trackingCrashLog(true).       //是否收集闪退，默认 true
                        trackingConsoleLog(false).     //是否收集控制台日志，默认 true
                        trackingUserSteps(false).      //是否跟踪用户操作步骤，默认 true
                        crashWithScreenshot(false).    //收集闪退是否附带截图，默认 true
                        build();
                Bugtags.start("567f4243553e310f7c23de7e3b3c64db", application, Bugtags.BTGInvocationEventBubble, options);//初始化
                Bugtags.setUserData("爱悠网关序列号 ", Constants.gatewaySn);
                isBugTagsInitilized = true;
            }
        });


    }

}
