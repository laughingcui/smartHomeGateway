package com.everyoo.smartgateway.everyoocore.message.processor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.everyoo.smartgateway.everyoocore.bean.ModeBean;
import com.everyoo.smartgateway.everyoocore.message.core.BroadcastAction;
import com.everyoo.smartgateway.everyoocore.message.impl.ModeSwitchProcessorImpl;
import com.everyoo.smartgateway.everyoohttp.core.EveryooHttp;
import com.everyoo.smartgateway.everyoolocaldata.sp.SPHelper;
import com.everyoo.smartgateway.everyoosip.PjsipMsgAction;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

/**
 * Created by chaos on 2016/6/21.
 */
public class ModeSwitchProcessor implements ModeSwitchProcessorImpl {

    private EveryooHttp mHttp;
    private ModeBean modeBean;
    private final String TAG = "ModeSwitchProcessor ";
    private Context mContext;

    public ModeSwitchProcessor(Context context) {
        mHttp= InitApplication.mHttp;
        mContext = context;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtil.println(TAG + "handler", "modeBean.getStatus = " + modeBean.getStatus() + "Constants.currentMode = " + Constants.currentMode);
            switch (msg.what) {
                case Constants.MODE_SWITCH_SUCCESS:
                    if (modeBean.getStatus() == Constants.CONTROL_MODE) {
                        Constants.currentMode = Constants.CONTROL_MODE;
                    } else {
                        Constants.currentMode = Constants.WISDOM_MODE;
                    }
                    BroadcastAction.gatewayToSip(PjsipMsgAction.modeSwitch(modeBean.getUserId(), Constants.currentMode + ""), modeBean.getUserId());
                    break;
                case Constants.MODE_SWITCH_FAILED:
                    BroadcastAction.gatewayToSip(PjsipMsgAction.modeSwitch(modeBean.getUserId(), Constants.currentMode + ""), modeBean.getUserId());
                    break;
            }
            SPHelper.getInstance().saveMode(Constants.currentMode,mContext);
            LogUtil.println(TAG + "handler", "modeBean.getStatus = " + modeBean.getStatus() + "Constants.currentMode = " + Constants.currentMode);
        }
    };

    @Override
    public void processor(ModeBean modeBean) {
        if (modeBean != null) {
            this.modeBean = modeBean;
            mHttp.modeSwitch(mContext,modeBean.getStatus(), handler);
        } else {
            LogUtil.println(TAG + "parseMessage", "modeBean is null");
        }
    }
}
