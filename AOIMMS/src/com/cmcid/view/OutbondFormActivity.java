package com.cmcid.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.biz.OutbondBiz;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Tools;

public class OutbondFormActivity extends BaseActivity{
	
	private Spinner sp_linliao,sp_kufang,sp_kuwei;
	private Button btn_outbond,btn_cancel,btn_return;
	private TextView tv_username,tv_outbond_date;
	private String username,linliaoName,kufang,kuwei;
	private ArrayList<HashMap<String, String>> listOutbond;
	private ArrayList<HashMap<String, String>> listOutbond1;
	private OutbondReceiver receiver;
	
	private static final int GET_LINLIAO_NAME = 1;
	private static final int GET_KUFANG = 2;
	private static final int GET_KUWEI = 3;
	private static final int HUANYUAN = 4;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = msg.getData();
			switch(msg.what){
			case GET_LINLIAO_NAME:
				ArrayList<String> list1 = bundle.getStringArrayList("listLinliao");// listLinliao
				sp_linliao.setAdapter(new ArrayAdapter<String>(OutbondFormActivity.this, android.R.layout.simple_spinner_item, list1));
				break;
			case GET_KUFANG:
				ArrayList<String> list2 = bundle.getStringArrayList("listKufang");
				sp_kufang.setAdapter(new ArrayAdapter<String>(OutbondFormActivity.this, android.R.layout.simple_spinner_item, list2));
				break;
			case GET_KUWEI:
				ArrayList<String> list3 = bundle.getStringArrayList("listKuwei");
				sp_kuwei.setAdapter(new ArrayAdapter<String>(OutbondFormActivity.this, android.R.layout.simple_spinner_item, list3));
				break;
				
	   case HUANYUAN:

				Toast.makeText(getApplicationContext(), "货物数据已经还原！", 0).show();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_outbond_form);
			getData();
			initViews();
			addListener();
			
