package com.guide.lunar;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class Utility {

	// 计算两个日期之间的分钟数
	static public long getMinuteBetween (Date d1, Date d2) {
		
		int r = d1.compareTo(d2);
		long from;
		long to;
		
		if (0 == r){
			return 0;
		}
		else if (r < 0) {
			from = d1.getTime();
			to = d2.getTime();
		} else {
			from = d2.getTime();
			to = d1.getTime();
		}
		
		return (to - from) / (1000 * 60);
	}
	
	// 计算两个日期之间的天数
	static public long getDayBetween (Date d1, Date d2) {
		
		int r = d1.compareTo(d2);
		long from;
		long to;
		
		if (0 == r){
			return 0;
		}
		else if (r < 0) {
			from = d1.getTime();
			to = d2.getTime();
		} else {
			from = d2.getTime();
			to = d1.getTime();
		}
		
		return (to - from) / (1000 * 60 * 60 * 24);
	}

	// "yyyy-MM-dd"
	static public Date Str2Date (String s, String fmt) {
		try {
			return new SimpleDateFormat (fmt).parse(s);
		} catch (ParseException e) {
		} catch (Exception e) {
			
		}
		
		return null;		
	}
	
	// "yyyy-MM-dd HH:mm:ss"
	static public String Date2Str (Date d, String fmt) {
		try {
			return new SimpleDateFormat (fmt).format(d);
		} catch (Exception e) {
			
		}
		
		return null;
	}

	// 网络状态，三种
	// 0: 无网络
	// 1:WIFI
	// 2:2G/3G
	static public int checkNetworkAvailable (Context paramContext) {
		ConnectivityManager mConnectivity = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
		//检查网络连接，如果无网络可用，就不需要进行连网操作等  
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !info.isAvailable()) {
			return 0;
		}

		//判断网络连接类型，只有在3G或wifi里进行一些数据更新。  
		int netType = info.getType();
		//int netSubtype = info.getSubtype();

		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected() ? 1 : 0;
		} else if (netType == ConnectivityManager.TYPE_MOBILE ) {
					//&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
					//&& !mTelephony.isNetworkRoaming()) {
			return info.isConnected() ? 2 : 0;
		} else {
			return 0;
		}
	}
	
	static public void openWifiSettings (final Context paramContext) {
        Intent mIntent = new Intent("/");
        ComponentName comp = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        mIntent.setComponent(comp);
        mIntent.setAction("android.intent.action.VIEW");
        paramContext.startActivity(mIntent);
	}
	
	
	static public void showAlertDialog(final Context paramContext, String title, String msg, 
			String btnOk, String btnCancel,
			DialogInterface.OnClickListener okClick,
			DialogInterface.OnClickListener cancelClick)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(paramContext);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnOk, okClick);
        builder.setNegativeButton(btnCancel, cancelClick);
        builder.create();
        builder.show();
    }
    
}
