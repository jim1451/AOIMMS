package com.cmcid.util;

import android.app.Activity;
import android.app.ProgressDialog;

public class Tools {
	private static ProgressDialog progressDialog;
	//显示进度条
	public static void showProgressDialog(Activity activity,String message){
		if(progressDialog==null){
			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage(message);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}
	}
	//关闭进度条
	public static void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.cancel();
			progressDialog = null;
			System.gc();
		}
	}
}
