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
	public static boolean isSimulation = true;// trueΪģ�� ,falseΪ��ʵ����ȡ����
	private String readTime;// ��ʼѰ��ʱ��
	private String createTime;// ֹͣѰ��ʱ�䣬������EPC�����ȡ���������е�ʱ��
	private String username;

	private TApplication mTApplication;
	boolean bGetEpcThread = false;
	static int iTagCount = 0;
	private TextView tv_username;

	private Button btn_inventory, btn_stop, btn_clear, btn_inbond;
	private ListView lv_inbond_standby;
	private InbondAdapter adapter;
	static ArrayList<HashMap<String, String>> listData1;// ȥ���ظ����ʱ����EPC��list����
	static ArrayList<HashMap<String, String>> listData;// ����ɨ�赽EPC��list����

	public static InbondActivity inbondActivity;
	// message type
	public final static int RET_RECEIVE_DATA = 1;
//	protected static final int RET_RECEIVE_DATA1 = 0;
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RET_RECEIVE_DATA:
				// ����UI��������ʾ��
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
			String strTempTime;// ȡ��ǰʱ��
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
					// 7��epc,5�����ʱ���
					// 0002--�����,07050102000003
					// 0003--������,07050108000017
					// 0005--Һѹ��,07070101000009
					// 0007--������,15990100000003
					// 0011,0012,0013--��ԭ��,12990000000029
				 String[] testEpc = { "00083000CCDC2015AAAAAAAA0078","00083000CCDC2015AAAAAAAA0001","00083000CCDC2015AAAAAAAA0036","00083000CCDC2015AAAAAAAA0222","00083000CCDC2015AAAAAAAA0223"};
			//		String[] testEpc = { "00083000CCDC2015AAAAAAAA0999","00083000CCDC2015AAAAAAAA0998"};
					
			//		String[] testEpc = { "3000CCDC2015AAAAAAAA0208","00083000CCDC2015AAAAAAAA0998"};
					//insert into pt_Goods (EPCID,CCDCID,XiYuName,Type) values ('3000CCDC2015AAAAAAAA0999','51050306000009','Blow-off valve 1/2" 02250100-042','gfgf')
