package com.everyoo.smartgateway.everyoozwave.zwavesdk;

import android.content.Context;
import android.util.Log;

import com.everyoo.smartgateway.everyoozwave.tronico.kuju.DeviceInfo;
import com.everyoo.smartgateway.everyoozwave.tronico.kuju.SdkConstants;
import com.everyoo.smartgateway.smartgateway.ActionId;
import com.everyoo.smartgateway.utils.BHDConverterUtils;
import com.everyoo.smartgateway.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/1/27.
 */
public class ParseDeviceInfoAction {

    private Context mContext;
    public ParseDeviceInfoAction(Context context) {
        mContext = context;
    }

    /**
     * 解析从sdk上报的设备信息
     *
     * @param
     */
    public void parseDeviceInfo(DeviceInfo deviceInfo, String userId) {
        LogUtil.println("ParseDeviceInfoAction parseDeviceInfo","userId = " + userId);
        if (deviceInfo != null){
            byte[] des = new byte[deviceInfo.cmdlen * 2 + 35];
            int length = DeviceInfoToByteArray(deviceInfo, des);
            sendData(deviceInfo.nodeId, des, length,userId);
        }else {
            LogUtil.println("ParseDeviceInfoAction parseDeviceInfo"," deviceInfo is null");
        }
    }


    public int DeviceInfoToByteArray(DeviceInfo df, byte[] des) {
        if (df == null)
            return -1;
        int len = 35 + 2 * df.cmdlen;
        int index = 0;
        des[index++] = df.ManufacturerId[0];
        des[index++] = df.ManufacturerId[1];
        des[index++] = df.ProductType[0];
        des[index++] = df.ProductType[1];
        des[index++] = df.ProductId[0];
        des[index++] = df.ProductId[1];

        des[index++] = df.CompmentCnt;
        des[index++] = df.ComponentType1;
        des[index++] = df.EndPoint1;
        des[index++] = df.ComponentType2;
        des[index++] = df.EndPoint2;
        des[index++] = df.ComponentType3;
        des[index++] = df.EndPoint3;
        des[index++] = df.ComponentType4;
        des[index++] = df.EndPoint4;

        des[index++] = df.FirmwareVersion[0];
        des[index++] = df.FirmwareVersion[1];
        des[index++] = df.SdkVersion[0];
        des[index++] = df.SdkVersion[1];
        des[index++] = df.nodeId;
        des[index++] = df.basic;
        des[index++] = df.generic;
        des[index++] = df.specific;
        des[index++] = df.capability;
        des[index++] = df.security;
        des[index++] = df.reserved;
        if (df.isOnline == SdkConstants.DEVICE_ONLINE) {
            des[index++] = 0x01;
        } else if (df.isOnline == SdkConstants.DEVICE_NOTONLINE) {
            des[index++] = 0x00;
        }
        des[index++] = df.InclusionDateTime[0];
        des[index++] = df.InclusionDateTime[1];
        des[index++] = df.InclusionDateTime[2];
        des[index++] = df.InclusionDateTime[3];
        des[index++] = df.InclusionDateTime[4];
        des[index++] = df.InclusionDateTime[5];
        des[index++] = df.cmdlen;
        Log.e("df.cmdlen", "df.cmdlen=" + df.cmdlen);
        Log.e("index--index--index", "index=" + index);
        for (int loop = 0; loop < df.cmdlen; loop++) {
            des[index++] = df.cmdClasseAndVersion[loop][0];
            des[index++] = df.cmdClasseAndVersion[loop][1];
        }
        return len;
    }


    private void sendData(byte nodeid, byte[] dtcontent, int dtcontentlen,String userId) {
        byte checksum = 0;
        int index = 0;
        int len = 5 + 4 + dtcontentlen;         //固定5个字节，再加上传进来的5个字节
        byte[] cmd = new byte[len];
        cmd[index++] = (byte) 0xa0;    //header
        cmd[index++] = 0x00;       //seqnumber
        cmd[index++] = (byte) len;             //length
        cmd[index++] = nodeid;        //Node Id
        cmd[index++] = 0x00;        //03: Report
        cmd[index++] = 0x00;    //command code
        cmd[index++] = 0x00;    //command code
        for (int i = 0; i < dtcontentlen; i++) {
            cmd[index++] = dtcontent[i];
            System.out.println("content is" + dtcontentlen + "|" + index + "|" + cmd[i] + "|" + dtcontent[0] + "|" + dtcontent[i]);
        }
        System.out.println("dtcontent is " + BHDConverterUtils.byteArrayToHexString(dtcontent, 0, dtcontentlen - 1));
        for (int loop = 0; loop < len - 2; loop++) {
            checksum = (byte) (checksum + cmd[loop]);
        }
        cmd[index++] = checksum;     //checksum
        cmd[index++] = (byte) 0xa1;   //tailer
        LogUtil.println("ParseDeviceInfoAction sendData","userId = " + userId);
        MessageProcessor.sendToGateway(MessageProcessor.generateMsg(nodeid, BHDConverterUtils.byte2HexStrNonSpace(cmd,cmd.length), ActionId.DEVICE_INFOMATION, userId));

    }


}
