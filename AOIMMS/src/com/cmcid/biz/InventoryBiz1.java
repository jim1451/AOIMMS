package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.cmcid.TApplication;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.logMa;

public class InventoryBiz1 extends IntentService {

	private static final int OK = 0;
	private static final int OK1 = 1;
	int k = 0;
	int mun = 0;
	ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();// 时间段盘点的
	ArrayList<HashMap<String, String>> listData1 = new ArrayList<HashMap<String, String>>(); // 库存

	public InventoryBiz1() {
		super("inventory_biz");
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case OK:
				Toast.makeText(getApplicationContext(), "货物盘点准确无误！", 0).show();
				break;
			case OK1:
				Toast.makeText(getApplicationContext(), "货物盘点还待继续！", 0).show();
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onHandleIntent(Intent intent) {
		// ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String,
		// String>>) intent
		// .getSerializableExtra("listinventory");
		// ArrayList<HashMap<String, String>> list1 = (ArrayList<HashMap<String,
		// String>>) intent
		// .getSerializableExtra("listinventory1");
		String dateString = intent.getStringExtra("dateString");
		String dateString1 = intent.getStringExtra("dateString1");
		// String username = intent.getStringExtra("username");
		// String userID = "";
		// String kufang = intent.getStringExtra("kufang");
		// String kufangHao = "";
		// String kuwei = intent.getStringExtra("kuwei");
		// String kuweiHao = "";
		ArrayList<String> listEPC = new ArrayList<String>();
		Statement state = null;
		String DanID = "";
		String pangdianDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));

