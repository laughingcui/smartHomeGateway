package com.everyoo.smartgateway.everyoocore.message.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.everyoo.smartgateway.everyoocore.bean.EventMessage;
import com.everyoo.smartgateway.smartgateway.Constants;
import com.everyoo.smartgateway.smartgateway.InitApplication;
import com.everyoo.smartgateway.utils.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolService extends Service {
    private static final String TAG = "RecClientInfoService";
    private ThreadPoolExecutor uploadThreadPoolExecutor;
    private ThreadPoolExecutor downloadThreadPoolExecutor;
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 10;
    private String uploadMessage;
    private String downloadMessage;
    private MsgProcessor msgProcessor;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.println(TAG + "onCreate", "create ThreadPoolService");
        msgProcessor = new MsgProcessor(ThreadPoolService.this);
        uploadThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(20), rejectedExecutionHandler);
        downloadThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(20), rejectedExecutionHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        InitApplication.mEventBus.register(this);
        return START_NOT_STICKY;
    }

    /**
     * 分配线程处理上报任务
     */
    public void excuteUploadTask(final String message) {
        uploadThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                msgProcessor.receiveUploadMsg(message);
            }
        });
    }

    public void excuteDownloadTask(final String message) {
        downloadThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                msgProcessor.receiveDownloadMsg(message);
            }
        });
    }

    /**
     * 任务拒绝处理事件（当任务量大于消息队列长度时，便会出现此异常）
     */
    public RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LogUtil.println(TAG + "RejectedExecutionHandler", " process unhandled message");
        }
    };

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void handlerEventMessage(EventMessage msg){
        if (Constants.ACTION_UPLOAD_MESSAGE.equals(msg.getMessageType())){
            LogUtil.controlLog(TAG + "broadcastReceiver","received upload message");
            uploadMessage = msg.getMessage();
            excuteUploadTask(uploadMessage);
        }else if (Constants.ACTION_DOWNLOAD_MESSAGE.equals(msg.getMessageType())){
            downloadMessage = msg.getMessage();
            excuteDownloadTask(downloadMessage);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        InitApplication.mEventBus.unregister(this);
        uploadThreadPoolExecutor.shutdownNow();
        downloadThreadPoolExecutor.shutdownNow();
    }


}
