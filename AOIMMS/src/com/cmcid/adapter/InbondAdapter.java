package com.cmcid.adapter;

import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcid.R;
import com.cmcid.TApplication;
import com.cmcid.biz.InbondBiz;
import com.cmcid.util.ExceptionUtil;
import com.cmcid.util.logMa;
import com.cmcid.view.InbondActivity;

public class InbondAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<HashMap<String, String>> list;
	private ArrayList<HashMap<String, String>> listepc;
	public HashMap<String, String> editorValue = new HashMap<String, String>();
	
	
	private InbondActivity inbondActivity;
	public InbondAdapter(InbondActivity  inbondActivity) {
		this.inbondActivity = inbondActivity;
	}
		
	
	
	public InbondAdapter(Context context, ArrayList<HashMap<String, String>> list, ArrayList<HashMap<String, String>> list1) {
		this.context = context;
		this.list = list;
		this.listepc = list1;
		init();
	}
	private void init() {
		editorValue.clear();
	}
	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	private Integer index = -1;
	ViewHolder vh = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = View.inflate(context, R.layout.lv_item_inbond_data, null);
			index = position;
			vh = new ViewHolder();
			vh.tvNo = (TextView) convertView.findViewById(R.id.lvItemID_inbond);// 序号
			vh.tvCCDC = (TextView) convertView.findViewById(R.id.lvItemCCDC_inbond);//物资编码
			vh.tvName = (TextView) convertView.findViewById(R.id.lvItemName_inbond);//物资名称
			vh.tvXiyuName = (TextView) convertView.findViewById(R.id.lvItemXiyuName_inbond);//西语名称
			vh.tvType = (TextView) convertView.findViewById(R.id.lvItemType_inbond);//规格型号
			vh.tvUnit = (TextView) convertView.findViewById(R.id.lvItemUnit_inbond);//单位
			vh.tvInbondNum = (TextView) convertView.findViewById(R.id.lvItemNum_inbond);// 数量
			vh.etUnitPrice = (EditText) convertView.findViewById(R.id.lvItemUnitPrice_inbond);// 单价
			vh.etUnitPrice.setTag(position);
			vh.tvTotalMoney = (TextView) convertView.findViewById(R.id.lvItemTotalMoney_inbond);// 金额
		//	vh.tvTotalMoney1 = (TextView) convertView.findViewById(R.id.lvItemTotalMoney_inbond1);
			vh.btnBack = (Button) convertView.findViewById(R.id.lvItemBack_inbond);// 备注
			
			vh.etUnitPrice.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_UP){
						//MotionEvent { action=ACTION_UP, id[0]=0, x[0]=48.0, y[0]=44.0, toolType[0]=TOOL_TYPE_FINGER, 
						//buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=841264, downTime=841211, deviceId=0, source=0x1002 }
						index = (Integer) v.getTag();
//						String index2 = String.valueOf(index);
//						System.out.println("index"+index2);
						
					}
					return false;
				}
			});

			//给EditText添加监听
			class MyTextWatcher implements TextWatcher{
				private ViewHolder holder;
				public MyTextWatcher(ViewHolder holder) {
					this.holder = holder;
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					//logMa.d("martrin", "----beforeTextChanged----");
				}
				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String str=s.toString();
					if(s!=null && !"".equals(str)){
						try {
							int position = (Integer) holder.etUnitPrice.getTag();
							list.get(position).put("unitPrice", s.toString());// 当EditText数据发生改变的时候存到list变量中
							String num = list.get(position).get("rukuNum");
							double totalmoney = (Integer.parseInt(num))*(Double.parseDouble(s.toString()));
							DecimalFormat df = new DecimalFormat("0.00");
							list.get(position).put("totalMoney", String.valueOf(df.format(totalmoney)));
							logMa.d("martrin", "----totalMoney:"+String.valueOf(df.format(totalmoney)));
//							vh.tvTotalMoney.setText(String.valueOf(df.format(totalmoney)));//金额
							vh.tvTotalMoney.setText(String.valueOf(totalmoney));//金额
						//	vh.tvTotalMoney.setText(String.valueOf(totalmoney);
						//	vh.tvTotalMoney1.setText(String.valueOf(df.format(totalmoney)));
							
							HashMap<String, String> map;
							String strCCDC = list.get(position).get("ccdc");
							for (int x = 0; x < listepc.size(); x++) {
								map = listepc.get(x);
								if (strCCDC.equals(map.get("ccdc"))) {
									
									map.put("unitPrice", s.toString());
									

//									map.put("totalMoney", s.toString());
								map.put("totalMoney",String.valueOf(totalmoney) );
								}
								
							}
						} catch (Exception e) {
							// TODO: handle exception
							
						}
						
					}else{
//						int position = (Integer) holder.etUnitPrice.getTag();
//						list.get(position).put("totalMoney", "0.00");
//						String money=list.get(position).get("totalMoney");
//						vh.tvTotalMoney.setText(money.toString());//金额
					}
					
					// TODO Auto-generated method stub
					//logMa.d("martrin", "----onTextChanged----");
				}
				@Override
				public void afterTextChanged(Editable s) {
					//logMa.d("martrin", "----afterTextChanged----");
					// String str = s.toString();
					// int posDot = str.indexOf(".");
					// if(posDot<=0) return;
					// if(str.length()-posDot-1>2){
					// s.delete(posDot+3, posDot+4);
					// }
//					 if(s!=null && !"".equals(str)){
//						int position = (Integer) holder.etUnitPrice.getTag();
//						list.get(position).put("unitPrice", s.toString());// 当EditText数据发生改变的时候存到list变量中
//						String num = list.get(position).get("rukuNum");
//						double totalmoney = (Integer.parseInt(num))*(Double.parseDouble(s.toString()));
//						DecimalFormat df = new DecimalFormat(".00");
//						list.get(position).put("totalMoney", String.valueOf(df.format(totalmoney)));
//					logMa.d("martrin", "----totalMoney:"+String.valueOf(df.format(totalmoney)));
//					 vh.tvTotalMoney.setText(String.valueOf(df.format(totalmoney)));//金额
					
					
					
					String str=s.toString();
					if (s==null && "".equals(str)) {
						int position = (Integer) holder.etUnitPrice.getTag();
						list.get(position).put("totalMoney", "0.00");
						String money=list.get(position).get("totalMoney");
						vh.tvTotalMoney.setText(money.toString());//金额
					}
						
					// }
				}
			}
			vh.etUnitPrice.addTextChangedListener(new MyTextWatcher(vh));
			
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
			vh.etUnitPrice.setTag(position);
		}
		
		final int ipos = position;
		final HashMap<String, String> map = list.get(position);
		String value = map.get("no");
		if(value!=null)vh.tvNo.setText(value);//序码
		
		value = map.get("ccdc");
		if(value!=null)vh.tvCCDC.setText(value);//物资编码
		
		value = map.get("name");
		if(value!=null)vh.tvName.setText(value);//物资名称
		value = map.get("xiyuName");
		if(value!=null)vh.tvXiyuName.setText(value);//西语名称
		value = map.get("type");
		if(value!=null)vh.tvType.setText(value);//规格型号
		value = map.get("unit");
		if(value!=null)vh.tvUnit.setText(value);//单位
		value = map.get("rukuNum");
		if(value!=null)vh.tvInbondNum.setText(value);//数量
		value = map.get("unitPrice");
		if(value!=null && !"".equals(value))vh.etUnitPrice.setText(value);//单价
		value = map.get("totalMoney");
		if(value!=null && !"".equals(value))vh.tvTotalMoney.setText(value);//金额
		
