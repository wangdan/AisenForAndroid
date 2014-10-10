package com.m.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.m.common.context.GlobalContext;

public class NetworkUtils {

	public static boolean signalAvailable = false;
	
	public static boolean isSignalAvailable() {
		return signalAvailable;
	}
	
	public static boolean isNetworkAvailable(){
		ConnectivityManager cm = (ConnectivityManager) GlobalContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean networkAvailable = false;
		if(cm.getActiveNetworkInfo() != null){
			networkAvailable = cm.getActiveNetworkInfo().isAvailable();
		}
		return networkAvailable;
	}
	
	public static String getIMEI() {
		TelephonyManager tm = (TelephonyManager) GlobalContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null)
			return tm.getDeviceId();
		
		return "";
	}
	
}
