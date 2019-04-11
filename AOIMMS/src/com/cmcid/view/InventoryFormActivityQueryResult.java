package com.cmcid.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.adapter.InventoryResultAdapter;
import com.cmcid.adapter.InventoryShowAdapter;
import com.cmcid.adapter.TuikuAdapter;
import com.cmcid.adapter.YikuAdapter;
import com.cmcid.util.DevBeep;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Session;
import com.cmcid.util.logMa;
import com.handset.Device;

public class InventoryFormActivityQueryResult extends BaseActivity {
	public static boolean isSimulation = true;// true为模拟 ,false为真实机器取数据
	private String username;
	private Button btn_inventory, btn_stop, btn_clear, btn_yiku;
	private TApplication mTApplication;
	boolean bGetEpcThread = false;
	static int iTagCount = 0;
	private TextView tv_username;
	static ArrayList<HashMap<String, String>> listData1;// 去掉重复物资编码的EPC的list集合
	static ArrayList<HashMap<String, String>> listData;// 所有扫描到EPC的list集合listTuiku1;
	private ListView lv_showu_standby;
	private InventoryResultAdapter adapter;
	private   String ziduan,tiaojian,chaxuzhi,zhi;
	
	public static InventoryFormActivityQueryResult inventoryformactivityqueryresult;
	// message type
	public final static int RET_RECEIVE_DATA = 1;
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RET_RECEIVE_DATA:
				// 更新UI并发出提示音
				adapter.notifyDataSetChanged();
				DevBeep.PlayOK();
				break;
			case 4:
				tv_username.setText(TApplication.strUser);
				break;
			case Device.CMCID_MSG_PRESS_DOWN:
				btn_inventory.setEnabled(false);
				btn_stop.setEnabled(true);
				break;
			case Device.CMCID_MSG_PRESS_UP:
				btn_inventory.setEnabled(true);
				btn_stop.setEnabled(false);
				break;
			}
		};
	};

	class GetEpcThread extends Thread {
		private String strTempTime;

		public void run() {
			Statement state = null;
			// bGetEpcThread = true;
			if (bGetEpcThread == true) {
				try {
					state = TApplication.connection.createStatement();
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {

					ResultSet rs;
				String sql1="";

					if (tiaojian.equals("like") ) {
							String 	haoString = "'%";
							String     haoString1 = "%'";
	                    sql1 = "select *   from    pt_InventoryResult  where "+ziduan+" "+tiaojian+" "+haoString+""+chaxuzhi+""+haoString1+" ";

					}else {
						String     haoString = "'";
						sql1 = "select *   from    pt_InventoryResult  where "+ziduan+" "+tiaojian+"  "+haoString+""+chaxuzhi+""+haoString+"";
					}
					
                
					rs = state.executeQuery(sql1);
					// HashMap<String,String> info = new
					// HashMap<String,String>();
					while (rs.next()) {

						HashMap<String, String> info = new HashMap<String, String>();
						// if (! rs.next() ) break;
						String CCDCID1 = rs.getString("CCDCID");
						String Name1 = rs.getString("Name");
						String XiYuName1 = rs.getString("XiYuName");
						String Type1 = rs.getString("Type");
						String UnitA1 = rs.getString("UnitA");
						String UnitPrice1 = rs.getString("UnitPrice");
				//		String RuKuNum1 = rs.getString("RuKuNum");
						String Moneyt1 = rs.getString("Moneyt");

						String MinStock1 = rs.getString("MinStock");
						String MaxStock1 = rs.getString("MaxStock");
						String ABCitem1 = rs.getString("ABCitem");
						String KuFang1 = rs.getString("KuFang");

						String KuWei1 = rs.getString("KuWei");
						String UserA1 = rs.getString("UserA");
						String DebtOpu1 = rs.getString("DebtOpu");
					//	String rukuDate1 = rs.getString("rukuDate");

						logMa.d("martrin", "----lstate.executeQuery(sql1);");

						// if(!flag){
						// 不重复则插入一条新数据
						iTagCount++;
						// HashMap<String,String> info = new
						// HashMap<String,String>();
						// if (! rs.next() ) break;
						info.put("CCDCID", CCDCID1);
						info.put("Name", Name1);
						info.put("XiYuName", XiYuName1);
						info.put("Type", Type1);
						info.put("UnitA", UnitA1);
						info.put("Moneyt", Moneyt1);
						info.put("MinStock", MinStock1);
						info.put("MaxStock", MaxStock1);
						info.put("UnitPrice", UnitPrice1);
						// info.put("count", "1");
						info.put("DebtOpu", DebtOpu1);
						// info.put("KuWeiHao", KuWeiHao);
						// info.put("kuFangHao", kuFangHao);
						strTempTime = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date(System
								.currentTimeMillis()));// 读卡时间
						info.put("ReadTime", strTempTime);// 读卡时间
						// listData.add(info);
						info.put("no", String.valueOf(iTagCount));
						listData1.add(info);
						// logMa.d("martrin",
						// "----listData1.size="+listData1.size()+"\n");
						// }
						// }
					}
					rs.close();
					// }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////大循环
					if (listData1.size() > 0) {
						handler.obtainMessage(RET_RECEIVE_DATA).sendToTarget();
						// DevBeep.PlayOK();
						try {
							sleep(150);
						} catch (InterruptedException e) {
						}
					}
					// if (isSimulation) {// true为模拟 ,false为真实机器取数据
					// break;
					// }
					// bGetEpcThread = false;

				} catch (Exception e) {
					Log.i("UHF", e.toString());
				} finally {
					try {
						state.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_query_result);

			Intent received = getIntent();	
			ziduan = received.getStringExtra("ziduan");
			
			if (ziduan.equals("物质编码")) {
				ziduan = "CCDCID";
			}
			if (ziduan.equals("数量")) {
				ziduan = "StockNum";
			}
			if (ziduan.equals("价格")) {
				ziduan = "UnitPrice";
			}
			if (ziduan.equals("规格型号")) {
				ziduan = "Type";
			}
			if (ziduan.equals("西语名称")) {
				ziduan = "XiYuName";
			}
			
			
			
		     tiaojian = received.getStringExtra("tiaojian");
		     chaxuzhi = received.getStringExtra("chaxuzhi");
		//	  zhi = received.getStringExtra("zhi");
			
			
			mTApplication = (TApplication) getApplication();
			mTApplication.setHandler(handler);
			DevBeep.init(this);

			initViews();
			setModelPower();
			addListener();
			logMa.d("martrin", "----addListener()=====");
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}

	private void initViews() {
		inventoryformactivityqueryresult = this;

		btn_inventory = (Button) findViewById(R.id.btn_query1_Inventory);
		btn_stop = (Button) findViewById(R.id.btn_query1_Stop);
		btn_clear = (Button) findViewById(R.id.btn_query1_Clear);
		btn_stop.setEnabled(false);

		// 显示用户名
		// tv_username = (TextView) findViewById(R.id.tv_show_username);
		// // username = getIntent().getStringExtra("username");
		// username=TApplication.strUser;
		// tv_username.setText(username);

		// btn_yiku = (Button) findViewById(R.id.btn_show_form);

		lv_showu_standby = (ListView) findViewById(R.id.lv_query1_data);
		listData1 = new ArrayList<HashMap<String, String>>();
		// listData = new ArrayList<HashMap<String,String>>();
		adapter = new InventoryResultAdapter(this, listData1);
		lv_showu_standby.setAdapter(adapter);

		if (Session.getSimulation().equals("1")) {
			isSimulation = true;// true为模拟 ,false为真实机器取数据
		} else {
			isSimulation = false;// true为模拟 ,false为真实机器取数据
		}

	}

	private void addListener() {
		// 寻卡
		btn_inventory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTApplication.mService != null) {
					mTApplication.mService.device.uhfInventory();
				}
				btn_inventory.setEnabled(false);
				btn_stop.setEnabled(true);
			}
		});
		// 停止寻卡
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopInventory();
			}
		});
		// 清除
		btn_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listData1.clear();
				// listData.clear();
				adapter.notifyDataSetChanged();
				iTagCount = 0;
			}
		});
		// btn_yiku.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Intent intent = new
		//
		// Intent intent = new
		// Intent(InventoryFormActivityQueryResult.this,InventoryActivityQurey.class);
		// startActivity(intent);
		//
		//
		// }
		// });
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (bGetEpcThread == false) {
			GetEpcThread thread = new GetEpcThread();
			thread.start();
			// logMa.d("martrin", "----thread.start();=====");
			bGetEpcThread = true;
		}
		if (mTApplication.bBind) {
			mTApplication.mService.device.setSendAlive(true);
			setModelPower();
		}
		iTagCount = 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (bGetEpcThread) {
			stopInventory();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bGetEpcThread = false;
		}
		if (mTApplication.mService != null) {
			mTApplication.mService.device.uhfPowerOff();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void stopInventory() {
		if (mTApplication.mService != null) {
			mTApplication.mService.device.uhfStopInventory();
		}
		btn_stop.setEnabled(false);
		btn_inventory.setEnabled(true);
	}

	private void setModelPower() {
		mTApplication.mService.device.uhfPowerOn();
		mTApplication.mService.device.setModeUB(true);
		mTApplication.mService.device.setHandler(handler);
	}
}
