package com.everyoo.smartgateway.everyoozwave.tronico.kuju;
public class QueueManager {
	private int MAX_SIZE=0;
	private int front = 0;
	private int rear = 0;
	private DataElement[] de = null;
	public QueueManager(int _MAX_SIZE){
		MAX_SIZE = _MAX_SIZE;
		de = new DataElement[MAX_SIZE];
		for(int loop=0;loop < MAX_SIZE;loop++)
			de[loop] = new DataElement();
	}
    //??????
    public synchronized void clearQueue() {
    	this.front = 0;
    	this.rear = 0;
    }
    //????
    public boolean pushQueue(byte[] data,int len) {
    	if ((rear + 1) % MAX_SIZE == front) {
            return false;
        }
        for(int loop = 0;loop < len;loop++){
        	de[rear].buf[loop] = data[loop];
        }
        de[rear].length = len;
        rear = (rear + 1) % MAX_SIZE;
        return true;
    }
    //????
    public DataElement popQueue() {
        if(this.front == this.rear) {
            return null;
        }
        int i = front;
        front = (front+1)%MAX_SIZE;
        return de[i];
    }



    //?????????
    public boolean isQueueEmpty() {
        return (front == rear);
    }
    //?????????????
    public int getQueueNum() {
    	if(rear>front){
    		return rear - front;
    	}else if(rear<front){
    		return (MAX_SIZE - front)+rear;
    	}else{
    		return 0;
    	}
    }
    //???command id?????????????
    public DataElement getDataByCmdId(int start, byte cmdId) {
    	int loop = 0;
    	if(start > rear){
    		for(loop=0;loop<rear;loop++){
    			if(de[rear-loop-1].buf[3]==cmdId){
    				return de[rear-loop-1];
    			}
    		}
    		for(loop=start;loop<MAX_SIZE;loop++){
    			if(de[loop].buf[3]==cmdId){
    				return de[loop];
    			}
    		}
    	}else if(start < rear){
    		for(loop=front;loop<rear;loop++){
    			if(de[loop].buf[3]==cmdId){
    				return de[loop];
    			}
    		}
    	}else{
    	    return null;	
    	}
    	return null;
    }
    public int getFrontIndexOfQueue(){
    	return front;
    }
    public int getRearIndexOfQueue(){
    	return rear;
    }
    public synchronized DataElement getDataByIndex(int index){
    	int i = index % MAX_SIZE;
    	return de[i];
    }
}
