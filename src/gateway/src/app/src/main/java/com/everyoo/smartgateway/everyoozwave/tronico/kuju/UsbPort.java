package com.everyoo.smartgateway.everyoozwave.tronico.kuju;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class UsbPort {
	private final Context mApplicationContext;
	private final UsbManager mUsbManager;
	//private final IUsbConnectionHandler mConnectionHandler;
	protected static final String ACTION_USB_PERMISSION = "com.zj.usbconn.USB";
	private final Handler mHandler;
	public static final int INITIALIZE_SUCCESS = 0;
	public static final int INITIALIZE_FAIL = 1;
	public static final int USB_DONGLE_ATTACHED = 2;
	public static final int USB_DONGLE_DETACHED = 3;

	private UsbEndpoint ep = null;
	private UsbEndpoint epout = null;
	private UsbInterface usbIf = null;
	private UsbDeviceConnection conn = null;
	private int isRegisterReceiverFlag=0;

	public UsbPort(Context parentActivity, Handler handler)
	{
		mApplicationContext = parentActivity;
		mUsbManager = (UsbManager) mApplicationContext
				.getSystemService(Context.USB_SERVICE);
		mHandler = handler;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        mApplicationContext.registerReceiver(mPermissionReceiver, filter);
        isRegisterReceiverFlag=1;
	}


	public synchronized UsbDevice getDev(int vid, int pid){
		UsbDevice dev = null;
		ep = null;
		usbIf = null;
		conn = null;
		HashMap<String, UsbDevice> devlist = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviter = devlist.values().iterator();
		while (deviter.hasNext()) {
			UsbDevice d = deviter.next();
			Log.d("usb device:","" + d.getDeviceName()+"  "
					+ String.format("%04X:%04X", d.getVendorId(),
							d.getProductId()));
			if( d.getVendorId() == vid  && d.getProductId() == pid ){
				dev = d;
				break;
			}
		}
		return  dev;
	}


	public  synchronized HashMap<String, UsbDevice> getUsbList()
	{
		return mUsbManager.getDeviceList();
	}

	public synchronized boolean isHasPermission(UsbDevice dev){
		return  mUsbManager.hasPermission(dev);
	}


	public synchronized void getPermission(UsbDevice dev){
		Log.d("karl", "now start to getPermission " + dev);
		if(dev == null )
			return;
		if(!isHasPermission(dev)){
			Log.d("karl", "Usb device no permission get, here requestPermission");
			PendingIntent pi = PendingIntent.getBroadcast(mApplicationContext,0,new Intent(
					ACTION_USB_PERMISSION),0);
			mUsbManager.requestPermission(dev, pi);
		}else{
			Log.d("karl", "Usb device permmisson ok, then direct notify it sucess");
			Message msg = mHandler.obtainMessage(INITIALIZE_SUCCESS);
			mHandler.sendMessage(msg);
		}
	}

	public synchronized void sendMsg(String msg, String charset, UsbDevice dev ){
		if( msg.length() == 0)
			return;
		byte[] send;
		try{
        	send = msg.getBytes(charset);
        }
        catch(UnsupportedEncodingException e)
        {
        	send = msg.getBytes();
        }
		this.sendByte(send, dev,send.length);
		this.sendByte(new byte[]{0x0D,0x0A,0x00},dev,3);
	}


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


	public int sendByte(byte[] bits, UsbDevice dev, int length){
		try {
			String str1 = byte2HexStr(bits, length);
//		StringBuilder sb=new StringBuilder(str1);
//		for (byte element: bits )
//		{
//			sb.append(String.valueOf(element));
//		}
//		str1=sb.toString();

			System.out.println("usb send data is " + str1 + "  and length is" + length);

			int ret = 0;
			if (bits == null)
				return -1;
			if (ep != null && usbIf != null && conn != null && epout != null) {
				ret = conn.bulkTransfer(ep, bits, length, 0);
			} else {
				if (conn == null)
					conn = mUsbManager.openDevice(dev);
				if (dev.getInterfaceCount() == 0) {
					return -1;
				}
				if (usbIf == null)
					usbIf = dev.getInterface(1);
				if (usbIf.getEndpointCount() == 0) {
					return -1;
				}

				for (int i = 0; i < usbIf.getEndpointCount(); i++) {
					if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
						if (usbIf.getEndpoint(i).getDirection() != UsbConstants.USB_DIR_IN)
							ep = usbIf.getEndpoint(i);
						else
							epout = usbIf.getEndpoint(i);
					}
				}

				if (conn.claimInterface(usbIf, true)) {
					ret = conn.bulkTransfer(ep, bits, length, 0);
				}
			}
			return ret;
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;

	}


	public int revByteArr(UsbDevice dev, byte[] bits, int length, int timeout)
	{
		int ret = 0;
        if( epout != null && usbIf != null && conn != null && epout != null ){
			ret = conn.bulkTransfer(epout, bits, length, timeout);
		}else{
			if( conn == null )
				conn = mUsbManager.openDevice(dev);
			if (dev.getInterfaceCount() == 0)  {
				return -1;
			}
			if(usbIf == null)
			    usbIf = dev.getInterface(1);
	        if (usbIf.getEndpointCount() == 0) {
	            return -1;
	        }

			for (int i = 0; i < usbIf.getEndpointCount(); i++) {
				if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
					if (usbIf.getEndpoint(i).getDirection() != UsbConstants.USB_DIR_IN)
						ep = usbIf.getEndpoint(i);
					else
						epout = usbIf.getEndpoint(i);
				}
			}

	        if( conn.claimInterface(usbIf, true) ){
				if (epout != null) {
					ret = conn.bulkTransfer(epout, bits, length, timeout);
				}
	        }
		}
        return ret;
	}

	public synchronized void close(boolean isUnregisterReceiver){
		if(isRegisterReceiverFlag==1 && isUnregisterReceiver==true){
		    mApplicationContext.unregisterReceiver(mPermissionReceiver);
		    isRegisterReceiverFlag=0;
		}
		if( conn != null ){
			Log.d("karl", "----usb real closed called---------------");
			conn.close();
			//ep = null;
			//epout = null;
			//usbIf = null;
			//conn = null;
		}
	}

	private final BroadcastReceiver mPermissionReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			//mApplicationContext.unregisterReceiver(this);
			Message msg = null;
			UsbDevice d = null;
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
	        	d = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
	    		for(int loop = 0; loop< SdkConstants.DongleInforLen; loop++){
	    			if(SdkConstants.DongleInfor[loop][0]==d.getVendorId() && SdkConstants.DongleInfor[loop][1]==d.getProductId()){
	    				msg = mHandler.obtainMessage(USB_DONGLE_ATTACHED);
	    				mHandler.sendMessage(msg);
	    				break;
	    			}
	    		}
	        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
	        	d = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
	    		for(int loop = 0; loop< SdkConstants.DongleInforLen; loop++){
	    			if(SdkConstants.DongleInfor[loop][0]==d.getVendorId() && SdkConstants.DongleInfor[loop][1]==d.getProductId()){
	    	        	msg = mHandler.obtainMessage(USB_DONGLE_DETACHED);
	    				mHandler.sendMessage(msg);
	    				break;
	    			}
	    		}
	        }

			if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
					UsbDevice dev = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					Log.d("karl","usb permission broadcast granted dev=" + dev);
					if(dev != null){
						Log.d("karl", "send INITIALIZE_SUCCESS to controoler\n");
						msg = mHandler.obtainMessage(INITIALIZE_SUCCESS);
						mHandler.sendMessage(msg);
					}
				}
			}

		}
	};

}




