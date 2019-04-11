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
import com.cmcid.adapter.InventoryShowAdapter;
import com.cmcid.adapter.TuikuAdapter;
import com.cmcid.adapter.YikuAdapter;
import com.cmcid.util.DevBeep;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Session;
import com.cmcid.util.logMa;
import com.handset.Device;

public class InventoryFormActivityShow extends BaseActivity {
	public static boolean isSimulation = true;// true为模拟 ,false为真实机器取数据
	private String username;
	private Button btn_inventory, btn_stop, btn_clear, btn_chaxun;
	private TApplication mTApplication;
	boolean bGetEpcThread = false;
	static int iTagCount = 0;
	private TextView tv_username;
	static ArrayList<HashMap<String, String>> listData1;// 去掉重复物资编码的EPC的list集合
	static ArrayList<HashMap<String, String>> listData;// 所有扫描到EPC的list集合listTuiku1;
	private ListView lv_show_standby;
	private InventoryShowAdapter adapter;

	public static InventoryFormActivityShow inventoryformactivityshow;
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

					 String sql1 = "select *   from    pt_InventoryResult";

					rs = state.executeQuery(sql1);
				
					while (rs.next()) {

						HashMap<String, String> info = new HashMap<String, String>();
						// if (! rs.next() ) break;
						String CCDCID1 = rs.getString("CCDCID");//物质编码
						String Name1 = rs.getString("Name");//名称
						String XiYuName1 = rs.getString("XiYuName");
						String Type1 = rs.getString("Type");//类型
						String UnitA1 = rs.getString("UnitA");//单位
						String UnitPrice1 = rs.getString("UnitPrice");//单价
						String StockNum1 = rs.getString("StockNum");//
						String Moneyt1 = rs.getString("Moneyt");//

						String MinStock1 = rs.getString("MinStock");//最低库存
						String MaxStock1 = rs.getString("MaxStock");//最高库存
						String ABCitem1 = rs.getString("ABCitem");//ABC分类
						String KuFang1 = rs.getString("KuFang");//

						String KuWei1 = rs.getString("KuWei");//
						String UserA1 = rs.getString("UserA");//
						String DebtOpu1 = rs.getString("DebtOpu");//
						
						String InventNum1 = rs.getString("InventNum");//实际数量
						String InventMoney1 = rs.getString("InventMoney");//实际金额 
						String Status1 = rs.getString("Status");//状态  

						logMa.d("martrin", "----lstate.executeQuery(sql1);");

						iTagCount++;

						info.put("CCDCID", CCDCID1);
						info.put("Name", Name1);
						info.put("XiYuName", XiYuName1);
						info.put("Type", Type1);
						info.put("UnitA", UnitA1);
						info.put("UnitPrice", UnitPrice1);
						info.put("StockNum", StockNum1);
						info.put("Moneyt", Moneyt1);
						info.put("MinStock", MinStock1);
						info.put("MaxStock", MaxStock1);
						info.put("ABCitem", ABCitem1);
						// info.put("count", "1");
						info.put("DebtOpu", DebtOpu1);
						 info.put("KuWei", KuWei1);
						 info.put("KuFang",KuFang1);
						 info.put("InventNum",InventNum1);
						 info.put("InventMoney",InventMoney1);
						 info.put("Status",Status1);
						 
						 
						 
//						strTempTime = new SimpleDateFormat(
//								"yyyy-MM-dd HH:mm:ss").format(new Date(System
//								.currentTimeMillis()));// 读卡时间
//						info.put("ReadTime", strTempTime);// 读卡时间

						info.put("no", String.valueOf(iTagCount));
						listData1.add(info);
						// logMa.d("martrin",
						// "----listData1.size="+listData1.size()+"\n");
						// }
						// }
					}
					rs.close();
					
					if (listData1.size() > 0) {
						handler.obtainMessage(RET_RECEIVE_DATA).sendToTarget();
						 DevBeep.PlayOK();
						try {
							sleep(150);
						} catch (InterruptedException e) {
						}
					}

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
			setContentView(R.layout.activity_inventory_show);

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
		inventoryformactivityshow = this;

		btn_inventory = (Button) findViewById(R.id.btn_show_Inventory);
		btn_stop = (Button) findViewById(R.id.btn_show_Stop);
		btn_clear = (Button) findViewById(R.id.btn_show_Clear);
		btn_stop.setEnabled(false);

		// 显示用户名
		tv_username = (TextView) findViewById(R.id.tv_show_username);
		// username = getIntent().getStringExtra("username");
		username = TApplication.strUser;
		tv_username.setText(username);

		btn_chaxun = (Button) findViewById(R.id.btn_show_form);

		lv_show_standby = (ListView) findViewById(R.id.lv_show_data);
		listData1 = new ArrayList<HashMap<String, String>>();
		// listData = new ArrayList<HashMap<String,String>>();
		adapter = new InventoryShowAdapter(this, listData1);
		lv_show_standby.setAdapter(adapter);

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
		btn_chaxun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new

				Intent intent = new Intent(InventoryFormActivityShow.this,
						InventoryActivityQurey.class);
				startActivity(intent);

			}
		});
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
