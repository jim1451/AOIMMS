package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cmcid.TApplication;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.IntentService;
import android.content.Intent;
//import android.os.Handler;
import android.widget.Toast;
import android.os.Handler;
import com.cmcid.TApplication;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.logMa;
import android.R.integer;

public   class UntilBiz {
	
	
	   static ResultSet rs;
	   static Statement state = null;
	   static String    RuKuNumtotal="";
	   static String    Moneyttotal="";
	  public static Boolean IsInventDataOk() throws SQLException{
		  Boolean bFlag = true;
		  state = TApplication.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		  String    RuKuNum2="";
		//  String Moneyt1= "";
		  String Moneyt2= "";
		String sqlString8 = "select sum(convert(decimal(10),RuKuNum)) as RuKuNum, sum(convert(decimal(10,2),Moneyt)) as Moneyt from pt_Stock";
		rs = state.executeQuery(sqlString8);
		if (rs.next()) {
								RuKuNumtotal	 = rs.getString("RuKuNum");
			                  Moneyttotal = rs.getString("Moneyt");
			
		}
		
		String sqlString9 = "select count(*) as RuKuNum , sum(convert(decimal(10,2),UnitPricf)) as Moneyt from pt_EPCOri";
		rs = state.executeQuery(sqlString9);
		if (rs.next()) {
			 RuKuNum2 = rs.getString("RuKuNum");
			 Moneyt2 = rs.getString("Moneyt");
		}
		if (Integer.parseInt(RuKuNumtotal) != Integer.parseInt(RuKuNum2)) {
			
		
//			handler.obtainMessage(STOCKERROR).sendToTarget();
			return false;
		}
		if (Double.parseDouble(Moneyttotal) != Double.parseDouble(Moneyt2) ) {

			//handler.obtainMessage(STOCKERROR1).sendToTarget();
			return false;
		}

		  return bFlag;
	  }
	  
	  public static Boolean IsInventDataOk1() throws SQLException {
		boolean bFlag = true;
		String RuKuNum = null;
		String Moneyt = null;
		String RuKuNum1 = null;
		String Moneyt1 = null;
		String RuKuNum2 = null;
		String Moneyt2 = null;
		String RuKuNum3 = null;
		String Moneyt3 = null;
		String RuKuNum4 = null;
		String Moneyt4 = null;
		//基本库存
		String sqlString="select sum(convert(decimal(10),RuKuNum)) as RuKuNum, sum(convert(decimal(10,2),Moneyt)) " +
				"as Moneyt from pt_Stock where RuKuID != 'I20150101000999'";
		rs = state.executeQuery(sqlString);
		if (rs.next()) {
			 RuKuNum = rs.getString("RuKuNum");
             Moneyt = rs.getString("Moneyt");
		}
		//入库
//		String sqlString1="select sum(convert(decimal(10),RuKuNum)) as RuKuNum, sum(convert(decimal(10,2),Moneyt)) as Moneyt from pt_RuKuT where RuKuID != 'I20150101000999'";
		String sqlString1="select sum(convert(decimal(10),RuKuNum)) as RuKuNum, sum(convert(decimal(10,2),Moneyt)) as Moneyt from pt_RuKuT";
		rs = state.executeQuery(sqlString1);
		if (rs.next()) {
			 RuKuNum1 = rs.getString("RuKuNum");
             Moneyt1 = rs.getString("Moneyt");
		}
		//出库
		String sqlString2="select sum(convert(decimal(10),ChuKuNum)) as RuKuNum, sum(convert(decimal(10,2),Moneyt)) as Moneyt from pt_ChuKuT";
		rs = state.executeQuery(sqlString2);
		if (rs.next()) {
			 RuKuNum2 = rs.getString("RuKuNum");
             Moneyt2 = rs.getString("Moneyt");
		}
		//退库
		String sqlString3="select sum(convert(decimal(10),ChuKuNum)) as RuKuNum, sum(convert(decimal(10,2),Moneyt)) as Moneyt from pt_TuiKuT";
		rs = state.executeQuery(sqlString3);
		if (rs.next()) {
			 RuKuNum3 = rs.getString("RuKuNum");
             Moneyt3 = rs.getString("Moneyt");
		}
	
		

		if(RuKuNum==null){

			 RuKuNum = "0";

			}



		if(RuKuNum1==null){

		 RuKuNum1 = "0";

		}
		if(RuKuNum2==null){

			RuKuNum2 = "0";

			}
		if(RuKuNum3==null){

			RuKuNum3 = "0";

			}
	
		if(Moneyt==null){

			Moneyt = "0";

			}
		if(Moneyt1==null){

			Moneyt1 = "0";

			}
		if(Moneyt2==null){

			Moneyt2 = "0";

			}
		if(Moneyt3==null){

			Moneyt3 = "0";

			}
		
//		int ruku = Integer.parseInt(RuKuNum)+ Integer.parseInt(RuKuNum1)- Integer.parseInt(RuKuNum2)+Integer.parseInt(RuKuNum3);
		int ruku = Integer.parseInt(RuKuNum1)- Integer.parseInt(RuKuNum2)+Integer.parseInt(RuKuNum3);
		if ( Integer.parseInt(RuKuNumtotal) != ruku) {
			return false;
		}
		
	//	double money = Math.round(Double.parseDouble(Moneyt) +Double.parseDouble(Moneyt1)-Double.parseDouble(Moneyt2)+Double.parseDouble(Moneyt3));
		double money = Math.round(Double.parseDouble(Moneyt1)-Double.parseDouble(Moneyt2)+Double.parseDouble(Moneyt3));
		if (money !=Math.round(Double.parseDouble(Moneyttotal))) {
			return false;
		}
		
		
		return bFlag;
		
	}
	  
	  
	  
	  
	  
	  
	
}
