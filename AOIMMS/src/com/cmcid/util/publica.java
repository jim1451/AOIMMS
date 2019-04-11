package com.cmcid.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.cmcid.lib.SysLib;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class publica {
	
	private static String str_DatabaseIP = "192.168.0.71";
	private static String str_DatabaseUser = "sa";
	private static String str_DatabasePass = "WineApp001";
	private static String str_UpdateIP = "http://192.168.0.71:8080/examples/";
	private static String str_LogIP = "192.168.0.71";
	private static String str_LogPort = "8080";
	
	
	public static String getDatabaseIP() {
		return str_DatabaseIP;
	}

	public static void setDatabaseIP(String a) {
		str_DatabaseIP = a;
	}
	
	public static String getDatabaseUser() {
		return str_DatabaseUser;
	}

	public static void setDatabaseUser(String a) {
		str_DatabaseUser = a;
	}
	
	public static String getDatabasePass() {
		return str_DatabasePass;
	}

	public static void setDatabasePass(String a) {
		str_DatabasePass = a;
	}
	
	public static String getUpdateIP() {
		return str_UpdateIP;
	}

	public static void setUpdateIP(String a) {
		str_UpdateIP = a;
	}
	
	public static String getLogIP() {
		return str_LogIP;
	}

	public static void setLogIP(String a) {
		str_LogIP = a;
	}
	
	public static String getLogPort() {
		return str_LogPort;
	}

	public static void setLogPort(String a) {
		str_LogPort = a;
	}
	
}
