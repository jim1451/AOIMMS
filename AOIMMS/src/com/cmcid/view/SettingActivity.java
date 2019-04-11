package com.cmcid.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cmcid.R;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Session;
import com.cmcid.util.logMa;
/**
 * 系统设置
 * @author Administrator
 */
public class SettingActivity extends BaseActivity{
	
	private EditText et_edit_DatabaseIP;
	private EditText et_edit_DatabaseName;
	private EditText et_edit_DatabaseUser;
	private EditText et_edit_DatabasePass;
	private EditText et_edit_UpdateIP;
	private EditText et_edit_LogIP;
	private EditText et_edit_LogPort;
	private Button btn_edit_save,btn_edit_cancel;
	private CheckBox cb_check_sim;
	
	private String str_edit_DatabaseIP;
	private String str_edit_DatabaseName;
	private String str_edit_DatabaseUser;
	private String str_edit_DatabasePass;
	private String str_edit_UpdateIP;
	private String str_edit_LogIP;
	private String str_edit_LogPort;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_setting);
			initViews();//初始化控件
			addListener();//添加监听
			
			
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	private void initViews() {
		et_edit_DatabaseIP = (EditText)findViewById(R.id.et_edit_DatabaseIP);
		et_edit_DatabaseName = (EditText)findViewById(R.id.et_edit_DatabaseName);
		et_edit_DatabaseUser = (EditText)findViewById(R.id.et_edit_DatabaseUser);
		et_edit_DatabasePass = (EditText)findViewById(R.id.et_edit_DatabasePass);
		et_edit_UpdateIP = (EditText)findViewById(R.id.et_edit_UpdateIP);
		et_edit_LogIP = (EditText)findViewById(R.id.et_edit_LogIP);
		et_edit_LogPort = (EditText)findViewById(R.id.et_edit_LogPort);
		cb_check_sim = (CheckBox) findViewById(R.id.check_sim);

		btn_edit_save = (Button)findViewById(R.id.btn_edit_save);
		btn_edit_cancel = (Button)findViewById(R.id.btn_edit_cancel);
		
		//init
		et_edit_DatabaseIP.setText(Session.getDatabaseIP());
		et_edit_DatabaseName.setText(Session.getDatabaseName());
		et_edit_DatabaseUser.setText(Session.getDatabaseUser());
		et_edit_DatabasePass.setText(Session.getDatabasePass());
		et_edit_UpdateIP.setText(Session.getUpdateIP());
		et_edit_LogIP.setText(Session.getLogIP());
		et_edit_LogPort.setText(Session.getLogPort());
		if (Session.getSimulation().equals("1")) {
			cb_check_sim.setChecked(true);
		} else {
			cb_check_sim.setChecked(false);
		}
		
		
	}
	private void addListener() {
		btn_edit_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				str_edit_DatabaseIP = et_edit_DatabaseIP.getText().toString();
				str_edit_DatabaseUser = et_edit_DatabaseUser.getText().toString();
				str_edit_DatabasePass = et_edit_DatabasePass.getText().toString();
				str_edit_UpdateIP = et_edit_UpdateIP.getText().toString();
				str_edit_LogIP = et_edit_LogIP.getText().toString();
				str_edit_LogPort = et_edit_LogPort.getText().toString();
				
				//save it
				Session.setDatabaseIP(str_edit_DatabaseIP);
				Session.setDatabaseName(str_edit_DatabaseName);
				Session.setDatabaseUser(str_edit_DatabaseUser);
				Session.setDatabasePass(str_edit_DatabasePass);
				Session.setUpdateIP(str_edit_UpdateIP);
				Session.setLogIP(str_edit_LogIP);
				Session.setLogPort(str_edit_LogPort);
				if (cb_check_sim.isChecked()) {
					Session.setSimulation("1");
				} else {
					Session.setSimulation("0");
				}
				
				
				logMa.setDestIP(Session.getLogIP());//"192.168.0.180");
				logMa.setDestPort(Integer.valueOf(Session.getLogPort()));//8080);
				

				finish();
				
			}
		});
		btn_edit_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
