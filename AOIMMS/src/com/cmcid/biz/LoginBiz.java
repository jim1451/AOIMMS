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

public class LoginBiz extends IntentService{

	public LoginBiz() {
		super("login");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String username = intent.getStringExtra("username");
		String password = intent.getStringExtra("password");
		String right = null;
		boolean isAndroidLogin = true;
		try {
			int status = -1;
			String username1 = "";
			String password1 = "";
			String sql = "select * from pt_POperate";
			Statement state = TApplication.connection.createStatement();
			ResultSet rs = state.executeQuery(sql);
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			while(rs.next()){
				//��ȡ�û����������Ȩ�ޣ�����¼�Ա�
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", rs.getString("OpName"));
				map.put("password", rs.getString("Pwd"));
				map.put("right", rs.getString("RightDes"));
				list.add(map);
			}
			for(int i=0;i<list.size();i++){
				Map<String, String> map = list.get(i);
				username1 = map.get("username").trim();
				password1 = map.get("password").trim();
				//�û�������
				if(!username.equals(username1)){
					status = Constant.NO_THIS_PEOPLE;
					continue;
				}
				//�������
				if(username.equals(username1)&&!password.equals(password1)){
					status = Constant.PASSWORD_ERROR;
					break;
				}
				//��¼�ɹ�
				if(username.equals(username1) && password.equals(password1)){
					status = Constant.LOGIN_SUCCESS;
					right = map.get("right").trim();
					break;
				}
			}
			//��¼ʱ��
			long times = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String format = sdf.format(new Date(times));
			String sql2 = "update pt_POperate set LoginTime = '"+format+"' where OpName = '"+username+"'";
			String sql3 = "";
			if(isAndroidLogin){
				//�ֳֻ��˵�¼
				sql3 = "update pt_POperate set LoginWay = 0 where OpName = '"+username+"'";
			}else{
				//pc�˵�¼
				sql3 = "update pt_POperate set LoginWay = 1 where OpName = '"+username+"'";
			}
			state.execute(sql2);
			state.execute(sql3);
			Intent i = new Intent(Constant.LOGIN_BIZ);
			i.putExtra(Constant.KEY_DATA, status);
			i.putExtra("right", right);
			i.putExtra("user", username1);
			sendBroadcast(i);
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
}
