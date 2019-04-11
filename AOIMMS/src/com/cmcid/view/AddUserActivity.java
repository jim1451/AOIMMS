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
	//��ʼ���ؼ�
	private void initViews() {
		etUsername = (EditText)findViewById(R.id.et_add_username);
		etPassword = (EditText)findViewById(R.id.et_add_password);
		etConfirmPwd = (EditText)findViewById(R.id.et_add_confirm_password);
		rb_admin = (RadioButton)findViewById(R.id.rb_admin_user);
		rb_ordinary = (RadioButton)findViewById(R.id.rb_ordinary_user);
		btn_add = (Button)findViewById(R.id.btn_add_done);
		btn_add_cancel = (Button)findViewById(R.id.btn_add_Cancel);
		
	}
	//��Ӽ���
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
				//����  1����Ա    2��ͨ�û�
				int right = -1;
				if(rb_admin.isChecked()){
					right = 1;
				}
				if(rb_ordinary.isChecked()){
					right = 2;
				}
				StringBuilder sb = new StringBuilder();
				//�û�������Ϊ��
				if(TextUtils.isEmpty(username)){
					sb.append("�û���Ϊ��!");
				}
				//���벻��Ϊ��
				if(TextUtils.isEmpty(password)){
					sb.append("\n����Ϊ��!");
				}
				//ȷ������
				if(TextUtils.isEmpty(confirmPwd)){
					sb.append("\nȷ������Ϊ��!");
				}
				//����δѡ��
				if(right==-1){
					sb.append("\n�û�����δѡ��!");
				}
				if(!TextUtils.isEmpty(sb.toString())){
					Toast.makeText(AddUserActivity.this, sb.toString(),Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(AddUserActivity.this,AddUserBiz.class);
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				intent.putExtra("right", right);
				//��������û�����
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
	//�㲥��������û�������
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
