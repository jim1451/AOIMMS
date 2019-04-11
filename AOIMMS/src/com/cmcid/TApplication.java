package com.cmcid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.cmcid.biz.DeviceService;
import com.cmcid.biz.DeviceService.MyBinder;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.LogUtil;
import com.cmcid.util.Session;
import com.cmcid.util.logMa;
import com.cmcid.view.MainActivity;

public class TApplication extends Application{
	
	//false 正在开发中     true 产品已应用
	public static boolean isRelease = false;
	//连接数据库
	public static Connection connection;
	public static TApplication instance;
	private static TApplication myApplication;
	public static List<Activity> listActivity = new ArrayList<Activity>();
	//[com.cmcid.view.MainActivity@42b53748, com.cmcid.view.InbondActivity@42e6f850, 
	//com.cmcid.view.LoginActivity@42f33000, com.cmcid.view.InbondFormActivity@42fccf90, com.cmcid.view.OutbondActivity@43104fe8, 
	//com.cmcid.view.OutbondFormActivity@431371d0, com.cmcid.view.InbondActivity@43201398, com.cmcid.view.InbondFormActivity@432bd7c0]
	
	public static ArrayList<String> listrestore =  new ArrayList<String>();
	
	//url = jdbc:sqlserver://192.168.0.177:1433; DatabaseName=ptdb;
	//name = sa;
	//password = 123;
	//判断是否为第一次登录应用 false未登录 
	public static boolean isFistRun=false;
	public static String strUser = "";//login user name
	private String url,user,password;
	
	public DeviceService mService;
	Intent intent;
	Handler handler;
	public boolean bBind = false;
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
//			String CCDCID1 = "0300";
//			if (CCDCID1.contains("0300") ){
//				int ii;
//				ii = 0;
//			}
			System.out.println("----ServiceConnected!!----");
			logMa.d("martrin", "----ServiceConnected----");
			MyBinder binder = (MyBinder) service;
			mService = binder.GetService();
			mService.setBinder(getApplicationContext(), handler);
			mService.device.setModeUB(true);
			mService.device.setSendAlive(true);
			mService.device.uhfPowerOn();
			bBind = true;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out.println("----ServiceDisconnected!!----");
			logMa.d("martrin", "----ServiceDisconnected----");
			mService.device.setModeUB(false);
			bBind = false;
		}
		
	};
	
	@Override
	public void onCreate() {
		myApplication = this;
		super.onCreate();

		
		logMa.setDestIP(Session.getLogIP());//"192.168.0.180");
		logMa.setDestPort(Integer.valueOf(Session.getLogPort()));//8080);
		intent = new Intent(this, DeviceService.class);
		startService(intent);
		try{
			logMa.d("martrin", "----onCreate----");
			bindService(intent, conn, Service.BIND_AUTO_CREATE);
			instance = this;
			readConfig();
			connectServer();
		}catch(Exception e){e.printStackTrace();}
		
	}
	//解析服务器连接地址
	private void readConfig() throws Exception {
//		XmlResourceParser xmlResourceParser = this.getResources().getXml(R.xml.config);
//		int eventType = xmlResourceParser.getEventType();
//		while(eventType!=XmlResourceParser.END_DOCUMENT){
//			switch(eventType){
//			case XmlResourceParser.START_TAG:
//				String str = xmlResourceParser.getName();
//				if(str.equals("url")){
					//url = xmlResourceParser.nextText();//jdbc:jtds:sqlserver://192.168.0.245:1433/ptdb
					url = "jdbc:jtds:sqlserver://"+Session.getDatabaseIP()+":1433/"+Session.getDatabaseName();
					logMa.d("martrin", "----url----" + url);
//				}
//				if(str.equals("user")){
					//user = xmlResourceParser.nextText();
					user = Session.getDatabaseUser();//
					logMa.d("martrin", "----user----" + user);
//				}
//				if(str.equals("password")){
					//password = xmlResourceParser.nextText();
					password = Session.getDatabasePass();//
					logMa.d("martrin", "----password----" + password);
//				}
//				break;
//			}
//			eventType = xmlResourceParser.next();
//		}
	}
	
	//连接数据库
	private void connectServer() {
		new Thread(){
			public void run() {
				try {
				//	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					Class.forName("net.sourceforge.jtds.jdbc.Driver");
//					Toast.makeText(TApplication.this, "麻烦先等待几秒", 0).show();
					connection = DriverManager.getConnection(url, user, password);
					//connection = (Connection)DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.149:1433/ptdb", "sa", "123");
			//		connection = DriverManager.getConnection("jdbc:sqlserver://192.168.0.147:1433; DatabaseName=ptdb", "sa", "123");
					LogUtil.i("连接server数据库", "成功");
					logMa.d("martrin", "----Database connect success----");
				//	MainActivity.mainActivity.myHandler.obtainMessage(3).sendToTarget();
					MainActivity.mainActivity.handler.obtainMessage(3).sendToTarget();
				} catch (Exception e) {
					logMa.d("martrin",
							"----Database connect fail----" + e.getMessage());
					ExceptionUtil.handleException(e);
				}
			};
		}.start();
	}
	//退出整个应用
	public void exit(){
		try {
			for(Activity activity : listActivity){
				activity.finish();
				LogUtil.i("退出", activity.toString() + "finish()了");
				logMa.d("martrin", "----exit----");
			}
			connection.close();
			System.exit(0);
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
			logMa.d("martrin", "----exit fail----");
		}
	}
	
	/**
	 * Access to current global Context of the application, pay attention to use
	 * this method must be in the AndroidManifest. XML configuration
	 * MyApplication
	 * 
	 * @Title: getAppContext
	 * @return Context
	 */
	public static Context getAppContext() {
		if (null == myApplication) {
			throw new RuntimeException(
					"Please AndroidManifest. XML configuration MyApplication");
		}
		return myApplication;
	}

	/**
	 * Access to current global Application of the Application, pay attention to
	 * use this method must be in the AndroidManifest. XML configuration
	 * MyApplication
	 * 
	 * @Title: getAppContext
	 * @return Application
	 */
	public static TApplication getApp() {
		if (null == myApplication) {
			throw new RuntimeException(
					"Please AndroidManifest. XML configuration MyApplication");
		}
		return myApplication;
	}
	
}

