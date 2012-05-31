package com.guide.lunar;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class Utility {

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
	
	
	static public void setNetwork(final Context paramContext, String title, String msg, String btnOk, String btnCancel)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(paramContext);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnOk, new DialogInterface.OnClickListener() {
            
            // @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent("/");
                ComponentName comp = new ComponentName("com.android.settings",
                        "com.android.settings.WirelessSettings");
                mIntent.setComponent(comp);
                mIntent.setAction("android.intent.action.VIEW");
                paramContext.startActivity(mIntent);
            }
        });
        builder.setNegativeButton(btnCancel, new DialogInterface.OnClickListener() {
            
            // @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }
    
}