			receiver = new OutbondReceiver();
			this.registerReceiver(receiver, new IntentFilter(Constant.OUTBOND_BIZ));
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	
	private void getData() {
		//从表中获得 领料单位 库房 库位
		new Thread(){
			public void run() {
				Statement state = null;
				try {
					state = TApplication.connection.createStatement();
					String sql1 = "select * from pt_LinLiaoT";
					String sql2 = "select * from pt_KuFangT  where kuFangHao  = '1'";
					String sql3 = "select * from pt_KuWeiT  where  KuWeiHao  = '1' ";
					//领料单位
					ResultSet rs = state.executeQuery(sql1);
					ArrayList<String> listLinliao = new ArrayList<String>(); 
					while(rs.next()){
						String LinliaoNameItem = rs.getString("linliaoName");
						listLinliao.add(LinliaoNameItem);
					}
					
					//库房
					rs = state.executeQuery(sql2);
					ArrayList<String> listKufang = new ArrayList<String>();
					while(rs.next()){
						String kufangItem = rs.getString("kuFangName");
						listKufang.add(kufangItem);
					}
				
					//库位
					rs = state.executeQuery(sql3);
					ArrayList<String> listKuwei = new ArrayList<String>();
					while(rs.next()){
						String kuweiItem = rs.getString("KuWeiName");
						listKuwei.add(kuweiItem);
					}
					rs.close();
					
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("listLinliao", listLinliao);
					msg.what = GET_LINLIAO_NAME;
					msg.setData(bundle);
					handler.sendMessage(msg);
					
					msg = new Message();
					bundle = new Bundle();
					bundle.putStringArrayList("listKufang", listKufang);
					msg.what = GET_KUFANG;
					msg.setData(bundle);
					handler.sendMessage(msg);
					
					msg = new Message();
					bundle = new Bundle();
					bundle.putStringArrayList("listKuwei", listKuwei);
					msg.what = GET_KUWEI;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (Exception e) {
					ExceptionUtil.handleException(e);
				}finally{
					try {state.close();} catch (SQLException e) {e.printStackTrace();}
				}
			};
		}.start();
	}
	
	
	
	
	/**
	 * 对数据有进行还原处理 获取TApplication.listrestore全局变量，进行对应的操作
	 */
	
	private void getReturn() {
		new Thread(){
		
			public void run() {
				Statement state = null;
				for (int i = 0; i < TApplication.listrestore.size(); i++) {
					
				
				try {
					String  sqlString = 	TApplication.listrestore.get(i);

					
					state = TApplication.connection
							.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
									ResultSet.CONCUR_UPDATABLE);
						state.executeUpdate(sqlString);

				 
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				 TApplication.listrestore.clear();
				 try {state.close();} catch (SQLException e) {e.printStackTrace();}
				 
				 handler.obtainMessage(HUANYUAN).sendToTarget();
				 return;
			};
			
		}.start();
		
	}
	
	
	
	
	
	
	
	
	
	private void initViews() {
		listOutbond = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listOutbond"); 
		listOutbond1 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listOutbond1"); 
		username = getIntent().getStringExtra("username");
		tv_username = (TextView)findViewById(R.id.tv_outbond_form_username);
		tv_username.setText(username);
		
		tv_outbond_date = (TextView)findViewById(R.id.tv_outbond_date);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		tv_outbond_date.setText(date);
		
		sp_linliao = (Spinner)findViewById(R.id.sp_outbond_lingliaoName);
		sp_kufang = (Spinner)findViewById(R.id.sp_outbond_kufang);
		sp_kuwei = (Spinner)findViewById(R.id.sp_outbond_kuwei);
		
		btn_outbond = (Button)findViewById(R.id.btn_outbond_form_outbond);
		btn_cancel = (Button)findViewById(R.id.btn_outbond_form_cancel);
		btn_return = (Button)findViewById(R.id.btn_huanyuan_form_outbond);
	}
	private void addListener() {
		btn_outbond.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			//	linliaoName = sp_linliao.getSelectedItem().toString();/////////////////////////////////////////////////////////////
				kufang = sp_kufang.getSelectedItem().toString();
				kuwei = sp_kuwei.getSelectedItem().toString();
				
				//弹窗提示，确认出库
				new AlertDialog.Builder(OutbondFormActivity.this).setTitle(R.string.outbond)
				.setMessage(R.string.confirm_outbond_message)
				.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					//	Tools.showProgressDialog(OutbondFormActivity.this, "正在为您进行出库操作中...");
						Intent intent = new Intent(OutbondFormActivity.this,OutbondBiz.class);
						intent.putExtra("linliao", linliaoName);
						intent.putExtra("kufang", kufang);
						intent.putExtra("kuwei", kuwei);
						intent.putExtra("username", username);
						intent.putExtra("listOutbond", listOutbond);
						intent.putExtra("listOutbond1", listOutbond1);
						startService(intent);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.create().show();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getReturn();

			}
		});
		
		
		
	}
	//广播接受出库操作处理后的数据
	class OutbondReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Tools.closeProgressDialog();
			ArrayList<String> list = intent.getStringArrayListExtra("listEPC");
			if(list.size()==0){//全部入库，弹窗提示
				new AlertDialog.Builder(context).setMessage(R.string.outbond_done_message)
				.setPositiveButton(R.string.continue_inventory, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				})
				.setNegativeButton(R.string.back_to_main, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(OutbondFormActivity.this,MainActivity.class);
						intent.putExtra("username", username);
						startActivity(intent);
						finish();
					}
				}).create().show();
			}else{//有已经在库中的EPC，提示并显示
				View view = View.inflate(context, R.layout.lv_epc_exist_view, null);
				TextView tv_epc_already_exist = (TextView) view.findViewById(R.id.tv_epc_already_exist);
				tv_epc_already_exist.setText(R.string.outbond_epc_exist);
				ListView lv = (ListView) view.findViewById(R.id.lv_bond_epc_exist);
				lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list));
				new AlertDialog.Builder(context).setView(view)
				.setPositiveButton(R.string.continue_inventory, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				})
				.setNegativeButton(R.string.back_to_main, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(OutbondFormActivity.this,MainActivity.class);
						intent.putExtra("username", username);
						startActivity(intent);
						finish();
					}
				})
				.create().show();
			}
		}
	}
}
