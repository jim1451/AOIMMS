package com.cmcid.view;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.adapter.UsernameAdapter;
import com.cmcid.biz.DeleteUserBiz;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.ViewHolder;

public class DeleteUserActivity extends BaseActivity{
	
	private ListView lv_username;
	private ArrayList<String> list;
	private ArrayList<String> listSelect = new ArrayList<String>();
	private Button btn_delete,btn_delete_cancel;
	private DeleteReceiver receiver;
	private UsernameAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_delete_user);
			DeleteUserBiz.getUsernames();
			initViews();
			addListener();
			
			//注册广播
			receiver = new DeleteReceiver();
			this.registerReceiver(receiver, new IntentFilter(Constant.DELETE_USER_BIZ));
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//解除广播注册
		this.unregisterReceiver(receiver);
	}
	private void initViews() {
		lv_username = (ListView)findViewById(R.id.lv_username);
		
		btn_delete = (Button)findViewById(R.id.btn_delete);
		btn_delete_cancel = (Button)findViewById(R.id.btn_delete_cancel);
	}
	private void addListener() {
		lv_username.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
				ViewHolder vh = (ViewHolder) view.getTag();
				// 改变CheckBox的状态
				vh.cb_item_delete.toggle();
				 // 将CheckBox的选中状况记录下来
				UsernameAdapter.getIsSelected().put(position, vh.cb_item_delete.isChecked());
				if(vh.cb_item_delete.isChecked()==true){
					listSelect.add(list.get(position));
				}else{
					listSelect.remove(list.get(position));
				}
			}
		});
		btn_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDeleteDialog();
			}
		});
		btn_delete_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	//删除用户对话框
	protected void showDeleteDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(R.string.message_delete)
		.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeleteUserBiz.deleteUsers(listSelect);
				dialog.cancel();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		})
		.show();
	}
	//广播接受删除用户处理结果
	class DeleteReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra(Constant.KEY_DATA, -1);
			switch(status){
			case Constant.GET_USER_SUCCESS:
				list = intent.getStringArrayListExtra("userlist");
				adapter = new UsernameAdapter(context, list);
				lv_username.setAdapter(adapter);
				break;
			case Constant.DELETE_SUCCESS:
				Toast.makeText(context, R.string.delete_success,Toast.LENGTH_SHORT).show();
				DeleteUserBiz.getUsernames();
				break;
			case Constant.DELETE_FAILURE:
				Toast.makeText(context, R.string.delete_failure,Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}
