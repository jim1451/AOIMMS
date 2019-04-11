package com.cmcid.view;

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
import android.widget.RadioButton;
import android.widget.Toast;


import com.cmcid.R;
import com.cmcid.biz.AddUserBiz;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;

public class AddUserActivity extends BaseActivity{
	
	private EditText etUsername,etPassword,etConfirmPwd;
	private Button btn_add,btn_add_cancel;
	private AddUserReceiver receiver;
	private RadioButton rb_admin,rb_ordinary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_add_user);
			initViews();
			addListener();
			
			receiver = new AddUserReceiver();
			this.registerReceiver(receiver, new IntentFilter(Constant.ADD_USER_BIZ));
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	//初始化控件
	private void initViews() {
		etUsername = (EditText)findViewById(R.id.et_add_username);
		etPassword = (EditText)findViewById(R.id.et_add_password);
		etConfirmPwd = (EditText)findViewById(R.id.et_add_confirm_password);
		rb_admin = (RadioButton)findViewById(R.id.rb_admin_user);
		rb_ordinary = (RadioButton)findViewById(R.id.rb_ordinary_user);
		btn_add = (Button)findViewById(R.id.btn_add_done);
		btn_add_cancel = (Button)findViewById(R.id.btn_add_Cancel);
		
	}
	//添加监听
	private void addListener() {
		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();
				String confirmPwd = etConfirmPwd.getText().toString();
				if(!confirmPwd.equals(password)&&(confirmPwd.length()!=0)){
					etConfirmPwd.setError(String.valueOf(R.string.two_Input_password_different));
					return;
				}
				//类型  1管理员    2普通用户
				int right = -1;
				if(rb_admin.isChecked()){
					right = 1;
				}
				if(rb_ordinary.isChecked()){
					right = 2;
				}
				StringBuilder sb = new StringBuilder();
				//用户名不能为空
				if(TextUtils.isEmpty(username)){
					sb.append("用户名为空!");
				}
				//密码不能为空
				if(TextUtils.isEmpty(password)){
					sb.append("\n密码为空!");
				}
				//确认密码
				if(TextUtils.isEmpty(confirmPwd)){
					sb.append("\n确认密码为空!");
				}
				//类型未选择
				if(right==-1){
					sb.append("\n用户类型未选择!");
				}
				if(!TextUtils.isEmpty(sb.toString())){
					Toast.makeText(AddUserActivity.this, sb.toString(),Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(AddUserActivity.this,AddUserBiz.class);
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				intent.putExtra("right", right);
				//启动添加用户服务
				startService(intent);
			}
		});
		btn_add_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	//广播接受添加用户处理结果
	class AddUserReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra(Constant.KEY_DATA, -1);
			switch(status){
			case Constant.ADD_USER_SUCCESS:
				Toast.makeText(context, R.string.add_user_success, Toast.LENGTH_SHORT).show();
				break;
			case Constant.ADD_USER_FAILURE:
				Toast.makeText(context, R.string.add_user_failure, Toast.LENGTH_SHORT).show();
				break;
			case Constant.USER_EXISTS:
				Toast.makeText(context, R.string.user_already_exists, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}
