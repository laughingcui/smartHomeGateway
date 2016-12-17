/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class ConfPortInfo {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ConfPortInfo(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ConfPortInfo obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_ConfPortInfo(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setPortId(int value) {
    pjsua2JNI.ConfPortInfo_portId_set(swigCPtr, this, value);
  }

  public int getPortId() {
    return pjsua2JNI.ConfPortInfo_portId_get(swigCPtr, this);
  }

  public void setName(String value) {
    pjsua2JNI.ConfPortInfo_name_set(swigCPtr, this, value);
  }

  public String getName() {
    return pjsua2JNI.ConfPortInfo_name_get(swigCPtr, this);
  }

  public void setFormat(MediaFormatAudio value) {
    pjsua2JNI.ConfPortInfo_format_set(swigCPtr, this, MediaFormatAudio.getCPtr(value), value);
  }

  public MediaFormatAudio getFormat() {
    long cPtr = pjsua2JNI.ConfPortInfo_format_get(swigCPtr, this);
    return (cPtr == 0) ? null : new MediaFormatAudio(cPtr, false);
  }

  public void setTxLevelAdj(float value) {
    pjsua2JNI.ConfPortInfo_txLevelAdj_set(swigCPtr, this, value);
  }

  public float getTxLevelAdj() {
    return pjsua2JNI.ConfPortInfo_txLevelAdj_get(swigCPtr, this);
  }

  public void setRxLevelAdj(float value) {
    pjsua2JNI.ConfPortInfo_rxLevelAdj_set(swigCPtr, this, value);
  }

  public float getRxLevelAdj() {
    return pjsua2JNI.ConfPortInfo_rxLevelAdj_get(swigCPtr, this);
  }

  public void setListeners(IntVector value) {
    pjsua2JNI.ConfPortInfo_listeners_set(swigCPtr, this, IntVector.getCPtr(value), value);
  }

  public IntVector getListeners() {
    long cPtr = pjsua2JNI.ConfPortInfo_listeners_get(swigCPtr, this);
    return (cPtr == 0) ? null : new IntVector(cPtr, false);
  }

  public ConfPortInfo() {
    this(pjsua2JNI.new_ConfPortInfo(), true);
  }

}
