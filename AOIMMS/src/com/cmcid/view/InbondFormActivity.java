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

import android.R.integer;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.biz.InbondBiz;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Tools;
import com.cmcid.util.logMa;

public class InbondFormActivity extends BaseActivity{
	
	private TextView tv_username;
	private String username,ISPname,kufang,kuwei,piaoHao,zhanghu;
	private ArrayList<HashMap<String, String>> listInbond;//对应的EPC
	private ArrayList<HashMap<String, String>> listInbond1;//对应的物资
	private TextView tv_inbond_date;
	private EditText et_PiaoHao;
	private Spinner sp_ISPname,sp_kufang,sp_kuwei,sp_zhanghu;
	private ArrayAdapter<String> adapter1,adapter2,adapter3,adapter4;
	private Button btn_inbond,btn_cancel,btn_return;
	
	
	
	
	private static final int GET_ISP_NAME = 1;
	private static final int GET_KUFANG = 2;
	private static final int GET_KUWEI = 3;
	private static final int GET_ZHANGHU = 4;
	private static final int HUANYUAN = 5;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = msg.getData();
			switch(msg.what){
			case GET_ISP_NAME:
				//下拉显示供应商名称
				ArrayList<String> list1 = bundle.getStringArrayList("listISP");
				adapter1 = new ArrayAdapter<String>(InbondFormActivity.this, android.R.layout.simple_spinner_item, list1);
				sp_ISPname.setAdapter(adapter1);
				break;
			case GET_KUFANG:
				//下拉显示库房名称
				ArrayList<String> list2 = bundle.getStringArrayList("listKufang");
				adapter2 = new ArrayAdapter<String>(InbondFormActivity.this, android.R.layout.simple_spinner_item, list2);
				sp_kufang.setAdapter(adapter2);
				break;
			case GET_KUWEI:
				//下拉显示库位名称
				ArrayList<String> list3 = bundle.getStringArrayList("listKuwei");
				adapter3 = new ArrayAdapter<String>(InbondFormActivity.this, android.R.layout.simple_spinner_item, list3);
				sp_kuwei.setAdapter(adapter3);
				break;
			case GET_ZHANGHU:
				
				ArrayList<String> list4 = bundle.getStringArrayList("listZhanghu");
				adapter4 = new ArrayAdapter<String>(InbondFormActivity.this, android.R.layout.simple_spinner_item, list4);
				sp_zhanghu.setAdapter(adapter4);
				break;
				
			case HUANYUAN:

				Toast.makeText(getApplicationContext(), "货物数据已经还原！", 0).show();
				break;
			}
		};
	};
	private InbondReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_inbond_form);
			getData();
			initViews();
			addListener();
			
			receiver = new InbondReceiver();
			this.registerReceiver(receiver, new IntentFilter(Constant.INBOND_BIZ));
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
		//从表中获得ISP 库房  库位 名称
		new Thread(){
			public void run() {
				Statement state = null;
				try {
					state = TApplication.connection.createStatement();
					String sql1 = "select * from pt_ISP";
					String sql2 = "select * from pt_KuFangT";
					String sql3 = "select * from pt_KuWeiT";
					String sql4 = "select * from pt_Debt where DebtOptName = '海运库'";
					//ISP
					ResultSet rs = state.executeQuery(sql1);
					ArrayList<String> listISP = new ArrayList<String>(); 
					while(rs.next()){
						String ISPnameItem = rs.getString("ISPName");
						listISP.add(ISPnameItem);
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
					//账户类别
					rs = state.executeQuery(sql4);
					ArrayList<String> listZhanghu = new ArrayList<String>();
					while(rs.next()){
						String zhanghuItem = rs.getString("DebtOptName");
						System.out.println("ItemzhanghuItemzhanghuItem"+zhanghuItem);
						listZhanghu.add(zhanghuItem);
					}
					rs.close();
					
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("listISP", listISP);
					msg.what = GET_ISP_NAME;
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
					
					msg = new Message();
					bundle = new Bundle();
					bundle.putStringArrayList("listZhanghu", listZhanghu);
					msg.what = GET_ZHANGHU;
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
		tv_username = (TextView)findViewById(R.id.tv_inbond_form_username);
		username = getIntent().getStringExtra("username");
		tv_username.setText(username);
		listInbond = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listInbond");
		listInbond1 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listInbond1");
		logMa.d("martrin", "----EPCNum=" + listInbond.size()+",CCDCIDNum="+listInbond1.size());
		
		//获得当前时间
		tv_inbond_date = (TextView)findViewById(R.id.tv_inbond_date);
		String inbond_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		tv_inbond_date.setText(inbond_date);
		
		sp_ISPname = (Spinner)findViewById(R.id.sp_inbond_ISPname);
		et_PiaoHao = (EditText)findViewById(R.id.et_inbond_piaohao);
		sp_kufang = (Spinner)findViewById(R.id.sp_inbond_kufang);
		sp_kuwei = (Spinner)findViewById(R.id.sp_inbond_kuwei);
		sp_zhanghu = (Spinner) findViewById(R.id.sp_inbond_zhanghu);
		
		
		btn_inbond = (Button)findViewById(R.id.btn_inbond_form_inbond);
		btn_cancel = (Button)findViewById(R.id.btn_inbond_form_cancel);
		btn_return =  (Button) findViewById( R.id.btn_huangyuan_form_inbond);
	}
	private void addListener() {
		//DatePickerDialog 选择时间日期
		tv_inbond_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				int year=c.get(Calendar.YEAR);
				int month=c.get(Calendar.MONTH);
				int day=c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				DatePickerDialog dialog = new DatePickerDialog(InbondFormActivity.this, new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						tv_inbond_date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
					}
				}, year, month, day);
				dialog.show();
			}
		});
		//入库
		btn_inbond.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ISPname = sp_ISPname.getSelectedItem().toString();
				kufang = sp_kufang.getSelectedItem().toString();
				kuwei = sp_kuwei.getSelectedItem().toString();
				piaoHao = et_PiaoHao.getText().toString();
				zhanghu = sp_zhanghu.getSelectedItem().toString();
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(InbondFormActivity.this);
				dialog.setTitle(R.string.inbond)
				.setMessage(R.string.confirm_inbond_message)
				.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (piaoHao.isEmpty()){
							Toast.makeText(InbondFormActivity.this, "发票号不能为空!",Toast.LENGTH_SHORT).show();
							return;
						}
				//		Tools.showProgressDialog(InbondFormActivity.this, "正在为您进行入库操作中...");
						Intent intent = new Intent(InbondFormActivity.this,InbondBiz.class);
						intent.putExtra("ISPname", ISPname);
						intent.putExtra("kufang", kufang);
						intent.putExtra("kuwei", kuwei);
						intent.putExtra("zhanghu", zhanghu);
						
						intent.putExtra("piaoHao", piaoHao);
						intent.putExtra("username", username);
						intent.putExtra("listInbond", listInbond);// 有多少个EPC,比如有18个EPC
						intent.putExtra("listInbond1", listInbond1);// 有多少个物资编码如有1+11+1+1+1+1+1+1这么8种物资
						
						startService(intent);//启动入库服务
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
	
	
	
	
	
	
	//处理入库service处理后的结果
	class InbondReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Tools.closeProgressDialog();
			ArrayList<String> listEPC = intent.getStringArrayListExtra("listEPC");
			if(listEPC.size()==0){//全部入库，弹窗提示
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setMessage(R.string.inbond_done_message)
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
						Intent intent = new Intent(InbondFormActivity.this,MainActivity.class);
						intent.putExtra("username", username);
						startActivity(intent);
						finish();
					}
				}).create().show();
			}else{//有已经在库中的EPC，提示并显示
				View view = View.inflate(context, R.layout.lv_epc_exist_view, null);
				TextView tv_epc_already_exist = (TextView) view.findViewById(R.id.tv_epc_already_exist);
				tv_epc_already_exist.setText(R.string.inbond_epc_exist);
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
						Intent intent = new Intent(InbondFormActivity.this,MainActivity.class);
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
