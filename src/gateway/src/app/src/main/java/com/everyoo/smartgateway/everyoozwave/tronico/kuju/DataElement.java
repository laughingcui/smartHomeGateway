package com.everyoo.smartgateway.everyoozwave.tronico.kuju;
public class DataElement {
	public byte[] buf=null;
	public int length;
    // ���캯����ʼ��buf�����Լ�length����
	public DataElement(){
		//new update
		// buf=new byte[128];
		buf=new byte[256];
		length=0;;
	}
}