//		if (Integer.parseInt(map.get("rukuNum"))>1) {
//			position += Integer.parseInt(map.get("rukuNum"));
//		}
		
		
		
		
		
		vh.etUnitPrice.clearFocus();
		if(index!=-1 && index==position)vh.etUnitPrice.requestFocus(); 
		
		//撤销上一步操作
		vh.btnBack.setText(R.string.inbond_cancel);
		vh.btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMessage(map, ipos);
				
			//	list.remove(index);
				//notifyDataSetChanged();

				
				
			}
		});
		return convertView;
	}
	protected void showMessage(final HashMap<String, String> map, final int ipos) {
	
		new AlertDialog.Builder(context).setMessage(R.string.back_inbond_message)
		.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
				HashMap<String, String> map;
				String strCCDC = list.get(ipos).get("ccdc");//strCCDC=07050102000003
				
				/*
				 * delete epc  of ccdcid
				 */
				while (true){
					boolean bflag = false;
					for (int x = 0; x < listepc.size(); x++) {
						map = listepc.get(x);
						if (strCCDC.equals(map.get("ccdc"))) {
							listepc.remove(x);
							
	//						list.remove(x);
							
							bflag = true;
							break;
						}
					}
					if (!bflag) {
						break;
					}
				}
				
					while (true){
						boolean bflag1 = false;
						for (int x = 0; x < list.size(); x++) {
							map = list.get(x);
							if (strCCDC.equals(map.get("ccdc"))) {
								list.remove(x);
								bflag1 = true;
								break;
							}
						}
						if (!bflag1) {
							break;
						}
					}
					
						logMa.d("martrin", "----listepc="+listepc.size()+", list="+list.size());
						
						//list.remove(ipos);//ipos
						notifyDataSetChanged();
				
				
				
//				new Thread(){
//					public void run() {
//						try {
//							Statement state = TApplication.connection.createStatement();
//							String sql = "delete from pt_RuKuT where CCDCID = '"+map.get("ccdc")+"'";
//							int executeUpdate = state.executeUpdate(sql);
//							handler.obtainMessage(shangchu).sendToTarget();
//							
//							
//							
//						} catch (Exception e) {
//							ExceptionUtil.handleException(e);
//						}
//					};
//				}.start();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		})
		.create().show();
	}
	class ViewHolder{
		TextView tvNo;// 序码
		TextView tvCCDC;// 物资编码
		TextView tvName;// 物资名称
		TextView tvXiyuName;// 西语名称
		TextView tvType;// 规格型号
		TextView tvUnit;// 单位
		TextView tvInbondNum;// 数量
		EditText etUnitPrice;// 单价
		TextView tvTotalMoney;// 金额
	//	TextView tvTotalMoney1;
		Button btnBack;// 备注
	}
}
