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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.adapter.InventoryAdapter;
import com.cmcid.adapter.TuikuAdapter;
import com.cmcid.adapter.YikuAdapter;
import com.cmcid.util.DevBeep;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.Session;
import com.cmcid.view.PopMenu.OnItemClickListener;
import com.handset.Device;

public class InventoryActivity extends BaseActivity implements
		OnItemClickListener {
	public static boolean isSimulation = true;// trueΪģ�� ,falseΪ��ʵ����ȡ����
	private String username;
	private Button btn_inventory, btn_stop, btn_clear, btn_yiku;
	private TApplication mTApplication;
	boolean bGetEpcThread = false;
	static int iTagCount = 0;
	private TextView tv_username;
	static ArrayList<HashMap<String, String>> listData1;// ȥ���ظ����ʱ����EPC��list����
	static ArrayList<HashMap<String, String>> listData;// ����ɨ�赽EPC��list����listTuiku1;
	private ListView lv_inventory_standby;
	private InventoryAdapter adapter;
	private ImageView iv_nav_inventory;
	private PopMenu popMenu;
	public static InventoryActivity inventoryActivity;
	// message type
	public final static int RET_RECEIVE_DATA = 1;
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RET_RECEIVE_DATA:
				// ����UI��������ʾ��
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

		public void run(){   	
			Statement state = null;
			bGetEpcThread = true;
			while(bGetEpcThread == true){
				try{
					state = TApplication.connection.createStatement();
					sleep(100);
				}
				catch (InterruptedException e){e.printStackTrace();}
				catch (SQLException e) {e.printStackTrace();}
				try
				{
					int cnt = mTApplication.mService.device.getSize();
					
//					String[] testEpc = { "08000002", "08000003", "08000005",
//							"08000007", "08000011", "08000012", "08000013" };   00083000CCDC2015AAAAAAAA0078
					
					
					
					 String[] testEpc = { "00083000CCDC2015AAAAAAAA0151","00083000CCDC2015AAAAAAAA0001","00083000CCDC2015AAAAAAAA0036","00083000CCDC2015AAAAAAAA0222","00083000CCDC2015AAAAAAAA0223"};//��ȡEPC��ֵ
					if (isSimulation) {// trueΪģ�� ,falseΪ��ʵ����ȡ����
						// test
						cnt = testEpc.length;
					}
					
					
					
					for(int j = 0;j < cnt; j++)
					{
						String epc;
						if (isSimulation) {// trueΪģ�� ,falseΪ��ʵ����ȡ����
							epc = testEpc[j];
						} else {
							epc = mTApplication.mService.device.getItem();
						}
						
						
						//String epc =  mTApplication.mService.device.getItem();
						epc = epc.trim().replace(" ", "");//ȥ�����߼��м�Ŀո�
						epc = epc.substring(4, epc.length());

//					String	  sql1 = "SELECT      RuKuID, pt_Stock.EPCID as EPCID, pt_Stock.CCDCID as CCDCID, RuKuNum, UnitPrice, Moneyt, ISPid, PiaoHao, rukuDate, kuFangHao, KuWeiHao, OpID, Demo, " +
//								"(select top 1 EPCID from pt_EPCOri where pt_EPCOri.CCDCID = pt_Stock.CCDCID) as EPCIDA, " +
//								"(select top 1 Name from pt_Goods where pt_Goods.CCDCID = pt_Stock.CCDCID) as Name, " +
//								"(select top 1 XiYuName from pt_Goods where pt_Goods.CCDCID = pt_Stock.CCDCID) as XiYuName, " +
//								"(select top 1 Type from pt_Goods where pt_Goods.CCDCID = pt_Stock.CCDCID) as Type, " +
//								"(select top 1 UnitA from pt_Goods where pt_Goods.CCDCID = pt_Stock.CCDCID) as UnitA, " +
//								"(select top 1 DebtOpt from pt_EPCOri where pt_EPCOri.CCDCID = pt_Stock.CCDCID) as DebtOpt "+
//								"FROM pt_Stock, pt_EPCOri where pt_EPCOri.CCDCID = pt_Stock.CCDCID and pt_EPCOri.EPCID = '"+ epc +"' ";
						String	  sql1 ="SELECT pt_Stock.RuKuID as RuKuID, pt_Stock.EPCID as EPCID, pt_Stock.CCDCID as CCDCID, pt_Stock.RuKuNum as RuKuNum, pt_Stock.UnitPrice as UnitPrice, pt_Stock.Moneyt as Moneyt, "
								+"pt_Stock.ISPid as ISPid, pt_Stock.PiaoHao as PiaoHao, pt_Stock.rukuDate as rukuDate,"
//								+"pt_KuFangT.kuFangName as kuFangHao,"
//								+"pt_KuWeiT.KuWeiName as KuWeiHao,"

								+"pt_KuFangT.kuFangName as kuFangName,"
								+"pt_KuWeiT.KuWeiName as KuWeiName,"
								+"pt_KuFangT.kuFangHao as kuFangHao,"
								+"pt_KuWeiT.KuWeiHao as KuWeiHao,"
								
								
								
								+"pt_EPCOri.EPCID as EPCIDA,"
								+"pt_EPCOri.UnitPricf as UnitPricf,"
								+"pt_Debt.DebtOptName as DebtOpt,"
								+"pt_EPCOri.DebtOpt as DebtOpta,"
								+"pt_CCDC.Name as Name,"
								+"pt_CCDC.XiYuName as XiYuName,"
								+"pt_CCDC.Type as Type,"
								+"pt_CCDC.UnitA as UnitA,OpID,"
								+"pt_Stock.Demo as Demo"
								+" FROM pt_Stock, pt_EPCOri, pt_CCDC, pt_KuFangT, pt_KuWeiT, pt_Debt"
								+" where pt_EPCOri.CCDCID = pt_Stock.CCDCID "
								+" and pt_EPCOri.UnitPricf = pt_Stock.UnitPrice"
								+" and pt_CCDC.CCDCID = pt_Stock.CCDCID"
								+" and pt_EPCOri.kuFangHaoOri = pt_KuFangT.kuFangHao"
								+" and pt_EPCOri.KuWeiHaoOri = pt_KuWeiT.KuWeiHao"
								+" and pt_Debt.DebtOptID = pt_EPCOri.DebtOpt"
								+" and pt_EPCOri.EPCID = '"+epc+"' ";
						
						

						ResultSet rs1 = state.executeQuery(sql1);
						String CCDC = "";
						String name = "";
						String RuKuNum = "";
						String unit = "";
						String xiyuName = "";
						String type = "";
						String unitPrice = "";
						String EPCID = "";
						String DebtOpu = "";
						String kuFangHao = "";
						String KuWeiHao ="";
						String kuFangName = "";
						String KuWeiName = "";
						if(rs1.next()){
							CCDC = rs1.getString("CCDCID");
							EPCID = rs1.getString("EPCID");
				//			unitPrice =  rs1.getString("UnitPrice");
							RuKuNum =  rs1.getString("RuKuNum");
							name = rs1.getString("Name");
							unit = rs1.getString("UnitA");
							xiyuName = rs1.getString("XiYuName");
							type = rs1.getString("Type");//
							DebtOpu = rs1.getString("DebtOpt");//unitPrice
							unitPrice = rs1.getString("unitPrice");
							kuFangHao = rs1.getString("kuFangHao");
							KuWeiHao=rs1.getString("KuWeiHao");
							kuFangName = rs1.getString("kuFangName");
							KuWeiName = rs1.getString("KuWeiName");
//							String sql2 = "select * from pt_RuKuT where EPCID = '"+epc+"'";
//							rs1 = state.executeQuery(sql2);
//							while(rs1.next()){
//								unitPrice = rs1.getString("UnitPrice");
//							}
							
							HashMap<String,String> item;
							boolean bFound = false;
							//�ж��Ƿ�ɨ�赽����ͬһ��EPC
							for(int i=0;i < listData.size();i++){
								item = listData.get(i);
								if(epc.equals(item.get("epc")))
								{
									item.put("count", String.valueOf((Integer.parseInt(item.get("count")) + 1)));
									strTempTime = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date(System
													.currentTimeMillis()));// ����ʱ��
									item.put("ReadTime", strTempTime);
									
									
									bFound = true;
									break;
								}
							}
							if(!bFound){
								boolean flag = false;
								HashMap<String, String> map;
								//�ж����ʱ����Ƿ��ظ�
								for(int x=0;x<listData1.size();x++){
									map = listData1.get(x);
									if(CCDC.equals(map.get("ccdc"))){
										//�ظ����ۼ�
										int chukuNum = Integer.parseInt(map.get("chukuNum"))+1;
										map.put("chukuNum", String.valueOf(chukuNum));
										
										HashMap<String,String> info = new HashMap<String,String>();
										info.put("name", name);
										info.put("RuKuNum", RuKuNum);
										info.put("xiyuName", xiyuName);
										info.put("type", type);
										info.put("unit", unit);
										info.put("ccdc", CCDC);
										info.put("chukuNum", "1");
										info.put("unitPrice", unitPrice);
										info.put("epc", epc);
										info.put("count", "1");
										info.put("DebtOpu", DebtOpu);//
										info.put("KuWeiHao", KuWeiHao);
										info.put("kuFangHao", kuFangHao);
										info.put("kuFangName", kuFangName);
										info.put("KuWeiName", KuWeiName);
										strTempTime = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss")
												.format(new Date(System
														.currentTimeMillis()));// ����ʱ��
										info.put("ReadTime", strTempTime);// ����ʱ��
										listData.add(info);
										flag = true;
										break;
									}
								}
								if(!flag){
									//���ظ������һ��������
									iTagCount++;
									HashMap<String,String> info = new HashMap<String,String>();          			 
									info.put("chukuNum", "1");
									info.put("epc", epc);  
									info.put("name", name);
									info.put("RuKuNum", RuKuNum);
									info.put("xiyuName", xiyuName);
									info.put("type", type);
									info.put("unit", unit);
									info.put("ccdc", CCDC);
									info.put("unitPrice", unitPrice);
									info.put("count", "1");    
									info.put("DebtOpu", DebtOpu);
									info.put("KuWeiHao", KuWeiHao);
									info.put("kuFangHao", kuFangHao);
									info.put("kuFangName", kuFangName);
									info.put("KuWeiName", KuWeiName);
									strTempTime = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date(System
													.currentTimeMillis()));// ����ʱ��
									info.put("ReadTime", strTempTime);// ����ʱ��
									listData.add(info);
									info.put("no", String.valueOf(iTagCount));          
									listData1.add(info);
								}
							}
						}
						rs1.close();
					}
					if(cnt > 0){
						handler.obtainMessage(RET_RECEIVE_DATA).sendToTarget();
						//DevBeep.PlayOK();		
						try {sleep(150);} catch (InterruptedException e) {}
					}
					if (isSimulation) {// trueΪģ�� ,falseΪ��ʵ����ȡ����
						break;
					}
					
					
				}catch(Exception e){
					Log.i("UHF", e.toString());
				}finally{
					try {state.close();}catch (SQLException e){e.printStackTrace();}
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.activity_inventory);

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
		inventoryActivity = this;

		btn_inventory = (Button) findViewById(R.id.btn_inventory_Inventory);
		btn_stop = (Button) findViewById(R.id.btn_inventory_Stop);
		btn_clear = (Button) findViewById(R.id.btn_inventory_Clear);
		iv_nav_inventory = (ImageView) findViewById(R.id.iv_nav_inventory);
		popMenu = new PopMenu(this);
		popMenu.addItems(new int[] { R.string.inventory_tijiao,
				R.string.inventory_duibi, R.string.inventory_jieguo });
		popMenu.setOnItemClickListener(this);

		btn_stop.setEnabled(false);

		// ��ʾ�û���
		tv_username = (TextView) findViewById(R.id.tv_inventory_username);
		// username = getIntent().getStringExtra("username");
		// username=TApplication.strUser;
		tv_username.setText(username);

		// btn_yiku = (Button) findViewById(R.id.btn_inventory_form);

		lv_inventory_standby = (ListView) findViewById(R.id.lv_inventory_data);
		listData1 = new ArrayList<HashMap<String, String>>();
		listData = new ArrayList<HashMap<String, String>>();
		adapter = new InventoryAdapter(this, listData1);
		lv_inventory_standby.setAdapter(adapter);

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
				if (mTApplication.mService != null) {
					mTApplication.mService.device.uhfInventory();
				}
				btn_inventory.setEnabled(false);
				btn_stop.setEnabled(true);
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

		iv_nav_inventory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popMenu.showAsDropDown(v);
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

	@Override
	public void onItemClick(int position) {
		switch (position) {
		case 0:
			if (TApplication.isFistRun == false) {
				Log.e("debug", "��һ������");
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(InventoryActivity.this,
						InventoryFormActivity.class);
				intent.putExtra("username", username);
				intent.putExtra("listinventory", listData);// �ж��ٸ�EPC,������18��EPC
				intent.putExtra("listinventory1", listData1);//
				// startActivity(new Intent(this,InventoryFormActivity1.class));
				startActivity(intent);
			}
			break;
		case 1:
			Intent intent = new Intent(InventoryActivity.this,
					InventoryFormActivity1.class);
			startActivity(intent);
			break;
		case 2:
			Intent intent1 = new Intent(InventoryActivity.this,
					InventoryFormActivityShow.class);
			startActivity(intent1);
			break;

		}
	}
}
