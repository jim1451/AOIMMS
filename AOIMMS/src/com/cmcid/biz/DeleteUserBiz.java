package com.cmcid.biz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import android.content.Intent;

import com.cmcid.TApplication;
import com.cmcid.util.Constant;
import com.cmcid.util.ExceptionUtil;

public class DeleteUserBiz {
	//��ȡ�����û����������б�
	public static void getUsernames(){
		new Thread(){
			public void run() {
				ArrayList<String> list = new ArrayList<String>();
				int status = -1;
				try {
					String sql = "select * from pt_POperate";
					Statement state = TApplication.connection.createStatement();
					ResultSet rs = state.executeQuery(sql);
					while(rs.next()){
						String username = rs.getString("OpName");
						list.add(username);
					}
					rs.close();
					state.close();
					status = Constant.GET_USER_SUCCESS;
					Intent intent = new Intent(Constant.DELETE_USER_BIZ);
					intent.putExtra(Constant.KEY_DATA, status);
					intent.putExtra("userlist", list);
					TApplication.instance.sendBroadcast(intent);
				} catch (SQLException e) {
					ExceptionUtil.handleException(e);
				}
			};
		}.start();
	}
	//ɾ���û�
	public static void deleteUsers(final ArrayList<String> list){
		new Thread(){
			public void run() {
				int status = -1;
				try {
					Statement state = TApplication.connection.createStatement();
					for(int i=0;i<list.size();i++){
						//ɾ��һ�������û�
						String username = list.get(i);
						String sql = "delete from pt_POperate where OpName = '"+username+"'";
						state.executeUpdate(sql);
					}
					status = Constant.DELETE_SUCCESS;
					state.close();
				} catch (Exception e) {
					status = Constant.DELETE_FAILURE;
					ExceptionUtil.handleException(e);
				}finally{
					Intent intent = new Intent(Constant.DELETE_USER_BIZ);
					intent.putExtra(Constant.KEY_DATA, status);
					TApplication.instance.sendBroadcast(intent);
				}
			};
		}.start();
	}
}