		try {
			// 编辑退库单号 格式：日期+四位数（0000递增）
			String format = pangdianDate.trim().replace("-", "")
					.substring(0, 8);
			state = TApplication.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql1 = "select * from pt_InventoryResult where DanID like '%"
					+ format + "%' order by DanID asc";
			ResultSet rs = state.executeQuery(sql1);
			if (rs.next()) {
				String str = rs.getString("DanID");
				rs.last();
				str = rs.getString("DanID");
				int num = Integer.parseInt(str.substring(9));
				DanID = "p" + format + String.format("%0" + 6 + "d", num + 1);
			} else {
				DanID = "p" + format + "000001";
			}

			/**
			 * 根据库存表的搜寻出一列数据包含库存中的所有条数的数据
			 */
			String sqlString = "SELECT     RuKuID, EPCID, CCDCID, "
					+ "(select top 1 Name from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as Name, "
					+ "(select top 1 XiYuName from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as XiYuName, "
					+ "(select top 1 Type from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as Type, "
					+ "(select top 1 UnitA from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as UnitA, "
					+ " (select top 1 MinStock from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as MinStock,"
					+ " (select top 1 MaxStock from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as MaxStock,"
					+ " (select top 1 ABCitem from pt_CCDC where pt_CCDC.CCDCID = pt_Stock.CCDCID) as ABCitem, "
					+ "(select top 1 kuFangName from pt_KuFangT where pt_KuFangT.kuFangHao = pt_Stock.kuFangHao) as KuFang, "
					+ " (select top 1 KuWeiName from pt_KuWeiT where pt_KuWeiT.KuWeiHao = pt_Stock.KuWeiHao) as KuWei, "
					+ " (select top 1 OpName from pt_POperate where pt_POperate.OpID = pt_Stock.OpID) as UserA, "
					+ "(select top 1 DebtOptName from pt_Debt where pt_Debt.DebtOptID = pt_Stock.DebtOpu) as DebtOptName, "
					+ "RuKuNum, UnitPrice, Moneyt, ISPid, PiaoHao, rukuDate, kuFangHao, KuWeiHao, OpID, DebtOpu, Demo "
					+ "FROM         pt_Stock";
			rs = state.executeQuery(sqlString);

			while (true) {
				HashMap<String, String> info1 = new HashMap<String, String>();
				if (!rs.next())
					break;
				String CCDCID1 = rs.getString("CCDCID");
//				if (CCDCID1.contains("0002") ){
//					int ii;
//					ii = 0;
//				}

				
				
				
				
				
				String UnitPrice1 = rs.getString("UnitPrice");
				String DebtOpu1 = rs.getString("DebtOpu");
				String RuKuNum1 = rs.getString("RuKuNum");

				String Name1 = rs.getString("Name");
				String XiYuName1 = rs.getString("XiYuName");
				String Type1 = rs.getString("Type");
				String UnitA1 = rs.getString("UnitA");
				// String UnitPrice1= rs.getString("UnitPrice");
				// String RuKuNum1= rs.getString("RuKuNum");
				String Moneyt1 = rs.getString("Moneyt");
				String MinStock1 = rs.getString("MinStock");
				String MaxStock1 = rs.getString("MaxStock");
				String ABCitem1 = rs.getString("ABCitem");
				String KuFang1 = rs.getString("KuFang");

				String KuWei1 = rs.getString("KuWei");
				String UserA1 = rs.getString("UserA");
				// String DebtOpu1= rs.getString("DebtOpu");
				String rukuDate1 = rs.getString("rukuDate");

				info1.put("CCDCID", CCDCID1);// 物资名称
				info1.put("UnitPrice", UnitPrice1);// EPC号
				info1.put("DebtOpu", DebtOpu1);// 西语名称
				info1.put("RuKuNum", RuKuNum1);
				info1.put("Name", Name1);
				info1.put("XiYuName", XiYuName1);
				info1.put("Type", Type1);
				info1.put("UnitA", UnitA1);
				info1.put(" Moneyt", Moneyt1);
				info1.put("MinStock", MinStock1);

				info1.put("MaxStock", MaxStock1);
				info1.put("ABCitem", ABCitem1);
				info1.put("KuFang", KuFang1);
				info1.put("KuWei", KuWei1);
				info1.put("UserA", UserA1);

				info1.put("rukuDate", rukuDate1);

				listData1.add(info1);

			}
			// }

			logMa.d("martrin", "----listData1.size=" + listData1.size() + "\n");

			// 获取上传数据的总数mun1
			// String stringsql2 =
			// "select  count(DanID) as mun1 from pt_InventoryKuT  where '"+dateString+"' <= InkuDate and InKuDate <= '"+dateString1+"'";
			// rs = state.executeQuery(stringsql2);
			// String mun1= "";
			// if (rs.next()) {
			// mun1 = rs.getString("mun1");
			// }

			// int intmun1 = Integer.parseInt(mun1);
			// for(int h = 0; h<intmun1; h++){
			/**
			 * 从上传的结果表根据时间条件搜索出对用的数据的 CCDCID   UnitPrice DebtOpu字段，然后组成对的listData表
			 */
			
			String stringsql1 = "SELECT     DanID, EPCID, CCDCID, InKuNum, UnitPrice, Moneyt, MaHao, InkuDate, kuFangHao, KuWeiHao, OpID, DebtOpu, Demo FROM    pt_InventoryKuT  where '"
					+ dateString
					+ "' <= InkuDate and InKuDate <= '"
					+ dateString1 + "'";
			rs = state.executeQuery(stringsql1);

			while (rs.next()) {
				HashMap<String, String> info = new HashMap<String, String>();
				String CCDCID = rs.getString("CCDCID").trim();
				String UnitPrice = rs.getString("UnitPrice").trim();
				String DebtOpu = rs.getString("DebtOpu").trim();

				info.put("CCDCID", CCDCID);// 物资名称
				info.put("UnitPrice", UnitPrice);// EPC号
				info.put("DebtOpu", DebtOpu);// 西语名称
				listData.add(info);
			}

			// }

			logMa.d("martrin", "----listData.size=" + listData.size() + "\n");

			for (int i = 0; i < listData1.size(); i++) {// 以库存标的总数为外循环，进行三个参数的比较，比较一样
														// K 就增加1

				HashMap<String, String> map1 = new HashMap<String, String>();
				map1 = listData1.get(i);
				String strTmpCCDCID1 = map1.get("CCDCID"); // 物资编码
				String strTmpEpcID = "";//盘点结果表EPC为空
				String strUnitPrice1 = map1.get("UnitPrice");
				String strDebtOpu1 = map1.get("DebtOpu"); // 单价
				String strRuKuNum1 = map1.get("RuKuNum");

				String Name1 = map1.get("Name");
				String XiYuName1 = map1.get("XiYuName");
				String Type1 = map1.get("Type");
				String UnitA1 = map1.get("UnitA");
				// String UnitPrice1= rs.getString("UnitPrice");
				// String RuKuNum1= rs.getString("RuKuNum");
				String Moneyt1 = map1.get("Moneyt");
				String MinStock1 = map1.get("MinStock");
				String MaxStock1 = map1.get("MaxStock");
				String ABCitem1 = map1.get("ABCitem");
				String KuFang1 = map1.get("KuFang");

				String KuWei1 = map1.get("KuWei");
				String UserA1 = map1.get("UserA");
				// String DebtOpu1= rs.getString("DebtOpu");
				String rukuDate1 = map1.get("rukuDate");
				String demo = "";

				int intRuKuNum1 = Integer.parseInt(strRuKuNum1);
				k = 0;
				for (int n = 0; n < listData.size(); n++) {// 以上传的数据为循环的次数
					HashMap<String, String> map = new HashMap<String, String>();
					map = listData.get(n);
					String strTmpCCDCID = map.get("CCDCID"); // 物资编码

					String strUnitPrice = map.get("UnitPrice");
					String strDebtOpu = map.get("DebtOpu"); // 单价
					
//					if (strTmpCCDCID == strTmpCCDCID1)
//					{
//						k = 0;
//					}
					

					if (strTmpCCDCID.equals(strTmpCCDCID1) 
							&& strUnitPrice .equals(strUnitPrice1)
							&& strDebtOpu.equals(strDebtOpu1)) {
						k++;
					}
				}
				String Status1 = "";
				logMa.d("martrin", "kkkk" +k + "\n");
				if (k == intRuKuNum1) {
					Status1 = "平衡";
				}
				if (k < intRuKuNum1) {
					Status1 = "盘亏";
				}
				if (k > intRuKuNum1) {
					Status1 = "亏盘";
				}

				double totalmoney = k*(Double.parseDouble(strUnitPrice1));
				//DecimalFormat df = new DecimalFormat("0.00");
				String sql2 = ("insert into pt_InventoryResult ([DanID], EPCID, CCDCID, Name, XiYuName, Type, "
						+ "UnitA, StockNum, UnitPrice, Moneyt, MinStock, MaxStock, ABCitem,KuFang,KuWei,UserA,DebtOpu,InventoryTime,InventNum,InventMoney,Status,"
						+

						"[Demo]) " + "values ('"
						+ DanID
						+ "', '"
						+ strTmpEpcID
						+ "', "
						+ "'"
						+ strTmpCCDCID1
						+ "', '"
						+ Name1
						+ "', '"
						+ XiYuName1
						+ "', "
						+ "'"
						+ Type1
						+ "', '"
						+ UnitA1
						+ "', '"
						+ strRuKuNum1
						+ "', "
						+ "'"
						+ strUnitPrice1
						+ "', '"
						+ Moneyt1
						+ "', '"
						+ MinStock1
						+ "', "
						+ "'"
						+ MaxStock1
						+ "', "
						+ "'"
						+ ABCitem1
						+ "', '"
						+ KuFang1
						+ "','"
						+ KuWei1
						+ "','"
						+ UserA1
						+ "','"
						+ strDebtOpu1
						+ "','"
						+ rukuDate1
						+ "','"
						+ k
						+ "','"
						+ totalmoney
						+ "','" + Status1 + "'," + "'" + demo + "') ");
				// rs = state.executeQuery(sql2);
				state.executeUpdate(sql2);

			}

			// }

			rs.close();

			Intent i = new Intent(Constant.INVENTORY_BIZ1);
			i.putStringArrayListExtra("listEPC", listEPC);
			sendBroadcast(i);
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		} finally {
			try {
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		}
	}
}
