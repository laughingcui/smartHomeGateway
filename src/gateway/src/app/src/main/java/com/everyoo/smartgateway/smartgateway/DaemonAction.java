package com.everyoo.smartgateway.smartgateway;

import android.content.Context;

import com.everyoo.smartgateway.utils.FileUtil;
import com.yyh.fork.NativeRuntime;


/**
 * Created by abc on 2016/7/27.
 */
public class DaemonAction {


    public static void daemonAction(final Context context) {
        String executable = "libhelper.so";
        String aliasfile = "helper";
        String parafind = "/data/data/" + context.getPackageName() + "/" + aliasfile;
        String retx = "false";
        NativeRuntime.getInstance().RunExecutable(context.getPackageName(), executable, aliasfile, context.getPackageName() + "/com.everyoo.smartgateway.everyoocore.networkobserver.NetworkService");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NativeRuntime.getInstance().startService(context.getPackageName() + "/com.everyoo.smartgateway.everyoocore.networkobserver.NetworkService", new FileUtil().createRootPath(context));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
