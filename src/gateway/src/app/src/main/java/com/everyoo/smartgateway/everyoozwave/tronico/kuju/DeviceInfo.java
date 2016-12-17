package com.everyoo.smartgateway.everyoozwave.tronico.kuju;
public class DeviceInfo{
	public byte[] ManufacturerId=new byte[2];
	public byte[] ProductType=new byte[2];
	public byte[] ProductId=new byte[2];
	public byte CompmentCnt;
	public byte ComponentType1;
	public byte EndPoint1;
	public byte ComponentType2;
	public byte EndPoint2;
	public byte ComponentType3;
	public byte EndPoint3;
	public byte ComponentType4;
	public byte EndPoint4;
	public byte[] FirmwareVersion=new byte[2];
	public byte[] SdkVersion=new byte[2];
	public byte  nodeId;
	public byte basic;
	public byte generic;
	public byte specific;
	public byte  capability; 
	public byte  security;
	public byte  reserved;
	public byte  isOnline= SdkConstants.UNKNOWN_STATE;
	public byte offlinetimes=0;
	public byte[] InclusionDateTime=new byte[6]; 
	public byte cmdlen=0;                                      
	public byte[][] cmdClasseAndVersion;   //2*cmdlen	
	public long revDataTime;
	
//	public enum DEVSTATE {
//	     UNKNOWN_STATE,
//	     DEVICE_ONLINE,
//	     DEVICE_NOTONLINE,
//	}
}


