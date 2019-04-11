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

public class OutbondBiz extends IntentService {
	protected static final int CHUKU = 0;
	protected static final int CHUKU2 = 1;
	private static final int STOCKERROR = 2;
	private static final int STOCKERROR1 = 3;
	private static final int STOCKERROR3 = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHUKU:
				Toast.makeText(OutbondBiz.this, "请确认物品的数量！", 0).show();
				break;
			case CHUKU2:
				Toast.makeText(OutbondBiz.this, "请确认物品，其已经出库！", 0).show();
				break;
			case STOCKERROR:
				Toast.makeText(OutbondBiz.this, "库存物品有误，请确认！", 0).show();
				break;
			
			case STOCKERROR3:
				Toast.makeText(OutbondBiz.this, "出库有误，请确认！", 0).show();
				break;
			default:
				break;
			}

		};
	};

	public OutbondBiz() {
		super("outbond_biz");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String username = intent.getStringExtra("username");
		String userID = "";
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listOutbond");
		ArrayList<HashMap<String, String>> list1 = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listOutbond1");
		String linliaoName = intent.getStringExtra("linliao");
		String linliaoHao = "";
		String kufang = intent.getStringExtra("kufang");
		String kufangHao = "";
		String kuwei = intent.getStringExtra("kuwei");
		String kuweiHao = "";

		Statement state = null;
		String chukuDanHao = "";
		String chukuDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));
		try {
			// 编辑入库单号 格式为 r+日期+6位数（000000递增）
			String format = chukuDate.trim().replace("-", "").substring(0, 8);
			state = TApplication.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql = "select * from pt_ChuKuT where ChuKuID like '%"
					+ format + "%' order by ChuKuID asc";
			ResultSet rs = state.executeQuery(sql);
			if (rs.next()) {
				String str = rs.getString("ChuKuID");
				rs.last();
				str = rs.getString("ChuKuID");
				int num = Integer.parseInt(str.substring(9));
				chukuDanHao = "c" + format
						+ String.format("%0" + 6 + "d", num + 1);
			} else {
				chukuDanHao = "c" + format + "000001";
			}

			// 通过领料单位名称 获取领料单号
			String sql1 = "select * from pt_LinLiaoT where linliaoName = '"
					+ linliaoName + "'";
			rs = state.executeQuery(sql1);
			while (rs.next())
				linliaoHao = rs.getString("linliaoHao");

			// 通过库房名 获得库房号
			String sql2 = "select * from pt_KuFangT where kuFangName = '"
					+ kufang + "'";
			rs = state.executeQuery(sql2);
			while (rs.next())
				kufangHao = rs.getString("kuFangHao");

			// 通过库位号 获得库位号
			String sql3 = "select * from pt_KuWeiT where KuWeiName = '" + kuwei
					+ "'";
			rs = state.executeQuery(sql3);
			while (rs.next())
				kuweiHao = rs.getString("KuWeiHao");

			// 通过username获取userID
			String username1 = TApplication.strUser; ;
			String sql4 = "select * from pt_POperate where OpName = '"
					+ username1 + "'";
			rs = state.executeQuery(sql4);
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
		
			
			
			
			
			
			
			
			
			
			
			
			

			// 先加循环语句
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
				String sqlsString1 = "select * from pt_EPCOri where EPCID = '"
						+ epc + "'";
				rs = state.executeQuery(sqlsString1);
				if (rs.next()) {
					break;
				}else {
					handler.obtainMessage(CHUKU2).sendToTarget();
					return;
				}
			}

			
			TApplication.listrestore.clear();//在操作数据之前错对应的清空操作
			// 将原始表的数据做对应的更新删除
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
//				String ccdc = map.get("ccdc");
//				String chukuNum = "1";
//				String unitPrice = map.get("unitPrice");
//				String DebtOpu = map.get("DebtOpt");
				String EPCID ="";
				String CiShu = "";
				String ReadTime =""; 
				String CreateTime = "";
				String CCDCID ="";
				String UnitPricf ="";
				String DebtOpt = "";
				String KuFangHaoOri = "";
				String KuWeiHaoOri = "";
				/**
				 * 获取还原数据
				 */
				
				String sqlsString3 = "select * from pt_EPCOri where EPCID ='"
						+ epc + "'";
				rs = state.executeQuery(sqlsString3);
				if (rs.next()) {
					 EPCID = rs.getString("EPCID");
					CiShu = rs.getString("CiShu");
					 ReadTime = rs.getString("ReadTime");
					 CreateTime = rs.getString("CreateTime");
					 CCDCID = rs.getString("CCDCID");
					 UnitPricf = rs.getString("UnitPricf");
					DebtOpt = rs.getString("DebtOpt");
					KuFangHaoOri = rs.getString("KuFangHaoOri");
					KuWeiHaoOri = rs.getString("KuWeiHaoOri");
				}
				
				
	     	String		sSql = 	"insert into pt_EPCOri ([EPCID], CiShu, ReadTime, CreateTime, CCDCID,UnitPricf,DebtOpt,KuFangHaoOri,KuWeiHaoOri) "
				+ "values ('"
				+ EPCID
				+ "', '"
				+ CiShu
				+ "', "
				+ "'"
				+ ReadTime
				+ "',"
				+ "'"
				+ CreateTime
				+ "',"
				+ "'"
				+ CCDCID
				+ "',"
				+ "'" + UnitPricf + "'," + "'" + DebtOpt + "'," + "'" + KuFangHaoOri + "'," + "'" + KuWeiHaoOri + "') ";
		    TApplication.listrestore.add(sSql);

				
				
				
				

				String sqlsString2 = "delete from pt_EPCOri where EPCID ='"
						+ epc + "'";
				state.executeUpdate(sqlsString2);

			}

			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
				String ccdc = map.get("ccdc");
				String chukuNum = "1";
				String unitPrice = map.get("unitPrice");
				String DebtOpu = map.get("DebtOpt");
				String kuFangHao1 = map.get("kuFangHao");
				String KuWeiHao1 = map.get("KuWeiHao");

				/**
				 * 将对应的数据还原删除
				 */
				
				String sSql= "delete from pt_ChuKuT where EPCID = '" + epc
						+ "' and CCDCID = '"+ccdc+"' and UnitPrice = '"+unitPrice+"' and chukuDate = '"+chukuDate+"' and DebtOpu = '"+DebtOpu+"'";
				TApplication.listrestore.add(sSql);
				
				/////////////////////////////////////////////////////////
				
				
				String sql6 = "insert into pt_ChuKuT (ChuKuID,EPCID,CCDCID,ChuKuNum,UnitPrice,Moneyt,linliaoHao,chukuDate,kuFangHao,KuWeiHao,OpID,DebtOpu) "
						+ "values('"
						+ chukuDanHao
						+ "','"
						+ epc
						+ "','"
						+ ccdc
						+ "','"
						+ chukuNum
						+ "','"
						+ unitPrice
						+ "','"
						+ unitPrice
						+ "','"
						+ linliaoHao
						+ "','"
						+ chukuDate
						+ "','"
						+ kuFangHao1 + "','" + KuWeiHao1 + "','" + userID + "','" + DebtOpu + "')";

				// insert into pt_ChuKuT
				// (ChuKuID,EPCID,CCDCID,ChuKuNum,UnitPrice,Moneyt,linliaoHao,chukuDate,kuFangHao,KuWeiHao,OpID)
				// values('201512150001','0003','07050108000017','1','12.23','12.23','1','2015-12-15
				// 09:57:37','1','1','')
				state.executeUpdate(sql6);
				// }
			}

			// 将库存表的数据做对应的修改；

			for (int i = 0; i < list1.size(); i++) {
				HashMap<String, String> map = list1.get(i);
				//[{unit=, ccdc=51050306000009, epc=3000CCDC2015AAAAAAAA0999, count=1, no=1, name=, unitPrice=12.23, 
				//chukuNum=1, type=Temperature switch 02250100-095 240°3/4LS12-50HH , DebtOpt=海运库, xiyuName=Temperature switch 02250100-095 240°3/4LS12-50HH }]
				String strTmpCCDCID = map.get("ccdc"); // 物资编码
				String strTmpchukuNum = map.get("chukuNum"); // 界面上读取的数量
				String strTmpUnitPrice = map.get("unitPrice"); // 单价
			//	String strDebtOpt = map.get("DebtOpta");// 账户类型
				String strDebtOpt = map.get("DebtOpt");// 账户类型
				
		//		String no = map.get("no");
				
				

				String sSql = "select * from pt_Stock where CCDCID = '"
						+ strTmpCCDCID + "'and UnitPrice= '" + strTmpUnitPrice
						+ "'and DebtOpu='" + strDebtOpt + "' ";
				// logMa.d("martrin", "----sSql=" + sSql); 
				rs = state.executeQuery(sSql);
				if (rs.next()) {
					String rknum1 = rs.getString("RuKuNum");
					int rknum = Integer.parseInt(rknum1)
							- Integer.parseInt(strTmpchukuNum);
					// String rknumString = rknum1+strTmprukuNum;
					if (rknum > 0) {
						double db = (double) rknum
								* Double.parseDouble(strTmpUnitPrice);
						
						/**
						 *对应的数据还原操作
						 */

						double db1 = (double)  Integer.parseInt(rknum1)
								* Double.parseDouble(strTmpUnitPrice);
						
					String	sSql4 = "update pt_Stock set RuKuNum = '" + rknum1
								+ "',Moneyt='" + db1
								+ "'  where CCDCID = '" + strTmpCCDCID
								+ "'and UnitPrice= '" + strTmpUnitPrice
								+ "'and DebtOpu='" + strDebtOpt + "'";
						TApplication.listrestore.add(sSql4);
						
						
						
						
			
						
						String sqlsString3 = " update pt_Stock set RuKuNum = '"
								+ rknum + "',Moneyt='" + db
								+ "'  where CCDCID = '" + strTmpCCDCID
								+ "'and UnitPrice= '" + strTmpUnitPrice
								+ "'and DebtOpu='" + strDebtOpt + "'";
						state.executeUpdate(sqlsString3);
					}
					if (rknum == 0) {
						String  RuKuID = "";
						String  EPCID = "";
						String  CCDCID = "";
						String  RuKuNum = "";
						String  UnitPrice = "";
						String  Moneyt = "";
						String  ISPid = "";
						String  PiaoHao = "";
						String  rukuDate = "";
						String  kuFangHao = "";
						String  KuWeiHao = "";
						String  OpID = "";
						String  DebtOpu = "";
						String  Demo = "";
						
						String  sqlString = " select * from pt_Stock   where CCDCID = '"
								+ strTmpCCDCID
								+ "'and UnitPrice= '"
								+ strTmpUnitPrice
								+ "'and DebtOpu='"
								+ strDebtOpt + "'";
						rs = state.executeQuery(sqlString);
						if (rs.next()) {
							RuKuID = rs.getString("RuKuID");
							EPCID = rs.getString("EPCID");
							CCDCID = rs.getString("CCDCID");
							RuKuNum = rs.getString("RuKuNum");
							UnitPrice = rs.getString("UnitPrice");
							Moneyt = rs.getString("Moneyt");
							ISPid = rs.getString("ISPid");
							PiaoHao = rs.getString("PiaoHao");
							rukuDate = rs.getString("rukuDate");
							kuFangHao = rs.getString("kuFangHao");
							KuWeiHao = rs.getString("KuWeiHao");
							OpID = rs.getString("OpID");
							DebtOpu = rs.getString("DebtOpu");
							Demo = rs.getString("Demo");
							
						}
						
						/**
						 * 库存删除还原，先把原来的库存要删除数据做备份处理
						 */
						
					String	sSql5 = ("insert into pt_Stock ([RuKuID], EPCID, CCDCID, RuKuNum, UnitPrice, Moneyt, "
							+ "ISPid, PiaoHao, rukuDate, kuFangHao, KuWeiHao, OpID, DebtOpu,"
							+"[Demo]) " + "values ('"
							+ RuKuID
							+ "', '"
							+ EPCID
							+ "', "
							+ "'"
							+ CCDCID
							+ "', '"
							+ RuKuNum
							+ "', '"
							+ UnitPrice
							+ "', "
							+ "'"
							+ Moneyt
							+ "', '"
							+ ISPid
							+ "', '"
							+ PiaoHao
							+ "', "
							+ "'"
							+ rukuDate
							+ "', '"
							+ kuFangHao
							+ "', '"
							+ KuWeiHao
							+ "', "
							+ "'"
							+ OpID
							+ "', "
							+ "'" + DebtOpu + "', " + "'" + Demo + "') ");
						TApplication.listrestore.add(sSql5);
						
						
						
						
						String sqlsString4 = " delete pt_Stock   where CCDCID = '"
								+ strTmpCCDCID
								+ "'and UnitPrice= '"
								+ strTmpUnitPrice
								+ "'and DebtOpu='"
								+ strDebtOpt + "'";
						state.executeUpdate(sqlsString4);
					}
					if (rknum < 0) {
						// handler.obtainMessage(CHUKU).setTarget(null);
						handler.obtainMessage(CHUKU).sendToTarget();
						return;
					}

				}

				

		}
			
			if (false ==UntilBiz.IsInventDataOk() ){
				handler.obtainMessage(STOCKERROR3).sendToTarget();
				return;
			}
			if (false ==UntilBiz.IsInventDataOk1() ){
				handler.obtainMessage(STOCKERROR3).sendToTarget();
				return;
			}
			
			
			
			

			rs.close();

			Intent i = new Intent(Constant.OUTBOND_BIZ);
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
