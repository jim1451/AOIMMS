package com.cmcid.util;

import android.util.Log;

import com.cmcid.TApplication;

/**
 * 统一处理日志
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
