package com.cmcid.util;

import android.util.Log;

import com.cmcid.TApplication;

/**
 * ͳһ������־
 * @author Administrator
 */
public class LogUtil{
	public static void i(String tag,Object msg){
		if(TApplication.isRelease){
			return;
		}else{
			Log.i(tag, String.valueOf(msg));
		}
	}
}
