package com.everyoo.smartgateway.utils;

import android.util.Log;

import com.everyoo.smartgateway.everyoozwave.tronico.kuju.DeviceInfo;
import com.everyoo.smartgateway.everyoozwave.tronico.kuju.SdkConstants;


/**
 * Created by Administrator on 2015/10/12.
 */
public class BHDConverterUtils {

    /*
    hex string to byte array
     */
    public static byte[] hexStringToByteArray(String s) {
        if (s != null &&!s.equals("")) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }
            return data;
        }
       return null;

    }

    public static String byte2HexStrNonSpace(byte[] b,int length)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            //sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byte2HexStrSpace(byte[] b,int length)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byteArrayToHexString(byte[] b,int start,int stop)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=start;n<=stop;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            //sb.append(" ");
        }
        System.out.println("deviceinfo :"+sb);
        return sb.toString().toUpperCase().trim();
    }

    public static int deviceInfoToByteArray(DeviceInfo df, byte[] des) {
        if (df == null)
            return -1;
        int len = 35 + 2 * df.cmdlen;
        int index = 0;
        //des=new byte[len];
        des[index++] = (byte) len;
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
      /*  if (df.isOnline == true) {
            des[index++] = 0x01;
        } else if (df.isOnline == false) {
            des[index++] = 0x00;
        }*/
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


    public static double byteArrayToInt(byte[] cmd, int length) {
        return Integer.parseInt(BHDConverterUtils.byte2HexStrNonSpace(cmd, length));
    }
}
