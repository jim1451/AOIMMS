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

public class InventoryResultAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<HashMap<String, String>> list;
	public InventoryResultAdapter(Context context,ArrayList<HashMap<String, String>> list) {
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
			convertView = View.inflate(context, R.layout.lv_item_inventory_result, null);
			vh = new ViewHolder();
			vh.tvNo = (TextView) convertView.findViewById(R.id.lvItemID_result);
			vh.tvCCDC = (TextView) convertView.findViewById(R.id.lvItemCCDC_result);
			vh.tvOutbondNum = (TextView) convertView.findViewById(R.id.lvItemNum_result);
			vh.tvName = (TextView) convertView.findViewById(R.id.lvItemName_result);
			vh.tvXiyuName = (TextView) convertView.findViewById(R.id.lvItemXiyuName_result);
			vh.tvType = (TextView) convertView.findViewById(R.id.lvItemType_result);
			vh.tvUnit = (TextView) convertView.findViewById(R.id.lvItemUnit_result);
			vh.tvUnitPrice = (TextView) convertView.findViewById(R.id.lvItemUnitPrice_result);
			vh.tvTotalMoney = (TextView) convertView.findViewById(R.id.lvItemTotalMoney_result);
			vh.btnBack = (Button) convertView.findViewById(R.id.lvItemBack_result);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		final HashMap<String, String> map = list.get(position);
		
		
		
		vh.tvNo.setText(map.get("no"));
		vh.tvCCDC.setText(map.get("CCDCID"));
		
		
		String  noString  = map.get("CCDCID");
		String chukuNum = map.get("Moneyt");
	//	vh.tvOutbondNum.setText(chukuNum);
		vh.tvName.setText(map.get("UnitPrice"));
		vh.tvXiyuName.setText(map.get("XiYuName"));
		vh.tvType.setText(map.get("Type"));
	//	vh.tvUnit.setText(map.get("unit"));
		
		String unitPrice = map.get("UnitPrice");
//		vh.tvUnitPrice.setText(unitPrice);
//		double totalMoney = (Double.parseDouble(unitPrice))*(Integer.parseInt(chukuNum));
//		String totalMoney1 = new DecimalFormat(".00").format(totalMoney);
//		
//		vh.tvTotalMoney.setText(totalMoney1);
		vh.btnBack.setText(R.string.yiku_cancel);
		vh.btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMessage(map);
			}
		});
		return convertView;
	}
	protected void showMessage(HashMap<String, String> map) {
		//弹窗提示修改入库数量
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setMessage("您确定要取消刚才的移库操作吗？")
		.create().show();
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
		Button btnBack;
	}
}
