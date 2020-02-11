// IRegisterPicAidlInterface.aidl
package com.aiwinn.faceattendance;

// Declare any non-default types here with import statements
import com.aiwinn.faceattendance.IRegisterPicCallbackAidlInterface;
import android.content.Context;

interface IRegisterPicAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void setRegisterPicCallback(IRegisterPicCallbackAidlInterface mIRegisterPicCallback);
    void beginRunPicThread(in String [] mlistfilepath);

}
