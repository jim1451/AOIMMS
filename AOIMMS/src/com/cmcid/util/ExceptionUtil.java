package com.cmcid.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.cmcid.TApplication;
/**
 * 统一处理异常信息
 */
public class ExceptionUtil {
	public static void handleException(Exception e){
		if(TApplication.isRelease){
			
		}else{
			//把异常信息变成字符串
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			
			String string = stringWriter.toString();
			LogUtil.i("异常信息", string);
			e.printStackTrace();
		}
	}
}
