package com.cmcid.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.cmcid.TApplication;
/**
 * ͳһ�����쳣��Ϣ
 */
public class ExceptionUtil {
	public static void handleException(Exception e){
		if(TApplication.isRelease){
			
		}else{
			//���쳣��Ϣ����ַ���
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			
			String string = stringWriter.toString();
			LogUtil.i("�쳣��Ϣ", string);
			e.printStackTrace();
		}
	}
}
