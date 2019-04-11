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
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.biz.EditUserBiz;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
/**
 * 编辑用户，修改密码
 * @author Administrator
 */
public class EditUserActivity extends BaseActivity{
	
	private EditText etUsername,etOldPwd,etNewPwd,etNewConfirmPwd;
	private String username,password;
	private String oldPwd,newPwd,newConfirmPwd;
	private Button btn_edit_save,btn_edit_cancel;
	private EditUserReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_edit_user);
			initViews();//初始化控件
			addListener();//添加监听
			
			receiver = new EditUserReceiver();
			this.registerReceiver(receiver, new IntentFilter(Constant.EDIT_USER_BIZ));
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	private void initViews() {
		etUsername = (EditText)findViewById(R.id.et_edit_username);
		username = getIntent().getStringExtra("username");
		password = getIntent().getStringExtra("password");
		etUsername.setText(username);
		etOldPwd = (EditText)findViewById(R.id.et_edit_old_password);
		etNewPwd = (EditText)findViewById(R.id.et_edit_new_password);
		etNewConfirmPwd = (EditText)findViewById(R.id.et_edit_new_confirm_password);
		
		btn_edit_save = (Button)findViewById(R.id.btn_edit_save);
		btn_edit_cancel = (Button)findViewById(R.id.btn_edit_cancel);
	}
	private void addListener() {
		btn_edit_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				oldPwd = etOldPwd.getText().toString();
				newPwd = etNewPwd.getText().toString();
				newConfirmPwd = etNewConfirmPwd.getText().toString();
				
				StringBuilder sb = new StringBuilder();
				//判断原密码是否输入正确
				etNewPwd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(!oldPwd.equals(password)){
							etOldPwd.setError("密码不正确");
							return;
						}
					}
				});
				//非空验证
				if(TextUtils.isEmpty(oldPwd)){
					sb.append("原密码为空!");
				}
				if(TextUtils.isEmpty(newPwd)){
					sb.append("\n新密码为空!");
				}
				if(TextUtils.isEmpty(newConfirmPwd)){
					sb.append("\n确认密码为空!");
				}
				if(!newConfirmPwd.equals(newPwd)&&newPwd.length()!=0&&newConfirmPwd.length()!=0){
					etNewConfirmPwd.setError("两次输入密码不一致");
					return;
				}
				if(!TextUtils.isEmpty(sb.toString())){
					Toast.makeText(EditUserActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
					return;
				}
				//启动编辑用户服务
				Intent intent = new Intent(EditUserActivity.this,EditUserBiz.class);
				intent.putExtra("username", username);
				intent.putExtra("newPwd", newPwd);
				startService(intent);
			}
		});
		btn_edit_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	class EditUserReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra(Constant.KEY_DATA, -1);
			switch(status){
			//成功
			case Constant.EDIT_USER_SUCCESS:
				Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT).show();
				break;
			//失败
			case Constant.EDIT_USER_FAILURE:
				Toast.makeText(context, R.string.save_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}