//					String[] testEpc = { "08000002", "08000003", "08000005",
//							"08000007", "08000011", "08000012", "08000013" };

					if (isSimulation) {// trueΪģ�� ,falseΪ��ʵ����ȡ����
						// test
						cnt = testEpc.length;
					}
					for (int j = 0; j < cnt; j++) {                    //�������һ��ѭ��,��ɨ��ı�ĸ���Ϊ����� ///////////////////////////////////////////////////////////////////////////////////////////////////////  
						String epc;
						if (isSimulation) {// trueΪģ�� ,falseΪ��ʵ����ȡ����
							epc = testEpc[j];
						} else {
							epc = mTApplication.mService.device.getItem();
						}

						String epcd = epc;
						epc = epc.trim().replace(" ", ""); // ȥ�����߼��м�Ŀո�
						epcd = epc;
						epcd = epcd.substring(4, epcd.length() - 0);
						logMa.d("martrin", "MYEpc=" + epc + ",epcd=" + epcd);// ���������
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
						if (rs1.next()) {// �ж�ɨ�赽��epc�Ƿ������ʻ������е�epc
							CCDC = rs1.getString("CCDCID");// ���ʱ���(����)
							name = rs1.getString("Name");// ��������
							unit = rs1.getString("UnitA");// ��λ
							xiyuName = rs1.getString("XiYuName");// ��������
							type = rs1.getString("Type");// ����ͺ�
							HashMap<String, String> item;
							boolean bFound = false;

							HashMap<String, String> itemsameEpc;

							// �ж��Ƿ�ɨ�赽����ͬһ��EPC
							for (int i = 0; i < listData.size(); i++) {
								item = listData.get(i);
								if (epcd.equals(item.get("epc"))) {
									item.put("count", String.valueOf((Integer
											.parseInt(item.get("count")) + 1)));// ����EPC�Ĵ���
									strTempTime = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date(System
													.currentTimeMillis()));// ����ʱ��
									item.put("ReadTime", strTempTime);

									bFound = true;
									break;
								}
							}
							if (!bFound) {// �����EPC����ͬ��EPC������
								boolean flag = false;
								HashMap<String, String> map;
								// �ж����ʱ����Ƿ��ظ�
								for (int x = 0; x < listData1.size(); x++) {
									map = listData1.get(x);
									if (CCDC.equals(map.get("ccdc"))) {
										// �ظ����ۼ�
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
														.currentTimeMillis()));// ����ʱ��
										info1.put("ReadTime", strTempTime);// ����ʱ��
										info1.put("no",
												String.valueOf(iTagCount));// ���
										info1.put("unitPrice", "12.23");
										info1.put("totalMoney", "12.23");
										info1.put("demoru", "");
										listData.add(info1);
										flag = true;
										break;
									}
								}
								if (!flag) {// �ֲ���ԭ����EPC���ֲ���ԭ�������ʱ���
									// ���ظ������һ��������
									iTagCount++;
									HashMap<String, String> info2 = new HashMap<String, String>();
									info2.put("rukuNum", "1");// �������
									info2.put("name", name);// ��������
									info2.put("epc", epcd);// EPC��
									info2.put("xiyuName", xiyuName);// ��������
									info2.put("type", type);// �ͺŹ��
									info2.put("unit", unit);// ��λ
									info2.put("ccdc", CCDC);// ���ʱ���
									info2.put("count", "1");// ����
									strTempTime = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date(System
													.currentTimeMillis()));// ����ʱ��
									info2.put("ReadTime", strTempTime);// ����ʱ��
									info2.put("no", String.valueOf(iTagCount));// ���
									info2.put("unitPrice", "00.00");
									info2.put("totalMoney", "00.00");
									info2.put("demoru", "");
									listData1.add(info2);
									listData.add(info2);
								}
							}
						}
						// ����ж��EPC��ֵ����MAP��

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
					if (isSimulation) {// trueΪģ�� ,falseΪ��ʵ����ȡ����
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

		// ��ʾ�û���
		tv_username = (TextView) findViewById(R.id.tv_inbond_username);
		// username = getIntent().getStringExtra("username");
	
		username = TApplication.strUser;
		tv_username.setText(username);

		// ��ⰴť
		btn_inbond = (Button) findViewById(R.id.btn_inbond_form);

		lv_inbond_standby = (ListView) findViewById(R.id.lv_inbond_data);
		listData1 = new ArrayList<HashMap<String, String>>();
		listData = new ArrayList<HashMap<String, String>>();
		adapter = new InbondAdapter(this, listData1, listData);
		lv_inbond_standby.setAdapter(adapter);

		if (Session.getSimulation().equals("1")) {
			isSimulation = true;// trueΪģ�� ,falseΪ��ʵ����ȡ����
		} else {
			isSimulation = false;// trueΪģ�� ,falseΪ��ʵ����ȡ����
		}
	}

	private void addListener() {
		// Ѱ��
		btn_inventory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StartInventory();

				readTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(System.currentTimeMillis()));// ����ʱ��
			}
		});
		// ֹͣѰ��
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopInventory();
			}
		});
		// ���
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
					Log.e("debug", "��һ������");
					// editor.putBoolean("isFirstRun", false);
					// editor.commit();
					Intent intent = new Intent(InbondActivity.this,
							LoginActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(InbondActivity.this,
							InbondFormActivity.class);
					
					
					intent.putExtra("username", username);
					intent.putExtra("listInbond", listData);// 7���ж��ٸ�EPC,������18��EPC
					intent.putExtra("listInbond1", listData1);// 5��
					// �ж��ٸ����ʱ�������1+11+1+1+1+1+1+1��ô8������
				
				
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

	protected void stopInventory() {// ֹͣ�̵�
		if (mTApplication.mService != null) {
			mTApplication.mService.device.uhfStopInventory();
		}
		logMa.d("martrin", "----StopInventory----");
		btn_stop.setEnabled(false);
		btn_inventory.setEnabled(true);
	}

	private void setModelPower() {
		// ����ģʽ�����
		mTApplication.mService.device.uhfPowerOn();
		mTApplication.mService.device.setModeUB(true);
		mTApplication.mService.device.setHandler(handler);
	}
}
