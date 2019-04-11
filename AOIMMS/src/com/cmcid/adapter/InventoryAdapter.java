package com.cmcid.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.cmcid.R;
import com.cmcid.adapter.YikuAdapter.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class InventoryAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<HashMap<String,String>> list;
	

	public InventoryAdapter(Context context,
			ArrayList<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView==null){
			convertView = View.inflate(context, R.layout.lv_item_inventory_data, null);
			vh = new ViewHolder();
			vh.tvNo = (TextView) convertView.findViewById(R.id.lvItemID_inventory);
			vh.tvCCDC = (TextView) convertView.findViewById(R.id.lvItemCCDC_inventory);
			vh.tvOutbondNum = (TextView) convertView.findViewById(R.id.lvItemNum_inventory);
			vh.tvName = (TextView) convertView.findViewById(R.id.lvItemName_inventory);
			vh.tvXiyuName = (TextView) convertView.findViewById(R.id.lvItemXiyuName_inventory);
			vh.tvType = (TextView) convertView.findViewById(R.id.lvItemType_inventory);
			vh.tvUnit = (TextView) convertView.findViewById(R.id.lvItemUnit_inventory);
			vh.tvUnitPrice = (TextView) convertView.findViewById(R.id.lvItemUnitPrice_inventory);
			vh.tvTotalMoney = (TextView) convertView.findViewById(R.id.lvItemTotalMoney_inventory);
			vh.tvKuFangHao = (TextView) convertView.findViewById(R.id.lvItemkuFangHao_inventory);
			vh.tvKuWeiHao = (TextView) convertView.findViewById(R.id.lvItemKuWeiHao_inventory);
			
	//		vh.btnBack = (Button) convertView.findViewById(R.id.lvItemBack_inventory);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		final HashMap<String, String> map = list.get(position);
		vh.tvNo.setText(map.get("no"));
		vh.tvCCDC.setText(map.get("ccdc"));
		String chukuNum = map.get("chukuNum");
		vh.tvOutbondNum.setText(chukuNum);
		vh.tvName.setText(map.get("name"));
		vh.tvXiyuName.setText(map.get("xiyuName"));
		vh.tvType.setText(map.get("type"));
		vh.tvUnit.setText(map.get("unit"));
		vh.tvKuFangHao.setText(map.get("kuFangName"));
		vh.tvKuWeiHao.setText(map.get("KuWeiName"));
		
		
		String unitPrice = map.get("unitPrice");
		vh.tvUnitPrice.setText(unitPrice);
		double totalMoney = (Double.parseDouble(unitPrice))*(Integer.parseInt(chukuNum));
		String totalMoney1 = new DecimalFormat(".00").format(totalMoney);
		
		vh.tvTotalMoney.setText(totalMoney1);
//		vh.btnBack.setText(R.string.inventory_cancel);
//		vh.btnBack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				showMessage(map);
//			}
//		});
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
		TextView tvKuFangHao;
		TextView tvKuWeiHao;
	//	Button btnBack;
	}

}
