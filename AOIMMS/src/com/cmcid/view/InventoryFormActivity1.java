package com.cmcid.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.biz.InventoryBiz;
import com.cmcid.biz.InventoryBiz1;
import com.cmcid.biz.TuikuBiz;
import com.cmcid.biz.YikuBiz;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Tools;


public class InventoryFormActivity1 extends BaseActivity{
	
	private Spinner sp_linliao,sp_kufang,sp_kuwei;
	private Button btn_inventory,btn_cancel;
	private String username,linliaoName,kufang,kuwei;
	private ArrayList<HashMap<String, String>> listinventory;
	private ArrayList<HashMap<String, String>> listinventory1;
	private TextView tv_username,tv_pang_date,tv_pang_date1;
	private TextView tv_pang_start,tv_pang_start1;
	
	
	
	private YikuReceiver receiver;
	
//	private static final int GET_LINLIAO_NAME = 1;
//	private static final int GET_KUFANG = 2;
//	private static final int GET_KUWEI = 3;
	
//	private Handler handler = new Handler(){
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			switch(msg.what){
////			case GET_LINLIAO_NAME:
////				//下拉显示领料单位名称
////				ArrayList<String> list1 = bundle.getStringArrayList("listLinliao");
////				sp_linliao.setAdapter(new ArrayAdapter<String>(InventoryFormActivity1.this, android.R.layout.simple_spinner_item, list1));
////				break;
////			case GET_KUFANG:
////				//下拉显示库房名称
////				ArrayList<String> list2 = bundle.getStringArrayList("listKufang");
////				sp_kufang.setAdapter(new ArrayAdapter<String>(InventoryFormActivity1.this, android.R.layout.simple_spinner_item, list2));
////				break;
//			case GET_KUWEI:
//				//下拉显示库位名称
//				ArrayList<String> list3 = bundle.getStringArrayList("listKuwei");
//				sp_kuwei.setAdapter(new ArrayAdapter<String>(InventoryFormActivity1.this, android.R.layout.simple_spinner_item, list3));
//				break;
//			}
//		};
//	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_inventory_form1);
			//getData();
			initViews();
			addListener();
			
			receiver = new YikuReceiver();
			this.registerReceiver(receiver, new IntentFilter(Constant.INVENTORY_BIZ1));
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
	//	listinventory = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listinventory");
	//	listinventory1 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listinventory1");
		username = getIntent().getStringExtra("username");
		tv_username = (TextView) findViewById(R.id.tv_inventory_form_username1);
		tv_username.setText(username);
		
		tv_pang_start = (TextView)findViewById(R.id.tv_inventory_date2);
		String inbond_date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		tv_pang_start.setText(inbond_date1);
		
		tv_pang_start1 = (TextView)findViewById(R.id.tv_inventory_date3);
		String inbond_date2 = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
		tv_pang_start1.setText(inbond_date2);
		
		
		tv_pang_date = (TextView)findViewById(R.id.tv_inventory_date1);
		String inbond_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		tv_pang_date.setText(inbond_date);
		
		tv_pang_date1 = (TextView)findViewById(R.id.tv_inventory_date4);
		String inbond_date4 = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
		tv_pang_date1.setText(inbond_date4);

		
		btn_inventory = (Button)findViewById(R.id.btn_inventory_form_inventory1);
		btn_cancel = (Button)findViewById(R.id.btn_inventory_form_cancel1);
	}
	private void addListener() {
		tv_pang_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				int year=c.get(Calendar.YEAR);
				int month=c.get(Calendar.MONTH);
				int day=c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				DatePickerDialog dialog = new DatePickerDialog(InventoryFormActivity1.this, new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						tv_pang_date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
					}
				}, year, month, day);
				dialog.show();
			}
		});
		tv_pang_date1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				int hourOfDay=c.get(Calendar.HOUR_OF_DAY);
				int min = c.get(Calendar.MINUTE);
				TimePickerDialog daDialog = new TimePickerDialog(InventoryFormActivity1.this, new OnTimeSetListener() {
					
					public void onTimeSet(TimePicker viewPicker, int hourOfDay, int min) {
						// TODO Auto-generated method stub
						tv_pang_date1.setText(hourOfDay+":"+min);
					}
				}, hourOfDay, min, true);
				daDialog.show();
			}
		});
		
		
		tv_pang_start1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Calendar c = Calendar.getInstance(Locale.CHINA);
					int hourOfDay=c.get(Calendar.HOUR_OF_DAY);
					int min = c.get(Calendar.MINUTE);
					TimePickerDialog daDialog = new TimePickerDialog(InventoryFormActivity1.this, new OnTimeSetListener() {
						
						public void onTimeSet(TimePicker viewPicker, int hourOfDay, int min) {
							// TODO Auto-generated method stub
							tv_pang_start1.setText(hourOfDay+":"+min);
						}
					}, hourOfDay, min, true);
					daDialog.show();
				}
			});
		
		tv_pang_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				int year=c.get(Calendar.YEAR);
				int month=c.get(Calendar.MONTH);
				int day=c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				DatePickerDialog dialog = new DatePickerDialog(InventoryFormActivity1.this, new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						tv_pang_start.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
					}
				}, year, month, day);
				dialog.show();
			}
		});
		
		
		
		
		
		
		

		
		
		btn_inventory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			//	linliaoName = sp_linliao.getSelectedItem().toString();
//				kufang = sp_kufang.getSelectedItem().toString();
//				kuwei = sp_kuwei.getSelectedItem().toString();
//				
				new AlertDialog.Builder(InventoryFormActivity1.this).setTitle(R.string.yiku)
				.setMessage(R.string.confirm_pangdian_message)
				.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String dateString = tv_pang_start.getText().toString().trim();
						String timeString = tv_pang_start1.getText().toString().trim();
						String dateString1 = tv_pang_date.getText().toString().trim();
						String timeString1 = tv_pang_date1.getText().toString().trim();
						
			//			Tools.showProgressDialog(TuikuFormActivity.this, "正在为您进行退库操作中...");
						Intent intent = new Intent(InventoryFormActivity1.this, InventoryBiz1.class);
//						intent.putExtra("linliaoName", linliaoName);
//						intent.putExtra("kufang", kufang);
//						intent.putExtra("kuwei", kuwei);
					intent.putExtra("dateString", dateString+" "+timeString);
//				//		intent.putExtra("timeString", timeString);
						intent.putExtra("dateString1", dateString1+" "+timeString1);
//				//		intent.putExtra("timeString1", timeString1);
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
	}
	//广播接受退库操作处理后的结果
	class YikuReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Tools.closeProgressDialog();
			ArrayList<String> listEPC = intent.getStringArrayListExtra("listEPC");
			if(listEPC.size()==0){//全部退库
				new AlertDialog.Builder(context).setMessage(R.string.inventory_done_message)
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
						Intent intent = new Intent(InventoryFormActivity1.this,MainActivity.class);
						intent.putExtra("username", username);
						startActivity(intent);
						finish();
					}
				}).create().show();
			}else{//已经存在已经退库的EPC，对应操作未生效
				View view = View.inflate(context, R.layout.lv_epc_exist_view, null);
				TextView tv_epc_already_exist = (TextView) view.findViewById(R.id.tv_epc_already_exist);
				tv_epc_already_exist.setText(R.string.tuiku_epc_exist);
				ListView lv = (ListView) view.findViewById(R.id.lv_bond_epc_exist);
				lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listEPC));
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
						Intent intent = new Intent(InventoryFormActivity1.this,MainActivity.class);
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
