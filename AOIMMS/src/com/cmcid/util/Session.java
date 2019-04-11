package com.cmcid.util;

import android.content.Context;

import com.cmcid.TApplication;


public class Session {
    
	/** session SharedPreference name */
	public static final String SESSION_FILE = "AOIMMS";

	public static final SharePrefsHelper sessionSp = new SharePrefsHelper(			
			TApplication.getAppContext(), SESSION_FILE, Context.MODE_PRIVATE);

	private static String getString(String key, String def) {
		return sessionSp.getString(key, def);
	}

	private static void setString(String key, String value) {
		sessionSp.setString(key, value);
	}
	
	
	/**
	 * 数据库的IP地址
	 */
	public static final String DATABASEIP = "DatabaseIP";
	public static void setDatabaseIP(String value) {
		setString(DATABASEIP, value);
	}

	public static String getDatabaseIP() {
	//	return "192.168.0.149";
		return getString(DATABASEIP, "192.168.0.168");
	}
	//192.168.0.71
	
	/**
	 * 数据库的名字
	 */
	public static final String DATABASENAME = "DatabaseName";
	public static void setDatabaseName(String value) {
		setString(DATABASENAME, value);
	}

	public static String getDatabaseName() {
		return getString(DATABASENAME, "ptdb");
	}
	
	
	/**
	 * 数据库的用户
	 */
	public static final String DATABASEUSER = "DatabaseUser";
	public static void setDatabaseUser(String value) {
		setString(DATABASEUSER, value);
	}

	public static String getDatabaseUser() {
		return getString(DATABASEUSER, "sa");
	}
	
	
	/**
	 * 数据库的密码
	 */
	public static final String DATABASEPASS = "DatabasePass";
	public static void setDatabasePass(String value) {
		setString(DATABASEPASS, value);
	}

	public static String getDatabasePass() {
		return getString(DATABASEPASS, "123");
	}
	
	
	/**
	 * 升级APP的网络地址
	 */
	public static final String UPDATEIP = "UpdateIP";
	public static void setUpdateIP(String value) {
		setString(UPDATEIP, value);
	}

	public static String getUpdateIP() {
		return getString(UPDATEIP, "http://192.168.0.71:8080/examples/");
	}
	
	
	/**
	 * 输出调试时日志的IP
	 */
	public static final String LOGIP = "LogIP";
	public static void setLogIP(String value) {
		setString(LOGIP, value);
	}

	public static String getLogIP() {
		return getString(LOGIP, "192.168.0.71");
	}
	
	
	/**
	 * 输出调试时日志的端口
	 */
	public static final String LOGPORT = "LogPort";
	public static void setLogPort(String value) {
		setString(LOGPORT, value);
	}

	public static String getLogPort() {
		return getString(LOGPORT, "8080");
	}
	
	
	
	/**
	 * 由于实际中通过USB口读取标签而仿真调试也是通过同一USB口，所以这里有个测试标志,1--模拟测试，0--真实读取EPC
	 */
	public static final String SIMULATION = "Simulation";//1-模拟
	public static void setSimulation(String value) {
		setString(SIMULATION, value);
	}

	public static String getSimulation() {
		return getString(SIMULATION, "0");// 0--不模拟,真实测试
	}

	
	
	
}
