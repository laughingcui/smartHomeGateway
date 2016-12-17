package com.everyoo.smartgateway.utils;

/**
 * Created by Administrator on 2015/11/19.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;


import com.everyoo.smartgateway.everyoocore.upgrade.UpgradeService;
import com.everyoo.smartgateway.smartgateway.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateUtil {
    private final String TAG = "UpdateUtil ";
    // 文件分隔符
    private static final String FILE_SEPARATOR = "/";
    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + FILE_SEPARATOR + "autoupdate" + FILE_SEPARATOR;
    // 下载应用存放全路径
    public static final String FILE_NAME = FILE_PATH + "gatewaylitedaemon.apk";
    // 更新应用版本标记
    private static final int UPDARE_TOKEN = 0x29;
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 0x31;

    private static final int FINISH_INSTALL_TOKEN = 0x30;
    private static final int INSTALL_BAD_TOKEN = 0x32;


    private Context mContext;
    private String message = "检测到本程序有新版本发布，建议您更新！";
    // private String spec = "http://192.168.101.150:93/gateway/upgrade";
    // 下载应用的对话框
    private Dialog dialog;
    // 下载应用的进度条
    private ProgressBar progressBar;
    // 进度条的当前刻度值
    private int curProgress;
    // 用户是否取消下载
    private boolean isCancel;

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";


    public UpdateUtil(Context context) {
        mContext = context;
    }

    private static final int INSTALL_SUCCESS = 12;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
                    System.out.println("completed " + curProgress + "%");
                    break;
                case INSTALL_TOKEN:
                    System.out.println("download successful");
                    //  installApp();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            installSlient(mContext, FILE_NAME);

                        }
                    }).start();
                    break;
                case INSTALL_SUCCESS:
                    System.out.println("install success");
                    RestartAppUtil.restartApp(mContext, 20 * 1000);
                    System.out.println("send broadcast");
                    mContext.stopService(new Intent(mContext, UpgradeService.class));
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 下载新版本应用
     */
    public void downloadApp(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;
                System.out.println("download Thread starts now");
                try {
                    System.out.println("create URL  object");
                    url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    System.out.println("connecting http: " + path);
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    System.out.println("file size: " + fileLength + "bytes");
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if (!filePath.exists()) {
                        filePath.mkdir();
                        System.out.println("created " + FILE_PATH);
                    } else
                        System.out.println(FILE_PATH + " already exists");
                    out = new FileOutputStream(new File(FILE_NAME));
                    byte[] buffer = new byte[1024 * 4];
                    int len = 0;
                    long readedLength = 0l;
                    while ((len = in.read(buffer)) != -1) {
                        // 用户点击“取消”按钮，下载中断
                        if (isCancel) {
                            break;
                        }
                        out.write(buffer, 0, len);
                        readedLength += len;
                        curProgress = (int) (((float) readedLength / fileLength) * 100);
                        handler.sendEmptyMessage(UPDARE_TOKEN);
                        if (readedLength >= fileLength) {
                            // 下载完毕，通知安装
                            System.out.println("download completed");
                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception Catched! download fail");
                    Constants.isInstall = false;
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                            System.out.println("fileoutput stream stop");
                        } catch (IOException e) {
                            System.out.println("fileoutputstream creation fail");
                            Constants.isInstall = false;
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                            System.out.println("fileinputstream close");
                        } catch (IOException e) {
                            Constants.isInstall = false;
                            System.out.println("fileinputstream creation fail");
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                        System.out.println("http connection stop");
                    }
                }
            }
        }).start();
    }

    /**
     * 安装新版本应用
     */
    /*
    private void installApp() {
        File appFile = new File(FILE_NAME);
        if (!appFile.exists()) {
            System.out.println("file not exist");
            return;
        }
        System.out.println("start install");
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    */
    private void installApp() {
        try {
            System.out.println("UpdateUtil has not root permission");
            System.out.println("start install");
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "pm install -r " + FILE_NAME});
            proc.waitFor();
            //下载完成，发送广播
            System.out.println("install complete");
            handler.sendEmptyMessage(INSTALL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("no root");
        }
    }


    public void install(final Context context, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                installSlient(context, filePath);
            }
        }).start();
    }

    /**
     * install slient
     *
     * @param context
     * @param filePath
     * @return 0 means normal, 1 means file not exist, 2 means other exception error
     */
    public int installSlient(Context context, String filePath) {
        File file;
        if (filePath == null || filePath.length() == 0 || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {
            return 1;
        }
        System.out.println(TAG + "installSilent prepare install");
        String[] args = {"pm", "install", "-r", filePath};
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result;
        try {
            System.out.println(TAG + "installSilent begin install");
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            System.out.println(TAG + "installSilent end install");
            handler.sendEmptyMessage(INSTALL_SUCCESS);// 测试证明，安装后会阻塞大概15秒，所以如果本行代码以下的代码执行时间大于15秒，handler就会失效（应用安装后会被系统停止，需要重启），从而无法实现重启。解决方案就是在应用重启方法中设置重启延时

            while ((s = successResult.readLine()) != null) {
                System.out.println(TAG + "installSilent end install2.5");
                successMsg.append(s);
                System.out.println(TAG + "installSilent end install3");
            }

            System.out.println(TAG + "installSilent end install4");
            while ((s = errorResult.readLine()) != null) {
                System.out.println(TAG + "installSilent end instal14.5");
                errorMsg.append(s);
                System.out.println(TAG + "installSilent end install5");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(TAG + "installSilent end install7");
            result = 2;
        } finally {
            System.out.println(TAG + "installSilent end install8");
            try {
                if (successResult != null) {
                    System.out.println(TAG + "installSilent end install9");
                    successResult.close();
                }
                if (errorResult != null) {
                    System.out.println(TAG + "installSilent end install10");
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(TAG + "installSilent end install11");
            }
            if (process != null) {
                System.out.println(TAG + "installSilent end install12");
                process.destroy();
                System.out.println(TAG + "installSilent end install13");
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
            System.out.println(TAG + "installSilent end install14");
        } else {
            System.out.println(TAG + "installSilent end install15");
            result = 2;
        }
        System.out.println(TAG + "installSilent successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
        //   handler.sendEmptyMessage(INSTALL_SUCCESS);
        return result;
    }

    public static void execShell(String cmd) {
        try {
            //权限设置
            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /***********************************************************************************************/

    private static boolean hasRootPerssion() {
        int i = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
        if (i != -1) {
            System.out.println("UpdateUtil hasRootPermision has root permission");
            return true;
        }
        System.out.println("UpdateUtil hasRootPermision has not root permission");
        return false;
    }

    // 执行linux命令但不关注结果输出
    protected static int execRootCmdSilent(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            int result = localProcess.exitValue();
            return (Integer) result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }

    //used to alert UI of install done and then reboot., not perform it until timeout.
    //in  order to have enough time for the transfer.
    public static void timer(int delaytime) {
        Timer timer1 = null;
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("now, reboot");
                execShell("reboot");
            }
        }, delaytime);
    }
}
