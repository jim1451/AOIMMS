package com.cmcid.view;

import org.w3c.dom.UserDataHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.biz.LoginBiz;
import com.cmcid.lib.SysLib;
import com.cmcid.util.Constant;
import com.cmcid.util.logMa;

public class LoginActivity extends BaseActivity {

	private EditText etUsername, etPassword;
	private Button btn_login, btn_cancel;
	private LoginReceiver receiver;
	private String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initViews();
		addListener();

		receiver = new LoginReceiver();
		this.registerReceiver(receiver, new IntentFilter(Constant.LOGIN_BIZ));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}

	private void initViews() {
		etUsername = (EditText) findViewById(R.id.et_login_username);
		etPassword = (EditText) findViewById(R.id.et_login_password);

		// This is a test!!!
		logMa.d("martrin", "login test start...");
		etUsername.setText("admin");
		etPassword.setText("1234");

		btn_login = (Button) findViewById(R.id.btnLogin);
		btn_cancel = (Button) findViewById(R.id.btnCancel);
	}

	private void addListener() {
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// SysLib.system("su -c ftpsvrs", "aa");
				// SysLib.system("su -c \"telnetd -l /system/bin/sh\"", "aa");
				username = etUsername.getText().toString();
				password = etPassword.getText().toString();
				StringBuilder sb = new StringBuilder();
				// 用户名不能为空
				if (TextUtils.isEmpty(username)) {
					sb.append("用户名为空!");
				}
				// 密码不能为空
				if (TextUtils.isEmpty(password)) {
					sb.append("\n密码为空!");
				}
				if (!TextUtils.isEmpty(sb.toString())) {
					Toast.makeText(LoginActivity.this, sb.toString(),
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 启动登录服务
				Intent intent = new Intent(LoginActivity.this, LoginBiz.class);
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				startService(intent);
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class LoginReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra(Constant.KEY_DATA, -1);// status=3
			String right = intent.getStringExtra("right");// 传递权限 administrator
			String usera = intent.getStringExtra("user");// user name
															// administrator
			switch (status) {
			case Constant.NO_THIS_PEOPLE:// 用户不存在
				Toast.makeText(context, R.string.user_not_exists,
						Toast.LENGTH_SHORT).show();
				break;
			case Constant.PASSWORD_ERROR:// 密码错误
				Toast.makeText(context, R.string.wrong_password,
						Toast.LENGTH_SHORT).show();
				break;
			case Constant.LOGIN_SUCCESS:// 登录成功
				Toast.makeText(context, R.string.login_success,
						Toast.LENGTH_SHORT).show();
				TApplication.isFistRun = true;
				TApplication.strUser = usera;
				// if (InbondActivity.inbondActivity ==
				// null&&TuikuActivity.tuikuActivity ==
				// null&&YikuActivity.yikuActivity == null) {
				// OutbondActivity.outbondActivity.handler.obtainMessage(3).sendToTarget();
				// //
				// TuikuActivity.tuikuActivity.handler.obtainMessage(4).sendToTarget();
				// }
				if (InbondActivity.inbondActivity != null) {
					// OutbondActivity.outbondActivity.handler.obtainMessage(3).sendToTarget();
					InbondActivity.inbondActivity.handler.obtainMessage(2)
							.sendToTarget();

				}

				if (OutbondActivity.outbondActivity != null) {

					OutbondActivity.outbondActivity.handler.obtainMessage(3)
							.sendToTarget();
				}
				if (TuikuActivity.tuikuActivity != null) {
					TuikuActivity.tuikuActivity.handler.obtainMessage(4)
							.sendToTarget();
				}
				if (YikuActivity.yikuActivity != null) {
					// TuikuActivity.tuikuActivity.handler.obtainMessage(4).sendToTarget();
					YikuActivity.yikuActivity.handler.obtainMessage(4)
							.sendToTarget();
				}
				if (InventoryActivity.inventoryActivity != null) {
					InventoryActivity.inventoryActivity.handler
							.obtainMessage(4).sendToTarget();
				}
				// OutbondActivity.outbondActivity.handler.obtainMessage(3).sendToTarget();
				// TuikuActivity.tuikuActivity.handler.obtainMessage(4).sendToTarget();
				// Intent i = new Intent(context,InbondActivity.class);
				// i.putExtra("right", right);
				// i.putExtra("username", username);
				// i.putExtra("password", password);
				// startActivity(i);
				finish();
				break;
			}
		}
	}
}
