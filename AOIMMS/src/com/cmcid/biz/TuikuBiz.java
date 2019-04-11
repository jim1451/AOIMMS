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

public class TuikuBiz extends IntentService {
	private static final int TUIKUZHONGFU = 0;
	private static final int TUKUSHUJU = 1;
	private static final int STOCKERROR = 2;
	private static final int STOCKERROR1 = 3;
	private static final int STOCKERROR2 = 4;

	public TuikuBiz() {
		super("tuiku_biz");
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TUIKUZHONGFU:
				Toast.makeText(getApplicationContext(), "请确认物品，此物品已经退库！", 0)
						.show();
				break;
			case TUKUSHUJU:
				Toast.makeText(getApplicationContext(), "确认退库数据！", 0).show();break;
			case STOCKERROR:
				Toast.makeText(getApplicationContext(), "库存物品数据有误，请确认！", 0).show();break;
			
			case STOCKERROR2:
				Toast.makeText(getApplicationContext(), "退库存物品有误，请确认！", 0).show();break;	
			default:
				break;
			}

		};
	};
	

	@Override
	protected void onHandleIntent(Intent intent) {
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listTuiku");
		ArrayList<HashMap<String, String>> list1 = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listTuiku1");
		String linliaoName = intent.getStringExtra("linliaoName");
		String linliaoHao = "";
		String username = intent.getStringExtra("username");
		String userID = "";
		String kufang = intent.getStringExtra("kufang");
		String kufangHao = "";
		String kuwei = intent.getStringExtra("kuwei");
		String kuweiHao = "";

		Statement state = null;
		String tuikuDanHao = "";
		String tuikuDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));

		try {
			// 编辑退库单号 格式：日期+四位数（0000递增）
			String format = tuikuDate.trim().replace("-", "").substring(0, 8);
			state = TApplication.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql1 = "select * from pt_TuiKuT where TuiKuID like '%"
					+ format + "%' order by TuiKuID asc";
			ResultSet rs = state.executeQuery(sql1);
			if (rs.next()) {
				String str = rs.getString("TuiKuID");
				rs.last();
				str = rs.getString("TuiKuId");
				int num = Integer.parseInt(str.substring(9));
				tuikuDanHao = "t" + format
						+ String.format("%0" + 6 + "d", num + 1);
			} else {
				tuikuDanHao = "t" + format + "000001";
			}

			// 通过领料单位名称 获取领料单号
			String sql2 = "select * from pt_LinLiaoT where linliaoName = '"
					+ linliaoName + "'";
			rs = state.executeQuery(sql2);
			while (rs.next())
				linliaoHao = rs.getString("linliaoHao");

			// 通过库房名 获得库房号
			String sql3 = "select * from pt_KuFangT where kuFangName = '"
					+ kufang + "'";
			rs = state.executeQuery(sql3);
			while (rs.next())
				kufangHao = rs.getString("kuFangHao");

			// 通过库位号 获得库位号
			String sql4 = "select * from pt_KuWeiT where KuWeiName = '" + kuwei
					+ "'";
			rs = state.executeQuery(sql4);
			while (rs.next())
				kuweiHao = rs.getString("KuWeiHao");

			// 通过username获取userID
			String username1 = TApplication.strUser; ;
			String sql5 = "select * from pt_POperate where OpName = '"
					+ username1 + "'";
			rs = state.executeQuery(sql5);
			while (rs.next()) {
				userID = rs.getString("OpID");
			}

			ArrayList<String> listEPC = new ArrayList<String>();
			
			
			
			if (false ==UntilBiz.IsInventDataOk() ){
				handler.obtainMessage(STOCKERROR).sendToTarget();
				return;
			}
			if (false ==UntilBiz.IsInventDataOk1() ){
				handler.obtainMessage(STOCKERROR).sendToTarget();
				return;
			}
			


			/**
			 * 做第一次遍历，查找在退库表中的数据是存在，如果存在就弹出对应的提示是退库物品
			 * 
			 */
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
				String sqlsString1 = "select * from pt_EPCOri where EPCID = '"
						+ epc + "'";
				rs = state.executeQuery(sqlsString1);
				if (rs.next()) {
				
					handler.obtainMessage(TUIKUZHONGFU).sendToTarget();
					return;
				}
			}
			
			
			
			
			
			
			
			TApplication.listrestore.clear();//在操作数据之前错对应的清空操作
			
			/**
			 * 根据EPC，将原始表的数据增加操作，第二次遍历操作
			 */
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
				String ReadTime = map.get("ReadTime");
				String ccdc = map.get("ccdc");
				String unitPrice = map.get("unitPrice");
				String DebtOpu = map.get("DebtOpu");
				String strTmppCreateTime = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss").format(new Date(System
						.currentTimeMillis()));// 读卡时间
				
				/**
				 * 做对应的删除操作，进行数据库还原
				 */
				
			String	   sSql2 = "delete from pt_EPCOri where EPCID = '" + epc
						+ "'";
				TApplication.listrestore.add(sSql2);
				
				
						
				String sqlsString2 = "insert into pt_EPCOri ([EPCID], ReadTime, CreateTime, CCDCID,UnitPricf,DebtOpt,KuFangHaoOri,KuWeiHaoOri) "
						+ "values ('"
						+ epc
						+ "', "
						+ "'"
						+ ReadTime
						+ "',"
						+ "'"
						+ strTmppCreateTime
						+ "',"
						+ "'"
						+ ccdc
						+ "',"
						+ "'" + unitPrice + "'," + "'" + DebtOpu + "',"+"'"+kufangHao+"',"+"'"+kuweiHao+"') ";
				state.executeUpdate(sqlsString2);

			}

			/**
			 * 根据对应的EPC 将对应的数据插入到退库表中，第三次遍历数据
			 */

			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
				String ccdc = map.get("ccdc");
				String tuikuNum = "1";
				String unitPrice = map.get("unitPrice");
				String DebtOpu = map.get("DebtOpu");
				
				/**
				 * 对应的退库流水账号，删除操作进行还原
				 */
			String	sSql3 = "delete from pt_TuiKuT where EPCID = '" + epc
						+ "' and CCDCID = '"+ccdc+"' and UnitPrice = '"+unitPrice+"' and tuikuDate = '"+tuikuDate+"' and DebtOpu = '"+DebtOpu+"'";
				TApplication.listrestore.add(sSql3);
				
				
				
				
				String sql7 = "insert into pt_TuiKuT (TuiKuID,EPCID,CCDCID,ChuKuNum,UnitPrice,Moneyt,linliaoHao,tuikuDate,kuFangHao,KuWeiHao,OpID,DebtOpu) "
						+ "values ('"
						+ tuikuDanHao
						+ "','"
						+ epc
						+ "','"
						+ ccdc
						+ "','"
						+ tuikuNum
						+ "','"
						+ unitPrice
						+ "','"
						+ unitPrice
						+ "','"
						+ linliaoHao
						+ "','"
						+ tuikuDate
						+ "','"
						+ kufangHao + "','" + kuweiHao + "','" + userID + "','" + DebtOpu + "')";
				state.executeUpdate(sql7);

			}

			/**
			 * 第四次遍历 根据对应的CCDCID UnitPrice DebtOpu获取对应的数据，界面获取的入库数量和后台库存数据比较 1
			 * 数据不存在就做对应的插入操作 2已经存在数据就更新操作
			 * 
			 */
			for (int i = 0; i < list1.size(); i++) {
				HashMap<String, String> map = list1.get(i);
				String strTmpCCDCID = map.get("ccdc"); // 物资编码
				String strTmpchukuNum = map.get("chukuNum"); // 数量
				String strTmpUnitPrice = map.get("unitPrice"); // 单价
				String strDebtOpt = map.get("DebtOpu");// 账户类型
				String KuWeiHao = map.get("KuWeiHao");
				String kuFangHao = map.get("kuFangHao");
				String epc = "";

				String sSql = "select * from pt_Stock where CCDCID = '"
						+ strTmpCCDCID + "'and UnitPrice= '" + strTmpUnitPrice
						+ "'and DebtOpu='" + strDebtOpt + "' ";
				// logMa.d("martrin", "----sSql=" + sSql);
				rs = state.executeQuery(sSql);
				if (rs.next()) {
					String rknum1 = rs.getString("RuKuNum");
					int rknum = Integer.parseInt(rknum1)
							+ Integer.parseInt(strTmpchukuNum);
					double db = (double) rknum
							* Double.parseDouble(strTmpUnitPrice);
					
					/**
					 * 对应的数据，更新还原操作
					 */
					
					
					double db1 =  Integer.parseInt(rknum1)
							* Double.parseDouble(strTmpUnitPrice);    
				String	sSql4 = "update pt_Stock set RuKuNum = '" + rknum1
							+ "',Moneyt='" + db1
							+ "'  where CCDCID = '" + strTmpCCDCID
							+ "'and UnitPrice= '" + strTmpUnitPrice
							+ "'and DebtOpu='" + strDebtOpt + "'";
					TApplication.listrestore.add(sSql4);
					
					

					String sql = " update pt_Stock set RuKuNum = '" + rknum
							+ "',Moneyt='" + db + "'  where CCDCID = '"
							+ strTmpCCDCID + "'and UnitPrice= '"
							+ strTmpUnitPrice + "'and DebtOpu='" + strDebtOpt
							+ "'";

					state.executeUpdate(sql);
				} else {
					double zongPrice = Double.parseDouble(strTmpUnitPrice)
							* Double.parseDouble(strTmpchukuNum);
					String strTmpDemo = "";
					String strTmpISPid = "";// 退库没有供应商，值为空。
					String strTmpPiaoHao = "";// 退库没有供应商，值为空。
					
                  /**
                   * 对应的数据，删除还原操作
                   */
				String	sSql5 = "delete from pt_Stock where CCDCID = '"
							+ strTmpCCDCID + "'and UnitPrice= '"
							+ strTmpUnitPrice + "'and DebtOpu='" + strDebtOpt
							+ "'";
					TApplication.listrestore.add(sSql5);
					
					
					
					
					
					String sqlsString4 = ("insert into pt_Stock ([RuKuID], EPCID, CCDCID, RuKuNum, UnitPrice, Moneyt, "
							+ "ISPid, PiaoHao, rukuDate, kuFangHao, KuWeiHao, OpID, DebtOpu,"
							+

							"[Demo]) " + "values ('"
							+ tuikuDanHao
							+ "', '"
							+ epc
							+ "', "
							+ "'"
							+ strTmpCCDCID
							+ "', '"
							+ strTmpchukuNum
							+ "', '"
							+ strTmpUnitPrice
							+ "', "
							+ "'"
							+ zongPrice
							+ "', '"
							+ strTmpISPid
							+ "', '"
							+ strTmpPiaoHao
							+ "', "
							+ "'"
							+ tuikuDate
							+ "', '"
							+ kuFangHao
							+ "', '"
							+ KuWeiHao
							+ "', "
							+ "'"
							+ userID
							+ "', "
							+ "'" + strDebtOpt + "', " + "'" + strTmpDemo + "') ");
					state.executeUpdate(sqlsString4);
				}

			}
		

			
			if (false ==UntilBiz.IsInventDataOk() ){
				handler.obtainMessage(STOCKERROR2).sendToTarget();
				return;
			}
			if (false ==UntilBiz.IsInventDataOk1() ){
				handler.obtainMessage(STOCKERROR2).sendToTarget();
				return;
			}
			
			rs.close();
			
			Intent i = new Intent(Constant.TUIKU_BIZ);
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
