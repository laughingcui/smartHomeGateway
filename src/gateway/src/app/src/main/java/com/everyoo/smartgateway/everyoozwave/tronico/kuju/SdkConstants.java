package com.everyoo.smartgateway.everyoozwave.tronico.kuju;

public class SdkConstants{

	public static final int UNKNOWN_STATE = 0x00;
	public static final int DEVICE_ONLINE = 0x01;
	public static final int DEVICE_NOTONLINE = 0x02;
	public static final int NORMAL_ACTION = 0x00;
	public static final int INCLUSION_ACTION = 0x01;
	public static final int EXCLUSION_ACTION = 0x02;
	public static final int DongleInforLen = 0x03;
	public static final int[][] DongleInfor={{0x0658,0x0200},{0x2A93,0x0302},{0x2A93,0x0311}};
}
