package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.Intent;

import com.cmcid.TApplication;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;

public class EditUserBiz extends IntentService{

	public EditUserBiz() {
		super("edituser");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String username = intent.getStringExtra("username");
		String newPwd = intent.getStringExtra("newPwd");
		int status = -1;
		try {
			//提取要编辑的用户名和用户密码
			Statement state = TApplication.connection.createStatement();
			String sql1 = "select * from pt_POperate where OpName = '"+username+"'";
			ResultSet rs = state.executeQuery(sql1);
			String password = "";
			while(rs.next()){
				password = rs.getString("Pwd");
			}
			rs.close();
			password = newPwd;
			//更新用户数据
			String sql2 = "update pt_POperate set Pwd = '"+password+"' where OpName = '"+username+"'";
			int executeUpdate = state.executeUpdate(sql2);
			if(executeUpdate==1){
				//获取当前时间
				String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
				String sql3 = "update pt_POperate set UpdateTime = '"+format+"' where OpName = '"+username+"'";
				state.execute(sql3);
				status = Constant.EDIT_USER_SUCCESS;
			}else{
				status = Constant.EDIT_USER_FAILURE;
			}
			state.close();
		} catch (Exception e) {
			status = Constant.EDIT_USER_FAILURE;
			ExceptionUtil.handleException(e);
		}finally{
			Intent i = new Intent(Constant.EDIT_USER_BIZ);
			i.putExtra(Constant.KEY_DATA, status);
			sendBroadcast(i);
		}
	}

}
