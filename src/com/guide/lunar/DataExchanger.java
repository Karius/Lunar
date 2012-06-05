package com.guide.lunar;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class DataExchanger implements Parcelable {
	// 用来解决Activity之间传递对象问题的
	public HashMap<String, Object> datamap = new HashMap<String, Object>();
	
	public int count = 10;
	
	public DataExchanger () {
	}
	
	private DataExchanger (Parcel source) {
		datamap = source.readHashMap(HashMap.class.getClassLoader());
    	count = source.readInt ();
	}

	//@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeMap(datamap);
		dest.writeInt(count);
	}

    public static final Parcelable.Creator<DataExchanger> CREATOR = new Parcelable.Creator<DataExchanger>() {  
        //@Override  
        public DataExchanger createFromParcel(Parcel source) {  
            // TODO Auto-generated method stub  

        	return new DataExchanger (source);
        }  
  
        //@Override  
        public DataExchanger[] newArray(int size) {  
            // TODO Auto-generated method stub  
            return new DataExchanger[size];  
        }  
    };
    
}
