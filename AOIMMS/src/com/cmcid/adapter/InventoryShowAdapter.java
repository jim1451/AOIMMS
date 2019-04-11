package com.cmcid.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cmcid.R;

public class InventoryShowAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<HashMap<String, String>> list;
	public InventoryShowAdapter(Context context,ArrayList<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
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
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView==null){
			convertView = View.inflate(context, R.layout.lv_item_inventory_show, null);
			vh = new ViewHolder();
			vh.tvNo = (TextView) convertView.findViewById(R.id.lvItemID_show);
			vh.tvCCDC = (TextView) convertView.findViewById(R.id.lvItemCCDC_show);
			vh.tvOutbondNum = (TextView) convertView.findViewById(R.id.lvItemNum_show);
			vh.tvName = (TextView) convertView.findViewById(R.id.lvItemName_show);
			vh.tvXiyuName = (TextView) convertView.findViewById(R.id.lvItemXiyuName_show);
			vh.tvType = (TextView) convertView.findViewById(R.id.lvItemType_show);
			vh.tvUnit = (TextView) convertView.findViewById(R.id.lvItemUnit_show);
			vh.tvUnitPrice = (TextView) convertView.findViewById(R.id.lvItemUnitPrice_show);
			vh.tvTotalMoney = (TextView) convertView.findViewById(R.id.lvItemTotalMoney_show);  //
			vh. tvKuWei = (TextView) convertView.findViewById(R.id.lvItemKuWei_show); 
			vh.tvKufang = (TextView) convertView.findViewById(R.id.lvItemKufang_show); 
			vh.tvInventNum = (TextView) convertView.findViewById(R.id.lvItemInventNum_show); 
			vh.tvInventMoney = (TextView) convertView.findViewById(R.id.lvItemInventMoney_show); 
			vh.tvStatus = (TextView) convertView.findViewById(R.id.lvItemStatus_show); 
	//		vh.btnBack = (Button) convertView.findViewById(R.id.lvItemBack_show);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		final HashMap<String, String> map = list.get(position);
		
		
		
		vh.tvNo.setText(map.get("no"));
		vh.tvCCDC.setText(map.get("CCDCID"));
		
		
	
		vh.tvOutbondNum.setText(map.get("StockNum"));
		vh.tvName.setText(map.get("Name"));
		vh.tvXiyuName.setText(map.get("XiYuName"));
		vh.tvType.setText(map.get("Type"));
		vh.tvUnit.setText(map.get("UnitA"));
		vh.tvInventNum.setText(map.get("InventNum"));
		vh.tvInventMoney.setText(map.get("InventMoney"));
		vh.tvStatus .setText(map.get("Status"));
		
		String unitPrice = map.get("UnitPrice");
		String Num = map.get("StockNum");
		String KuWei =  map.get("KuWei");
		String Kufang =  map.get("KuFang");
		vh.tvUnitPrice.setText(unitPrice);
		double totalMoney = (Double.parseDouble(unitPrice))*(Integer.parseInt(Num));
		String totalMoney1 = new DecimalFormat(".00").format(totalMoney);
	
		vh.tvTotalMoney.setText(totalMoney1);
		vh. tvKuWei .setText(KuWei);
		vh.tvKufang.setText(Kufang);
		
		
		
//		vh.btnBack.setText(R.string.yiku_cancel);
//		vh.btnBack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				showMessage(map);
//			}
//		});
		return convertView;
	}
	
	class ViewHolder{
		TextView tvNo;
		TextView tvCCDC;
		TextView tvOutbondNum;
		TextView tvName;
		TextView tvXiyuName;
		TextView tvType;
		TextView tvUnit;
		TextView tvUnitPrice;
		TextView tvTotalMoney;  
		TextView tvKuWei; 
		TextView tvKufang; 
		TextView tvInventNum; 
		TextView tvInventMoney; 
		TextView tvStatus; 
	}
}
