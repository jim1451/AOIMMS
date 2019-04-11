package com.cmcid.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.adapter.InbondAdapter;
import com.cmcid.util.DevBeep;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Session;
import com.cmcid.util.logMa;
import com.handset.Device;

public class InbondActivity extends BaseActivity {
	public static boolean isSimulation = true;// true为模拟 ,false为真实机器取数据
	private String readTime;// 开始寻卡时间
	private String createTime;// 停止寻卡时间，即插入EPC及其读取次数到表中的时间
	private String username;

	private TApplication mTApplication;
	boolean bGetEpcThread = false;
	static int iTagCount = 0;
	private TextView tv_username;

	private Button btn_inventory, btn_stop, btn_clear, btn_inbond;
	private ListView lv_inbond_standby;
	private InbondAdapter adapter;
	static ArrayList<HashMap<String, String>> listData1;// 去掉重复物资编码的EPC的list集合
	static ArrayList<HashMap<String, String>> listData;// 所有扫描到EPC的list集合

	public static InbondActivity inbondActivity;
	// message type
	public final static int RET_RECEIVE_DATA = 1;
//	protected static final int RET_RECEIVE_DATA1 = 0;
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RET_RECEIVE_DATA:
				// 更新UI并发出提示音
				adapter.notifyDataSetChanged();

