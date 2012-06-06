package com.guide.lunar;

import com.guide.lunar.ViolationManager;

import java.util.HashMap;
import android.os.Parcel;
import android.os.Parcelable;  

public class ViolationResult {//implements Parcelable {

	public static final int ERROR_START = -2;
	public static final int ERROR_UNKNOWN = -1;
	public static final int ERROR_OK = 0;
	public static final int ERROR_NET = 1;      // 网络错误
	public static final int ERROR_DATA = 2;     // 车辆数据输入错误
	public static final int ERROR_PARSE = 3;    // 返回信息解析错误
	public static final int ERROR_LAST = 4;
	
	private int errorType = ERROR_UNKNOWN;
	private ViolationManager vLocalViolation;
	private ViolationManager vNonlocalViolation;
	
	public ViolationResult (int eType) {
		setErrorType (eType);
	}

	public ViolationResult (int eType, ViolationManager local, ViolationManager nonlocal) {
		setErrorType (eType);
		vLocalViolation = local;
		vNonlocalViolation = nonlocal;
	}
	
	
	public int getErrorType () {
		return errorType;
	}
	
	public void setErrorType (int eType) {
		errorType = (eType <= ERROR_START || eType >= ERROR_LAST) ? ERROR_UNKNOWN : eType;
	}
	
	public ViolationManager localViolation () {
		return vLocalViolation;
	}

	public ViolationManager nonlocalViolation () {
		return vNonlocalViolation;
	}
	
/*	// implements Parcelable interfaces
	
	//@Override
	public int describeContents () {
		return 0;
	}
	
	//@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(errorType);
		//map.put("local", vLocalViolation);
		//map.put("nonlocal", vNonlocalViolation);
		dest.writeMap(map);
	}

    public static final Parcelable.Creator<ViolationResult> CREATOR = new Parcelable.Creator<ViolationResult>() {  
    	  
        //@Override  
        public ViolationResult createFromParcel(Parcel source) {  
            // TODO Auto-generated method stub  
        	ViolationResult vr = new ViolationResult(ViolationResult.ERROR_UNKNOWN);  
            //vr.map = source.readHashMap(HashMap.class.getClassLoader());
        	vr.errorType = source.readInt();
        	//vr.vLocalViolation = (ViolationManager)source.readValue(ViolationManager.class.getClassLoader());
        	//vr.vNonlocalViolation = (ViolationManager)source.readValue(ViolationManager.class.getClassLoader());
        	
        	HashMap <String, Object> nMap = source.readHashMap(HashMap.class.getClassLoader()); 
        	vr.vLocalViolation = (ViolationManager)nMap.get ("local");
        	vr.vNonlocalViolation = (ViolationManager)nMap.get ("nonlocal"); 
            return vr;  
        }  
  
        //@Override  
        public ViolationResult[] newArray(int size) {  
            // TODO Auto-generated method stub  
            return null;  
        }  
    };*/  
}
