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

public class YikuBiz extends IntentService {
	private static final int STOCKERROR = 0;
	private static final int STOCKERROR1 = 1;

	public YikuBiz() {
		super("yiku_biz");
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STOCKERROR:
				Toast.makeText(getApplicationContext(), "�������������ȷ�ϣ�", 0)
						.show();
				break;
			case STOCKERROR1:
				Toast.makeText(getApplicationContext(), "�ƿ�����������ȷ�ϣ�", 0).show();
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onHandleIntent(Intent intent) {
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listYiku");
		ArrayList<HashMap<String, String>> list1 = (ArrayList<HashMap<String, String>>) intent
				.getSerializableExtra("listYiku1");
//		String linliaoName = intent.getStringExtra("linliaoName");
//		String linliaoHao = "";
		String username = intent.getStringExtra("username");
	//	String username1= intent.getStringExtra("username");
		
		
		
		
		
		
		String userID = "";
		String kufang = intent.getStringExtra("kufang");
		String kufangHao = "";
		String kuwei = intent.getStringExtra("kuwei");
		String kuweiHao = "";

		Statement state = null;
		String yikuDanHao = "";
		String yikuDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(System.currentTimeMillis()));

		try {
			// �༭�˿ⵥ�� ��ʽ������+��λ����0000������
			String format = yikuDate.trim().replace("-", "").substring(0, 8);
			state = TApplication.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql1 = "select * from pt_YiKuT where YiKuID like '%"
					+ format + "%' order by YiKuID asc";
			ResultSet rs = state.executeQuery(sql1);
			if (rs.next()) {
				String str = rs.getString("YiKuID");
				rs.last();
				str = rs.getString("YiKuId");
				int num = Integer.parseInt(str.substring(9));
				yikuDanHao = "y" + format
						+ String.format("%0" + 6 + "d", num + 1);
			} else {
				yikuDanHao = "y" + format + "000001";
			}

			// ͨ�����ϵ�λ���� ��ȡ���ϵ���
//			String sql2 = "select * from pt_LinLiaoT where linliaoName = '"
//					+ linliaoName + "'";
//			rs = state.executeQuery(sql2);
//			while (rs.next())
//				linliaoHao = rs.getString("linliaoHao");

			// ͨ���ⷿ�� ��ÿⷿ��
			String sql3 = "select * from pt_KuFangT where kuFangName = '"
					+ kufang + "'";
			rs = state.executeQuery(sql3);
			while (rs.next())
				kufangHao = rs.getString("kuFangHao");

			// ͨ����λ�� ��ÿ�λ��
			String sql4 = "select * from pt_KuWeiT where KuWeiName = '" + kuwei
					+ "'";
			rs = state.executeQuery(sql4);
			while (rs.next())
				kuweiHao = rs.getString("KuWeiHao");

			// ͨ��username��ȡuserID
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

			TApplication.listrestore.clear();//�ڲ�������֮ǰ���Ӧ����ղ���
			
			
			
			
			
			
			// ��ԭʼ�����������Ӧ�ĸ���ɾ��
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
				 * ��ȡ��ԭ����
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
				
				
//	     	String		sSql = 	"insert into pt_EPCOri ([EPCID], CiShu, ReadTime, CreateTime, CCDCID,UnitPricf,DebtOpt,KuFangHaoOri,KuWeiHaoOri) "
//				+ "values ('"
//				+ EPCID
//				+ "', '"
//				+ CiShu
//				+ "', "
//				+ "'"
//				+ ReadTime
//				+ "',"
//				+ "'"
//				+ CreateTime
//				+ "',"
//				+ "'"
//				+ CCDCID
//				+ "',"
//				+ "'" + UnitPricf + "'," + "'" + DebtOpt + "'," + "'" + KuFangHaoOri + "'," + "'" + KuWeiHaoOri + "') ";
				
				String  sSql = "update pt_EPCOri  set KuFangHaoOri = '"+KuFangHaoOri+"',KuWeiHaoOri = '"+KuWeiHaoOri+"' where EPCID = '"+epc+"' ";
		    TApplication.listrestore.add(sSql);

				
				
				
				

//				String sqlsString2 = "delete from pt_EPCOri where EPCID ='"
//						+ epc + "'";
		    
		    
		    	String sqlsString2 = "update pt_EPCOri set KuFangHaoOri = '"+kufangHao+"',KuWeiHaoOri = '"+kuweiHao+"' where EPCID = '"+epc+"' ";
				state.executeUpdate(sqlsString2);

			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			/**
			 * ���ݶ�Ӧ��EPC ����Ӧ�����ݲ��뵽�˿���У������α�������
			 */
     
			for (int i = 0; i < list.size(); i++) {
				
				
				

				HashMap<String, String> map = list.get(i);
				String epc = map.get("epc");
				String ccdc = map.get("ccdc");
				String yikuNum = "1";
				String unitPrice = map.get("unitPrice");
				String DebtOpu = map.get("DebtOpu");
				
				/**
				 * ���ݲ���֮ǰ�ȴ��Ӧ��  ɾ�����ݵı���
				 */
				String sSql= "delete from  pt_YiKuT  where EPCID = '" + epc
						+ "' and CCDCID = '"+ccdc+"' and UnitPrice = '"+unitPrice+"' and  rukuDate = '"+yikuDate+"' and DebtOpu = '"+DebtOpu+"'";
				TApplication.listrestore.add(sSql);
				
				
				
				
				
				
				
				String sql7 = "insert into pt_YiKuT (YiKuID,EPCID,CCDCID,RuKuNum,UnitPrice,Moneyt,rukuDate,kuFangHao,KuWeiHao,OpID,DebtOpu) "
						+ "values ('"
						+ yikuDanHao
						+ "','"
						+ epc
						+ "','"
						+ ccdc
						+ "','"
						+ yikuNum
						+ "','"
						+ unitPrice
						+ "','"
						+ unitPrice
						+ "','"
						+ yikuDate
						+ "','"
						+ kufangHao + "','" + kuweiHao + "','" + userID + "','" + DebtOpu + "')";
				state.executeUpdate(sql7);

			}

			/**
			 * ���Ĵα��� ���ݶ�Ӧ��CCDCID UnitPrice DebtOpu��ȡ��Ӧ�����ݣ������ȡ����������ͺ�̨������ݱȽ� 1
			 * ���ݲ����ھ�����Ӧ�Ĳ������ 2�Ѿ��������ݾ͸��²���
			 * 
			 */
			for (int i = 0; i < list1.size(); i++) {
				HashMap<String, String> map = list1.get(i);
				String strTmpCCDCID = map.get("ccdc"); // ���ʱ���
				String strTmpchukuNum = map.get("chukuNum"); // ����
				String strTmpUnitPrice = map.get("unitPrice"); // ����
				String strDebtOpt = map.get("DebtOpu");// �˻�����
				String KuWeiHao1 = map.get("KuWeiHao");
				String kuFangHao1 = map.get("kuFangHao");
				String epc = "";

				String sSql = "select * from pt_Stock where CCDCID = '"
						+ strTmpCCDCID + "'and UnitPrice= '" + strTmpUnitPrice
						+ "'and DebtOpu='" + strDebtOpt + "' ";
				// logMa.d("martrin", "----sSql=" + sSql);
				rs = state.executeQuery(sSql);
				if (rs.next()) {

					String sqlString= "update pt_Stock set kuFangHao = '" + kuFangHao1
							+ "',KuWeiHao='" + KuWeiHao1 + "'  where CCDCID = '"
							+ strTmpCCDCID + "'and UnitPrice= '"
							+ strTmpUnitPrice + "'and DebtOpu='" + strDebtOpt
							+ "'";
					TApplication.listrestore.add(sqlString);
					
					
					
					String sql = " update pt_Stock set kuFangHao = '" + kufangHao
							+ "',KuWeiHao='" + kuweiHao + "'  where CCDCID = '"
							+ strTmpCCDCID + "'and UnitPrice= '"
							+ strTmpUnitPrice + "'and DebtOpu='" + strDebtOpt
							+ "'";

					state.executeUpdate(sql);

				}

			}

			if (false ==UntilBiz.IsInventDataOk() ){
				handler.obtainMessage(STOCKERROR1).sendToTarget();
				return;
			}
			if (false ==UntilBiz.IsInventDataOk1() ){
				handler.obtainMessage(STOCKERROR1).sendToTarget();
				return;
			}
			
			
			
			
			rs.close();
			Intent i = new Intent(Constant.YIKU_BIZ);
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
