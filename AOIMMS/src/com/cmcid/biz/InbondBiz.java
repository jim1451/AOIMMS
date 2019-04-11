package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.R.integer;
import android.app.IntentService;
import android.content.Intent;
//import android.os.Handler;
import android.widget.Toast;
import android.os.Handler;
import com.cmcid.TApplication;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.logMa;

public class InbondBiz extends IntentService {
	private static final int EPC_CHONGFU = 0;
	private static final int EPC_ZHANGHU = 1;
	private static final int EPC_STOCK = 2;
	private static final int EPC_KUGENGXING = 3;
	private static final int STOCKERROR = 4;
	private static final int STOCKERROR1 = 5;
	private static final int ZERO = 6;
	String   strNo = "";
	String   strNo1 = "";
	// private ArrayList<String> listrestore;

	public InbondBiz() {
		super("inbond_biz");
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case EPC_CHONGFU:

				Toast.makeText(InbondBiz.this, " 第“"+strNo+"”项已验收，请更新操作!", 0).show();
				break;
			case EPC_ZHANGHU:
				Toast.makeText(InbondBiz.this, "请确认账户类型！", 0).show();
				break;
			case EPC_STOCK:
				Toast.makeText(InbondBiz.this, "库存已经存在，请重新确认货物！", 0).show();
				break;
			case EPC_KUGENGXING:
				Toast.makeText(InbondBiz.this, "库存已更新，数据更新完毕", 0).show();
				break;
			case STOCKERROR:
				Toast.makeText(InbondBiz.this, "库存数量有误，请确认", 0).show();
				break;
			case STOCKERROR1:
				Toast.makeText(InbondBiz.this, "进库数量有误，请确认", 0).show();
				break;
			case ZERO:
				Toast.makeText(InbondBiz.this, "第“"+strNo1+"”项入库价格请重新确认！", 0).show();
				break;
			}
		};
	};
	private String strunitPricef;

	@Override
	protected void onHandleIntent(Intent intent) {
		String username = intent.getStringExtra("username");
		String userID = "";
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listInbond");
		ArrayList<HashMap<String, String>> list1 = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listInbond1");

		String ISPname = intent.getStringExtra("ISPname");
		String ISPid = "";
		String kufang = intent.getStringExtra("kufang");
		String kuFangHao = "";

		String zhanghu = intent.getStringExtra("zhanghu");
		String zhanghuHao = "";

		String kuwei = intent.getStringExtra("kuwei");
		String KuWeiHao = "";
		String piaoHao = intent.getStringExtra("piaoHao");

		Statement state = null;
		String rukuDanHao = "";
		String rukuDanHao1 = "";// 定义库存表的书入库id为 i开头
		String rukuDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));
		try {
			// 编辑入库单号 格式为 r+日期+6位数（000000递增）
			String format = rukuDate.trim().replace("-", "").substring(0, 8);// 20111208
			state = TApplication.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql1 = "select * from pt_RuKuT where RuKuID like '%"
					+ format + "%' order by RuKuID asc";
			ResultSet rs = state.executeQuery(sql1);
			if (rs.next()) {
				String str = rs.getString("RuKuID");
				rs.last();
				str = rs.getString("RuKuID");
				int num = Integer.parseInt(str.substring(9));
				rukuDanHao = "r" + format
						+ String.format("%0" + 6 + "d", num + 1);
				rukuDanHao1 = "i" + format
						+ String.format("%0" + 6 + "d", num + 1);

			} else {
				rukuDanHao = "r" + format + "000001";// r20151213000001
				rukuDanHao1 = "i" + format + "000001";// r20151213000001
			}

			// 通过ISPname 获取ISPid
			String sql2 = "select * from pt_ISP where ISPName = '" + ISPname
					+ "'";
			System.out.println("sql2----" + sql2);
			rs = state.executeQuery(sql2);
			while (rs.next()) {
				ISPid = rs.getString("ISPid");
			}

			String sql6 = "select * from pt_Debt where DebtOptName = '"
					+ zhanghu + "'";
			rs = state.executeQuery(sql6);
			while (rs.next()) {
				zhanghuHao = rs.getString("DebtOptID");
			}

			// 通过库房名 获取kuFangHao
			String sql3 = "select * from pt_KuFangT where kuFangName = '"
					+ kufang + "'";
			rs = state.executeQuery(sql3);
			while (rs.next()) {
				kuFangHao = rs.getString("kuFangHao");
			}

			// 通过库位名 获取KuWeiHao
			String sql4 = "select * from pt_KuWeiT where KuWeiName = '" + kuwei
					+ "'";
			rs = state.executeQuery(sql4);
			while (rs.next()) {
				KuWeiHao = rs.getString("KuWeiHao");
			}

			// 通过username 获取userID  username
			
			String username1 = TApplication.strUser; ;
			String sql5 = "select * from pt_POperate where OpName = '"
					+ username1 + "'";
			rs = state.executeQuery(sql5);
			while (rs.next()) {
				userID = rs.getString("OpID");
			}

			ArrayList<String> listEPC = new ArrayList<String>();

			/**
			 * 检查账户类型
			 */
//			boolean bRukuZhanghu = false;
//			if (zhanghuHao.equals("1")) {
//				// bRukuZhanghu = false;// 设置为true就要判断
//				handler.obtainMessage(EPC_ZHANGHU).sendToTarget();
//				return;
//			}

			if (false == UntilBiz.IsInventDataOk()) {
				handler.obtainMessage(STOCKERROR).sendToTarget();
				return;
			}
			if (false == UntilBiz.IsInventDataOk1()) {
				handler.obtainMessage(STOCKERROR).sendToTarget();
				return;
			}

			// 判断是否已验收
			boolean bRuKuYes = false;
			String sSql;
			String sSql2;
			String sSql3;
			String sSql4;
			String sSql5;
			String strTemp;
			String strTemq;
			String strPrice;

			/**
			 * 进行第一次比遍厉 根据EPC 查询，如果存在原始表提示对应的数据是存在的。
			 */

			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				strTemq = map.get("ccdc");
				strTemp = map.get("epc");	
				strNo = map.get("no");
				String deopt = null;
				/**
				 * 判断对应的货物是否已经入库存
				 */

				sSql = "select * from pt_EPCOri where EPCID = '" + strTemp
						+ "' ";

				rs = state.executeQuery(sSql);
				if (rs.next()) {// 在
					
					handler.obtainMessage(EPC_CHONGFU).sendToTarget();

					return;
				}

			}

			TApplication.listrestore.clear();
			/**
			 * 第二次遍历 在原始表中插入对应的入库数据
			 */

			// 将EPC列表插入到EPC原始数据中
			for (int i = 0; i < list.size(); i++) { // 二次遍历
				String strTmpCCDCID; // 物资编码
				String strTmppEPCID; // EPC号
				String strTmppCiShu; // 次数
				String strTmppReadTime; // 读取时间
				String strunitPrice; // 价格
				String strTmppCreateTime = rukuDate; // 创建时间(保存后存贮时间)

				HashMap<String, String> map = list.get(i);
				strTmppEPCID = map.get("epc"); // EPC
				strTmppCiShu = map.get("count"); // 次数
				strTmppReadTime = map.get("ReadTime"); // 读取时间
				strTmpCCDCID = map.get("ccdc"); // 物资编码
				// strunitPricef = map.get("unitPrice");
				strunitPrice = map.get("unitPrice");// 价格
				strNo1 = map.get("no");
				
				
			//	int   price = Integer.parseInt(strunitPrice);
				if (strunitPrice.equals("00.00")) {
					handler.obtainMessage(ZERO).sendToTarget();
					return;
				}

				// 物资编码是不是唯一一条记录,还有EPC
				strTemp = strTmppEPCID;
				strTemq = strTmpCCDCID;
				strTemp = "";

				sSql2 = "delete from pt_EPCOri where EPCID = '" + strTmppEPCID
						+ "'";
				TApplication.listrestore.add(sSql2);

				sSql = "insert into pt_EPCOri ([EPCID], CiShu, ReadTime, CreateTime, CCDCID,UnitPricf,DebtOpt,KuFangHaoOri,KuWeiHaoOri) "
						+ "values ('"
						+ strTmppEPCID
						+ "', '"
						+ strTmppCiShu
						+ "', "
						+ "'"
						+ strTmppReadTime
						+ "',"
						+ "'"
						+ strTmppCreateTime
						+ "',"
						+ "'"
						+ strTmpCCDCID
						+ "',"
						+ "'" + strunitPrice + "'," + "'" + zhanghuHao + "',"+"'"+kuFangHao+"',"+"'"+KuWeiHao+"') ";

				logMa.d("martrin", "----sSql=" + sSql);
				// TRACE("sSql=%s\n", sSql);
				state.executeUpdate(sSql);
				
				
				

			}

			String strTmprukuDanHao; // 入库单号
			String strTmprukuDanHao1;
			String strTmpEpcID; // EPC号使用在库存中，写为空值
			String strTmpEpcID1; // EPC号使用在库存
			String strTmpCCDCID; // 物资编码
			String strTmprukuNum; // 入库数量
			String strTmpUnitPrice; // 单价
			String strTmprukuTotalPrice; // 金额
			String strTmpISPid; // 供应商编号
			String strTmpPiaoHao; // 票号
			String strTmprukuDate; // 入库日期
			String strTmpkuFangHao; // 库房ID
			String strTmpKuWeiHao; // 库位ID
			String strTmpOpID; // 用户ID
			String strTmpDemo; // 备注

			strTmprukuDanHao = rukuDanHao;
			strTmprukuDanHao1 = rukuDanHao1;
			strTmpEpcID = "";// 入库单中EPC定为空，因为一个物资编码可能有多个EPC，EPC放另一张表
			strTmpISPid = ISPid;
			strTmpPiaoHao = piaoHao;
			strTmprukuDate = rukuDate;
			strTmpkuFangHao = kuFangHao;
			strTmpKuWeiHao = KuWeiHao;
			strTmpOpID = userID;
			strTmpDemo = "";

			// 再将数据插队入库表中
			for (int i = 0; i < list.size(); i++) {

				// strTmprukuDanHao = rukuDanHao;

				strTmpEpcID = "";// 入库单中EPC定为空，因为一个物资编码可能有多个EPC，EPC放另一张表
				strTmpISPid = ISPid;
				strTmpPiaoHao = piaoHao;
				strTmprukuDate = rukuDate;
//				strTmpkuFangHao = kuFangHao;
//				strTmpKuWeiHao = KuWeiHao;
				strTmpOpID = userID;
				strTmpDemo = "";

				HashMap<String, String> map = list.get(i);

				strTmpCCDCID = map.get("ccdc"); // 物资编码
				// strTmprukuNum = map.get("rukuNum"); //数量
				// strTmprukuNum = "1";
				strTmpUnitPrice = map.get("unitPrice"); // 单价
				// strTmprukuTotalPrice = map.get("totalMoney"); // 金额
				// strTmprukuTotalPrice = strTmpUnitPrice;
				strTmpEpcID1 = map.get("epc");
				//strTmpDemo = map.get("demoru"); // 备注

				// ////////////////////////////////////////////////////////////////////////
				
				
				
				
				
//				String sqlString =  "select * from pt_RuKuT where EPCID = '"+strTmpEpcID1+"'";
//				 rs = state.executeQuery(sqlString);
//				if (rs.next()) {
//					//对应的数据库进行操作
//					String   kuFangHao1 = rs.getString("kuFangHao");
//					String   KuWeiHao1 = rs.getString("KuWeiHao");
//					
//					sSql3 = "update pt_RuKuT  set  kuFangHao = '"+kuFangHao1+"', KuWeiHao = '"+KuWeiHao1+"'   where EPCID = '"+strTmpEpcID1+"'";
//					TApplication.listrestore.add(sSql3);
//					
//					sqlString =  "update pt_RuKuT  set  kuFangHao = '"+strTmpkuFangHao+"', KuWeiHao = '"+strTmpKuWeiHao+"'   where EPCID = '"+strTmpEpcID1+"'";
//					state.executeUpdate(sqlString);
//				}else {
					sSql3 = "delete from pt_RuKuT where EPCID = '" + strTmpEpcID1
							+ "' and CCDCID = '"+strTmpCCDCID+"' and UnitPrice = '"+strTmpUnitPrice+"' and rukuDate = '"+strTmprukuDate+"' and DebtOpu = '"+zhanghuHao+"'";
					TApplication.listrestore.add(sSql3);

					String num = "1";
					strTemp = "";
					sSql = ("insert into pt_RuKuT ([RuKuID], EPCID, CCDCID, RuKuNum, UnitPrice, Moneyt, "
							+ "ISPid, PiaoHao, rukuDate, kuFangHao, KuWeiHao, OpID, DebtOpu,"
							+ "[Demo]) " + "values ('"
							+ strTmprukuDanHao
							+ "', '"
							+ strTmpEpcID1
							+ "', "
							+ "'"
							+ strTmpCCDCID
							+ "', '"
							+ num
							+ "', '"
							+ strTmpUnitPrice
							+ "', "
							+ "'"
							+ strTmpUnitPrice // 入库的数量为一，所以总价为价格
							+ "', '"
							+ strTmpISPid
							+ "', '"
							+ strTmpPiaoHao
							+ "', "
							+ "'"
							+ strTmprukuDate
							+ "', '"
							+ strTmpkuFangHao
							+ "', '"
							+ strTmpKuWeiHao
							+ "', "
							+ "'"
							+ strTmpOpID
							+ "', " + "'" + zhanghuHao + "', " + "'" + strTmpDemo + "') ");

					logMa.d("martrin", "----sSql=" + sSql);
					// TRACE("sSql=%s\n", sSql);
					state.executeUpdate(sSql);

				}
				
				
				
				
				
				

			
	//		}

			/**
			 * 判断对应的货物是否已经入库存,做对应的更新操作或是 插入数据操作。
			 */
			for (int i = 0; i < list1.size(); i++) {
				HashMap<String, String> map = list1.get(i);

				strTmpCCDCID = map.get("ccdc"); // 物资编码
				strTmprukuNum = map.get("rukuNum"); // 数量
				strTmpUnitPrice = map.get("unitPrice"); // 单价
				strTmprukuTotalPrice = map.get("totalMoney"); // 金额
				strTmpDemo = ""; // 备注

				String rknum1 = null;

				sSql = "select * from pt_Stock where CCDCID = '" + strTmpCCDCID
						+ "'and UnitPrice= '" + strTmpUnitPrice
						+ "'and DebtOpu='" + zhanghuHao + "' ";
				logMa.d("martrin", "----sSql=" + sSql);
				rs = state.executeQuery(sSql);

				if (rs.next()) {// 在
					bRuKuYes = true;
					rknum1 = rs.getString("RuKuNum");

				}

				/**
				 * 如果判断数据是存在的，获取对应的 RuKuNum入库数据，进行更新操作、
				 */
				if (bRuKuYes) {

					// update 进行对应的操作数据、、、、、、、、、、、、、、、、、、、、、、
					int rknum = Integer.parseInt(rknum1)
							+ Integer.parseInt(strTmprukuNum);
					double db = (double) rknum
							* Double.parseDouble(strTmpUnitPrice);

					// /////////////////////////////////////////////////////////////////////////////////
					double db1 =  Integer.parseInt(rknum1)
							* Double.parseDouble(strTmpUnitPrice);            //对应的库存的总价格，库存的数量*对应的单价
					
					
					
					sSql4 = "update pt_Stock set RuKuNum = '" + rknum1
							+ "',Moneyt='" + db1
							+ "'  where CCDCID = '" + strTmpCCDCID
							+ "'and UnitPrice= '" + strTmpUnitPrice
							+ "'and DebtOpu='" + zhanghuHao + "'";
					TApplication.listrestore.add(sSql4);
					// ////////////////////////////////////////////////////////////////////////////////////////////////////

					String sql = " update pt_Stock set RuKuNum = '" + rknum
							+ "',Moneyt='" + db + "'  where CCDCID = '"
							+ strTmpCCDCID + "'and UnitPrice= '"
							+ strTmpUnitPrice + "'and DebtOpu='" + zhanghuHao
							+ "'";

					state.executeUpdate(sql);
					// 添加对话提示更新成功
					// handler.obtainMessage(EPC_KUGENGXING).sendToTarget();

					// return;

				} else {// 如果数据在库存中是不存在的做对应的插入操作。

					double zongPrice = Double.parseDouble(strTmpUnitPrice)
							* Double.parseDouble(strTmprukuNum);

					/**
					 * 在执行之前保存对应的删除语句，以便还原
					 */

					sSql5 = "delete from pt_Stock where CCDCID = '"
							+ strTmpCCDCID + "'and UnitPrice= '"
							+ strTmpUnitPrice + "'and DebtOpu='" + zhanghuHao
							+ "'";
					TApplication.listrestore.add(sSql5);

					sSql = ("insert into pt_Stock ([RuKuID], EPCID, CCDCID, RuKuNum, UnitPrice, Moneyt, "
							+ "ISPid, PiaoHao, rukuDate, kuFangHao, KuWeiHao, OpID, DebtOpu,"
							+

							"[Demo]) " + "values ('"
							+ strTmprukuDanHao1
							+ "', '"
							+ strTmpEpcID
							+ "', "
							+ "'"
							+ strTmpCCDCID
							+ "', '"
							+ strTmprukuNum
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
							+ strTmprukuDate
							+ "', '"
							+ strTmpkuFangHao
							+ "', '"
							+ strTmpKuWeiHao
							+ "', "
							+ "'"
							+ strTmpOpID
							+ "', "
							+ "'" + zhanghuHao + "', " + "'" + strTmpDemo + "') ");

					logMa.d("martrin", "----sSql=" + sSql);
					state.executeUpdate(sSql);

				}
			}

			if (false == UntilBiz.IsInventDataOk()) {
				handler.obtainMessage(STOCKERROR1).sendToTarget();
				return;
			}
			if (false == UntilBiz.IsInventDataOk1()) {
				handler.obtainMessage(STOCKERROR1).sendToTarget();
				return;
			}

			rs.close();

			Intent i = new Intent(Constant.INBOND_BIZ);
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
		}
	}
}