				DevBeep.PlayOK();
				// Toast.makeText(InbondActivity.this, listData1+"", 0).show();
				break;
			case 2:
				tv_username.setText(TApplication.strUser);
				break;
			case Device.CMCID_MSG_PRESS_DOWN:
				logMa.d("martrin", "----CMCID_MSG_PRESS_DOWN-1---");
				btn_inventory.setEnabled(false);
				btn_stop.setEnabled(true);
				break;
			case Device.CMCID_MSG_PRESS_UP:
				logMa.d("martrin", "----CMCID_MSG_PRESS_UP-1---");
				btn_inventory.setEnabled(true);
				btn_stop.setEnabled(false);
				break;
				
	
			}
		};
	};

	class GetEpcThread extends Thread {
		private String samePrice;

		public void run() {
			Statement state = null;
			bGetEpcThread = true;
			String strTempTime;// 取当前时间
			String strTempp;
			while (bGetEpcThread == true) {
				try {
					
				//	handler.obtainMessage(RET_RECEIVE_DATA1).sendToTarget();
					state = TApplication.connection.createStatement();
					sleep(100);

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					int cnt = mTApplication.mService.device.getSize();
					// 7个epc,5样物资编码
					// 0002--柴机油,07050102000003
					// 0003--齿轮油,07050108000017
					// 0005--液压油,07070101000009
					// 0007--防渗布,15990100000003
					// 0011,0012,0013--黄原胶,12990000000029
				 String[] testEpc = { "00083000CCDC2015AAAAAAAA0078","00083000CCDC2015AAAAAAAA0001","00083000CCDC2015AAAAAAAA0036","00083000CCDC2015AAAAAAAA0222","00083000CCDC2015AAAAAAAA0223"};
			//		String[] testEpc = { "00083000CCDC2015AAAAAAAA0999","00083000CCDC2015AAAAAAAA0998"};
					
			//		String[] testEpc = { "3000CCDC2015AAAAAAAA0208","00083000CCDC2015AAAAAAAA0998"};
					//insert into pt_Goods (EPCID,CCDCID,XiYuName,Type) values ('3000CCDC2015AAAAAAAA0999','51050306000009','Blow-off valve 1/2" 02250100-042','gfgf')
//					String[] testEpc = { "08000002", "08000003", "08000005",
//							"08000007", "08000011", "08000012", "08000013" };

					if (isSimulation) {// true为模拟 ,false为真实机器取数据
						// test
						cnt = testEpc.length;
					}
					for (int j = 0; j < cnt; j++) {                    //在外面的一大循环,以扫描的表的个数为最大数 ///////////////////////////////////////////////////////////////////////////////////////////////////////  
						String epc;
						if (isSimulation) {// true为模拟 ,false为真实机器取数据
							epc = testEpc[j];
						} else {
							epc = mTApplication.mService.device.getItem();
						}

						String epcd = epc;
						epc = epc.trim().replace(" ", ""); // 去除两边及中间的空格
						epcd = epc;
						epcd = epcd.substring(4, epcd.length() - 0);
						logMa.d("martrin", "MYEpc=" + epc + ",epcd=" + epcd);// 例如读到了
						// 08000032
						// MYEpc=3000E2003016740C019116106D45
						String sql1 = "select * from pt_Goods where EPCID = '"
								+ epcd + "'";
					
						
						
						ResultSet rs1 = state.executeQuery(sql1);
						String CCDC = "";
						String name = "";
						String unit = "";
						String xiyuName = "";
						String type = "";
						if (rs1.next()) {// 判断扫描到的epc是否是物资基础表中的epc
							CCDC = rs1.getString("CCDCID");// 物资编码(主键)
							name = rs1.getString("Name");// 物资名称
							unit = rs1.getString("UnitA");// 单位
							xiyuName = rs1.getString("XiYuName");// 西语名称
							type = rs1.getString("Type");// 规格型号
							HashMap<String, String> item;
							boolean bFound = false;

							HashMap<String, String> itemsameEpc;

							// 判断是否扫描到的是同一个EPC
							for (int i = 0; i < listData.size(); i++) {
								item = listData.get(i);
								if (epcd.equals(item.get("epc"))) {
									item.put("count", String.valueOf((Integer
											.parseInt(item.get("count")) + 1)));// 增加EPC的次数
									strTempTime = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date(System
													.currentTimeMillis()));// 读卡时间
									item.put("ReadTime", strTempTime);

									bFound = true;
									break;
								}
							}
							if (!bFound) {// 另外的EPC，不同的EPC到这里
								boolean flag = false;
								HashMap<String, String> map;
								// 判断物资编码是否重复
								for (int x = 0; x < listData1.size(); x++) {
									map = listData1.get(x);
									if (CCDC.equals(map.get("ccdc"))) {
										// 重复则累加
										int rukuNum = Integer.parseInt(map
												.get("rukuNum")) + 1;
										map.put("rukuNum",
												String.valueOf(rukuNum));
										strTempp = map.get("unitPrice");
										double totalmoney = (rukuNum)
												* (Double.parseDouble(strTempp
														.toString()));
										DecimalFormat df = new DecimalFormat(
												".00");
										strTempp = String.valueOf(df
												.format(totalmoney));
										map.put("totalMoney", strTempp);

										HashMap<String, String> info1 = new HashMap<String, String>();
										info1.put("name", name);
										info1.put("xiyuName", xiyuName);
										info1.put("type", type);
										info1.put("unit", unit);
										info1.put("ccdc", CCDC);
										info1.put("rukuNum", "1");
										info1.put("epc", epcd);
										info1.put("count", "1");
										strTempTime = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss")
												.format(new Date(System
														.currentTimeMillis()));// 读卡时间
										info1.put("ReadTime", strTempTime);// 读卡时间
										info1.put("no",
												String.valueOf(iTagCount));// 序号
										info1.put("unitPrice", "12.23");
										info1.put("totalMoney", "12.23");
										info1.put("demoru", "");
										listData.add(info1);
										flag = true;
										break;
									}
								}
								if (!flag) {// 又不是原来的EPC，又不是原来的物资编码
									// 不重复则插入一条新数据
									iTagCount++;
									HashMap<String, String> info2 = new HashMap<String, String>();
									info2.put("rukuNum", "1");// 入库数量
									info2.put("name", name);// 物资名称
									info2.put("epc", epcd);// EPC号
									info2.put("xiyuName", xiyuName);// 西语名称
									info2.put("type", type);// 型号规格
									info2.put("unit", unit);// 单位
									info2.put("ccdc", CCDC);// 物资编码
									info2.put("count", "1");// 次数
									strTempTime = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date(System
													.currentTimeMillis()));// 读卡时间
									info2.put("ReadTime", strTempTime);// 读卡时间
									info2.put("no", String.valueOf(iTagCount));// 序号
									info2.put("unitPrice", "00.00");
									info2.put("totalMoney", "00.00");
									info2.put("demoru", "");
									listData1.add(info2);
									listData.add(info2);
								}
							}
						}
						// 如果有多个EPC把值存在MAP中

						rs1.close();
					}
					if (cnt > 0) {
						handler.obtainMessage(RET_RECEIVE_DATA).sendToTarget();
						// logMa.d("martrin",
						// "----obtainMessage(RET_RECEIVE_DATA)----");
						// DevBeep.PlayOK();
						try {
							sleep(150);
						} catch (InterruptedException e) {
						}
					}
					if (isSimulation) {// true为模拟 ,false为真实机器取数据
						break;
					}
				} catch (Exception e) {
					Log.i("UHF", e.toString());
					logMa.d("martrin", "----UHF----" + e.toString());
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
			setContentView(R.layout.activity_inbond);
			logMa.d("martrin", "----onCreate----");
			mTApplication = (TApplication) getApplication();
			mTApplication.setHandler(handler);
			DevBeep.init(this);

			initViews();
			setModelPower();
			addListener();
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}

	private void initViews() {
		inbondActivity = this;

		btn_inventory = (Button) findViewById(R.id.btn_inbond_Inventory);
		btn_stop = (Button) findViewById(R.id.btn_inbond_Stop);
		btn_clear = (Button) findViewById(R.id.btn_inbond_Clear);
		btn_stop.setEnabled(false);

		// 显示用户名
		tv_username = (TextView) findViewById(R.id.tv_inbond_username);
		// username = getIntent().getStringExtra("username");
	
		username = TApplication.strUser;
		tv_username.setText(username);

		// 入库按钮
		btn_inbond = (Button) findViewById(R.id.btn_inbond_form);

		lv_inbond_standby = (ListView) findViewById(R.id.lv_inbond_data);
		listData1 = new ArrayList<HashMap<String, String>>();
		listData = new ArrayList<HashMap<String, String>>();
		adapter = new InbondAdapter(this, listData1, listData);
		lv_inbond_standby.setAdapter(adapter);

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
				StartInventory();

				readTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(System.currentTimeMillis()));// 读卡时间
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
				listData.clear();
				adapter.notifyDataSetChanged();
				iTagCount = 0;
			}
		});
		btn_inbond.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// SharedPreferences
				// sharedPreferences=getSharedPreferences("share",
				// MODE_PRIVATE);
				// boolean isFirstRun=sharedPreferences.getBoolean("isFirstRun",
				// true);
				// Editor editor=sharedPreferences.edit();
				if (TApplication.isFistRun == false) {
					Log.e("debug", "第一次运行");
					// editor.putBoolean("isFirstRun", false);
					// editor.commit();
					Intent intent = new Intent(InbondActivity.this,
							LoginActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(InbondActivity.this,
							InbondFormActivity.class);
					
					
					intent.putExtra("username", username);
					intent.putExtra("listInbond", listData);// 7个有多少个EPC,比如有18个EPC
					intent.putExtra("listInbond1", listData1);// 5个
					// 有多少个物资编码如有1+11+1+1+1+1+1+1这么8种物资
				
				
					startActivity(intent);
				}

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		logMa.d("martrin", "----onStart----");
	}

	public void StartInventory() {
		if (mTApplication.mService != null) {
			mTApplication.mService.device.uhfInventory();
		}
		logMa.d("martrin", "----StartInventory----");
		btn_inventory.setEnabled(false);
		btn_stop.setEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		logMa.d("martrin", "----onResume----");
		if (bGetEpcThread == false) {
			GetEpcThread thread = new GetEpcThread();
			thread.start();
			bGetEpcThread = true;
			logMa.d("martrin", "----thread.start----");
		}
		if (mTApplication.bBind) {
			mTApplication.mService.device.setSendAlive(true);
			setModelPower();
			logMa.d("martrin", "----setModelPower----");
		}
		iTagCount = 0;

	}

	@Override
	public void onPause() {
		super.onPause();
		logMa.d("martrin", "----onPause----");
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

	/*
	 * @Override protected void onStop() { super.onStop(); if
	 * (!mTApplication.mService.device.getDeviceIsOpen()) {
	 * android.os.Process.killProcess(android.os.Process.myPid());
	 * System.out.println("Destroy"); } System.out.println("Destroy2"); }
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		inbondActivity = null;
		logMa.d("martrin", "----onDestroy----");
	}

	protected void stopInventory() {// 停止盘点
		if (mTApplication.mService != null) {
			mTApplication.mService.device.uhfStopInventory();
		}
		logMa.d("martrin", "----StopInventory----");
		btn_stop.setEnabled(false);
		btn_inventory.setEnabled(true);
	}

	private void setModelPower() {
		// 设置模式与电量
		mTApplication.mService.device.uhfPowerOn();
		mTApplication.mService.device.setModeUB(true);
		mTApplication.mService.device.setHandler(handler);
	}
}
