package com.cmcid.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cmcid.R;
import com.cmcid.util.ViewHolder;

public class UsernameAdapter extends BaseAdapter{
	
	private static HashMap<Integer, Boolean> isSelected;
	
	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}
	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		UsernameAdapter.isSelected = isSelected;
	}
	private Context context;
	private ArrayList<String> list;
	
	//初始化Adapter
	public UsernameAdapter(Context context,ArrayList<String> list){
		this.context = context;
		if(list==null){
			this.list = new ArrayList<String>();
		}else{
			this.list = list;
		}
		isSelected = new HashMap<Integer, Boolean>();
		initData();
	}
	
	//初始化isSelected的数据
	private void initData() {
		for(int i=0;i<this.list.size();i++){
			getIsSelected().put(i, false);
		}
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
			convertView = View.inflate(context, R.layout.user_lv_item, null);
			vh = new ViewHolder();
			vh.cb_item_delete = (CheckBox)convertView.findViewById(R.id.cb_item_delete);
			vh.tv_item_delete = (TextView)convertView.findViewById(R.id.tv_item_delete);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		vh.tv_item_delete.setText(list.get(position));
		// 根据isSelected来设置checkbox的选中状况
		vh.cb_item_delete.setChecked(getIsSelected().get(position));
		return convertView;
	}
}
