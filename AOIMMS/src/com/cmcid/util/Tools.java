package com.cmcid.util;

import android.app.Activity;
import android.app.ProgressDialog;

public class Tools {
	private static ProgressDialog progressDialog;
	//��ʾ������
	public static void showProgressDialog(Activity activity,String message){
		if(progressDialog==null){
			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage(message);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}
	}
	//�رս�����
	public static void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.cancel();
			progressDialog = null;
			System.gc();
		}
	}
}
