package com.cmcid.util;

import com.cmcid.TApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrefsHelper 
{
	private Context context;
	private String prefName;
	private int mode;
	
	public SharePrefsHelper(String prefName){
		this.context = TApplication.getAppContext();
		this.prefName = prefName;
		this.mode = Context.MODE_PRIVATE;
	}
	
	/**
	 * Constructor
	 * @param context 
	 * @param prefName sharedPreference pref name
	 * @param mode (MODE_PRIVATE MODE_WORLD_READABLE MODE_WORLD_WRITEABLE )
	 */
	public SharePrefsHelper(Context context, String prefName, int mode)
	{
		this.context = context;
		this.prefName = prefName;
		this.mode = mode;
	}
	
	public String getString(String key, String defValue)
	{
		return getPreferences().getString(key, defValue);
	}
	
	public boolean setString(String key, String value)
	{
		return getEditor().putString(key, value).commit();
	}
	
	public int getInt(String key, int defValue)
	{
		return getPreferences().getInt(key, defValue);
	}
	
	public boolean setInt(String key, int value)
	{
		return getEditor().putInt(key, value).commit();
	}
	
	public long getLong(String key, long defValue)
	{
		return getPreferences().getLong(key, defValue);
	}
	
	public boolean setLong(String key, long value)
	{
		return getEditor().putLong(key, value).commit();
	}
	
	public float getFloat(String key, float defValue)
	{
		return getPreferences().getFloat(key, defValue);
	}
	
	public boolean setFloat(String key, float value)
	{
		return getEditor().putFloat(key, value).commit();
	}
	
	public boolean getBoolean(String key, boolean defValue)
	{
		return getPreferences().getBoolean(key, defValue);
	}
	
	public boolean seBoolean(String key, boolean value)
	{
		return getEditor().putBoolean(key, value).commit();
	}
	
	public SharedPreferences getPreferences()
	{
		return context.getSharedPreferences(prefName, mode);
	}
	
	public SharedPreferences.Editor getEditor()
	{
		return getPreferences().edit();
	}
}
