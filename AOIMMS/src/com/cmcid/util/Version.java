package com.cmcid.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.cmcid.TApplication;

/**
 * Application version related tools
 */
public class Version {

	/**
	 *Gets the version number
	 *
	 * @return
	 */
	public static String getVersionName()
	{
		Context context = TApplication.getAppContext();
	    PackageManager packageManager = context.getPackageManager();  
	    //GetPackageName () is your current class package name, 0 stands for is to get version information  
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		
			return packInfo.versionName;   
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * The App for current versionCode (from 1 started), if not find (NameNotFoundException), return zero
	 * @Title: getVersionCode
	 * @Description: TODO
	 * @return
	 */
	public static int getVersionCode()
	{
		Context context = TApplication.getAppContext();
	    PackageManager packageManager = context.getPackageManager();  
	    //GetPackageName () is your current class package name, 0 stands for is to get version information  
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			
			return packInfo.versionCode;   
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	
}
