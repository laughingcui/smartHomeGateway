package com.everyoo.smartgateway.everyoozwave.tronico.kuju;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Controller
{
	private KeyDbHelper keydb;
	private static byte[] kepublic = new byte[16];
	private static byte[] kapublic = new byte[16];
	private static byte[] ktest = new byte[19];
	private static final long ONLINE_CHECK_WAITTIME=5000 * 60;
	private static final int CMDQUEUEMAX=1024;
	private static final int REVDATAQUEUEMAX=1024;
	private static final int ISONLINEQUEUEMAX=1024;
	private static boolean  markId = true;
	public static  byte[] bytes = null;
	private boolean IsDealTheData=false;
	private boolean IsFinished = true;
	private boolean IsFinished1 = true;
	private Context _CtrlAppContext=null;
	private static UsbDevice dev = null;
	private static UsbPort up = null;
    private static int mark = 0;
	private static int nodeIdMark = 0;
	private int _SerialCommand13Processing = 0;
	private ConditionVariable mConditionVariable13;
	private  byte[] getNonce = new byte[8];
	private RevThread rt=null;    
	private DealRevDataThread drt = null;
	private sendCmdThread sct=null;
    private boolean wakeupFlag = false;
    private int doorValue = 0;
	private ConditionVariable mConditionVariable;
	private CheckDevIfOnlineThread IsOnlineThread=null;
    private SendMessageToGatewayListener sendMessageToGatewayListener;
	private byte seqNo=0;
	private byte gcurrentsucid = 0x00;
	ArrayList<DeviceInfo> nodeList = null;
	ArrayList<Object> devList = null;
	QueueManager cmdQueueMng=null;              //发送命令队列
	QueueManager revDataQueueMng=null;        //接收数据队列
	QueueManager revDataQueueMngack=null;        //接收数据队列
	QueueManager revDataQueueMng13=null;        //接收数据队列
//	QueueManager isOnlineQueueMng=null;
	public int dongle_sucess_init = 0;

    private EffectInVisiableHandler mtimeHandler;
    private final int MOBILE_QUERY = 1;
    private EffectInVisiableHandler1 mtimeHandler1;
    private final int MOBILE_QUERY1 = 2;
    private int INCLUDEMARK = 0;
    private int EXECUTEMARK = 0;

	public Controller(Context ParentComponent, Handler handler){
		this._CtrlAppContext=ParentComponent;
		up=new UsbPort(this._CtrlAppContext, mHandler);
		cmdQueueMng=new QueueManager(CMDQUEUEMAX);
		revDataQueueMng=new QueueManager(REVDATAQUEUEMAX);
		revDataQueueMngack=new QueueManager(REVDATAQUEUEMAX);
		revDataQueueMng13 =new QueueManager(16);
		//isOnlineQueueMng=new QueueManager(ISONLINEQUEUEMAX);
		nodeList = new ArrayList<DeviceInfo>();

        this.mtimeHandler = new EffectInVisiableHandler();
        Message msg = this.mtimeHandler.obtainMessage(1);
        this.mtimeHandler1 = new EffectInVisiableHandler1();
        Message msg1 = this.mtimeHandler1.obtainMessage(2);

		keydb = new KeyDbHelper(ParentComponent);
		mConditionVariable = new ConditionVariable();
		mConditionVariable13 = new ConditionVariable();
		permission();
	}
	public void permission(){









		byte[] kav = new byte[] {
				(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55
		};
		byte[] kev = new byte[]{
				(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa
		};
		byte[] textnew = new byte[19];
		byte[] keyss = new byte[16];
		String key = keydb.getKey();
		if(key==null){
				String randomKey = getRandomCharString(32);
         		keydb.insert(randomKey);
			    keyss = HexString2Bytes(randomKey);
			    textnew[0] = 0x00;
			    textnew[1] =(byte)0x98;
			    textnew[2] = 0x06;
			    for(int i=0;i<16;i++){
				textnew[i+3] = keyss[i];
			 }
			   ktest = textnew;
		}else{
			keyss = HexString2Bytes(key);
			textnew[0] = 0x00;
			textnew[1] =(byte)0x98;
			textnew[2] = 0x06;
			for(int i=0;i<16;i++){
				textnew[i+3] = keyss[i];
			}
			ktest = textnew;
		}
		   try {
			   kapublic = EcbEncrypt(keyss, kav);
			   kepublic = EcbEncrypt(keyss, kev);
		   }catch(Exception e){
              e.printStackTrace();
		   }
	}

	public void resetKey(){

		try {
		byte[] kav = new byte[] { (byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55,(byte)0x55
		};
		byte[] kev = new byte[]{
				(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa,(byte)0xaa
		};
		byte[] textnew = new byte[19];
		byte[] keyss = new byte[16];
		 keydb.delData();
		String randomKey = getRandomCharString(32);
		keydb.insert(randomKey);
		keyss = HexString2Bytes(randomKey);
		textnew[0] = 0x00;
		textnew[1] =(byte)0x98;
		textnew[2] = 0x06;
		for(int i=0;i<16;i++){
			textnew[i+3] = keyss[i];
		}
		ktest = textnew;
			kapublic = EcbEncrypt(keyss, kav);
			kepublic = EcbEncrypt(keyss, kev);
		}catch(Exception e){
            e.printStackTrace();
		}
	}

	class SndDataTrack{
		int Action=0;
		int FuncId = 0;
		byte NodeId = 0;
	}

	SndDataTrack sndDataMonitor=new SndDataTrack();

	/*

     * 得到nodelist地址
     */
	public ArrayList<DeviceInfo> getNodeList(){
		return nodeList;
	}
    public void resetTime() {
        this.mtimeHandler.removeMessages(1);
        Message msg = this.mtimeHandler.obtainMessage(1);
        this.mtimeHandler.sendMessageDelayed(msg, 50000L);
    }

    public void resetTime1() {
        this.mtimeHandler1.removeMessages(2);
        Message msg = this.mtimeHandler1.obtainMessage(2);
        this.mtimeHandler1.sendMessageDelayed(msg, 50000L);
    }

	protected synchronized byte seqNo(){
		seqNo++;
		if (seqNo == 0) {
			seqNo++;
		}
		return seqNo;
	}
    public int getCount(){
		return revDataQueueMng.getQueueNum();
	}
	private void sndCmdClass1(int NodeId,byte[] CmdData,int CmdLen,byte FuncId){
		int loop=0,index=0;
		byte checkSum=(byte)0xFF;
		byte[] cmd=new byte[9+CmdLen];
		cmd[index++]=0x01;
		cmd[index++]=(byte)(7+CmdLen);
		cmd[index++]=0x00;
		cmd[index++]=0x13;
		cmd[index++]=(byte)NodeId;
		cmd[index++]=(byte)CmdLen;
		for(loop=0;loop<CmdLen;loop++)
			cmd[index++]=CmdData[loop];
		cmd[index++]=0x05;
		cmd[index++]=FuncId;
		for(loop=0;loop<7+CmdLen;loop++)
			checkSum=(byte)(checkSum^cmd[loop+1]);
		cmd[index]=checkSum;
		bytes = cmd;
		String str = byte2HexStr(cmd, cmd.length);
		cmdQueueMng.pushQueue(cmd, 9+CmdLen);
		mConditionVariable.open();
		//isOnlineQueueMng.pushQueue(cmd, 9+CmdLen);
	}
	private void sndCmdClass(int NodeId,byte[] CmdData,int CmdLen,byte FuncId){
		int loop=0,index=0;
		byte checkSum=(byte)0xFF;
		byte[] cmd=new byte[9+CmdLen];
		cmd[index++]=0x01;
		cmd[index++]=(byte)(7+CmdLen);
		cmd[index++]=0x00;
		cmd[index++]=0x13;
		cmd[index++]=(byte)NodeId;
		cmd[index++]=(byte)CmdLen;
		for(loop=0;loop<CmdLen;loop++)
			cmd[index++]=CmdData[loop];
		cmd[index++]=0x05;
		cmd[index++]=FuncId;
		for(loop=0;loop<7+CmdLen;loop++)
			checkSum=(byte)(checkSum^cmd[loop+1]);
		cmd[index]=checkSum;
		bytes = cmd;
		String str = byte2HexStr(cmd, cmd.length);
		cmdQueueMng.pushQueue(cmd, 9+CmdLen);
		mConditionVariable.open();
		//isOnlineQueueMng.pushQueue(cmd, 9+CmdLen);
	}

	/*
	private void zw_get_suc_nodeid(int NodeId) 
	{
		int loop = 0;
		byte checkSum = (byte)0xFF;
		byte[] cmd = new byte[9];

		cmd[0] = 0x01;	//sof
		cmd[1] = 0x04;  //len
		cmd[2] = 0x00; //req
		cmd[3] = 0x56;
		cmd[4] = seqNo(); 
		for (loop = 0; loop < 4; loop++) {
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		}
		cmd[5] = checkSum;

		gcurrentsucid = (byte)NodeId;
		Log.d("karl", "zw_get_suc_nodeid commadn send\n");
		sndUartCmd(cmd, 6);
	}
	*/
	private void zw_set_suc_nodeid(int NodeId) 
	{
		int loop = 0;
		byte checkSum = (byte)0xFF;
		byte[] cmd = new byte[9];

		cmd[0] = 0x01;	//sof
		cmd[1] = 0x06;  //len
		cmd[2] = 0x00; //req
		cmd[3] = 0x57;
		cmd[4] = (byte)NodeId;
		cmd[5] = (byte)0x25;
		cmd[6] = seqNo(); 
		for (loop = 0; loop < 8; loop++) {
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		}
		cmd[7] = checkSum;

		Log.d("karl", "zw_set suc nodeid send\n");
		sndUartCmd(cmd, 8);
	}
	private void sndUartCmd(byte[] CmdData,int CmdLen){
	    cmdQueueMng.pushQueue(CmdData, CmdLen);
		mConditionVariable.open();
	    //isOnlineQueueMng.pushQueue(CmdData, CmdLen);
	}
	private int SendDataAndWaitAck(byte[] cmd,int len){
		int loop=0;
		if(IsSdkAvailable()==false){
			return Events.UNKNOWN_ERROR;
		}
		/*
        if( IsDealTheData == false ){
        	IsDealTheData=true;
        	int tempRear = revDataQueueMngack.clearQueue();
			Log.e("SendDataAndWaitAck","要发送的数据为:"+byte2HexStr(cmd, len)+" 长度是"+len);
			if(cmd != null){
				bytes = cmd;
				String strs = byte2HexStr(cmd,cmd.length);
				up.sendByte(cmd,dev,len);
    		}
			for(loop=0;loop<15;loop++){
				if(tempRear != revDataQueueMngack.getRearIndexOfQueue())
					break;
				try{
					Thread.sleep(30);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			if(loop==15){
				IsDealTheData=false;
				return Events.NONE_RESPONSE;
			}
			synchronized (revDataQueueMngack) {
				DataElement de = revDataQueueMngack.getDataByIndex(tempRear);
				if(de != null){
					if(de.buf[0]==0x06){
						IsDealTheData=false;
						return Events.SUCCESS;
					}else if(de.buf[0]==0x18){
						IsDealTheData=false;
						return Events.CAN_ERROR;
					}else if(de.buf[0]==0x15){
						IsDealTheData=false;
						return Events.NAK_ERROR;
					}
				}
			}
        	IsDealTheData=false;
        }
		*/
		DataElement de = null;
		revDataQueueMngack.clearQueue();
		if (cmd != null) {
			up.sendByte(cmd, dev, len);
		}
		for (loop = 0; loop < 15; loop++) {
			de = revDataQueueMngack.popQueue();
			if (de == null) {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				if(de.buf[0]==0x06){
					return Events.SUCCESS;
				}else if(de.buf[0]==0x18){
					return Events.CAN_ERROR;
				}else if(de.buf[0]==0x15){
					return Events.NAK_ERROR;
				}
			}
		}
		return Events.UNKNOWN_ERROR;
	}
	private int SendDataAndWaitAck_Callback(byte[] cmd,int len){
		int loop=0;
		DataElement de = null;
		if(IsSdkAvailable()==false){
			return Events.UNKNOWN_ERROR;
		}
        if(true){
   //     	int tempRear = revDataQueueMng13.getRearIndexOfQueue();
			Log.e("SendDataAndWaitAck_Callback","要发送的数据为:"+byte2HexStr(cmd, len)+" 长度是"+len);
			if(cmd != null){
				bytes = cmd;
				String strs = byte2HexStr(cmd,cmd.length);
				up.sendByte(cmd,dev,len);
    		}
/*
			for(loop=0;loop<15;loop++){
				if(tempRear != revDataQueueMng13.getRearIndexOfQueue())
					break;
				try{
					Thread.sleep(30);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			if(loop==15){
				return Events.NONE_RESPONSE;
			}
			*/
			for (loop = 0; loop < 5; loop++) {
				mConditionVariable13.block(1000);
				de = revDataQueueMng13.popQueue();
				if (de == null) {
					Log.e("SendDataAndWaitAck_Callback", "now close and wait ack");
					mConditionVariable13.close();
					continue;
				} else if ((de.length != 1)) {
					Log.e("SendDataAndWaitAck_Callback", "want to get ack, but get lenght("+de.length+"):" + byte2HexStr(de.buf, de.length));
					loop++;
					continue;
				} else {
					break;
				}
			}
			if ((de == null) || (de.length != 1)) {
				if (de != null) {
					Log.e("SendDataAndWaitAck_Callback", "End want to get ack, but get lenght("+de.length+"):" + byte2HexStr(de.buf, de.length));
					return Events.NAK_ERROR;
				} else {
					Log.e("karl", "SendData And　WaitAck Callback get null");
					return Events.NAK_ERROR;
				}
			}
			synchronized (revDataQueueMng13) {
				if(de != null){
					if(de.buf[0]==0x06){
						return Events.SUCCESS;
					}else if(de.buf[0]==0x18){
						return Events.CAN_ERROR;
					}else if(de.buf[0]==0x15){
						return Events.NAK_ERROR;
					}
				}
			}
        }
		return Events.UNKNOWN_ERROR;
	}

	private int SendDataAndWaitForResponseCallback(byte[] cmd,int len, byte[] buf, byte[] callback){
		if(IsSdkAvailable()==false){
			Log.e("SendDataAndWaitForResponseCallback()","SDK不可用！！！dongle_sucess_init=" + dongle_sucess_init);
			if (dongle_sucess_init == 1) {
//			Log.e("karl", "sned to reset dongle");
//				Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(31, 0, 0, "", ""));
			}
				return Events.UNKNOWN_ERROR;
		}
		int loop=0;
		int recvcount = 2;
		int funcid = cmd[len-2];
		int nodeid = cmd[4];
		DeviceInfo nodeInfo = null;
		String byteNums = new String();
		DataElement de = null;
		for(loop=0;loop<15;loop++){
			mConditionVariable13.close();
			if(SendDataAndWaitAck_Callback(cmd,len) == Events.SUCCESS){
				break;
			}
			try {
				Thread.sleep(30);       //每延时50ms重发一次，问：是不是意味着每次发送数据的时候都
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(loop == 15){
			Log.e("SendDataAndWaitForResponseCallback()","发送数据失败！！！");
			return Events.UNKNOWN_ERROR;
		}

		Log.e("sendcmd", "IsDealTheData = " + IsDealTheData + ", funcid=" + funcid);
        if(true) {
			while (recvcount > 0) {	//need recv two message
				/*
				for(loop=0;loop<5*20;loop++){
					for(;;){
						de=revDataQueueMng13.popQueue();
						if( de == null || ((de.length > 4) && de.buf[3] == cmd[3])){
							break;
						}
					}
					if(de!=null) {
						break;
					}

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(loop==100 || de== null){
					Log.e("loop_value_callback","loop=" + loop);
					return Events.NONE_RESPONSE;
				}
				*/
				Log.e("karl", "start wait command 13 resp and callback");
				for (loop = 0; loop < 60; loop++) {
					mConditionVariable13.block(1000);
					de = revDataQueueMng13.popQueue();
					if (de == null) {
						mConditionVariable13.close();
						continue;
					} else if ((de.length > 4) && (de.buf[3] == cmd[3])) {
						break;
					} else {	//get not
						Log.e("karl", "wait command not resp and callback de.length=" + de.length);
						loop++;
					//	mConditionVariable13.close();
						continue;
					}
//					mConditionVariable13.close();
				}
				if ((de == null) || (de.length < 4)) {
					Log.e("kar", "wait command 13 resp callback timed out");
					return Events.NONE_RESPONSE;
				}

				if(de != null) {
					byteNums = byte2HexStr1(de.buf, de.length);
					if (de.length > 2) {
						Log.e("karl s<--", "before process(" + de.length+ ")(" + de.buf[de.length-2] + "):" +  byteNums);
					}
					Log.e("karl", "recvcount = " + recvcount);
					if (recvcount == 2) {
						if (de.length != 6) {
							recvcount++;
						} else {
							if (de.buf[4] == 0x00) {
								Log.e("karl", "tx command failed then retransfer ...");
								return Events.NONE_RESPONSE;
							}
						}
						for(loop=0;loop<de.length-2;loop++){
							buf[loop]=de.buf[loop+2];
						}
					} else {
						if (de.length > 2) {
							if ((de.length != 7)) {
								Log.e("karl", "wait for 7 length but get" + de.length);
								continue;
							}
							nodeInfo = Controller.this.GetNodeByNodeid(nodeid);
							if (nodeInfo !=null) {
								if (de.buf[5] != 0x00) {	//not online
									/*
									if (nodeInfo.isOnline != SdkConstants.DEVICE_NOTONLINE) {
										nodeInfo.offlinetimes++;
										if (nodeInfo.offlinetimes > 1) {
											nodeInfo.isOnline = SdkConstants.DEVICE_NOTONLINE;
											Controller.this.SendIfOnlineMsg(nodeInfo.nodeId, false);
											Log.e("karlonline", "node[" + nodeInfo.nodeId + "], send offline message to gateway");
										} else {
											Log.e("karlonline", "node[" + nodeInfo.nodeId + "], offlinetimes=" + nodeInfo.offlinetimes + ", need check again");
										}
									}
									*/
									Log.e("karlonline", "node[" + nodeInfo.nodeId + "] get callback not online");
									return Events.DEV_NOT_ONLINE;
								} else {					//on line
									/*
									if (nodeInfo.isOnline != SdkConstants.DEVICE_ONLINE) {
										nodeInfo.offlinetimes = 0;
										nodeInfo.isOnline = SdkConstants.DEVICE_ONLINE;
										Controller.this.SendIfOnlineMsg(nodeInfo.nodeId, true);
										Log.e("karlonline", "node[" + nodeInfo.nodeId + "], send online message to gateway");
									}
									nodeInfo.offlinetimes = 0;
									*/
								}
								//nodeInfo.revDataTime = System.currentTimeMillis();
								//Log.e("karlonline", "update(0x13) node[" + nodeInfo.nodeId + "] = " + nodeInfo.isOnline);
								Log.e("karlonline", "node[" + nodeInfo.nodeId + " get callback online");
								return Events.SUCCESS;
							} else {
								Log.e("karlonline", "get online check, but unkown device id" + nodeid);
							}
							if (de.buf[de.length-3] != funcid) {
								Log.e("karl", "get funcid=" + de.buf[de.length-2] + ", funcid=" + funcid);
								continue;
							}
							for(loop=0;loop<de.length-2;loop++){
								callback[loop]=de.buf[loop+2];
							}
						}
					}
				} else {
					Log.e("karl", "get de==null");
				}
				recvcount-=1;
			}
		}
		return Events.SUCCESS;
	}
	/*
     * 函数描述:
     *     发送指令,并且将接收到的响应的数据，将响应的数据存入到buf缓存中
     * 参数描述:
     *     cmd: in, 要发送的指令
     *     buf: out,将接收到的数据提取出来，存储到buf缓存中
     * 返回值描述:
     *     SUCCESS:操作成功，已得到版本信息
     *     NAK_ERROR:返回NAK error信息
     *     CAN_ERROR:返回CAN error信息
     *     NONE_RESPONSE:命令没有响应
     */
	private int SendDataAndWaitForResponse(byte[] cmd,byte[] buf){
		if(IsSdkAvailable()==false){
			Log.e("SendDataAndWaitForResponse()","SDK不可用！！！ dongle_sucess_init = " + dongle_sucess_init);
			if (dongle_sucess_init == 1) {
//				Log.e("karl", "sned to reset dongle");
//				Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(31, 0, 0, "", ""));
			}
			return Events.UNKNOWN_ERROR;
		}
		int loop=0;
		String byteNums = new String();
		DataElement de = null;
		for(loop=0;loop<15;loop++){
			if(SendDataAndWaitAck(cmd,cmd.length) == Events.SUCCESS){
				break;
			}
			try {
				Thread.sleep(50);       //每延时50ms重发一次，问：是不是意味着每次发送数据的时候都
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(loop == 15){
			Log.e("SendDataAndWaitForResponse()","发送数据失败！！！");
			return Events.UNKNOWN_ERROR;
		}

		Log.e("sendcmdresp", "IsDealTheData = " + IsDealTheData);
        if( IsDealTheData == false ){
        	IsDealTheData=true;
    		for(loop=0;loop<15;loop++){
    			for(;;){
    				de=revDataQueueMng.popQueue();
    				if( de == null || de.buf[3] == cmd[3] ){
    					break;
    				}
    			}
    			if(de!=null)
    				break;

    			try {
    				Thread.sleep(100);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		if(loop==15 || de== null){
    			Log.e("loop_value","loop=" + loop);
    			IsDealTheData=false;
    			return Events.NONE_RESPONSE;
    		}
			if(de != null){
				byteNums = byte2HexStr1(de.buf, de.length);
				Log.e("karl s<--", "before process:" +  byteNums);
				for(loop=0;loop<de.length-2;loop++){
					buf[loop]=de.buf[loop+2];
				}
			}
    	} else {
		}
        IsDealTheData=false;
		return Events.SUCCESS;
	}

	private int checkDataValid(byte[] buf,int len){
		int CheckSum=0xFF;
		Log.e("checkDataValid  len","len="+len);
		if(buf[0]==0x15){
			return Events.NAK_ERROR;
		}else if(buf[0]==0x18){
			return Events.CAN_ERROR;
		}else if(buf[0]==0x06){
			if(len>1){
				if(buf[1]==0x01){      //Check SOF
					if(buf[2]==len-3){        //Check length
					    for(int loop=0;loop<len-3;loop++){     //计算CheckSum
					        CheckSum=CheckSum^buf[loop+2];
					    }
					    if(CheckSum==buf[len-1]){              //对比CheckSum
					    	return Events.SUCCESS;
					    }
					}
				}
			}
		}else if(buf[0]==0x01){    //Check SOF
			if(buf[1]==len-2){     //Check length
			    for(int loop=0;loop<len-2;loop++){    //计算CheckSum
			    	CheckSum=CheckSum^buf[loop+1];
			    }
			    if(CheckSum==buf[len-1]){             //对比CheckSum
			    	return Events.SUCCESS;
			    }
			}
		}
		return Events.SUCCESS;
	}

	private int setControllerInfor(DeviceInfo node){
	    byte[] cmd = new byte[12];
	    cmd[0]=0x01;
	    cmd[1]=0x0a;
	    cmd[2]=0x00;
	    cmd[3]=0x03;
	    cmd[4]=0x01;
	    cmd[5]=0x02;
	    cmd[6]=0x01;
	    cmd[7]=0x03;
	    cmd[8]=0x21;
	    cmd[9]=0x20;
	    cmd[10]=(byte)0x86;
		cmd[11]=0x70;
		int ret = SendDataAndWaitAck(cmd,cmd.length);
		if( ret == Events.SUCCESS ){
		}
		return ret;
	}
	/*
     * 设置command class是否使能:flag --- true(使能),flag --- false(不使能)
     */
	private int SetCmdClassEnable(boolean flag){
		byte[] cmd = new byte[7];
		byte checkSum=(byte)0xFF;
		cmd[0] = 0x01;
		cmd[1] = 0x05;
		cmd[2] = 0x00;
		cmd[3] = (byte)0xea;
		cmd[4] = (byte)0xaa;
        if(flag == true){
		    cmd[5] = (byte)0xbb;
		    for(int loop=0;loop<5;loop++)
		    	checkSum=(byte)(checkSum^cmd[loop+1]);
		    cmd[6]=checkSum;
        }else if(flag == false){
		    cmd[5] = (byte)0x02;
		    for(int loop=0;loop<5;loop++)
		    	checkSum=(byte)(checkSum^cmd[loop+1]);
		    cmd[6]=checkSum;
        }
		int ret = SendDataAndWaitAck(cmd,cmd.length);
		return ret;
	}

	//发送在线离线状态至应用层
	private void SendIfOnlineMsg(int NodeId,boolean IsOnline){
		Message msg;
		Bundle b =new Bundle();
		if(IsOnline==true){
            this.sendBroadcastToGateway(this.encapsulateMessage(16, 17, NodeId, "", ""));
		}else if(IsOnline==false){
            this.sendBroadcastToGateway(this.encapsulateMessage(16, 18, NodeId, "", ""));
		}
	}

	/*
     * 函数描述
     *     检查睡眠类设备是否在线
     * 参数描述
     *     无
     * 返回值描述
     *     无
     */
	public long CheckSleepDeviceIfOnline(){
		long timewait = ONLINE_CHECK_WAITTIME;
		long currentwait = 0;
		int needcheck = 0;
		byte[] nodecheckid = new byte[256];
		int allcheckednum = 0;
		if(IsSdkAvailable()==false){
			Log.e("SendDataAndWaitAck()","SDK不可用！！！dongle success init = " + dongle_sucess_init);
			if (dongle_sucess_init == 1) {
//				Log.e("karl", "sned to reset dongle");
//				Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(31, 0, 0,"", ""));
			}
			return timewait;
		}
		Log.e("karlonline", "CheckSleepDeviceIfOnline start (" + System.currentTimeMillis() + ")--->");
		if(nodeList != null){
			synchronized(nodeList) {
				for(DeviceInfo node:nodeList) {
					needcheck = 0;
					if ((node.nodeId == 1) ||
							(((node.capability>>7) == 0x00) && ((node.security & 0x60) == 0)) || 
							((node.isOnline == SdkConstants.DEVICE_NOTONLINE))) {
						Log.e("karlonline", "nodeid["+ node.nodeId + "], node.capability=" + node.capability + ", node.security= " + node.security + ", node.isOnline =" + node.isOnline + ", no need to check online mode");
						continue;
					}
					{
						/*
						   java.util.Date RevDatetime=formatter.parse(node.revDataTime);
						   java.util.Date Now=formatter.parse(this.getCurrentTime());
						   */
						/*
						   long IntervalTime=System.currentTimeMillis() - node.revDataTime;
						   if(IntervalTime/1000 > 7500 && (node.capability>>7)==0x00){    //测试模式时间大于35毫秒,则判定设备睡眠类设备不在线
						   if(node.isOnline!=SdkConstants.DEVICE_NOTONLINE){
						   node.isOnline=SdkConstants.DEVICE_NOTONLINE;
						   this.SendIfOnlineMsg(node.nodeId,false);
						   }
						   }
						   */
						currentwait = System.currentTimeMillis() - node.revDataTime;	//time already wait
						currentwait = ONLINE_CHECK_WAITTIME - currentwait;				//this device next check time
						if (currentwait > 1000*10) {		//need wait next time
							if (currentwait < timewait) {
								timewait = currentwait;
							}
						} else {					//this device timed out				//need to check
							needcheck = 1;
							node.revDataTime = System.currentTimeMillis();
						}
						Log.e("karlonline", "check nodeid[" + node.nodeId + "], + revDataTime=" + node.revDataTime + ", offlinetimes=" + node.offlinetimes + ", currentwait=" + currentwait + ", needcheck = " + needcheck + ", now online state=" + node.isOnline);
						if (needcheck != 0) {
							//send nop command
							//CheckNonSleepDevIfOnline(node.nodeId);
							nodecheckid[allcheckednum++] = node.nodeId;	
						}
					}
				}
			}

			//here send all nop command
			for (int i = 0; i < allcheckednum; i++) {
				Log.e("karlonline", "CheckSleepDeviceIfOnline real send nop command,nodeid(" + nodecheckid[i]);
				CheckNonSleepDevIfOnline(nodecheckid[i]);
			}
		}
		Log.e("karlonline", "<---- CheckSleepDeviceIfOnline end");
		return timewait;
	}

	public void CheckNonSleepDevIfOnline(int nodeid){
		int checkSum=0xFF;
		byte[] cmd=new byte[10];
		cmd[0]=0x01;
		cmd[1]=0x08;
		cmd[2]=0x00;
		cmd[3]=0x13;
		cmd[4]=(byte)nodeid;
		cmd[5]=0x01;
		cmd[6]=0x00;
		cmd[7]=0x25;
		cmd[8]=seqNo();
		for(int loop=0;loop<8;loop++)
			checkSum=(byte)(checkSum^cmd[loop+1]);
		cmd[9]=(byte)checkSum;
		sndUartCmd(cmd,10);
	}

	public void SetDefaultRevtime(){
		synchronized(nodeList) {
			for(DeviceInfo node:nodeList){
				node.revDataTime= System.currentTimeMillis(); //System.currentTimeMillis(); //this.getCurrentTime();
			}
		}
	}

	/*
     * 函数描述:
     *     Copy the Node's current protocol information from the non-volatile
     * memory.
     * 参数描述:
     *     nodeInfo:out,将获取到的node信息存储在nodeInfo类中
     * 返回值描述:
     *     SUCCESS:操作成功，已得到版本信息
     *     NAK_ERROR:返回NAK error信息
     *     CAN_ERROR:返回CAN error信息
     *     NONE_RESPONSE:命令没有响应
     */
	private int GetNodeProtocolInfo(DeviceInfo nodeInfo){
		int loop=0;
		byte checkSum=(byte)0xFF;
		byte[] cmd=new byte[6];
		byte[] buf=new byte[256];
		int ret=0;
		cmd[0]=0x01;
		cmd[1]=0x04;
		cmd[2]=0x00;
		cmd[3]=0x41;
		cmd[4]=(byte)nodeInfo.nodeId;
	    for(loop=0;loop<4;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
	    cmd[5]=checkSum;
	    ret = SendDataAndWaitForResponse(cmd,buf);
	    Log.e("GetNodeProtocolInfo()",""+byte2HexStr(buf, 20));
	    if(ret== Events.SUCCESS){
    	    nodeInfo.capability=buf[2];
    	    nodeInfo.security=buf[3];
    	    nodeInfo.reserved=buf[4];
    	    nodeInfo.basic=buf[5];
    	    nodeInfo.generic=buf[6];
    	    nodeInfo.specific=buf[7];
	    }else{
	    	return ret;
	    }
	    return Events.SUCCESS;
	}

	/*
     * 函数描述
     *     加载controller里面的node list
     * 参数描述
     *
     * 返回之描述
     *     SUCCESS:操作成功，已得到controller里的node list
     *     NAK_ERROR:返回NAK error信息
     *     CAN_ERROR:返回CAN error信息
     */
	private int loadNode(){
		Log.e("usb dongle test","we are loading node now!!!");
		byte[] buf=new byte[256];
		byte[] cmd=new byte[5];
		byte[] bNode=new byte[29];
		DeviceInfo nodeInfo=null;
		int ret=0;
		int loop=0;
		int byNode=1;
		cmd[0]=0x01;
		cmd[1]=0x03;
		cmd[2]=0x00;
		cmd[3]=0x02;
		cmd[4]=(byte)0xFE;
		synchronized(nodeList) {
			nodeList.clear();
		}
	    ret=SendDataAndWaitForResponse(cmd,buf);
	    //Log.e("loadNode()---loadNode()---loadNode()",""+revDataQueueIndex);
		if(ret== Events.SUCCESS){
			for(loop=0;loop<29;loop++){
				bNode[loop]=buf[5+loop];
			}

		    for(loop=0;loop<29;loop++){
		    	if(bNode[loop]!=0){
		    		for(int j=0;j<8;j++){
		    			if((bNode[loop] & (1<<j))!=0){

		    				nodeInfo=new DeviceInfo();
		    				nodeInfo.nodeId=(byte)byNode;
		    				nodeInfo.isOnline= SdkConstants.UNKNOWN_STATE;
		    				if (GetNodeProtocolInfo(nodeInfo) == Events.SUCCESS) {
								nodeInfo.revDataTime= System.currentTimeMillis(); //this.getCurrentTime();
								nodeInfo.isOnline= SdkConstants.DEVICE_ONLINE;
								nodeInfo.offlinetimes = 0;
                                Controller.this.SendIfOnlineMsg(nodeInfo.nodeId, true);
								Log.e("karlonline", "send update node[" + nodeInfo.nodeId + "] = " + nodeInfo.isOnline);
							} else {
								nodeInfo.revDataTime= System.currentTimeMillis(); //this.getCurrentTime();
								nodeInfo.isOnline= SdkConstants.DEVICE_NOTONLINE;
								nodeInfo.offlinetimes = 2;
                                Controller.this.SendIfOnlineMsg(nodeInfo.nodeId, false);
								Log.e("karlonline", "send update node[" + nodeInfo.nodeId + "] = " + nodeInfo.isOnline);
							}
							nodeInfo.revDataTime = System.currentTimeMillis();
							synchronized(nodeList) {
								nodeList.add(nodeInfo);
							}
		    			}
		    			byNode++;
		    		}
		    	}else{
		    		byNode=byNode+8;
		    	}
		    }
		}else{
			return ret;
		}
		return Events.SUCCESS;
	}

	/*
     * 检查安全验证码
     */
	private boolean checkSecurityCode(){
		int ret = 0,loop = 0;
		int checkSum=0xFF;
		byte[] buf=new byte[256];
		byte[] cmd=new byte[15];
		cmd[0]=0x01;
		cmd[1]=0x0D;
		cmd[2]=0x00;
		cmd[3]=(byte)0xEB;
	    Time t=new Time();
	    t.setToNow();
	    int year=t.year;
	    cmd[4]=(byte)(year/100);
	    cmd[5]=(byte)(year%100);
	    cmd[6] = (byte)(t.month+1);
	    cmd[7] = (byte)t.monthDay;
	    cmd[8] = (byte)t.hour;
	    cmd[9] = (byte)t.minute;
	    cmd[10] = (byte)t.second;
	    cmd[11] = 0x35;
	    cmd[12] = 0x36;
	    cmd[13] = 0x37;
	    for(loop=0;loop<13;loop++)
	        checkSum=checkSum^cmd[loop+1];
	    cmd[14]=(byte)checkSum;
		ret=SendDataAndWaitForResponse(cmd,buf);
		if(ret== Events.SUCCESS){
			byte result=(byte)((cmd[4]^cmd[5]^cmd[6]^cmd[7]^cmd[8]^cmd[9]^cmd[10])+(cmd[11]^cmd[12]^cmd[13]));
			if(result == buf[2])
			    return true;
			else
				return false;
		}else{
			return false;
		}
	}


	/*
     * 初始化设备
     */
	public synchronized void initDevice()
	{
		//初始化发送接收命令队列
		int ret=0;
		byte[] nodecheckid = new byte[256];
		int allcheckednum = 0;

		for(int loop = 0; loop< SdkConstants.DongleInforLen; loop++){
            dev=up.getDev(SdkConstants.DongleInfor[loop][0], SdkConstants.DongleInfor[loop][1]);
            if(dev!=null)
            	break;
		}
		Log.d("karl", "-----usb init Device() called ----dev=" + dev);
		if( dev != null ){
			if(!(up.isHasPermission(dev))){
				Log.d("karl", "No permission held,then request...");
				up.getPermission(dev);
			}else{                                                 //已经获取了USB设备的访问权限，需将消息发送至主activity中去
				Log.d("karl", "permission held, then request ... + rt" + rt);
				if (rt == null) {
					rt=new RevThread();
					rt.start();

					sct=new sendCmdThread();
					mConditionVariable.close();
					sct.start();

					ret=loadNode();                                        //将node信息加载到动态数组中去
					if(ret!= Events.SUCCESS){
						closeDev(false);
						this.sendBroadcastToGateway(this.encapsulateMessage(0, 2, 0, "", ""));
						return;
					}

					if(nodeList!=null){
						synchronized(nodeList) {
							for(DeviceInfo item:nodeList){
								if( item.generic == ZW_classcmd.GENERIC_TYPE_STATIC_CONTROLLER ){
									//setControllerInfor(item);
									allcheckednum++;
								}
							}
						}
						for (int i=0; i < allcheckednum; i++) {
							setControllerInfor(null);
						}
					}
					ret = SetCmdClassEnable(true);
					if(ret!= Events.SUCCESS){
						closeDev(false);
						this.sendBroadcastToGateway(this.encapsulateMessage(0, 2, 0, "", ""));
						return;
					}
					drt=new DealRevDataThread();
					drt.start();
					IsOnlineThread = new CheckDevIfOnlineThread();
					IsOnlineThread.start();
					this.sendBroadcastToGateway(this.encapsulateMessage(0, 1, 0, "", ""));
				}
			}
		}
	}



   //空调控制器 关机模式
	public synchronized void powerForAirControl(int NodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x40;
		cmd[1] = 0x01;
		cmd[2] = 0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());

	}
	//空调控制器 加热模式
	public synchronized void hotForAirControl(int NodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x40;
		cmd[1] = 0x01;
		cmd[2] = 0x01;
		sndCmdClass(NodeId,cmd,length,seqNo());

	}
	//发送加密指令
	public synchronized void security(int nodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x62;
		cmd[1] = 0x01;
		cmd[2] = 0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}


	//空调控制器 制冷模式
	public synchronized  void windForAirControl(int NodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x40;
		cmd[1] = 0x01;
		cmd[2] = 0x02;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//空调控制器 自动模式
	public synchronized void autoForAirControl(int NodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x40;
		cmd[1] = 0x01;
		cmd[2] = 0x03;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//空调控制器 风扇开启
    public synchronized  void openSpeedForAirControl(int NodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x44;
		cmd[1] = 0x01;
		cmd[2] = 0x01;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//空调控制器 风扇自动

	public synchronized  void autoSpeedForAirControl(int NodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x44;
		cmd[1] = 0x01;
		cmd[2] = 0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}


	//加热温度设定
	public synchronized  void hotTemSet(int NodeId,int num){
		int length = 8;
		byte[] cmd = new byte[length];
		cmd[0] = 0x43;
		cmd[1] = 0x01;
		cmd[2] = 0x01;
		cmd[3] = 0x44;
		byte[] bytes = new byte[8];
		bytes = intToByte4(num);
		cmd[4] = bytes[0];
		cmd[5] = bytes[1];
		cmd[6] = bytes[2];
		cmd[7] = bytes[3];
		sndCmdClass(NodeId,cmd,length,seqNo());

	}

	//制冷温度设定
	public synchronized void windTemSet(int NodeId,int num){
		int length = 8;
		byte[] cmd = new byte[length];
		cmd[0] = 0x43;
		cmd[1] = 0x01;
		cmd[2] = 0x02;
		cmd[3] = 0x44;
		byte[] bytes = new byte[8];
		bytes = intToByte4(num);
		cmd[4] = bytes[0];
		cmd[5] = bytes[1];
		cmd[6] = bytes[2];
		cmd[7] = bytes[3];
		sndCmdClass(NodeId,cmd,length,seqNo());

	}

	//模式查询
    public synchronized  void modeSearch(int NodeId){
		int length = 2;
		byte[] cmd = new byte[length];
		cmd[0] = 0x40;
		cmd[1] = 0x02;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}
	//风扇查询
	public synchronized  void speedSearch(int NodeId){
		int length = 2;
		byte[] cmd = new byte[length];
		cmd[0] = 0x44;
		cmd[1] = 0x02;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//检测温度查询
	public synchronized  void queryTem(int NodeId){
        int length = 4;
		byte[] cmd = new byte[length];
		cmd[0] = 0x31;
		cmd[1] = 0x04;
		cmd[2] = 0x01;
		cmd[3] = 0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	public void setCommondForTem(int nodeId)
	{
		queryTem(nodeId);
	}

    public synchronized void queryTemForAirConditioner(int NodeId) {
		queryTem(NodeId);
	}
	//加热温度查询
	public synchronized  void queryHotTem(int NodeId){
       int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x43;
		cmd[1] = 0x02;
		cmd[2] = 0x01;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}
	public synchronized void clearDongle() {
        byte[] cmd = new byte[]{(byte)1, (byte)4, (byte)0, (byte)66, (byte)4, (byte)-67};
		up.sendByte(cmd, dev, cmd.length);
	}

	//制冷温度查询
	public synchronized void queryWindTem(int NodeId){
       int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x43;
		cmd[1] = 0x02;
		cmd[2] = 0x02;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//空调控制器学习功能
	public synchronized void studySet(int nodeId){
		int length = 6;
		byte[] cmd = new byte[length];
		cmd[0] = 0x70;
		cmd[1] = 0x04;
		cmd[2] = 0x1B;
		cmd[3] = 0x02;
		cmd[4] = 0x00;
		cmd[5] = 0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//空调控制器 学习配置功能
	public synchronized  void studyMake(int nodeId,int location){
		int length = 5;
		byte[] cmd = new byte[length];
		cmd[0] = 0x70;
		cmd[1] = 0x04;
		cmd[2] = 0x19;
		cmd[3] = 0x01;
		cmd[4] = (byte)location;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

    //学习位置
	public synchronized void studyLocation(int nodeId){
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x70;
		cmd[1] = 0x05;
		cmd[2] = 0x23;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//配置IR发射功率
	public synchronized  void deploy(int nodeId){
        int length = 5;
		byte[] cmd = new byte[length];
        cmd[0] = 0x70;
		cmd[1] = 0x04;
		cmd[2] = 0x1C;
		cmd[3] = 0x01;
		cmd[4] = (byte)0xFF;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//内置IR控制
	public synchronized  void IRControl(int nodeId){
	    int length = 5;
		byte[] cmd = new byte[length];
		cmd[0] = 0x70;
		cmd[1] = 0x04;
		cmd[2] = 0x20;
		cmd[3] = 0x01;
		cmd[4] = (byte)0xFF;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//摆动控制
	public synchronized void swayControl(int nodeId){
		int length = 5;
		byte[] cmd = new byte[length];
		cmd[0] = 0x70;
		cmd[1] = 0x04;
		cmd[2] = 0x21;
		cmd[3] = 0x01;
		cmd[4] = 0x01;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//温度补偿
	public synchronized  void temCompensate(int nodeId){
		int length = 5;
		byte[] cmd = new byte[length];
		cmd[0] = 0x70;
		cmd[1] = 0x04;
		cmd[2] = 0x25;
		cmd[3] = 0x01;
		cmd[4] = 0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}


	/*
	 1.设置灯开关及百分比
	 */
	public synchronized void setterForLight(int NodeId,int num){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x26;
		cmd[1]=0x01;
		cmd[2]=(byte)num;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}
	public synchronized void setterForLight1(int NodeId){
		int length=5;
		byte[] cmd=new byte[length];
		cmd[0]=0x33;
		cmd[1]=0x06;
		cmd[2]=0x40;
		cmd[3]=0x00;
		cmd[4]=0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}
	/*
	2.设置灯颜色
	 */
	public synchronized void setColorForLight(int NodeId,int status1,int status2,int status3){
		int length=9;
		byte[] cmd=new byte[length];
		cmd[0]=0x33;
		cmd[1]=0x05;
		cmd[2]=0x03;
		cmd[3]=0x02;
		cmd[4]=(byte)status1;
		cmd[5]=0x03;
		cmd[6]=(byte)status2;
		cmd[7]=0x04;
		cmd[8]=(byte)status3;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}


	/*
    优化网络
     */
	public synchronized void OptimizeNetWork(){
		byte[] cmd = new byte[7];
		byte[] buf=new byte[256];
		cmd[0]=0x01;
		cmd[1]=0x05;
		cmd[2]=0x00;
		cmd[3]=0x48;
		cmd[4]=0x01;
		cmd[5]=seqNo();
		cmd[6]=(byte)(seqNo()+3);
		up.sendByte(cmd, dev, cmd.length);

	}

	public void resetMethod(){
		byte[] cmd = new byte[6];
		byte[] buf=new byte[256];
		cmd[0]=0x01;
		cmd[1]=0x04;
		cmd[2]=0x00;
		cmd[3]=0x42;
		cmd[4]=0x04;
		cmd[5]=(byte)0xbd;
		up.sendByte(cmd, dev, cmd.length);
		resetKey();
	}
	/*
 * 开始inclusion模式
 */
	public void startInclusion(){
		//IsFinished = false;
		//IsFinished1= false;
		int loop = 0;
		byte[] buf = new byte[256];
		byte checkSum = (byte)0xFF;
		byte[] cmd = new byte[7];
		cmd[0]=0x01;
		cmd[1]=0x05;
		cmd[2]=0x00;
		cmd[3]=0x4A;
		cmd[4]=(byte)0x81;
		cmd[5]=seqNo();
	    for(loop=0;loop<5;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		cmd[6]=checkSum;

		int ret=SendDataAndWaitAck(cmd,7);
		Log.e("start inclusion data......",""+byte2HexStr(buf,10));

		if(ret== Events.SUCCESS){
			/*
			if(buf[3]==0x01){
				this.sendBroadcastToGateway(this.encapsulateMessage(6, 7, 0, "", ""));
				this.mtimeHandler.removeMessages(1);
				this.mtimeHandler1.removeMessages(2);
				this.INCLUDEMARK = 1;
				this.resetTime();
			}
			*/
		}else{
			return;
		}
	}

	/*
     * 停止inclusion模式
     */
	public void stopInclusion(){
		Log.e("stopInclusion function","正在停止inclusion mode........");
		int loop = 0;
		byte[] buf = new byte[256];
		byte checkSum = (byte)0xFF;
		byte[] cmd = new byte[7];
		cmd[0]=0x01;
		cmd[1]=0x05;
		cmd[2]=0x00;
		cmd[3]=0x4A;
		cmd[4]=0x05;
		cmd[5]=seqNo();
	    for(loop=0;loop<5;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		cmd[6]=checkSum;
		for(loop=0;loop<256;loop++)
			buf[loop]=0x00;

		int ret=SendDataAndWaitForResponse(cmd,buf);

	    Log.e("stopInclusion function",""+byte2HexStr(buf,20));
		if(ret== Events.SUCCESS){
			if(buf[3]==0x06){
				this.sendBroadcastToGateway(this.encapsulateMessage(6, 6, 0, "", ""));
                this.INCLUDEMARK = 0;
                this.mtimeHandler.removeMessages(1);
                this.mtimeHandler1.removeMessages(2);
			}
		}else{
			return;
		}
	}

	public void stopInclusionInternal(){
		Log.e("karl","stopInclusion internal");
		int loop = 0;
		byte checkSum = (byte)0xFF;
		byte[] cmd = new byte[7];
		cmd[0]=0x01;
		cmd[1]=0x05;
		cmd[2]=0x00;
		cmd[3]=0x4A;
		cmd[4]=0x05;
		cmd[5]=seqNo();
	    for(loop=0;loop<5;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		cmd[6]=checkSum;
		/*
		int ret=SendDataAndWaitAck(cmd,buf);

	    Log.e("stopInclusion function",""+byte2HexStr(buf,20));
		if(ret== Events.SUCCESS){
			if(buf[3]==0x06){
				this.sendBroadcastToGateway(this.encapsulateMessage(6, 6, 0, "", ""));
                this.INCLUDEMARK = 0;
                this.mtimeHandler.removeMessages(1);
                this.mtimeHandler1.removeMessages(2);
			}
		}else{
			return;
		}
		*/
		sndUartCmd(cmd, 7);
	}

	//开始exclusion模式
	public int startExclusion(){
		int loop = 0;
		byte checkSum = (byte)0xFF;
		byte[] buf=new byte[256];
		byte[] cmd=new byte[7];
		cmd[0] = 0x01;
		cmd[1] = 0x05;
		cmd[2] = 0x00;
		cmd[3] = 0x4b;
		cmd[4] = (byte)0x81;
		cmd[5] = seqNo();
	    for(loop=0;loop<5;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
	    cmd[6] = checkSum;

	 	int ret = SendDataAndWaitAck(cmd,7);
	 	if(ret == Events.SUCCESS){
			/*
	 	    if(buf[3] == 0x01){
                this.sendBroadcastToGateway(this.encapsulateMessage(10, 11, 0, "", ""));
                this.mtimeHandler.removeMessages(1);
                this.mtimeHandler1.removeMessages(2);
                this.EXECUTEMARK = 1;
                this.resetTime1();
	 	    }
			*/
	 	    return Events.SUCCESS;
	 	}else{
	 		return Events.UNKNOWN_ERROR;
	 	}
	}
	public synchronized void setBinaryOrder(int nodeId,int state){
		int length=3;
		byte[] cmd = new byte[length];
		cmd[0]=0x25;
		cmd[1]=0x01;
		cmd[2] = (byte)state;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	public int stopExclusionInternal(){
		int loop=0;
		byte checkSum = (byte)0xFF;
		byte[] cmd=new byte[7];
		Log.e("stopexclusion function debug","正在停止exclusion mode.");
		cmd[0]=0x01;
		cmd[1]=0x05;
		cmd[2]=0x00;
		cmd[3]=0x4b;
		cmd[4]=0x05;
		cmd[5]=seqNo();
		for(loop=0;loop<5;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		cmd[6]=checkSum;
		sndUartCmd(cmd, 7);
		/*
		int ret=SendDataAndWaitForResponse(cmd,buf);
		if(ret == Events.SUCCESS){
			if(buf[3]==0x06){
				cmd[3]=0x4a;
				cmd[5]=0x00;
				checkSum=(byte)0xFF;
				for(loop=0;loop<5;loop++)
			    	checkSum=(byte) (checkSum^cmd[loop+1]);
				cmd[6]=checkSum;
				Log.e("stopexclusion function debug",""+byte2HexStr(buf,20));
				ret=SendDataAndWaitAck(cmd,cmd.length);
				if(ret== Events.SUCCESS){
					this.sendBroadcastToGateway(this.encapsulateMessage(10, 12, 0, "", ""));
                this.mtimeHandler.removeMessages(1);
                this.mtimeHandler1.removeMessages(2);
                this.EXECUTEMARK = 0;
				}else{
					return Events.UNKNOWN_ERROR;
				}
			}
		}else{
			return Events.UNKNOWN_ERROR;
		}
		*/
		return Events.SUCCESS;
	}
	public int stopExclusion(){
		int loop=0;
		byte checkSum = (byte)0xFF;
		byte[] buf=new byte[256];
		byte[] cmd=new byte[7];
		Log.e("stopexclusion function debug","正在停止exclusion mode.");
		cmd[0]=0x01;
		cmd[1]=0x05;
		cmd[2]=0x00;
		cmd[3]=0x4b;
		cmd[4]=0x05;
		cmd[5]=seqNo();
		for(loop=0;loop<5;loop++)
	    	checkSum=(byte) (checkSum^cmd[loop+1]);
		cmd[6]=checkSum;
		int ret=SendDataAndWaitForResponse(cmd,buf);
		if(ret == Events.SUCCESS){
			if(buf[3]==0x06){
				cmd[3]=0x4a;
				cmd[5]=0x00;
				checkSum=(byte)0xFF;
				for(loop=0;loop<5;loop++)
			    	checkSum=(byte) (checkSum^cmd[loop+1]);
				cmd[6]=checkSum;
				Log.e("stopexclusion function debug",""+byte2HexStr(buf,20));
				ret=SendDataAndWaitAck(cmd,cmd.length);
				if(ret== Events.SUCCESS){
					this.sendBroadcastToGateway(this.encapsulateMessage(10, 12, 0, "", ""));
                this.mtimeHandler.removeMessages(1);
                this.mtimeHandler1.removeMessages(2);
                this.EXECUTEMARK = 0;
				}else{
					return Events.UNKNOWN_ERROR;
				}
			}
		}else{
			return Events.UNKNOWN_ERROR;
		}
		return Events.SUCCESS;
	}

	public synchronized DeviceInfo GetNodeByNodeid(int NodeId){
		if(nodeList != null){
			synchronized(nodeList) {
				for(DeviceInfo item:nodeList){
					if((item.nodeId&0xFF)==(NodeId&0xFF)){
						return item;
					}
				}
			}
		}
		return null;
	}

	//SDK方法是否可用.
	private boolean IsSdkAvailable(){
		Log.d("karl", "SDK: up = " + up + ", sct=" + sct + ", rt = " + rt + ", dev= " + dev);
		if(up==null || sct==null || rt==null || dev==null){
			return false;
		}

		Log.d("karl", "SDKrt.isAlive = " + rt.isAlive() + ", sct.isAlive = " + sct.isAlive());
		if(rt.isAlive()!=true || sct.isAlive()!=true){
			return false;
		}
		return true;
	}


	/*
     * 关闭设备
     */
	public synchronized void closeDev(boolean isUnregisterReceiver)
	{
		Log.d("karl", "-----------------------------closeDev-----------------------------");
		if(up!=null){
			SetCmdClassEnable(false);
		}
		if(IsOnlineThread!=null){
			System.out.println("Controller closeDev IsOnlineThread id = " + IsOnlineThread.getId());
			IsOnlineThread.StopThead();
			IsOnlineThread=null;
		}

		if(sct != null){
			System.out.println("Controller closeDev sct id = " + sct.getId());
		    sct.stopThread();
		    sct=null;
		}

			if(rt != null){
				System.out.println("Controller closeDev rt id = " + rt.getId());
				//isOnlineQueueMng.clearQueue();
				rt.stopThread();
				rt = null;
			}

		if(drt != null){
			System.out.println("Controller closeDev drt id = " + drt.getId());
			drt.stopThread();
			drt = null;
		}

		if(up!=null){
			Log.d("karl", "-----------up close called\n");
//			up.close(isUnregisterReceiver);
			revDataQueueMng.clearQueue();
			revDataQueueMng13.clearQueue();
			revDataQueueMngack.clearQueue();
			cmdQueueMng.clearQueue();
		}
	}

	//0 -- 累积功率,1 -- 瞬时功率
	public synchronized int GetPowerMeter(int nodeId,int type){
		int loop=0;
		byte checkSum = (byte)0xFF;
		byte[] cmd = new byte[12];
		cmd[0]=0x01;
		cmd[1]=0x0a;
		cmd[2]=0x00;
		cmd[3]=0x13;
		cmd[4]=(byte)nodeId;
		cmd[5]=0x03;
		cmd[6]=0x32;
		cmd[7]=0x01;
		if(type == 0)
		    cmd[8]=0x00;
		else if(type == 1)
			cmd[8]=0x10;
		cmd[9]=0x05;
		cmd[10]=seqNo();
		for(loop = 0;loop<10 ; loop++)
			checkSum=(byte)(checkSum^cmd[loop+1]);
		cmd[11]=checkSum;
		int ret = Events.SUCCESS;
		for(loop=0;loop<3;loop++){
		    ret= SendDataAndWaitAck(cmd,12);
		    if( ret == Events.SUCCESS )
		    	break;
		}
		return ret;
	}


	//status:1 ---- On , 0 ---- Off
	public synchronized int setOnOff(int nodeId,int status){

		int ret=0;
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x20;
		cmd[1]=0x01;
		cmd[2]=(byte)status;
		sndCmdClass(nodeId,cmd,length,seqNo());
		return ret;
	}

	//控制背景音乐 存储 的音乐
	//01 0e 00 13 1a 07 60 0d 00 02 20 01 ff 25 05 6e
	public synchronized int SetMutilSwitchOnOff1(int NodeId,int Endpoint,int type,int State){
		int ret=0;
		int length=9;
		byte[] cmd=new byte[length];
		cmd[0]=0x60;
		cmd[1]=0x0d;
		cmd[2]=0x00;
		cmd[3]=(byte)Endpoint;
		cmd[4]=0x20;
		cmd[5]=0x01;
		cmd[6]=(byte)type;
		cmd[7]=0x00;
		cmd[8]=(byte)State;
		sndCmdClass(NodeId,cmd,length,seqNo());
		return ret;
	}


	//控制多路开关面板
	//01 0e 00 13 1a 07 60 0d 00 02 20 01 ff 25 05 6e
	public synchronized int SetMutilSwitchOnOff(int NodeId,int Endpoint,int State){
		int ret=0;
		int length=7;
		byte[] cmd=new byte[length];
		cmd[0]=0x60;
		cmd[1]=0x0d;
		cmd[2]=0x00;
		cmd[3]=(byte)Endpoint;
		cmd[4]=0x20;
		cmd[5]=0x01;
		cmd[6]=(byte)State;
		sndCmdClass(NodeId,cmd,length,seqNo());
		return ret;
	}

	//控制电机设备或者调光设备完全打开
	public synchronized int CtrlDevOpen(int NodeId){
		return CtrlDevByPercentage(NodeId,99);
	}

	//控制调光设备或者电机设备完全关闭
	public synchronized int CtrlDevClose(int NodeId){
		return CtrlDevByPercentage(NodeId,0);
	}

	//通过百分比来控制电机设备

	/**
	 * 窗帘 控制 数据组装方法
	 * @param NodeId
	 * @param Percentage
	 * @return
	 */
	public synchronized int CtrlDevByPercentage(int NodeId,int Percentage){
		int index=0;
		int length = 4;
		byte checkSum=(byte)0xFF;
        int ret=0;

		byte[] cmd=new byte[length];
		cmd[index++]=0x26;
		cmd[index++]=0x01;
		if(Percentage < 99 && Percentage > 0){
			cmd[index++]=(byte)Percentage;
		}else if(Percentage > 99 || Percentage==99){
			cmd[index++]=(byte)0x63;
		}else{
			cmd[index++]=0x00;
		}

		cmd[index++]=0x00;

		sndCmdClass(NodeId, cmd, length, seqNo());
    	return ret;
	}

	//电机停止移动
	public synchronized void StopDevChanging(int NodeId){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]=0x26;
		cmd[1]=0x05;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

    public void executeCommond(int nodeId, int value, int num) {
        this.executeCommondMethod(nodeId, value, num);
    }

    private void executeCommondMethod(int nodeId, int value, int num) {
        if(value == 0) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 255) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 1) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 2) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 3) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 4) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 5) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 6) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 7) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 8) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 9) {
            this.setCommondClass(nodeId, 1, value);
        } else if(value == 10) {
            this.setCommondClass(nodeId, 3, num);
        } else if(value == 11) {
            this.setControlBackground(nodeId, 2, 1, num);
        } else if(value == 12) {
            this.setControlBackground(nodeId, 2, 2, num);
        } else if(value == 13) {
            this.setControlBackground(nodeId, 2, 3, num);
        }

    }

    public synchronized int setControlBackground(int no, int lu, int type, int State) {
        byte ret = 0;
        byte length = 9;
        byte[] cmd = new byte[length];
        cmd[0] = 96;
        cmd[1] = 13;
        cmd[2] = 0;
        cmd[3] = (byte)lu;
        cmd[4] = 32;
        cmd[5] = 1;
        cmd[6] = (byte)type;
        cmd[7] = 0;
        cmd[8] = (byte)State;
        this.sndCmdClass(no, cmd, length, this.seqNo());
        return ret;
    }

	//获取电机设备的移动百分比
	public synchronized void GetDevPercentage(int NodeId){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]=0x26;
		cmd[1]=0x02;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//得到Everyoo开关面板的开关状态
	public synchronized void GetEveryooSwitchState(int NodeId){
		int length=2;
        byte[] cmd = new byte[length];
		cmd[0]=0x20;
		cmd[1]=0x02;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}


	//获取MANUFACTURER信息
	//01 09 00 13 08 02 72 04 25 40 fc
	public synchronized void GetManufactureInfor(int NodeId,int SeqNum){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]=0x72;
		cmd[1]=0x04;
		sndCmdClass(NodeId,cmd,length,(byte)SeqNum);
	}
    //获取PM2.5的值
	public synchronized void getPM(int nodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=0x31;
		cmd[1]=0x04;
		cmd[2]=0x23;
		cmd[3]=0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
    public void setCommondForPM(int nodeId) {
		getPM(nodeId);
	}

	//获取插座额定功率
	public synchronized void GetSsing(int NodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=0x32;
		cmd[1]=0x01;
		cmd[2]=0x40;
		cmd[3]=0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//获取插座瞬时功率
	public synchronized void GetEding(int NodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=0x32;
		cmd[1]=0x01;
		cmd[2]=0x50;
		cmd[3]=0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//主动获取CO2的值
	public synchronized  void getCO2(int NodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=0x31;
		cmd[1]=0x04;
		cmd[2]=0x11;
		cmd[3]=0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

    public void setCommondForCO2(int nodeId) {
		getCO2(nodeId);
	}


	//主动获取温度的值

	public synchronized  void getTemperature(int NodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=0x31;
		cmd[1]=0x04;
		cmd[2]=0x01;
		cmd[3]=0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}
	//下发 nonce report指令
	public synchronized  void sendReport(int nodeId){
		byte[] randomBytes = getRandomChar(8);
		sendNonceReport(nodeId,randomBytes);
	}


	//NonceREPORT nonce主动发命令上报
	public synchronized  void sendNonceReport(int NodeId,byte[] nonce){
		int length=10;
		byte[] cmd=new byte[length];
		cmd[0]=(byte)0x98;
		cmd[1]=(byte)0x80;
		cmd[2]=nonce[0];
		cmd[3]=nonce[1];
		cmd[4] =nonce[2];
		cmd[5] =nonce[3];
		cmd[6] =nonce[4];
		cmd[7]=nonce[5];
		cmd[8]=nonce[6];
		cmd[9]=nonce[7];
		sndCmdClass(NodeId,cmd,length,seqNo());
	}

	//加密主题
	public synchronized  void sheme(int nodeId){
		int length = 3;
		byte[] cmd=new byte[length];
		cmd[0]=(byte)0x98;
		cmd[1]=(byte)0x04;
		cmd[2]=(byte)0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//主动获取湿度的值
	public synchronized  void getHumidity(int NodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=0x31;
		cmd[1]=0x04;
		cmd[2]=0x05;
		cmd[3]=0x00;
		sndCmdClass(NodeId,cmd,length,seqNo());
	}
	public void setCommondForHum(int nodeId) {
		getHumidity(nodeId);
	}
	//设置key
	public synchronized  void sendKey(int nodeId){
		byte[] randomBytes = getRandomChar(8);
		byte[]  IV = new byte[16];
		for(int i=0;i<16;i++){
			if(i<8){
				IV[i] = randomBytes[i];}
			else{
				IV[i] = getNonce[i-8];}
		}
		byte[] ke =  new byte[] { (byte)0x85,(byte)0x22,(byte)0x71,(byte)0x7D,(byte)0x3A,(byte)0xD1,(byte)0xFB,(byte)0xFE,(byte)0xAF,(byte)0xA1,(byte)0xCE,(byte)0xAA,(byte)0xFD,(byte)0xF5,(byte)0x65,(byte)0x65
		};
		byte[] text = new byte[] {
				(byte)0x00,(byte)0x98,(byte)0x06,(byte)0x44,(byte)0x45,(byte)0x8B,(byte)0x2A,(byte)0x2B,(byte)0x51,(byte)0xFF,(byte)0x4F,(byte)0x3E,(byte)0x80,(byte)0x4B,(byte)0x32,(byte)0xF9,(byte)0x64,(byte)0xA9,(byte)0xF4
		};
		try {
			byte[] encData = encrypt(ke,ktest,IV);
			byte[] ka = new byte[] {(byte)0x9A ,(byte)0xDA,(byte)0xE0,(byte)0x54,(byte)0xF6,(byte)0x3D,(byte)0xFA,(byte)0xFF,(byte)0x5E,(byte)0xA1,(byte)0x8E,(byte)0x45,(byte)0xED,(byte)0xF6,(byte)0xEA,(byte)0x6F };
			byte[] ecbRsult = EcbEncrypt(ka,IV);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ecbRsult.length; i++) {
				String hex = Integer.toHexString(ecbRsult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append("--"+hex.toUpperCase());
			}
			//组装验证数据
			byte[] mac = new byte[23];
			mac[0] = (byte)0x81;
			mac[1] = (byte)0x01;
			mac[2] = (byte)nodeId;
			mac[3] = (byte)0x13;
			for(int i=0;i<19;i++){
				mac[4+i] = encData[i];
			}
			StringBuffer sb1 = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				String hex = Integer.toHexString(mac[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb1.append("--"+hex.toUpperCase());
			}
			byte[] bb= new byte[16];
			for(int i= 0;i<16;i++){
				bb[i] = mac[i];
			}
			StringBuffer sb2 = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				String hex = Integer.toHexString(bb[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb2.append("--"+hex.toUpperCase());
			}
			byte[] xorResult = xor(ecbRsult,bb);
			StringBuffer sb3 = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				String hex = Integer.toHexString(xorResult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb3.append("--"+hex.toUpperCase());
			}
			byte[] result = EcbEncrypt(ka,xorResult);
			StringBuffer sb4 = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				String hex = Integer.toHexString(result[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb4.append("--"+hex.toUpperCase());
			}
			byte[] cc= new byte[16];
			for(int i= 0;i<16;i++){
				if(i+16<mac.length){
					cc[i] = mac[i+16];}
				else{
					cc[i] = 0x00;
				}
			}
			StringBuffer sb5 = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				String hex = Integer.toHexString(cc[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb5.append("--"+hex.toUpperCase());
			}
			byte[] xorResult1 = xor(result,cc);
			StringBuffer sb6 = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				String hex = Integer.toHexString(xorResult1[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb6.append("--"+hex.toUpperCase());
			}
			byte[] result1 = EcbEncrypt(ka,xorResult1);
			StringBuffer sb7 = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				String hex = Integer.toHexString(result1[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb7.append("--"+hex.toUpperCase());
			}
			byte[] b = new byte[38];
			byte[] mark = new byte[]{(byte)0x98,(byte)0x81};
			for(int i=0;i<39;i++){
				if(i>=0&&i<2){
					b[i] =mark[i];
				}
				if(i>=2 &&i<10){
					b[i]=randomBytes[i-2];
				}else if(i>=10&&i<29){
					b[i]=encData[i-10];
				}else if(i==29){
					b[i] = getNonce[0];
					System.out.print(b[i]);
				}else if(i>29&&i<38){
					b[i] = result1[i-30];
				}
			}
			sendOrder(nodeId,b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IsFinished = true;
	}

    //assosiation
	public synchronized  void sendssosiation(int nodeId){
		byte[] randomBytes = getRandomChar(8);
		byte[]  IV = new byte[16];
		for(int i=0;i<16;i++){
			if(i<8){
				IV[i] = randomBytes[i];}
			else{
				IV[i] = getNonce[i-8];}
		}
		byte[] ke =  new byte[] { (byte)0xAE,(byte)0x82,(byte)0x8C,(byte)0x58,(byte)0x3C,(byte)0xC1,(byte)0x0A,(byte)0x1A,(byte)0x4B,(byte)0x45,(byte)0x46,(byte)0xBE,(byte)0x7F,(byte)0xBF,(byte)0xA8,(byte)0xAA

		};
		byte[] text = new byte[] {
				(byte)0x00,(byte)0x85,(byte)0x01,(byte)0x01,(byte)0x01
		};


		try {
			byte[] encData = encrypt(kepublic,text,IV);
			byte[]  ka = new byte[]{(byte)0x40,(byte)0xE2,(byte)0x18,(byte)0x5A,(byte)0xF3,(byte)0x0F,(byte)0x37,(byte)0x5B,(byte)0x91,(byte)0x34,(byte)0xA4,(byte)0x15,(byte)0x95,(byte)0x91,(byte)0x5F,(byte)0x0E
			};

			byte[] ecbRsult = EcbEncrypt(kapublic,IV);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ecbRsult.length; i++) {
				String hex = Integer.toHexString(ecbRsult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append("--"+hex.toUpperCase());
			}
			//组装验证数据
			byte[] mac = new byte[16];
			mac[0] = (byte)0x81;
			mac[1] = (byte)0x01;
			mac[2] = (byte)nodeId;
			mac[3] = (byte)0x05;
			for(int i=0;i<12;i++){
				if(i>=0&&i<5){
					mac[4+i] = encData[i];}
				else if(i>=5&&i<12){
					mac[4+i]=0x00;
				}
			}
			StringBuffer sb1 = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				String hex = Integer.toHexString(mac[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb1.append("--"+hex.toUpperCase());
			}

			byte[] xorResult = xor(ecbRsult,mac);

			byte[] result = EcbEncrypt(kapublic,xorResult);
			byte[] b = new byte[24];
			byte[] mark = new byte[]{(byte)0x98,(byte)0x81};
			for(int i=0;i<24;i++){
				if(i>=0&&i<2){
					b[i] =mark[i];
				}
				if(i>=2 &&i<10){
					b[i]=randomBytes[i-2];
				}else if(i>=10&&i<15){
					b[i]=encData[i-10];
				}else if(i==15){
					b[i] = getNonce[0];
				}else if(i>15&&i<24){
					b[i] = result[i-16];
				}
			}
			StringBuffer sb3 = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb3.append("--"+hex.toUpperCase());
			}
			sendOrder(nodeId,b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IsFinished1 = true;

	}

	//下发控制

	public synchronized  void sendOrder(int nodeId){
		byte[] randomBytes = getRandomChar(8);
		byte[]  IV = new byte[16];
		for(int i=0;i<16;i++){
			if(i<8){
				IV[i] = randomBytes[i];}
			else{
				IV[i] = getNonce[i-8];}
		}
		byte[] ke =  new byte[] { (byte)0xAE,(byte)0x82,(byte)0x8C,(byte)0x58,(byte)0x3C,(byte)0xC1,(byte)0x0A,(byte)0x1A,(byte)0x4B,(byte)0x45,(byte)0x46,(byte)0xBE,(byte)0x7F,(byte)0xBF,(byte)0xA8,(byte)0xAA

		};
		byte[] text = new byte[] {
				(byte)0x00,(byte)0x62,(byte)0x01,(byte)0x00
		};
        if(this.doorValue == 1) {
            text = new byte[]{(byte)0, (byte)98, (byte)1, (byte)1};
        } else if(this.doorValue == -1) {
            text = new byte[]{(byte)0, (byte)98, (byte)1, (byte)-1};
        }


		try {
			byte[] encData = encrypt(kepublic,text,IV);
			byte[]  ka = new byte[]{(byte)0x40,(byte)0xE2,(byte)0x18,(byte)0x5A,(byte)0xF3,(byte)0x0F,(byte)0x37,(byte)0x5B,(byte)0x91,(byte)0x34,(byte)0xA4,(byte)0x15,(byte)0x95,(byte)0x91,(byte)0x5F,(byte)0x0E
			};
			byte[] ecbRsult = EcbEncrypt(kapublic,IV);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ecbRsult.length; i++) {
				String hex = Integer.toHexString(ecbRsult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append("--"+hex.toUpperCase());
			}
			byte[] mac = new byte[16];
			mac[0] = (byte)0x81;
			mac[1] = (byte)0x01;
			mac[2] = (byte)nodeId;
			mac[3] = (byte)0x04;
			for(int i=0;i<12;i++){
				if(i>=0&&i<4){
				mac[4+i] = encData[i];}
				else if(i>=4&&i<12){
				mac[4+i]=0x00;
				}
			}
			StringBuffer sb1 = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				String hex = Integer.toHexString(mac[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb1.append("--"+hex.toUpperCase());
			}

			byte[] xorResult = xor(ecbRsult,mac);

			byte[] result = EcbEncrypt(kapublic,xorResult);
			byte[] b = new byte[23];
			byte[] mark = new byte[]{(byte)0x98,(byte)0x81};
			for(int i=0;i<23;i++){
				if(i>=0&&i<2){
					b[i] =mark[i];
				}
				if(i>=2 &&i<10){
					b[i]=randomBytes[i-2];
				}else if(i>=10&&i<14){
					b[i]=encData[i-10];
				}else if(i==14){
					b[i] = getNonce[0];
				}else if(i>14&&i<23){
					b[i] = result[i-15];
				}
			}
								StringBuffer sb3 = new StringBuffer();
								for (int i = 0; i < b.length; i++) {
									String hex = Integer.toHexString(b[i] & 0xFF);
									if (hex.length() == 1) {
										hex = '0' + hex;
									}
									sb3.append("--"+hex.toUpperCase());
								}
			sendOrder(nodeId,b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mark = 0;
	}

	public synchronized  void sendOrder1(int nodeId){
		byte[] randomBytes = getRandomChar(8);
		byte[]  IV = new byte[16];
		for(int i=0;i<16;i++){
			if(i<8){
				IV[i] = randomBytes[i];}
			else{
				IV[i] = getNonce[i-8];}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < IV.length; i++) {
			String hex = Integer.toHexString(IV[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append("--"+hex.toUpperCase());
		}
		byte[] ke =  new byte[] { (byte)0xAE,(byte)0x82,(byte)0x8C,(byte)0x58,(byte)0x3C,(byte)0xC1,(byte)0x0A,(byte)0x1A,(byte)0x4B,(byte)0x45,(byte)0x46,(byte)0xBE,(byte)0x7F,(byte)0xBF,(byte)0xA8,(byte)0xAA};
		byte[] text = new byte[] {(byte)0x00,(byte)0x98,(byte)0x02};
		try {
			byte[] encData = encrypt(kepublic,text,IV);
			StringBuffer sb22 = new StringBuffer();
			for (int i = 0; i < encData.length; i++) {
				String hex = Integer.toHexString(encData[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb22.append("--"+hex.toUpperCase());
			}
			byte[]  ka = new byte[]{(byte)0x40,(byte)0xE2,(byte)0x18,(byte)0x5A,(byte)0xF3,(byte)0x0F,(byte)0x37,(byte)0x5B,(byte)0x91,(byte)0x34,(byte)0xA4,(byte)0x15,(byte)0x95,(byte)0x91,(byte)0x5F,(byte)0x0E
			};
			byte[] ecbRsult = EcbEncrypt(kapublic,IV);
			StringBuffer sbv = new StringBuffer();
			for (int i = 0; i < ecbRsult.length; i++) {
				String hex = Integer.toHexString(ecbRsult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sbv.append("--"+hex.toUpperCase());
			}
			byte[] mac = new byte[16];
			mac[0] = (byte)0x81;
			mac[1] = (byte)0x01;
			mac[2] = (byte)nodeId;
			mac[3] = (byte)0x03;
			for(int i=0;i<12;i++){
				if(i>=0&&i<3){
					mac[4+i] = encData[i];}
				else if(i>=3&&i<12){
					mac[4+i]=0x00;
				}
			}
			StringBuffer sb1 = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				String hex = Integer.toHexString(mac[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb1.append("--"+hex.toUpperCase());
			}
			byte[] xorResult = xor(ecbRsult,mac);
			StringBuffer sb2 = new StringBuffer();
			for (int i = 0; i < xorResult.length; i++) {
				String hex = Integer.toHexString(xorResult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb2.append("--"+hex.toUpperCase());
			}
			byte[] result = EcbEncrypt(kapublic,xorResult);
			StringBuffer sbee = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				String hex = Integer.toHexString(result[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sbee.append("--"+hex.toUpperCase());
			}
			byte[] b = new byte[22];
			byte[] mark = new byte[]{(byte)0x98,(byte)0x81};
			for(int i=0;i<22;i++){
				if(i>=0&&i<2){
					b[i] =mark[i];
				}
				if(i>=2 &&i<10){
					b[i]=randomBytes[i-2];
				}else if(i>=10&&i<13){
					b[i]=encData[i-10];
				}else if(i==13){
					b[i] = getNonce[0];
				}else if(i>=14&&i<22){
					b[i] = result[i-14];
				}
			}
			StringBuffer sb3 = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb3.append("--"+hex.toUpperCase());
			}
			sendOrder(nodeId,b);
		} catch (Exception e) {
			e.printStackTrace();
		}

		IsFinished1=true;
	}



	//关机模式
	public synchronized  void getPower(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//加热模式
	public synchronized  void getHot(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x01;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//制冷模式
	public synchronized  void getWind(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x02;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//自动模式
	public synchronized  void getAuto(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x03;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//只开风扇模式
	public synchronized  void getOnly(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x06;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//低速模式
	public synchronized  void getLow(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x44;
		cmd[1]=0x01;
		cmd[2]=0x01;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//中速模式
	public synchronized  void getMid(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x44;
		cmd[1]=0x01;
		cmd[2]=0x05;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//高速模式
	public synchronized  void getHigh(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x44;
		cmd[1]=0x01;
		cmd[2]=0x03;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//获取风速状态
	public synchronized  void getWindMode(int nodeId){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]=0x44;
		cmd[1]=0x02;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
    public synchronized void speedModeSearch(int nodeId) {
		getWindMode(nodeId);
	}

    private synchronized int setCommondClass(int no, int type, int State) {
        byte ret = 0;
        byte length = 7;
        byte[] cmd = new byte[length];
        cmd[0] = 96;
        cmd[1] = 13;
        cmd[2] = 0;
        cmd[3] = (byte)type;
        cmd[4] = 32;
        cmd[5] = 1;
        cmd[6] = (byte)State;
        this.sndCmdClass(no, cmd, length, this.seqNo());
        return ret;
    }

	//获取模式状态
	public synchronized  void getMode(int nodeId){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x02;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

    private synchronized void $m(int nodeId) {
        byte length = 2;
        byte[] cmd = new byte[length];
        cmd[0] = 64;
        cmd[1] = 2;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    public synchronized void setSpeedForFreshAir(int nodeId, int status) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 57;
        cmd[1] = 7;
        cmd[2] = (byte)status;
        sndCmdClass(nodeId, cmd, length, this.seqNo());
	}
    public synchronized void setModeForFreshAir(int nodeId, int status) {
		int length = 3;
		byte[] cmd = new byte[length];
		cmd[0] = 0x39;
		cmd[1] = 0x01;
		cmd[2] = (byte)status;
		sndCmdClass(nodeId, cmd, length, seqNo());
	}

    private synchronized void $gm(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 68;
        cmd[1] = 1;
        cmd[2] = 5;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }

    private synchronized void $hi(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 68;
        cmd[1] = 1;
        cmd[2] = 3;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    private synchronized void $n(int nodeId) {
        byte length = 2;
        byte[] cmd = new byte[length];
        cmd[0] = 68;
        cmd[1] = 2;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    public synchronized void $auto(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 68;
        cmd[1] = 1;
        cmd[2] = 0;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    public void setCommondForWindSet(int nodeId, int value) {
        if(value == 0) {
        //    this.$lo(nodeId);
			openSpeedForAirControl(nodeId);
        } else if(value == 1) {
            this.$gm(nodeId);
        } else if(value == 2) {
            this.$hi(nodeId);
        } else if(value == 3) {
            this.$n(nodeId);
        } else if(value == 4) {
            this.setSpeedForFreshAir(nodeId, 1);
        } else if(value == 5) {
            this.setSpeedForFreshAir(nodeId, 2);
        } else if(value == 6) {
            this.setSpeedForFreshAir(nodeId, 3);
        } else if(value == 7) {
            this.$auto(nodeId);
        }
	}
    private synchronized void $w(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 64;
        cmd[1] = 1;
        cmd[2] = 0;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }

    private synchronized void $fi(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 64;
        cmd[1] = 1;
        cmd[2] = 1;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    private synchronized void $ga(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 64;
        cmd[1] = 1;
        cmd[2] = 11;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }

    private synchronized void $gf(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 64;
        cmd[1] = 1;
        cmd[2] = 2;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    private synchronized void $go(int nodeId) {
        byte length = 3;
        byte[] cmd = new byte[length];
        cmd[0] = 64;
        cmd[1] = 1;
        cmd[2] = 6;
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    public synchronized void $m(int nodeId, int temperature) {
        byte length = 6;
        byte[] cmd = new byte[length];
        cmd[0] = 67;
        cmd[1] = 1;
        cmd[2] = 1;
        cmd[3] = 34;
        byte[] num = this.intToByteArray1(temperature);
        cmd[4] = num[0];
        cmd[5] = num[1];
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }
    public synchronized void setCommondForActiveRequest(int nodeId, int value) {
        if(value == 0) {
            this.$w(nodeId);
        } else if(value == 1) {
            this.$fi(nodeId);
        } else if(value == 2) {
            this.$gf(nodeId);
        } else if(value == 3) {
            this.$ga(nodeId);
        } else if(value == 4) {
            this.$go(nodeId);
        } else if(value == 5) {
            this.$m(nodeId);
        } else if(value == 6) {
            this.setModeForFreshAir(nodeId, 0);
        } else if(value == 7) {
            this.setModeForFreshAir(nodeId, 1);
        } else if(value == 8) {
            this.$m(nodeId);
        } else if(value == 9) {
            this.$n(nodeId);
        }

    }

    public synchronized void setTemForAirConditioner(int nodeId, int num, int value) {
        if(value == 0) {
            this.hotTemSet(nodeId, num);
        } else if(value == 1) {
            this.windTemSet(nodeId, num);
        } else if(value == 2) {
            this.queryHotTem(nodeId);
        } else if(value == 3) {
            this.queryWindTem(nodeId);
        } else if(value == 4) {
            this.queryTemForAirConditioner(nodeId);
        }

    }

    public synchronized void getSpeedForFreshAir(int nodeId) {
		byte length = 2;
		byte[] cmd = new byte[length];
		cmd[0] = 0x39;
		cmd[1] = 0x08;
		sndCmdClass(nodeId, cmd, length, seqNo());
	}

    public synchronized void getModeForFreshAir(int nodeId) {
		int length = 2;
		byte[] cmd = new byte[length];

		cmd[0] = 0x39;
		cmd[1] = 2;

		sndCmdClass(nodeId, cmd, length, seqNo());
	}
	//水采暖温控器 关机模式
	public synchronized  void waterControlOffMode(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x00;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
    //水采暖温控器 加热模式
	public synchronized  void fireMode(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x01;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

	//水采暖温控器 自动模式
	public synchronized  void autoMode(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x01;
		cmd[2]=0x0B;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

    public synchronized void AirConditionerSet(int nodeId, int num, int value) {
        if(value == 0) {
            this.setIR(nodeId, num);
        }

    }

    public void setOnMessageSendListener(SendMessageToGatewayListener sendMessageToGatewayListener) {
        this.sendMessageToGatewayListener = sendMessageToGatewayListener;
    }

    public void sendBroadcastToGateway(Message message) {
        Log.e("magapeng", "send method to gateway");
        if(this.sendMessageToGatewayListener != null) {
            this.sendMessageToGatewayListener.sendMessageToGateway(message);
        }

    }

    private synchronized void setIR(int nodeId, int num) {
        byte length = 6;
        byte[] cmd = new byte[length];
        cmd[0] = 112;
        cmd[1] = 4;
        cmd[2] = 27;
        cmd[3] = 2;
        byte[] src = this.intToByteArray1(num);
        cmd[4] = src[0];
        cmd[5] = src[1];
        this.sndCmdClass(nodeId, cmd, length, this.seqNo());
    }

	//水采暖温控器 模式查询
	public synchronized void searchMode(int nodeId){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]=0x40;
		cmd[1]=0x02;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	//风机盘管设置加热模式下对比温度
	public synchronized  void setTem(int nodeId,int temperature){
		int length=6;
		byte[] cmd=new byte[length];
		cmd[0]=0x43;
		cmd[1]=0x01;
		cmd[2]=0x01;
		cmd[3]=0x22;
		byte[] num = intToByteArray1(temperature);
        cmd[4]=num[0];
		cmd[5]=num[1];
		sndCmdClass(nodeId,cmd,length,seqNo());

	}

	//风机盘管设置制冷模式下对比温度
	public synchronized  void setTem1(int nodeId,int temperature){
		int length=6;
		byte[] cmd=new byte[length];
		cmd[0]=0x43;
		cmd[1]=0x01;
		cmd[2]=0x02;
		cmd[3]=0x22;
		byte[] num = intToByteArray1(temperature);
		cmd[4]=num[0];
		cmd[5]=num[1];
		sndCmdClass(nodeId,cmd,length,seqNo());

	}
    public void setCommondForTemp(int nodeId, int value, int num) {
        if(value == 0) {
            this.setTem(nodeId, num);
        } else if(value == 1) {
            getTem(nodeId);
        }

    }

	//int 转 byte[2]
	public  byte[] intToByteArray1(int i) {
		byte[] result = new byte[2];
		result[0] = (byte)((i >> 8) & 0xFF);
		result[1] = (byte)(i & 0xFF);
		return result;
	}

	//int 转 byte[4]
	public byte[] intToByte4(int value){
		byte[] src = new byte[4];
		src[0] =  (byte) ((value>>24) & 0xFF);
		src[1] =  (byte) ((value>>16) & 0xFF);
		src[2] =  (byte) ((value>>8) & 0xFF);
		src[3] =  (byte) (value & 0xFF);
		return src;
	}


    //风机盘管加热模式获取温度
	public synchronized  void getTem(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x43;
		cmd[1]=0x02;
		cmd[2]=0x01;
		sndCmdClass(nodeId,cmd,length,seqNo());

	}
	public synchronized void openDoor(int nodeId, int value){
		getNonce(nodeId);
		mark = 1;
		nodeIdMark = nodeId;
        this.doorValue = value;
	}
	public synchronized  void getNonce(int nodeId){
		int length=2;
		byte[] cmd=new byte[length];
		cmd[0]= (byte) 0x98;
		cmd[1]=0x40;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}
	public synchronized  void sendOrder(int nodeId,byte[] cmd){
		sndCmdClass(nodeId,cmd,cmd.length,seqNo());
	}

    //制冷模式下获取
	public synchronized  void getTem1(int nodeId){
		int length=3;
		byte[] cmd=new byte[length];
		cmd[0]=0x43;
		cmd[1]=0x02;
		cmd[2]=0x02;
		sndCmdClass(nodeId,cmd,length,seqNo());

	}
    //打开静音
	public synchronized  void openSilent(int nodeId){
	}
	//关闭静音
    public synchronized void closeSilent(int nodeId){
	}
	/*
     * 处理数据handle
     */
    private final  MyHandler mHandler = new MyHandler();
    
	public class MyHandler extends Handler {
		@Override
        public void handleMessage(Message msg) {
			int allcheckednum = 0;
			Log.d("karl", "usb handler start msg.what = " + msg.what + "Events.INITIALIZE_SUCCESS= " + Events.INITIALIZE_SUCCESS);
			switch (msg.what) {
				case 0:
				case Events.INITIALIZE_SUCCESS:
					Log.d("karl", "get INITALIZE_SUCCESS ...\n"+rt);
					int ret=0;
						if (rt == null) {
							rt=new RevThread();
							rt.start();
							sct=new sendCmdThread();
							sct.start();

							ret=loadNode();
							if(ret != Events.SUCCESS){
								closeDev(false);
								msg = mHandler.obtainMessage(Events.INITIALIZE_FAILURE);
								Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(0, 2, 0, "", ""));
								Log.d("karl", "usb handler exit by loadnode error\n");
								return;
							}

							if(nodeList!=null){
								synchronized(nodeList) {
									for(DeviceInfo item:nodeList){
										if( item.generic == ZW_classcmd.GENERIC_TYPE_STATIC_CONTROLLER ){
											//	setControllerInfor(item);
											allcheckednum++;
										}
									}	
								}
								for (int i = 0; i < allcheckednum; i++) {
									setControllerInfor(null);
								}
							}

							ret = SetCmdClassEnable(true);
							if(ret != Events.SUCCESS){
								closeDev(false);
								Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(0, 2, 0, "", ""));
								Log.d("karl", "usb handler exit b cmdclass eanble true");
								return;
							}	

							drt=new DealRevDataThread();
							drt.start();
							IsOnlineThread = new CheckDevIfOnlineThread();
							IsOnlineThread.start();   
							Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(0, 1, 0, "", ""));
						}
					break;
				case Events.INITIALIZE_FAILURE:
					closeDev(false);
					Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(0, 2, 0, "", ""));
					break;
				case Events.USB_DONGLE_DETACHED:
					Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(3, 5, 0, "", ""));
					break;
				case Events.USB_DONGLE_ATTACHED:
					Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(3, 4, 0, "", ""));
					break;
					//case Events.DEVICE_INCLUDED:
					//	break;
				default:
					Log.d("karl", "switch defulat function..");
					break;
			}
			Log.d("karl", "usb handler exit by default");
        }
    };

	/*
         * 接收数据线程，将接收到的全部数据暂时都存储在一个队列，等待处理
         */
	public class RevThread extends Thread {
		byte[] TotalRevData = new byte[512];   //接收到的数据全部存入此处
		byte[] Revtemp = new byte[512];   //接收到的数据全部存入此处
		int buf_cur = 0;					   //next data received index
		int ret;
		private volatile boolean _run=true; 
		private int error_times = 0;

		public int process_zwave_received_data(byte revdata[], int len)
		{
			int status = RevStatus.SOF_START_MODE;
			int pcur = 0;
			int pend = len;
			int remainsize = 0;
			DataElement DebugDe=null;
			byte[] cmdack = new byte[2];
			byte[] subarray = new byte[256]; 

			while(pcur < pend) { //还有数据
				Log.e("karlr", "Process status" + status + ",pcur=" + pcur + ",pend=" + pend);
				switch (status) {
					case RevStatus.SOF_START_MODE:
						if (revdata[pcur] == 0x06) {
							subarray[0] = 0x06;
							if (_SerialCommand13Processing == 1) {
								revDataQueueMng13.pushQueue(subarray,1); 
								mConditionVariable13.open();
							} else {
								revDataQueueMngack.pushQueue(subarray, 1);
							}
							pcur++;
							Log.e("karlr", "get one ack frame\n");
						} else if (revdata[pcur] == 0x15) {
							pcur++;
							if (_SerialCommand13Processing == 1) {
								revDataQueueMng13.pushQueue(subarray,1); 
								mConditionVariable13.open();
							} else {
								revDataQueueMngack.pushQueue(subarray, 1);
							}
							Log.e("karlr", "get one nak frame");
						} else if (revdata[pcur] == 0x18) {
							pcur++;
							if (_SerialCommand13Processing == 1) {
								revDataQueueMng13.pushQueue(subarray,1); 
								mConditionVariable13.open();
							} else {
								revDataQueueMngack.pushQueue(subarray, 1);
							}
							Log.e("karlr", "get one can frame");
						} else if (revdata[pcur] == 0x01) {
							status = RevStatus.SOF_DATA_MODE;
						} else {
							Log.e("karlr", "error data ---> dump " + revdata[pcur]);
							pcur++;
						}
						break;
					case RevStatus.SOF_DATA_MODE:
						remainsize = pend - pcur;
						if (remainsize < 2) {
							status = RevStatus.SOF_NEED_MORE_DATA_MODE;
							break;
						}
						if ((revdata[pcur+1] & 0xff) > (remainsize - 2)) {
							status = RevStatus.SOF_NEED_MORE_DATA_MODE;
							break;
						}
						//get one frame
						for (int i=0; i < ((revdata[pcur+1] & 0xff) + 2); i++) {
							subarray[i] = revdata[pcur+i];
						}
						if (checkDataValid(subarray, (revdata[pcur+1] & 0xff) +2) == Events.SUCCESS) {
							if ((_SerialCommand13Processing == 1) && ((remainsize > 4) && revdata[pcur+3] == 0x13)) {
								revDataQueueMng13.pushQueue(subarray,(revdata[pcur+1] & 0xff)+ 2); 
								mConditionVariable13.open();
							} else {
								revDataQueueMng.pushQueue(subarray, (revdata[pcur+1] & 0xff) + 2);
							}
							Log.e("karlr", "get one data frame:" + byte2HexStr(subarray, (revdata[pcur+1] & 0xff)+2));
						} else {
							Log.e("karlr", "get one data frame(ack error):" + byte2HexStr(subarray, (revdata[pcur+1] & 0xff)+2));
						}
						cmdack[0] =0x06;
						up.sendByte(cmdack, dev, 1);
						pcur += (revdata[pcur+1] & 0xff) + 2;
						status = RevStatus.SOF_START_MODE;
						break;

					case RevStatus.SOF_NEED_MORE_DATA_MODE:
						break;
				}
				if (status == RevStatus.SOF_NEED_MORE_DATA_MODE)
					break;
			}
need_more_data:
			remainsize = pend - pcur;
			if (remainsize > 0) {
				if (pcur != 0) {
					//move the remain data
					for (int i = 0; i < remainsize; i++) {
						revdata[i] = revdata[pcur+i];
					}
				}
			}
			return remainsize;
		}
		@Override
			public void run(){
				long startreadtime; 
				long endreadtime;
				int waittime;
				while(_run){
					startreadtime = System.currentTimeMillis();
					if (buf_cur != 0) {
						waittime = 20;
						ret = up.revByteArr(dev, Revtemp, 256, waittime+10);		//wait for 8s wait data 20ms
					} else {
						waittime = 1000;
						ret = up.revByteArr(dev, Revtemp, 256, waittime+100); 
					}
					endreadtime = System.currentTimeMillis();
					if (ret < 0) {
						if ((System.currentTimeMillis() - startreadtime) >= waittime) {
							Log.e("karlr", "usb read raw timeout");
							buf_cur = 0;
							continue;
						}
//						Log.e("karlr", "current time = " + System.currentTimeMillis() + ",  starttime=" + startreadtime);
						Log.e("karlr", "usb read raw data error ret=" + ret + "（" + currentThread().getId() + "), please check, set _run=false, and exit, errortimes=" + error_times + ",dongle_sucess_init=" + dongle_sucess_init);
						error_times++;
						if (dongle_sucess_init == 1) {
							dongle_sucess_init = 0;
							//Log.e("karl", "dongle read error, and then sendboracse to reset the donle");
							//Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(31, 0, 0, "", ""));
						}
						if (error_times > 5) {
							Log.e("karl", "dongle read error, and then sendboracse to reset the donle" +currentThread().getId());
							Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(31, 0, 0, "", ""));
							this._run = false;
							break;
						} else {
							try {
								Thread.sleep(350);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						continue;
					} else if (ret == 0) {
						//timeout
						Log.e("karlr", "usb read time out, then reset receive buf" + currentThread().getId());
						buf_cur = 0;
						continue;
					} else {
						dongle_sucess_init = 1;
						error_times = 0;
						for (int i = 0; i < ret; i++) {
							TotalRevData[i+buf_cur] = Revtemp[i];
						}
						buf_cur += ret;	 	
						Log.e("karlr", "usb read raw length(" + ret+"," + buf_cur + ")" + "(" + currentThread().getId() + "):" + byte2HexStr(TotalRevData, buf_cur));
						buf_cur = process_zwave_received_data(TotalRevData, buf_cur);
					}
				}
				Log.e("karlr","接收数据线程已经退出去了" + currentThread().getId());
				Controller.this.rt = null;
			}

		//停止线程运行
		public void stopThread(){
			//this.stop();
			this._run=false;
			this.interrupt();

			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/*
     * 需要延时才能收到结果的命令，就利用此线程来进行处理。比如获取
     */
	public class sendCmdThread extends Thread {
		private volatile boolean _run=true; 
		int tepCmdQueueIndex=0;
		int gSendtimes = 0;
		int gSendSucesstimes = 0;
		int ret = 0; 
		byte[] resp = new byte[256];
		byte[] callback = new byte[256];
		
	    @Override
	    public void run(){
	    	DataElement de = null;
	    	int loop=0;
			int online = 0;
	    	while(_run){	
				Log.e("karl--->", "now sendCmdThread " + currentThread().getId() + "cmdQueueMng.isQueueEmpty = " + cmdQueueMng.isQueueEmpty());
				if(cmdQueueMng.isQueueEmpty() != true) {
					while(cmdQueueMng.isQueueEmpty() != true) {
						de=cmdQueueMng.popQueue();
						online = 0;
						for(loop=0;loop<3;loop++){
							Log.e("karl-->", "get send cmd[3] = " +  de.buf[3]);
							if (de.buf[3] == 0x13) {
								//ret = SendDataAndWaitAck(de.buf, de.length);		//区分3种命令
								//cmd 13 need get callback and resp
								Log.e("karl", "----------------------------------------cmd start ------------------------");
								_SerialCommand13Processing = 1; 
								//before send cmd first clean cmd13 queue	
								revDataQueueMng13.clearQueue();
								DeviceInfo node = Controller.this.GetNodeByNodeid(de.buf[4]);
								if ((node != null)&& ((node.isOnline == SdkConstants.DEVICE_NOTONLINE))) {
									Log.e("karl", "device nodeid[" + de.buf[4] + "]" + ", offlinetimes =" + node.offlinetimes + " not online then not send cmd13");
									ret = Events.SUCCESS;
								} else {
									ret = SendDataAndWaitForResponseCallback(de.buf, de.length, resp, callback);		//区分3种命令
									gSendtimes++;
									if (ret == Events.SUCCESS) {
										gSendSucesstimes++;
									}
									Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(32, gSendtimes, gSendSucesstimes, "", ""));
								}
								_SerialCommand13Processing = 0; 
								Log.e("karl", "----------------------------------------cmd start end ------------------------");
							} else {
								ret = SendDataAndWaitAck(de.buf, de.length);		//区分3种命令
							}
							if(ret == Events.SUCCESS) {
								online = 1;
								break; 
							} else if (ret == Events.DEV_NOT_ONLINE) {
								online = 2;
								break;
							}

							//command timeout retry it
							try {
								Thread.sleep(350);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (de.buf[3] == 0x13) {
							DeviceInfo node = Controller.this.GetNodeByNodeid(de.buf[4]);
							if ((node != null)&& ((node.isOnline == SdkConstants.DEVICE_NOTONLINE))) { 
								Log.e("karlonline", "cmd13 returned but node is already not online");
							} else {
								if (online == 2) {
									//not online
									if (node.isOnline != SdkConstants.DEVICE_NOTONLINE) {
										node.offlinetimes++;
										if (node.offlinetimes > 1) {
											node.isOnline = SdkConstants.DEVICE_NOTONLINE;
											Controller.this.SendIfOnlineMsg(node.nodeId, false);
											Log.e("karlonline", "node[" + node.nodeId + "], send offline message to gateway");
										} else {
											Log.e("karlonline", "node[" + node.nodeId + "], offlinetimes=" + node.offlinetimes + ", need check again");
										}
									}
								} else if (online == 1) {
									if (node.isOnline != SdkConstants.DEVICE_ONLINE) {
										node.offlinetimes = 0;
										node.isOnline = SdkConstants.DEVICE_ONLINE;
										Controller.this.SendIfOnlineMsg(node.nodeId, true);
										Log.e("karlonline", "node[" + node.nodeId + "], send online message to gateway");
									}
									node.offlinetimes = 0;
								}
								node.revDataTime = System.currentTimeMillis();
								Log.e("karlonline", "update(0x13) node[" + node.nodeId + "] = " + node.isOnline);
							}
						}
					}
					mConditionVariable.close();
				} else {
					mConditionVariable.close();
					mConditionVariable.block();//没有数据,则等待数据
				}
			}
			Log.d("karl", "Thread sendCmdThread exit!!!");
	    }
	    
		private void stopThread(){
			//this.stop();
			this._run=false;
			mConditionVariable.open();
			try {
			Thread.sleep(450);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
    *循环遍历接收数据队列，处理队列中的数据
    */
	public class DealRevDataThread extends Thread {
        private volatile boolean _run = true;

        public DealRevDataThread() {
        }

        public void run() {
            DataElement de = null;
            boolean loop = false;
            int length = 0;
            byte[] buf = new byte[256];
            boolean Endpoint = false;
            String[] $str = new String[11];
            new String();

            while(this._run) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException var25) {
                    var25.printStackTrace();
                }

                QueueManager len = Controller.this.revDataQueueMng;
                int var27;
                synchronized(Controller.this.revDataQueueMng) {
                    if(Controller.this.revDataQueueMng.isQueueEmpty()) {
                        continue;
                    }

                    if(!Controller.this.IsDealTheData) {
                        Controller.this.IsDealTheData = true;
                        de = Controller.this.revDataQueueMng.popQueue();
                        if(de == null) {
                            Controller.this.IsDealTheData = false;
                            continue;
                        }

                        if(de.length > 1) {
                            length = (de.buf[1] & 255) - 1;
                        }

                        for(var27 = 0; var27 < length; ++var27) {
                            buf[var27] = de.buf[var27 + 2];
                        }

                        String byteNums = Controller.this.byte2HexStr1(de.buf, 11);
                        $str = byteNums.split("_");
						Log.d("karl", "dealthread" + byteNums);
                        Controller.this.IsDealTheData = false;
                    }
                }

                if(Controller.this.revDataQueueMng.getQueueNum() > 110) {
                    Controller.this.IsDealTheData = false;
                }

                if(de != null) {
                    Controller.this.byte2HexStr1(de.buf, 16);
                } else {
					continue;
				}

                label736:
                switch(buf[1] & 255) {
                case 4:
                    if(buf[5] == 98 && buf[6] == 3) {
                        if(buf[7] == -1) {		//lock
                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 37, buf[3], buf[7] + "", ""));
                        } else if ((buf[0] == 0x00) || (buf[1] == 0x01)) {
                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 37, buf[3], buf[7] + "", ""));
                        }
                        DeviceInfo df = Controller.this.GetNodeByNodeid(buf[3]);
						if (df != null) {
                            df.revDataTime = System.currentTimeMillis(); //update the device online state
							Log.e("karlonline", "update node[" + df.nodeId + "] = " + df.isOnline + ", updated to online");
                             if(df.isOnline != SdkConstants.DEVICE_ONLINE) {
								df.offlinetimes = 0;
                                df.isOnline = SdkConstants.DEVICE_ONLINE;
                                Controller.this.SendIfOnlineMsg(df.nodeId, true);
								Log.e("karlonline", "node[" + df.nodeId + "], send online info to gateway");
                            }
							df.offlinetimes = 0;
						}
                    } else {
                        if(buf[5] == -104 && buf[6] == -128) {
                            byte[] var30 = new byte[8];

                            for(int var33 = 7; var33 < 15; ++var33) {
                                var30[var33 - 7] = buf[var33];
                                Controller.this.getNonce[var33 - 7] = buf[var33];
                            }

                            if(!Controller.this.IsFinished) {
                                Controller.this.sendKey(buf[3]);
                                Controller.this.sendReport(buf[3]);
                                Controller.this.getNonce(buf[3]);
                                break;
                            }

                            if(Controller.this.IsFinished && !Controller.this.IsFinished1) {
                                Controller.this.sendssosiation(buf[3]);
                                break;
                            }

                            if(Controller.mark == 1) {
                                Controller.this.sendOrder(Controller.nodeIdMark);
                            }
                        }

                        byte var31 = buf[3];
                        Object var34 = null;
                        DeviceInfo df = Controller.this.GetNodeByNodeid(var31);
                        if(df != null) {			//get 0x04 information
                            df.revDataTime = System.currentTimeMillis(); //update the device online state
							Log.e("karlonline", "update node[" + df.nodeId + "] = " + df.isOnline + ", updated to online");
                             if(df.isOnline != SdkConstants.DEVICE_ONLINE) {
								df.offlinetimes = 0;
                                df.isOnline = SdkConstants.DEVICE_ONLINE;
                                Controller.this.SendIfOnlineMsg(df.nodeId, true);
								Log.e("karlonline", "node[" + df.nodeId + "], send online info to gateway");
                            }

                            Object cmd = null;
                            boolean DataType = false;
                            long var39;
                            float var40;
                            byte[] var42;
                            if(buf[7] == 14 && buf[8] == 34) {
                                var42 = new byte[]{buf[9], buf[10]};
                                var39 = Long.parseLong(Controller.this.byte2HexStrNonSpace(var42, 2), 16);
                                var40 = (float)var39 / 10.0F;
                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 24, var31, var40 + "", ""));
                            } else if(buf[5] == 67 && buf[6] == 3 && buf[7] == 1 && buf[8] == 68) {
                                var42 = new byte[]{buf[9], buf[10], buf[11], buf[12]};
                                var39 = Long.parseLong(Controller.this.byte2HexStrNonSpace(var42, 4), 16);
                                var40 = (float)var39 / 100.0F;
                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, var40 + "", "2"));
                            } else if(buf[5] == 67 && buf[6] == 3 && buf[7] == 2 && buf[8] == 68) {
                                var42 = new byte[]{buf[9], buf[10], buf[11], buf[12]};
                                var39 = Long.parseLong(Controller.this.byte2HexStrNonSpace(var42, 4), 16);
                                var40 = (float)var39 / 100.0F;
                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, var40 + "", "3"));
                            } else {
                                byte state;
                                if(buf[5] == 49 && buf[6] == 5 && buf[7] == 1 && buf[8] == 1) {
                                    state = buf[9];
                                    Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 3, var31, state + "", ""));
                                } else {
                                    byte[] var35;
                                    byte[] var38;
                                    int var41;
									Log.d("karl", "--------get switch buf[5] = " + buf[5]);
                                    switch(buf[5] & 255) {

										/*
										case 0x80: 
											Log.d("karl", "get batery cap report: length=" + de.length);
											Log.d("karl", "buf[6]= " + buf[6] + ", buf[7] = " + buf[7]);	
											break;
											*/
                                    case 32:
                                        if($str[0].equals("01") && $str[1].equals("09") && $str[2].equals("00") && $str[3].equals("04") && $str[4].equals("00") && $str[7].equals("20") && $str[8].equals("03")) {
                                            String var43 = $str[5];
                                            byte[] var45 = Controller.this.hexStringToByteArray(var43);
                                            if($str[9].equals("00")) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 8, var45[0], "0", ""));
                                            } else if($str[9].equals("FF")) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 8, var45[0], "255", ""));
                                            }
                                        } else if(buf[6] == 1) {
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 7, var31, (255 & buf[7]) + "", ""));
                                        }
                                        break label736;
                                    case 37:
                                        if(buf[6] == 3) {
                                            if($str[9].equals("00")) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 36, var31, "0", ""));
                                            } else if($str[9].equals("FF")) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 36, var31, "255", ""));
                                            }
                                        }
                                        break label736;
                                    case 38:
                                        if(buf[6] == 3) {
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 10, var31, buf[7] + "", ""));
                                        }
                                        break label736;
                                    case 48:
										if (buf[8] == 0x0c) {
											if(buf[6] == 3) {
												Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 4, var31, (255 & buf[7]) + "", ""));
												Log.d("karl", "get sensor alarm report send alarm report to gateway");
											}
										} else if (buf[8] == 0x0a) {
											Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 7, var31, (255 & buf[7]) + "", ""));
										} else if (buf[8] == 0x08) {
											Log.d("karl", "get remove detected no message upload");
										} else {
											Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 4, var31, (255 & buf[7]) + "", ""));
										}
                                        break label736;
                                    case 49:
                                        if(buf[6] == 5) {
                                            float var44;
                                            if(buf[7] == 1 && buf[8] == 2) {
                                                var38 = new byte[]{(byte)(buf[9] & 127), buf[10]};
                                                var44 = (float) Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16) / 10.0F;
                                                if(buf[9] >> 7 == 1) {
                                                    var44 = 0.0F - var44;
                                                }

                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 3, var31, var44 + "", ""));
                                            } else if(buf[7] == 1 && buf[8] == 34) {
                                                var38 = new byte[]{(byte)(buf[9] & 127), (byte)(buf[10] & 255)};
                                                var44 = (float) Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16) / 10.0F;
                                                if(buf[9] >> 7 == 1) {
                                                    var44 = 0.0F - var44;
                                                }

                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 3, var31, var44 + "", ""));
                                            } else if(buf[7] == 3 && buf[8] == 10) {
                                                var38 = new byte[]{(byte)(buf[9] & 127), buf[10]};
                                                var41 = Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16);
                                                if(buf[9] >> 7 == 1) {
                                                    var41 = 0 - var41;
                                                }

                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 5, var31, var41 + "", ""));
                                            } else if(buf[7] == 5 && buf[8] == 10) {
                                                var38 = new byte[2];
                                                if(buf[0] == 10) {
                                                    var38[0] = buf[9];
                                                    var38[1] = buf[10];
                                                    var41 = Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16) / 100;
                                                    Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 14, var31, var41 + "", ""));
                                                }
                                            } else if(buf[7] == 18 && buf[8] == 10) {
                                                var38 = new byte[2];
                                                if(buf[8] == 10) {
                                                    var38[0] = buf[9];
                                                    var38[1] = buf[10];
                                                    var41 = 0;

                                                    try {
                                                        var41 = Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16);
                                                    } catch (Exception var22) {
                                                        ;
                                                    }

                                                    Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 15, var31, var41 + "", ""));
                                                }
											} else if (buf[7] == 0x12 && buf[8] == 0x01) {
												//add flow value
												Log.d("karl", "zwave multilevel sensor report, flow value=" + buf[9]);
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 15, var31, buf[9]+ "", ""));
											} else if (buf[7] == 0x03 && buf[8] == 0x09) {
												Log.d("karl", "zwave multilevel sensor report, light or rain value=" + buf[9]);
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 0x12, var31, buf[9]+ "", ""));
                                            } else if(buf[7] == 12 && buf[8] == 2) {
                                                var38 = new byte[2];
                                                if(buf[8] == 2) {
                                                    try {
                                                        var38[0] = buf[9];
                                                        var38[1] = buf[10];
                                                        var41 = Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16);
                                                        Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 16, var31, var41 + "", ""));
                                                    } catch (Exception var21) {
                                                        ;
                                                    }
                                                }
                                            } else if(buf[7] == 27 && buf[8] == 2) {
                                                var38 = new byte[2];
                                                if(buf[8] == 2) {
                                                    try {
                                                        var38[0] = buf[9];
                                                        var38[1] = buf[10];
                                                        var41 = Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16);
                                                        Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 17, var31, var41 + "", ""));
                                                    } catch (Exception var20) {
                                                        ;
                                                    }
                                                }
                                            } else if(buf[7] == 3 && buf[8] == 2) {
                                                DataType = true;
                                                var38 = new byte[2];
                                                if(buf[8] == 2) {
                                                    var38[0] = buf[9];
                                                    var38[1] = buf[10];
                                                    var41 = Integer.parseInt(Controller.this.byte2HexStrNonSpace(var38, 2), 16);
                                                    Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 18, var31, var41 + "", ""));
                                                }
                                            } else if(buf[7] == 35) {
                                                var38 = new byte[]{buf[8], buf[9], buf[10]};
                                                var41 = buf[10] & 255 | (buf[9] & 255) << 8;
                                                float valuenew = (float)var41 / 10.0F;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 21, var31, valuenew + "", ""));
                                            } else if(buf[7] == 17 && buf[8] == 2) {
                                                var38 = new byte[]{buf[8], buf[9], buf[10]};
                                                new Bundle();
                                                var41 = buf[10] & 255 | (buf[9] & 255) << 8;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 19, var31, var41 + "", ""));
                                            } else if(buf[7] == 5 && buf[8] == 1) {
                                                var38 = new byte[]{buf[8], buf[9], buf[10]};
                                                new Bundle();
                                                var41 = buf[9] & 255;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 14, var31, var41 + "", ""));
                                            }
                                        }
                                        break label736;
                                    case 50:
                                        if(buf[6] == 2) {
                                            if((buf[7] & 255) == 33 && (buf[8] & 255) == 52) {
                                                var42 = new byte[]{buf[9], buf[10], buf[11], buf[12]};
                                                var39 = Long.parseLong(Controller.this.byte2HexStrNonSpace(var42, 4), 16);
                                                var40 = (float)var39 / 10.0F;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 1, var31, var40 + "", ""));
                                            } else if((buf[7] & 255) == 33 && (buf[8] & 255) == 132) {
                                                var42 = new byte[]{buf[9], buf[10], buf[11], buf[12]};
                                                var39 = Long.parseLong(Controller.this.byte2HexStrNonSpace(var42, 4), 16);
                                                var40 = (float)var39 / 10000.0F;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 2, var31, var40 + "", ""));
                                            } else if((buf[7] & 255) == 1 && (buf[8] & 255) == 132) {
                                                var42 = new byte[]{buf[9], buf[10], buf[11], buf[12]};
                                                var39 = Long.parseLong(Controller.this.byte2HexStrNonSpace(var42, 4), 16);
                                                var40 = (float)var39 / 10000.0F;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 2, var31, var40 + "", ""));
                                            }
                                        }
                                        break label736;
                                    case 57:
                                        if(buf[6] == 3) {
                                            if(buf[7] == 0) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, "6", "0"));
                                            } else if(buf[7] == 1) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, "7", "0"));
                                            }
                                        } else if(buf[6] == 9) {
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, buf[7] + 3 + "", "1"));
                                        }
                                        break label736;
                                    case 64:
                                        byte var37 = buf[7];
                                        if(buf[6] == 3) {
                                            if(var37 == 11) {
                                                var37 = 3;
                                            } else if(var37 == 6) {
                                                var37 = 4;
                                            }

                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, var37 + "", "0"));
                                        }
                                        break label736;
                                    case 67:
                                        if(buf[6] == 3 && buf[7] == 1 && buf[8] == 34) {
                                            DataType = true;
                                            var38 = new byte[]{buf[9], buf[10]};
                                            var41 = this.byteArrayToInt(var38);
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, var41 + "", "2"));
                                        }
                                        break label736;
                                    case 68:
                                        state = buf[7];
                                        if(buf[6] == 3) {
                                            if(state == 1) {
                                                state = 0;
                                            } else if(state == 3) {
                                                state = 2;
                                            } else if(state == 5) {
                                                state = 1;
                                            } else if(state == 0) {
                                                state = 7;
                                            }

                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 23, var31, state + "", "1"));
                                        }
                                        break label736;
                                    case 91:
                                        Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 32, var31, buf[9] + "", ""));
                                        break label736;
                                    case 96:
                                        if((buf[6] & 255) == 13 && (((buf[9] & 255) == 32) || ((buf[9] & 255) == 0x25)) && (buf[10] & 255) == 3) {
                                            if(buf[11] == -1) {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 9, var31, "1", buf[7] + ""));
                                            } else {
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 9, var31, buf[11] + "", buf[7] + ""));
                                            }
                                        }
                                        break label736;
                                    case 113:
                                        if((buf[6] & 255) == 5) {
                                            byte AlertType = buf[11];
                                            byte State = buf[8];
											byte mytype = 11;
											if ((AlertType == 0x01)) {
												mytype = 7;
												if (buf[12] != 0) {
													State = (byte)0xff;
												} else {
													State = 0x00;
												}
											} else if (AlertType == 0x12) {
												mytype = 7;
												if (buf[12] != 0) {
													State = (byte)0xff;
												} else {
													State = 0x00;
												}
											} else if (AlertType == 0x05) {
												mytype = 7;
												if (buf[12] != 0x00) {
													State = (byte)0xff;
												} else {
													State = (byte) 0x00;
												}
											} else if (AlertType == 0x07) {
												if (buf[12] == 0x08) {
													State = (byte)0xff;
												} else if (buf[12] == 0x00) {
													State = (byte)0x00;
												}
												mytype = 4;
											} else if (AlertType == 0x06) {
												if (buf[12] == 0x16) {
													State = (byte)0xff;
												} else if (buf[12] == 0x17) {
													State = (byte)0x00;
												}
												mytype = 7;
											} else if (AlertType == 0x0A) {
												mytype = Events.SOS_DEVICE_REPORT;
												if (buf[10] != 0x00) {
													State = (byte) 0xff;
												}
											} else {
												State = buf[8];
											}
											Log.d("karl", "-----get yanwu sensor-----" + var31 + ",mtype =" + mytype + ", State=" + State + ", buf[12]=" +buf[12] + ", buf[8]=" + buf[8]);
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, mytype, var31, (255 & State) + "", ""));
                                        }
                                        break label736;
                                    case 114:
                                        if((buf[6] & 255) == 5) {
                                            boolean var36 = false;

                                            for(var27 = 0; var27 < df.cmdlen; ++var27) {
                                                if((df.cmdClasseAndVersion[var27][0] & 255) == 133) {
                                                    var36 = true;
                                                    break;
                                                }
                                            }
											Log.d("karl", "======get val36 before" + var36);

                                            df.ManufacturerId[0] = buf[7];
                                            df.ManufacturerId[1] = buf[8];
                                            df.ProductType[0] = buf[9];
                                            df.ProductType[1] = buf[10];
                                            df.ProductId[0] = buf[11];
                                            df.ProductId[1] = buf[12];
                                            if(buf[7] == 1 && buf[8] == 94 && buf[9] == -128 && ((buf[10] == 24) || (buf[10] == 0x28)) && buf[11] == 0 && buf[12] == 1) {
                                                Controller.this.getNonce(var31);
												var36 = false;
											}

/*
											Log.d("karl", "ManufacturerId[0] " + buf[7] + ", 8=" + buf[8] + ", 9=" + buf[9] + ", 10=" + buf[10] + ", 11=" + buf[11] + ", 12=" + buf[12]);
											if (buf[7] == 82 && buf[8] == 84 && buf[9] == 0x00 && buf[10] == 1 && buf[11] == -123 && buf[12] == 16) {
												zw_set_suc_nodeid(var31);
											}
											*/
                                            if(buf[7] == 82 && buf[8] == 84 && buf[9] == 1 && buf[10] == 1 && buf[11] == -125 && buf[12] == 119) {
                                                var35 = new byte[]{(byte)-123, (byte)5};
                                                Controller.this.sndCmdClass(var31, var35, 2, Controller.this.seqNo());
                                                Controller.this.deploy(var31);
                                                Controller.this.IRControl(var31);
                                                Controller.this.swayControl(var31);
                                                Controller.this.temCompensate(var31);
                                                Controller.this.setIR(var31, 49);
                                                var35 = new byte[4];
                                            }


                                            for(var27 = 0; var27 < df.cmdlen; ++var27) {
                                                if((df.cmdClasseAndVersion[var27][0] & 255) == 132) {
                                                    Controller.this.wakeupFlag = true;
                                                    break;
                                                }
                                            }

                                            if(df.generic == 16) {
                                                if((df.ManufacturerId[0] & 255) == 82 && (df.ManufacturerId[1] & 255) == 84 && (df.ProductType[0] & 255) == 147 && (df.ProductType[1] & 255) == 3 && (df.ProductId[0] & 255) == 128 && (df.ProductId[1] & 255) == 144) {
                                                    df.CompmentCnt = 3;
                                                    df.ComponentType1 = 1;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 1;
                                                    df.EndPoint2 = 2;
                                                    df.ComponentType3 = 1;
                                                    df.EndPoint3 = 3;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                } else if((df.ManufacturerId[0] & 255) == 2 && (df.ManufacturerId[1] & 255) == 21 && (df.ProductId[0] & 255) == 16 && (df.ProductId[1] & 255) == 3) {
                                                    df.CompmentCnt = 3;
                                                    df.ComponentType1 = 1;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 1;
                                                    df.EndPoint2 = 2;
                                                    df.ComponentType3 = 1;
                                                    df.EndPoint3 = 3;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                } else if((df.ManufacturerId[0] & 255) == 1 && (df.ManufacturerId[1] & 255) == 37 && (df.ProductId[0] & 255) == 16 && (df.ProductId[1] & 255) == 1) {
                                                    df.CompmentCnt = 1;
                                                    df.ComponentType1 = 1;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 0;
                                                    df.EndPoint2 = 0;
                                                    df.ComponentType3 = 0;
                                                    df.EndPoint3 = 0;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                } else if((df.ManufacturerId[0] & 255) == 2 && (df.ManufacturerId[1] & 255) == 21 && (df.ProductId[0] & 255) == 16 && (df.ProductId[1] & 255) == 2) {
                                                    df.CompmentCnt = 2;
                                                    df.ComponentType1 = 1;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 1;
                                                    df.EndPoint2 = 2;
                                                    df.ComponentType3 = 0;
                                                    df.EndPoint3 = 0;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                } else {
                                                    df.CompmentCnt = 1;
                                                    df.ComponentType1 = 3;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 0;
                                                    df.EndPoint2 = 0;
                                                    df.ComponentType3 = 0;
                                                    df.EndPoint3 = 0;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                }
                                            } else if(df.generic == 49) {
                                                df.CompmentCnt = 1;
                                                df.ComponentType1 = 0;
                                                df.EndPoint1 = 1;
                                                df.ComponentType2 = 0;
                                                df.EndPoint2 = 0;
                                                df.ComponentType3 = 0;
                                                df.EndPoint3 = 0;
                                                df.ComponentType4 = 0;
                                                df.EndPoint4 = 0;
                                            } else if(df.generic == 32) {
                                                if((df.ManufacturerId[0] & 255) == 1 && (df.ManufacturerId[1] & 255) == 17 && (df.ProductType[0] & 255) == 32 && (df.ProductType[1] & 255) == 0 && (df.ProductId[0] & 255) == 0 && (df.ProductId[1] & 255) == 1) {
                                                    df.CompmentCnt = 2;
                                                    df.ComponentType1 = 10;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 9;
                                                    df.EndPoint2 = 2;
                                                    df.ComponentType3 = 0;
                                                    df.EndPoint3 = 0;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                } else if((df.ManufacturerId[0] & 255) == 1 && (df.ManufacturerId[1] & 255) == 17 && (df.ProductType[0] & 255) == 32 && (df.ProductType[1] & 255) == 0 && (df.ProductId[0] & 255) == 0 && (df.ProductId[1] & 255) == 2) {
                                                    df.CompmentCnt = 3;
                                                    df.ComponentType1 = 4;
                                                    df.EndPoint1 = 1;
                                                    df.ComponentType2 = 5;
                                                    df.EndPoint2 = 2;
                                                    df.ComponentType3 = 9;
                                                    df.EndPoint3 = 3;
                                                    df.ComponentType4 = 0;
                                                    df.EndPoint4 = 0;
                                                }
                                            } else if(df.generic == 17 && df.specific == 5) {
                                                df.CompmentCnt = 1;
                                                df.ComponentType1 = 16;
                                                df.EndPoint1 = 1;
                                                df.ComponentType2 = 0;
                                                df.EndPoint2 = 0;
                                                df.ComponentType3 = 0;
                                                df.EndPoint3 = 0;
                                                df.ComponentType4 = 0;
                                                df.EndPoint4 = 0;
                                            } else if((df.generic & 255) == 17 && (df.specific & 255) == 1) {
                                                df.CompmentCnt = 1;
                                                df.ComponentType1 = 13;
                                                df.EndPoint1 = 1;
                                                df.ComponentType2 = 0;
                                                df.EndPoint2 = 0;
                                                df.ComponentType3 = 0;
                                                df.EndPoint3 = 0;
                                                df.ComponentType4 = 0;
                                                df.EndPoint4 = 0;
                                            } else if((df.generic & 255) == 161 && (df.specific & 255) == 2) {
                                                df.CompmentCnt = 1;
                                                df.ComponentType1 = 8;
                                                df.EndPoint1 = 1;
                                                df.ComponentType2 = 0;
                                                df.EndPoint2 = 0;
                                                df.ComponentType3 = 0;
                                                df.EndPoint3 = 0;
                                                df.ComponentType4 = 0;
                                                df.EndPoint4 = 0;
                                            } else if((df.generic & 255) == 15 && (df.specific & 255) == 1) {
                                                df.CompmentCnt = 1;
                                                df.ComponentType1 = 17;
                                                df.EndPoint1 = 1;
                                                df.ComponentType2 = 0;
                                                df.EndPoint2 = 0;
                                                df.ComponentType3 = 0;
                                                df.EndPoint3 = 0;
                                                df.ComponentType4 = 0;
                                                df.EndPoint4 = 0;
                                            }

                                            if(var36) {
                                                if(Controller.this.sndDataMonitor.Action == 1) {
                                                    var35 = new byte[]{(byte)-123, (byte)5};
                                                    Controller.this.sndCmdClass(var31, var35, 2, Controller.this.seqNo());
                                                }
                                            } else if(Controller.this.sndDataMonitor.Action == 1) {
                                                Controller.this.sndDataMonitor.Action = 0;
                                                Controller.this.sndDataMonitor.FuncId = 0;
                                                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(6, 8, var31, "", ""));
												DeviceInfo df1 = Controller.this.GetNodeByNodeid(var31);
												if (df1 != null) {
													df1.revDataTime = System.currentTimeMillis(); //Controller.this.getCurrentTime();
													df1.isOnline = SdkConstants.DEVICE_ONLINE;
													df1.offlinetimes = 0;
													Controller.this.SendIfOnlineMsg(var31, true);
													Log.d("karlonline", "deviced[" + df1.nodeId + "], added and set online2 send to gateway");
												} else {
													Log.d("karlonline", "deviced[" + var31 + "], no DeviceInfo found2");
												}
                                            }
                                        }
                                        break label736;
                                    case 128:
                                        if((buf[6] & 255) == 3) {
											Log.d("karl", "battery capability report nodeId=" + var31 + ", buf[7]=" + buf[7]);
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(15, 13, var31, buf[7] + "", ""));
                                        }
                                        break label736;
                                    case 132:
                                        if(buf[6] == 7 && Controller.this.sndDataMonitor.Action != 1) {
                                            var35 = new byte[]{(byte)-124, (byte)8};
                                            Controller.this.sndCmdClass(var31, var35, 2, Controller.this.seqNo());
                                        }
                                        break label736;
                                    case 133:
                                        if((buf[6] & 255) == 6) {
                                            state = buf[7];
                                            if(df.generic == 49) {
                                                try {
                                                    Thread.sleep(3500L);
                                                } catch (Exception var23) {
                                                    var23.printStackTrace();
                                                }

                                                var35 = new byte[]{(byte)-123, (byte)1, (byte)state, (byte)1};
                                                Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
											}else if((df.generic&0xFF)== ZW_classcmd.GENERIC_TYPE_SENSOR_NOTIFICATION && (df.specific&0xFF)== ZW_classcmd.SPECIFIC_TYPE_REPEATER_SLAVE){
                                                var35 = new byte[]{(byte)-123, (byte)1, (byte)0x01, (byte)1};
                                                Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());

                                            } else if(df.generic == 32) {
                                                var35 = new byte[]{(byte)-123, (byte)1, (byte)state, (byte)1};
                                                Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
                                            } else if(df.generic == 16) {
												Log.d("karl", "df.generic = " + df.generic + ", df.specific = " + df.specific);
                                                if(df.ManufacturerId[0] == 82 && df.ManufacturerId[1] == 84 && (df.ProductType[0] & 255) == 147 && (df.ProductType[1] & 255) == 3 && (df.ProductId[0] & 255) == 128 && (df.ProductId[1] & 255) == 144) {
                                                    length = 7;
                                                    var35 = new byte[length];
                                                    var35[0] = -114;
                                                    var35[1] = 1;
                                                    var35[2] = 4;
                                                    var35[3] = 1;
                                                    var35[4] = 0;
                                                    var35[5] = 0;
                                                    var35[6] = 0;
                                                    Controller.this.sndCmdClass(var31, var35, length, Controller.this.seqNo());
                                                } else if (df.specific == 0x01) {
													var35 = new byte[]{(byte)-123, (byte)1, (byte)1, (byte)1};
													Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
                                                } else {
                                                    var35 = new byte[]{(byte)-123, (byte)1, (byte)state, (byte)1};
                                                    Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
                                                }
                                            } else if(df.generic == 17 && df.specific == 5) {
                                                var35 = new byte[]{(byte)-123, (byte)1, (byte)state, (byte)1};
                                                Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
                                            } else if((df.generic & 255) == 17 && (df.specific & 255) == 1) {
                                                var35 = new byte[]{(byte)-123, (byte)1, (byte)state, (byte)1};
                                                Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
                                            } else if((df.generic & 255) == 161 && (df.specific & 255) == 2) {
                                                var35 = new byte[]{(byte)-123, (byte)1, (byte)1, (byte)1};
                                                Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());
                                            } else {
                                                if(df.ManufacturerId[0] == 1 && df.ManufacturerId[1] == 95 && df.ProductType[0] == 9 && df.ProductType[1] == 5 && df.ProductId[0] == 2 && df.ProductId[1] == 1) {
                                                    this.setAsociation(var31);
                                                } else {
                                                    var35 = new byte[]{(byte)-123, (byte)1, (byte)1, (byte)1};
                                                    Controller.this.sndCmdClass(var31, var35, 4, Controller.this.seqNo());	 //association
                                                }

                                                byte[] stateNum = new byte[]{(byte)-128, (byte)2};

                                                Controller.this.sndCmdClass(var31, stateNum, 2, Controller.this.seqNo());
                                                if(Controller.this.wakeupFlag) {
                                                    var35 = new byte[]{(byte)-124, (byte)4, (byte)0, (byte)1, (byte)0x2c, (byte)1};
                                                    Controller.this.sndCmdClass(var31, var35, 6, Controller.this.seqNo());
                                                }

                                                Controller.this.wakeupFlag = false;
                                            }

                                            Controller.this.sndDataMonitor.Action = 0;
                                            Controller.this.sndDataMonitor.FuncId = 0;
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(6, 8, var31, "", ""));
                                        }
                                        break label736;
                                    case 134:
									case -122:
										Log.d("karl", "-------get 134 case");
                                        if(buf[6] == 20) {
                                            var35 = new byte[2];

                                            for(var27 = 0; var27 < df.cmdlen; ++var27) {
                                                if(df.cmdClasseAndVersion[var27][0] == buf[7]) {
                                                    df.cmdClasseAndVersion[var27][1] = buf[8];
                                                    break;
                                                }
                                            }

                                            if(var27 != df.cmdlen - 1) {
                                                var35 = new byte[]{(byte)-122, (byte)19, df.cmdClasseAndVersion[var27 + 1][0]};
                                                Controller.this.sndCmdClass(var31, var35, 3, Controller.this.seqNo());
                                            } else if(Controller.this.sndDataMonitor.Action == 1) {
                                                ;
                                            }
                                        } else if(buf[6] == 18) {
                                            df.SdkVersion[0] = buf[8];
                                            df.SdkVersion[1] = buf[9];
                                            df.FirmwareVersion[0] = buf[10];
                                            df.FirmwareVersion[1] = buf[11];
                                            if(Controller.this.sndDataMonitor.Action == 1) {
                                                var35 = new byte[]{(byte)114, (byte)4};
                                                Controller.this.sndCmdClass(var31, var35, 2, Controller.this.seqNo());
                                            }
											Log.d("karl", "---------------send device version report--------------");
                                            Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(Events.DEVICE_VERSION_REPORT, 0, var31, buf[8] +"." + buf[9], buf[10] +"."+ buf[11]));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 65:
                    if(($str[0].equals("01") && $str[1].equals("09") && $str[3].equals("41") && $str[7].equals("04") || $str[0].equals("01") && $str[1].equals("09") && $str[3].equals("41") && $str[7].equals("01")) && Controller.this.sndDataMonitor.Action == 1) {
                        DeviceInfo var29 = Controller.this.GetNodeByNodeid(Controller.this.sndDataMonitor.NodeId);
                        if(var29 != null) {
                            var29.capability = buf[2];
                            var29.security = buf[3];
                            var29.reserved = buf[4];
                            var29.basic = buf[5];
                            var29.generic = buf[6];
                            var29.specific = buf[7];
                            byte[] var32 = new byte[]{(byte)1, (byte)11, (byte)1};
                            Controller.this.sndCmdClass(Controller.this.sndDataMonitor.NodeId, var32, 3, Controller.this.seqNo());
                            byte[] b = new byte[]{(byte)-122, (byte)17};
                            if(Controller.this.sndDataMonitor.NodeId != 0) {
                                Controller.this.sndCmdClass(Controller.this.sndDataMonitor.NodeId, b, 2, Controller.this.seqNo());
                            }
                        }
                    }
                    break;
                case 73:
                    byte var28 = buf[4];
					synchronized(nodeList) {
						if(Controller.this.nodeList != null && (buf[2] & 255) == 132) {
							Iterator NodeId = Controller.this.nodeList.iterator();

label731:
							while(true) {
								DeviceInfo i;
								do {
									if(!NodeId.hasNext()) {
										Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(7, 7, 0, "", ""));
										break label731;
									}

									i = (DeviceInfo)NodeId.next();
								} while(i.nodeId != buf[3]);

								if(i.cmdClasseAndVersion == null && var28 >= 3) {
									i.cmdClasseAndVersion = new byte[var28 - 3][2];
								}

								for(var27 = 8; var27 < var28 - 3; ++var27) {
									i.cmdClasseAndVersion[var27 - 8][0] = buf[var27];
								}
							}
						}
					}
                    break;
                case 74:
					Log.d("karl", "get 4a, start inclusion callback buf[3] = " + buf[3]);
					if (buf[3] == 0x01) {
						Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(6, 7, 0, "", ""));
						Controller.this.mtimeHandler.removeMessages(1);
						Controller.this.mtimeHandler1.removeMessages(2);
						Controller.this.INCLUDEMARK = 1;
						Controller.this.resetTime();
					} else {
						try {
							this.$deal(buf, length);
						} catch (Exception var24) {
							;
						}

						Controller.this.mtimeHandler.removeMessages(1);
						Controller.this.mtimeHandler1.removeMessages(2);
						Controller.this.INCLUDEMARK = 0;
					}
                    break;
                case 75:
					Log.d("karl", "get 4b, start exclusion callback, buf[3] = " + buf[3]);
					if (buf[3] == 0x01) {
						Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(10, 11, 0, "", ""));
						Controller.this.mtimeHandler.removeMessages(1);
						Controller.this.mtimeHandler1.removeMessages(2);
						Controller.this.EXECUTEMARK = 1;
						Controller.this.resetTime1();
					} else if (buf[3] == 0x06) {
						
					} else {
						this.$dealE(buf, length);
						Controller.this.mtimeHandler.removeMessages(1);
						Controller.this.mtimeHandler1.removeMessages(2);
						Controller.this.EXECUTEMARK = 0;
					}
					break;
					/*
				case 0x56:
						zw_set_suc_nodeid(buf[2]);
						break;
						*/
                }


                for(var27 = 0; var27 < 256; ++var27) {
                    buf[var27] = 0;
                }
            }
			Log.d("karl", "DealRevDataThread exit!!!\n");
        }

        public synchronized void $GTE(int NodeId) {
            byte length = 4;
            byte[] cmd = new byte[length];
            cmd[0] = 49;
            cmd[1] = 4;
            cmd[2] = 1;
            cmd[3] = 0;
            Controller.this.sndCmdClass(NodeId, cmd, length, Controller.this.seqNo());
        }

        private void setAsociation(int nodeId) {
            byte length = 4;
            byte[] cmd = new byte[length];
            cmd[0] = -123;
            cmd[1] = 1;
            cmd[2] = 2;
            cmd[3] = 1;
            Controller.this.sndCmdClass(nodeId, cmd, length, Controller.this.seqNo());
        }

        public synchronized void $HUM(int NodeId) {
            byte length = 4;
            byte[] cmd = new byte[length];
            cmd[0] = 49;
            cmd[1] = 4;
            cmd[2] = 5;
            cmd[3] = 0;
            Controller.this.sndCmdClass(NodeId, cmd, length, Controller.this.seqNo());
        }

        public synchronized void $CV(int NodeId) {
            byte length = 4;
            byte[] cmd = new byte[length];
            cmd[0] = 49;
            cmd[1] = 4;
            cmd[2] = 17;
            cmd[3] = 0;
            Controller.this.sndCmdClass(NodeId, cmd, length, Controller.this.seqNo());
        }

        private void $dealE(byte[] buf, int len) {
            boolean nodeId = false;
            Iterator var4;
            DeviceInfo item;
            byte nodeId1;
            switch(buf[3]) {
            case 2:
            case 5:
            case 6:
            default:
                break;
            case 3:
                nodeId1 = buf[4];
				synchronized(Controller.this.nodeList) {
					var4 = Controller.this.nodeList.iterator();

					while(var4.hasNext()) {
						item = (DeviceInfo)var4.next();
						if(item.nodeId == nodeId1) {
							Controller.this.nodeList.remove(item);
							break;
						}
					}
				}

                nodeId1 = buf[4];
				Controller.this.stopExclusionInternal();
                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(10, 13, nodeId1, "", ""));
                break;
            case 4:
                nodeId1 = buf[4];
				synchronized(Controller.this.nodeList) {
					var4 = Controller.this.nodeList.iterator();

					while(var4.hasNext()) {
						item = (DeviceInfo)var4.next();
						if(item.nodeId == nodeId1) {
							Controller.this.nodeList.remove(item);
							break;
						}
					}
				}

                nodeId1 = buf[4];
				Controller.this.stopExclusionInternal();
                Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(10, 13, nodeId1, "", ""));
            }

        }

        public synchronized void $PMV(int nodeId) {
            byte length = 4;
            byte[] cmd = new byte[length];
            cmd[0] = 49;
            cmd[1] = 4;
            cmd[2] = 35;
            cmd[3] = 0;
            Controller.this.sndCmdClass(nodeId, cmd, length, Controller.this.seqNo());
        }

        public int byteArrayToInt(byte[] b) {
            byte[] a = new byte[2];
            int i = a.length - 1;

            for(int j = b.length - 1; i >= 0; --j) {
                if(j >= 0) {
                    a[i] = b[j];
                } else {
                    a[i] = 0;
                }

                --i;
            }

            int v0 = (a[0] & 255) << 8;
            int v1 = a[1] & 255;
            return v0 + v1;
        }

        private void $deal(byte[] buf, int len) {
            boolean loop = false;
            boolean nodeId = false;
			boolean security = false;
            int var10;
            switch(buf[3]) {
            case 3:
                DeviceInfo node = new DeviceInfo();
                node.nodeId = buf[4];
                Log.e("current nodeId", buf[4] + "");
                node.basic = buf[6];
                node.generic = buf[7];
                node.specific = buf[8];
                if(buf[5] >= 3) {
                    node.cmdlen = (byte)(buf[5] - 3);
                }

                node.cmdClasseAndVersion = new byte[node.cmdlen][2];

                for(var10 = 9; var10 < 9 + node.cmdlen; ++var10) {
                    node.cmdClasseAndVersion[var10 - 9][0] = buf[var10];
					if (buf[var10] == (byte)0x98) {
						security=true;
						Controller.this.IsFinished = false;
						Controller.this.IsFinished1 = false;
					}
                }

				if (GetNodeByNodeid(node.nodeId) == null)  {
					synchronized(Controller.this.nodeList) {
						Controller.this.nodeList.add(node);
					}
				}
                break;
            case 4:
                DeviceInfo $node = new DeviceInfo();
                $node.nodeId = buf[4];
                Log.e("current nodeId", buf[4] + "");
                $node.basic = buf[6];
                $node.generic = buf[7];
                $node.specific = buf[8];
                if(buf[5] >= 3) {
                    $node.cmdlen = (byte)(buf[5] - 3);
                }

                $node.cmdClasseAndVersion = new byte[$node.cmdlen][2];

                for(var10 = 9; var10 < 9 + $node.cmdlen; ++var10) {
                    $node.cmdClasseAndVersion[var10 - 9][0] = buf[var10];
					if (buf[var10] == (byte)0x98) {
						security = true;
						Controller.this.IsFinished = false;
						Controller.this.IsFinished1 = false;
					}
                }

				if (GetNodeByNodeid($node.nodeId) == null) {
					synchronized(Controller.this.nodeList) {
					Controller.this.nodeList.add($node);
					}
				}
                break;
            case 5:
                byte var11 = buf[4];
                Controller.this.sndDataMonitor.Action = 1;
                Controller.this.sndDataMonitor.NodeId = (byte)var11;
                DeviceInfo df = Controller.this.GetNodeByNodeid(var11);
                if(df != null) {
                    df.revDataTime = System.currentTimeMillis(); //Controller.this.getCurrentTime();
                    df.isOnline = SdkConstants.DEVICE_ONLINE;
					df.offlinetimes = 0;
					Controller.this.SendIfOnlineMsg(df.nodeId, true);

					Log.d("karl", "Inclusion process success, now send stop inclusion command");

					Controller.this.stopInclusionInternal();

					Log.d("karlonline", "deviced[" + df.nodeId + "], added and send online to gateway");
                    byte checkSum = -1;
                    byte[] cmd = new byte[]{(byte)1, (byte)4, (byte)0, (byte)65, Controller.this.sndDataMonitor.NodeId, (byte)0};

                    for(var10 = 0; var10 < 4; ++var10) {
                        checkSum ^= cmd[var10 + 1];
                    }

                    cmd[5] = checkSum;
                    Controller.this.sndUartCmd(cmd, 6);
                }
            }

        }

        public void stopThread() {
	//		this.stop();
            this._run = false;
			this.interrupt();
			Log.d("karl", "Send DealRevDataThread exit ..");
			try {
			Thread.sleep(120);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	private void setAsociation(int nodeId){
		int length=4;
		byte[] cmd=new byte[length];
		cmd[0]=(byte)0x85;
		cmd[1]=0x01;
		cmd[2]=0x02;
		cmd[3]=0x01;
		sndCmdClass(nodeId,cmd,length,seqNo());
	}

    public interface SendMessageToGatewayListener {
        void sendMessageToGateway(Message var1);
    }

	public class CheckDevIfOnlineThread extends Thread {
		private boolean _run=true;
		private long timewait = ONLINE_CHECK_WAITTIME;		//first run by one minute
		public CheckDevIfOnlineThread(){
			
		}
		@Override
		public void run(){
			while(_run){
				try {                         
					Log.e("karlonline", "online check timewait" + timewait/1000 + "sec");
					Thread.sleep(timewait);     //每隔5分钟检测一次
					//Thread.sleep(1000*10*5);        //每隔10秒检测一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				timewait = CheckSleepDeviceIfOnline();     //假若发现离线的睡眠类设备，则将消息返回至handler中
			}
		}
		
		public void StopThead(){
			this._run=false;
		}
	}
	public void getData(byte[] ManufacturerId,byte[] ProductType,byte[] ProductId,int NodeId){
			if(ManufacturerId[0]==1 && ManufacturerId[1]==95&& ProductType[0]==9&& ProductType[1]==5&& ProductId[0]==2&& ProductId[1]==1 ) {
	            getCO2(NodeId);
	            getTemperature(NodeId);
	            getHumidity(NodeId);
         }
	else if(ManufacturerId[0]==1 && ManufacturerId[1]==95&& ProductType[0]==10&& ProductType[1]==5&&ProductId[0]==2&& ProductId[1]==1){
	           System.out.println("PM2.5 get here");
	           getPM(NodeId);
	           getTemperature(NodeId);
				getHumidity(NodeId);

}
	}



	private String getCurrentTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String strTime=formatter.format(curDate);
		return strTime;
	}
	
	private String byte2HexStrNonSpace(byte[] b, int length)
	{    
	    String stmp="";
	    StringBuilder sb = new StringBuilder("");
	    for (int n=0;n<length;n++)    
	    {    
	        stmp = Integer.toHexString(b[n] & 0xFF);
	        sb.append((stmp.length()==1)? "0"+stmp : stmp);
	    }    
	    return sb.toString().toUpperCase().trim();  
	}

	/*
     * 功能描述:将十六进制数据转换成字符串，便于测试
     * 参数描述:
     *     b:要转换的十六进制数组
     *     length:b的有效数据的长度
     * 返回值描述：
     *     返回一个表现形式为十六进制的字符串
     */
	private String byte2HexStr(byte[] b, int length)
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

	private String byte2HexStr1(byte[] b, int length)
	{
		String stmp="";
		StringBuilder sb = new StringBuilder("");
		for (int n=0;n<length;n++)
		{
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length()==1)? "0"+stmp : stmp);
			if(n!=length)
			sb.append("_");
		}
		return sb.toString().toUpperCase().trim();
	}

    public Message encapsulateMessage(int what, int type, int nodeId, String value, String keepField) {
        Message message = this.mHandler.obtainMessage();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("nodeId", nodeId);
        bundle.putString("value", value);
        bundle.putString("keepField", keepField);
        message.setData(bundle);
        return message;
    }

    public String getRandomCharString(int length){
		char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e'};
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(chr[random.nextInt(15)]
			);
		}
		return buffer.toString();
	}

	public  byte[] getRandomChar(int length) {
		char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e'};
		byte[] randomBytes = new byte[8];
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(chr[random.nextInt(15)]
			);
			randomBytes[i] =(byte)chr[random.nextInt(15)];
		}
		return randomBytes;
	}

	public  byte[] encrypt(byte[] key,byte[] text,byte[] iv) throws Exception {
		SecretKeySpec ky2 = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE,  ky2, ivSpec);
		byte [] b = cipher.doFinal(text);
		return b;
	}
	private  byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public  byte[] EcbEncrypt(byte[] key1,byte[] test1) throws Exception {
		SecretKeySpec key = new SecretKeySpec(key1, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		byte[] b = cipher.doFinal(test1);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append("--"+hex.toUpperCase());
		}
		return b;
	}

	public byte[] xor(byte[] num1,byte[] num2){
		byte[] rr = new byte[16];
		for(int i=0;i<16;i++){
			byte bb = (byte) ((num1[i])^(num2[i]));
			rr[i] = bb;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rr.length; i++) {
			String hex = Integer.toHexString(rr[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append("--"+hex.toUpperCase());
		}
        return rr;
	}



	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		_b0 = (byte)(_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		byte ret = (byte)(_b0 ^ _b1);
		return ret;
	}

	public static byte[] HexString2Bytes(String src){
		byte[] ret = new byte[src.length()/2];
		byte[] tmp = src.getBytes();
		for(int i=0; i<src.length()/2; i++){
			ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
		}
		return ret;
	}

    private class EffectInVisiableHandler1 extends Handler {
        private EffectInVisiableHandler1() {
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
            case 2:
                if(Controller.this.EXECUTEMARK == 1) {
                    Controller.this.stopExclusion();
                    Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(10, 14, 0, "", ""));
                }
            default:
            }
        }
    }

    private class EffectInVisiableHandler extends Handler {
        private EffectInVisiableHandler() {
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
            case 1:
                if(Controller.this.INCLUDEMARK == 1) {
                    Controller.this.stopInclusion();
                    Controller.this.sendBroadcastToGateway(Controller.this.encapsulateMessage(6, 9, 0, "", ""));
                }
            default:
            }
        }
    }

}
