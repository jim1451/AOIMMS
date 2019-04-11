package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.content.Intent;

import com.cmcid.TApplication;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;

public class AddUserBiz extends IntentService{

	public AddUserBiz() {//�޲ι��췽��
		super("adduser");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		String username = intent.getStringExtra("username");
		String password = intent.getStringExtra("password");
		int right = intent.getIntExtra("right", -1);
		String type = "";//�û����ͣ�Ȩ�ޣ�
		if(right==1){
			type = "administrator";
		}else{
			type = "ordinary";
		}
		int status = -1;
		try {
			//��֤���û��Ƿ��Ѿ�����
			Statement state = TApplication.connection.createStatement();
			String sql1 = "select * from pt_POperate where OpName = '"+username+"'";
			ResultSet rs1 = state.executeQuery(sql1);
			while(rs1.next()){
				status = Constant.USER_EXISTS;
			}
			rs1.close();
			if(status==Constant.USER_EXISTS){
				//���㲥
				Intent i = new Intent(Constant.ADD_USER_BIZ);
				i.putExtra(Constant.KEY_DATA, status);
				sendBroadcast(i);
			}else{
				//��ȡ�µ��û����ݱ�
				String sql2 = "select * from pt_POperate";
				ResultSet rs2 = state.executeQuery(sql2);
				List<Map<String, String>> list = new ArrayList<Map<String,String>>();
				while(rs2.next()){
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", rs2.getString("OpName"));
					map.put("password", rs2.getString("Pwd"));
					list.add(map);
				}
				int userId = list.size()+1;
				//�����µ��û�
				//����ʱ��
				long times = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String format = sdf.format(new Date(times));
				String sql3 = "insert into pt_POperate (OpID,OpName,OpRight,Pwd,RightDes,CreateTime) values ('"+userId+"','"+username+"','"+right+"','"+password+"','"+type+"','"+format+"')";
				int executeUpdate = state.executeUpdate(sql3);
				//1 ��ʾ����ɹ�
				if(executeUpdate==1){
					status = Constant.ADD_USER_SUCCESS;
				}else{
					status = Constant.ADD_USER_FAILURE;
				}
				rs2.close();
				state.close();
				Intent i = new Intent(Constant.ADD_USER_BIZ);
				i.putExtra(Constant.KEY_DATA, status);
				sendBroadcast(i);
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
}
