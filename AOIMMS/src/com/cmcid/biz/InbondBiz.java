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

				Toast.makeText(InbondBiz.this, " �ڡ�"+strNo+"���������գ�����²���!", 0).show();
				break;
			case EPC_ZHANGHU:
				Toast.makeText(InbondBiz.this, "��ȷ���˻����ͣ�", 0).show();
				break;
			case EPC_STOCK:
				Toast.makeText(InbondBiz.this, "����Ѿ����ڣ�������ȷ�ϻ��", 0).show();
				break;
			case EPC_KUGENGXING:
				Toast.makeText(InbondBiz.this, "����Ѹ��£����ݸ������", 0).show();
				break;
			case STOCKERROR:
				Toast.makeText(InbondBiz.this, "�������������ȷ��", 0).show();
				break;
			case STOCKERROR1:
				Toast.makeText(InbondBiz.this, "��������������ȷ��", 0).show();
				break;
			case ZERO:
				Toast.makeText(InbondBiz.this, "�ڡ�"+strNo1+"�������۸�������ȷ�ϣ�", 0).show();
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
		String rukuDanHao1 = "";// �������������idΪ i��ͷ
		String rukuDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));
		try {
			// �༭��ⵥ�� ��ʽΪ r+����+6λ����000000������
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

			// ͨ��ISPname ��ȡISPid
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

			// ͨ���ⷿ�� ��ȡkuFangHao
			String sql3 = "select * from pt_KuFangT where kuFangName = '"
					+ kufang + "'";
			rs = state.executeQuery(sql3);
			while (rs.next()) {
				kuFangHao = rs.getString("kuFangHao");
			}

			// ͨ����λ�� ��ȡKuWeiHao
			String sql4 = "select * from pt_KuWeiT where KuWeiName = '" + kuwei
					+ "'";
			rs = state.executeQuery(sql4);
			while (rs.next()) {
				KuWeiHao = rs.getString("KuWeiHao");
			}

			// ͨ��username ��ȡuserID  username
			
			String username1 = TApplication.strUser; ;
			String sql5 = "select * from pt_POperate where OpName = '"
					+ username1 + "'";
			rs = state.executeQuery(sql5);
			while (rs.next()) {
				userID = rs.getString("OpID");
			}

			ArrayList<String> listEPC = new ArrayList<String>();

			/**
			 * ����˻�����
			 */
//			boolean bRukuZhanghu = false;
//			if (zhanghuHao.equals("1")) {
//				// bRukuZhanghu = false;// ����Ϊtrue��Ҫ�ж�
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

			// �ж��Ƿ�������
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
			 * ���е�һ�αȱ��� ����EPC ��ѯ���������ԭʼ����ʾ��Ӧ�������Ǵ��ڵġ�
			 */

			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				strTemq = map.get("ccdc");
				strTemp = map.get("epc");	
				strNo = map.get("no");
				String deopt = null;
				/**
				 * �ж϶�Ӧ�Ļ����Ƿ��Ѿ�����
				 */

				sSql = "select * from pt_EPCOri where EPCID = '" + strTemp
						+ "' ";

				rs = state.executeQuery(sSql);
				if (rs.next()) {// ��
					
					handler.obtainMessage(EPC_CHONGFU).sendToTarget();

					return;
				}

			}

			TApplication.listrestore.clear();
			/**
			 * �ڶ��α��� ��ԭʼ���в����Ӧ���������
			 */

			// ��EPC�б���뵽EPCԭʼ������
			for (int i = 0; i < list.size(); i++) { // ���α���
				String strTmpCCDCID; // ���ʱ���
				String strTmppEPCID; // EPC��
				String strTmppCiShu; // ����
				String strTmppReadTime; // ��ȡʱ��
				String strunitPrice; // �۸�
				String strTmppCreateTime = rukuDate; // ����ʱ��(��������ʱ��)

				HashMap<String, String> map = list.get(i);
				strTmppEPCID = map.get("epc"); // EPC
				strTmppCiShu = map.get("count"); // ����
				strTmppReadTime = map.get("ReadTime"); // ��ȡʱ��
				strTmpCCDCID = map.get("ccdc"); // ���ʱ���
				// strunitPricef = map.get("unitPrice");
				strunitPrice = map.get("unitPrice");// �۸�
				strNo1 = map.get("no");
				
				
			//	int   price = Integer.parseInt(strunitPrice);
				if (strunitPrice.equals("00.00")) {
					handler.obtainMessage(ZERO).sendToTarget();
					return;
				}

				// ���ʱ����ǲ���Ψһһ����¼,����EPC
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

			String strTmprukuDanHao; // ��ⵥ��
			String strTmprukuDanHao1;
			String strTmpEpcID; // EPC��ʹ���ڿ���У�дΪ��ֵ
			String strTmpEpcID1; // EPC��ʹ���ڿ��
			String strTmpCCDCID; // ���ʱ���
			String strTmprukuNum; // �������
			String strTmpUnitPrice; // ����
			String strTmprukuTotalPrice; // ���
			String strTmpISPid; // ��Ӧ�̱��
			String strTmpPiaoHao; // Ʊ��
			String strTmprukuDate; // �������
			String strTmpkuFangHao; // �ⷿID
			String strTmpKuWeiHao; // ��λID
			String strTmpOpID; // �û�ID
			String strTmpDemo; // ��ע

			strTmprukuDanHao = rukuDanHao;
			strTmprukuDanHao1 = rukuDanHao1;
			strTmpEpcID = "";// ��ⵥ��EPC��Ϊ�գ���Ϊһ�����ʱ�������ж��EPC��EPC����һ�ű�
			strTmpISPid = ISPid;
			strTmpPiaoHao = piaoHao;
			strTmprukuDate = rukuDate;
			strTmpkuFangHao = kuFangHao;
			strTmpKuWeiHao = KuWeiHao;
			strTmpOpID = userID;
			strTmpDemo = "";

			// �ٽ����ݲ��������
			for (int i = 0; i < list.size(); i++) {

				// strTmprukuDanHao = rukuDanHao;

				strTmpEpcID = "";// ��ⵥ��EPC��Ϊ�գ���Ϊһ�����ʱ�������ж��EPC��EPC����һ�ű�
				strTmpISPid = ISPid;
				strTmpPiaoHao = piaoHao;
				strTmprukuDate = rukuDate;
//				strTmpkuFangHao = kuFangHao;
//				strTmpKuWeiHao = KuWeiHao;
				strTmpOpID = userID;
				strTmpDemo = "";

				HashMap<String, String> map = list.get(i);

				strTmpCCDCID = map.get("ccdc"); // ���ʱ���
				// strTmprukuNum = map.get("rukuNum"); //����
				// strTmprukuNum = "1";
				strTmpUnitPrice = map.get("unitPrice"); // ����
				// strTmprukuTotalPrice = map.get("totalMoney"); // ���
				// strTmprukuTotalPrice = strTmpUnitPrice;
				strTmpEpcID1 = map.get("epc");
				//strTmpDemo = map.get("demoru"); // ��ע

				// ////////////////////////////////////////////////////////////////////////
				
				
				
				
				
//				String sqlString =  "select * from pt_RuKuT where EPCID = '"+strTmpEpcID1+"'";
//				 rs = state.executeQuery(sqlString);
//				if (rs.next()) {
//					//��Ӧ�����ݿ���в���
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
							+ strTmpUnitPrice // ��������Ϊһ�������ܼ�Ϊ�۸�
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
			 * �ж϶�Ӧ�Ļ����Ƿ��Ѿ�����,����Ӧ�ĸ��²������� �������ݲ�����
			 */
			for (int i = 0; i < list1.size(); i++) {
				HashMap<String, String> map = list1.get(i);

				strTmpCCDCID = map.get("ccdc"); // ���ʱ���
				strTmprukuNum = map.get("rukuNum"); // ����
				strTmpUnitPrice = map.get("unitPrice"); // ����
				strTmprukuTotalPrice = map.get("totalMoney"); // ���
				strTmpDemo = ""; // ��ע

				String rknum1 = null;

				sSql = "select * from pt_Stock where CCDCID = '" + strTmpCCDCID
						+ "'and UnitPrice= '" + strTmpUnitPrice
						+ "'and DebtOpu='" + zhanghuHao + "' ";
				logMa.d("martrin", "----sSql=" + sSql);
				rs = state.executeQuery(sSql);

				if (rs.next()) {// ��
					bRuKuYes = true;
					rknum1 = rs.getString("RuKuNum");

				}

				/**
				 * ����ж������Ǵ��ڵģ���ȡ��Ӧ�� RuKuNum������ݣ����и��²�����
				 */
				if (bRuKuYes) {

					// update ���ж�Ӧ�Ĳ������ݡ�������������������������������������������
					int rknum = Integer.parseInt(rknum1)
							+ Integer.parseInt(strTmprukuNum);
					double db = (double) rknum
							* Double.parseDouble(strTmpUnitPrice);

					// /////////////////////////////////////////////////////////////////////////////////
					double db1 =  Integer.parseInt(rknum1)
							* Double.parseDouble(strTmpUnitPrice);            //��Ӧ�Ŀ����ܼ۸񣬿�������*��Ӧ�ĵ���
					
					
					
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
					// ��ӶԻ���ʾ���³ɹ�
					// handler.obtainMessage(EPC_KUGENGXING).sendToTarget();

					// return;

				} else {// ��������ڿ�����ǲ����ڵ�����Ӧ�Ĳ��������

					double zongPrice = Double.parseDouble(strTmpUnitPrice)
							* Double.parseDouble(strTmprukuNum);

					/**
					 * ��ִ��֮ǰ�����Ӧ��ɾ����䣬�Ա㻹ԭ
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
