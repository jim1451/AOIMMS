package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
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

public class InventoryBiz extends IntentService {
	private static final int STOCKERROR = 0;
	private static final int STOCKERROR1 = 1;

	public InventoryBiz() {
		super("yiku_biz");
	}

//	Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case STOCKERROR:
//				Toast.makeText(getApplicationContext(), "货物已经盘点入库，请重新确认！", 0)
//						.show();
//				break;
//			
//			default:
//				break;
//			}
//
//		};
//	};

	@Override
	protected void onHandleIntent(Intent intent) {
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listinventory");
		ArrayList<HashMap<String, String>> list1 = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listinventory1");
//		String linliaoName = intent.getStringExtra("linliaoName");
//		String linliaoHao = "";
		String username = intent.getStringExtra("username");
		String userID = "";
		String kufang = intent.getStringExtra("kufang");
		String kufangHao = "";
		String kuwei = intent.getStringExtra("kuwei");
		String kuweiHao = "";

		Statement state = null;
		String DanID = "";
		String tuikuDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));

		try {
			// 编辑退库单号 格式：日期+四位数（0000递增）
			String format = tuikuDate.trim().replace("-", "").substring(0, 8);
			state = TApplication.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql1 = "select * from pt_InventoryKuT where DanID like '%"
					+ format + "%' order by DanID asc";
			ResultSet rs = state.executeQuery(sql1);
			if (rs.next()) {
				String str = rs.getString("DanID");
				rs.last();
				str = rs.getString("DanID");
				int num = Integer.parseInt(str.substring(9));
				DanID = "n" + format
						+ String.format("%0" + 6 + "d", num + 1);
			} else {
				DanID = "n" + format + "000001";
			}

		

			ArrayList<String> listEPC = new ArrayList<String>();


			
			for (int i = 0; i < list.size(); i++) {
				
				
				String inventoryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));;
				HashMap<String, String> map = list.get(i);
				String strTmpCCDCID = map.get("ccdc"); // 物资编码
				//String strTmpchukuNum = map.get("chukuNum"); // 数量
				String RuKuNum = map.get("RuKuNum");
				String strTmpUnitPrice = map.get("unitPrice"); // 单价
				double totalMoney = (Double.parseDouble(strTmpUnitPrice))*(Integer.parseInt(RuKuNum));
				String totalMoney1 = new DecimalFormat(".00").format(totalMoney);
				
				
				
				
				String strDebtOpt = map.get("DebtOpu");// 账户类型
				String KuWeiHao = map.get("KuWeiHao");
				String kuFangHao = map.get("kuFangHao");
				//String ReadTime = map.get("ReadTime");
				String ReadTime = inventoryDate;
				String epc =  map.get("epc");
				String username1 = TApplication.strUser;
				String sql4 = "select * from pt_POperate where OpName = '"
						+ username1 + "'";
				rs = state.executeQuery(sql4);
				while (rs.next()) {
					userID = rs.getString("OpID");
				}
				
				String mahao = "";
			String	sSql = "insert into pt_InventoryKuT ([DanID], EPCID, CCDCID, InKuNum, UnitPrice,Moneyt,MaHao,InkuDate,kuFangHao,KuWeiHao,OpID,DebtOpu) "
						+ "values ('"
						+ DanID
						+ "', '"
						+ epc
						+ "', "
						+ "'"
						+ strTmpCCDCID
						+ "',"
						+ "'"
						+ RuKuNum
						+ "',"
						+ "'"
						+ strTmpUnitPrice
						+ "',"
						+ "'" + totalMoney1 + "'," + "'" + mahao + "',"+"'"+ReadTime+"',"+"'"+kuFangHao+"',"+"'"+kuFangHao+"',"+"'"+userID+"',"+"'"+strDebtOpt+"') ";

				logMa.d("martrin", "----sSql=" + sSql);
				// TRACE("sSql=%s\n", sSql);
				state.executeUpdate(sSql);
			}

		
			
			
			
			
			rs.close();
			Intent i = new Intent(Constant.INVENTORY_BIZ);
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
