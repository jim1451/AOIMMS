package com.cmcid.view;

import java.io.File;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.lib.SysLib;
import com.cmcid.util.AndroidApkUpdater;
import com.cmcid.util.ClsPublic;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Session;
import com.cmcid.util.Version;
import com.cmcid.util.logMa;
import com.cmcid.view.PopMenu.OnItemClickListener;


public class MainActivity extends BaseActivity implements OnItemClickListener{
	
	private ImageView iv_nav;
	private PopMenu popMenu;
	private TextView tv_username;
	private TextView tv_ip;
	private TextView tv_ver;
	private String right,username,password;
	private Button btn_inbond,btn_outbond,btn_remove,btn_inventory,btn_yiku;
	private RelativeLayout rl;
	private LinearLayout ll;
	
	Context myContext;
	public static MainActivity mainActivity;
	public Handler myHandler = null;
	AndroidApkUpdater updater;
	String sUrl = "";
	String sSavePathFileName = "";
	String sFileName = "";

	Dialog alertDialog = null;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
        	setContentView(R.layout.activity_main);
			logMa.d("martrin", "----onCreate----");
        	initViews();
        	addListener();
        	
        } catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
        
	}
	
	
	private void initViews() {
		tv_ver = (TextView) findViewById(R.id.tv_ver);
		tv_ver.setText(tv_ver.getText() + Version.getVersionName());
		tv_username = (TextView)findViewById(R.id.tv_username);
		Intent intent = getIntent();
		right = intent.getStringExtra("right");
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		tv_username.setText(username);
		
		iv_nav = (ImageView)findViewById(R.id.iv_nav);
		popMenu = new PopMenu(this);
		popMenu.addItems(new int[] { R.string.user_add, R.string.user_delete,
				R.string.user_edit, R.string.action_settings, R.string.Update, R.string.exit });
		popMenu.setOnItemClickListener(this);
		
		btn_inbond = (Button)findViewById(R.id.btn_main_activity_inbond);
		btn_outbond = (Button)findViewById(R.id.btn_main_activity_outbond);
		btn_remove = (Button)findViewById(R.id.btn_main_activity_tuiku);
		btn_inventory = (Button)findViewById(R.id.btn_main_activity_inventory);
		 btn_yiku		=		(Button) findViewById(R.id.btn_main_activity_yikui);
		rl = (RelativeLayout) findViewById(R.id.rl_1);
		ll = (LinearLayout) findViewById(R.id.ll_1);


		tv_ip = (TextView)findViewById(R.id.tv_ip);
		String bb[] = ClsPublic.GetLocalIP();
		tv_ip.setText(tv_ip.getText()+" ("+bb[0]+")");
		
		mainActivity = this;
		myContext = this;

		
 //建立弹出框，提示客户
        
    	alertDialog = new AlertDialog.Builder(this). 
				//设置标题
	            setTitle("提示").setCancelable(false).
	            //设置内容
	            setMessage("数据库连接中稍等几秒钟，如不行，请检查登录参数或重登录!!!!").
	            //设置按钮事件
	            	setPositiveButton("确定", new DialogInterface.OnClickListener() { 
	                 
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                //	System.exit(0);
	                	dialog.dismiss();
	                	
	                	// TODO Auto-generated method stub  
	                } 
	            }).
				//创建
	            create();
		//显示
				alertDialog.show();
				logMa.d("martrin", "----alertDialog.show----");
				
				
	}
	private void addListener() {
		MyListener myListener = new MyListener();
		btn_inbond.setOnClickListener(myListener);
		btn_outbond.setOnClickListener(myListener);
		        btn_yiku.setOnClickListener(myListener);
		btn_remove.setOnClickListener(myListener);
		btn_inventory.setOnClickListener(myListener);
		iv_nav.setOnClickListener(myListener);
		rl.setOnClickListener(myListener);
		ll.setOnClickListener(myListener);
		tv_ip.setOnClickListener(myListener);
		
		ClsPublic.setstrUpdateURL(Session.getUpdateIP());//"http://192.168.0.71:8080/examples/");
		ClsPublic.setstrUpdateApk("AOIMMS.apk");
		ClsPublic.setstrUpdateSet("ver.txt");
		updater = new AndroidApkUpdater(this) {
			String jsonUrl = "";// "http://192.168.0.71:8080/examples/ver.txt";

			@Override
			protected Version getServerVersion() throws Exception {
				String sFileVer = "ver.txt";// 一定和APK在同一层目录
				jsonUrl = ClsPublic.getstrUpdateURL()+ClsPublic.getstrUpdateApk();//"http://192.168.0.71:8080/examples/AOIMMS.apk";//Session.getDownUrl();
				jsonUrl = ClsPublic.getPath(jsonUrl);
				jsonUrl += sFileVer;
				JSONObject jsonObject = ClsPublic.get(jsonUrl);
				if (jsonObject == null)
					return null;
				int verCode = jsonObject.getInt("verCode");
				String verName = jsonObject.getString("verName");
				String apkUrl = jsonObject.getString("url");
				AndroidApkUpdater.Version version = new AndroidApkUpdater.Version(
						verName, verCode, apkUrl);
				logMa.d("martrin", version.toString());
				return version;
			}

			@Override
			protected String getPackageName() {
				return "com.cmcid";
			}
		};



	}
	//事件
	class MyListener implements OnClickListener{
	

		private String tag1;

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_main_activity_inbond:startActivity(InbondActivity.class);break;
			case R.id.btn_main_activity_yikui:startActivity(YikuActivity.class);break;
			case R.id.btn_main_activity_outbond:startActivity(OutbondActivity.class);break;
			case R.id.btn_main_activity_tuiku:startActivity(TuikuActivity.class);break;
			case R.id.btn_main_activity_inventory:startActivity(InventoryActivity.class);break;
			case R.id.iv_nav:popMenu.showAsDropDown(v);break;
			case R.id.rl_1:
				SysLib.system("su -c \"killall ftpsvrs\"", "aa");
				SysLib.system("su -c \"killall telnetd\"", "aa");
				Toast.makeText(MainActivity.this, "kill", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ll_1:
				SysLib.system("su -c ftpsvrs", "aa");
				SysLib.system("su -c \"telnetd -l /system/bin/sh\"", "aa");
				Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
				break;
			case R.id.tv_ip:

				break;
				
			}
		}
	}
	public void startActivity(Class clazz) {
		Intent intent = new Intent(MainActivity.this,clazz);
		intent.putExtra("username", username);
		startActivity(intent);
	}
	
	@Override
	public void onItemClick(int position) {
		switch(position){
		case 0:
			if("administrator".equals(right)){
				startActivity(new Intent(MainActivity.this,AddUserActivity.class));
			}else{
				Toast.makeText(MainActivity.this, R.string.user_permission, Toast.LENGTH_SHORT).show();
			}
			break;
		case 1:
			if("administrator".equals(right)){
				startActivity(new Intent(MainActivity.this,DeleteUserActivity.class));
			}else{
				Toast.makeText(MainActivity.this, R.string.user_permission, Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			Intent intent = new Intent(MainActivity.this,EditUserActivity.class);
			intent.putExtra("username", username);
			intent.putExtra("password", password);
			startActivity(intent);
			break;
		case 3://设置
			Intent intenta = new Intent(MainActivity.this,
					SettingActivity.class);
			startActivity(intenta);
			break;
		case 4://升级
			updater.setUpdaterContext(myContext);
			updater.update();
			break;
		case 5:
			if(!TApplication.instance.mService.device.getDeviceIsOpen()){
				TApplication.instance.exit();
			}
			Intent intentx = new Intent(Intent.ACTION_MAIN);
            intentx.addCategory(Intent.CATEGORY_HOME);
            intentx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);            
            startActivity(intentx);
            System.exit(0);
			break;
		}
	}

	public Handler handler = new Handler() {
		// 定义处理信息的方法
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				updater.setUpdaterContext(myContext);
				updater.update();
				break;

			case 2:
				Context cContext = (Context) msg.obj;
				updater.setUpdaterContext(cContext);
				updater.update();
				break;
				
			case 3:
				logMa.d("martrin", "----alertDialog.dismiss----");
				alertDialog.dismiss();
				break;	
				
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!TApplication.instance.mService.device.getDeviceIsOpen()) {
			TApplication.instance.exit();
		}
	}

}
