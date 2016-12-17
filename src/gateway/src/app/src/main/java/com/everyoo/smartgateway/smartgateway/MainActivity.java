package com.everyoo.smartgateway.smartgateway;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.everyoo.gatewaylitedaemon.R;
import com.everyoo.smartgateway.everyoozwave.zwavesdk.MessageManager;
import com.everyoo.smartgateway.utils.RestartAppUtil;
import com.everyoo.smartgateway.utils.UpdateUtil;

public class MainActivity extends Activity {

    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("name.length = " + name.length());
                RestartAppUtil.restartApp(MainActivity.this, 0);
            }
        });

        findViewById(R.id.btn_kill_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_kill_sdk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MessageManager.controller.closeDev(true);
                    }
                }).start();

            }
        });

        /*findViewById(R.id.up_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitApplication.mHttp.uploadFile(MainActivity.this);
            }
        });*/


    }
}
