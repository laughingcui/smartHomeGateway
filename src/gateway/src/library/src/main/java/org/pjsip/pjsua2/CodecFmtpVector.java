/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class CodecFmtpVector {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CodecFmtpVector(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CodecFmtpVector obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_CodecFmtpVector(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CodecFmtpVector() {
    this(pjsua2JNI.new_CodecFmtpVector__SWIG_0(), true);
  }

  public CodecFmtpVector(long n) {
    this(pjsua2JNI.new_CodecFmtpVector__SWIG_1(n), true);
  }

  public long size() {
    return pjsua2JNI.CodecFmtpVector_size(swigCPtr, this);
  }

  public long capacity() {
    return pjsua2JNI.CodecFmtpVector_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    pjsua2JNI.CodecFmtpVector_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return pjsua2JNI.CodecFmtpVector_isEmpty(swigCPtr, this);
  }

  public void clear() {
    pjsua2JNI.CodecFmtpVector_clear(swigCPtr, this);
  }

  public void add(CodecFmtp x) {
    pjsua2JNI.CodecFmtpVector_add(swigCPtr, this, CodecFmtp.getCPtr(x), x);
  }

  public CodecFmtp get(int i) {
    return new CodecFmtp(pjsua2JNI.CodecFmtpVector_get(swigCPtr, this, i), false);
  }

  public void set(int i, CodecFmtp val) {
    pjsua2JNI.CodecFmtpVector_set(swigCPtr, this, i, CodecFmtp.getCPtr(val), val);
  }

}
