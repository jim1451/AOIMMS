package com.cmcid.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmcid.R;

public class PopMenu implements OnItemClickListener{
	
	private LayoutInflater inflater;
	private Context context;
	private ListView listView;
	private PopupWindow popupWindow;
	private OnItemClickListener onItemClickListener;
	private List<Integer> list;
	
	public interface OnItemClickListener{
		public void onItemClick(int position);
	}
	//���ò˵���������
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	public PopMenu(Context context){
		this.context = context;
		list = new ArrayList<Integer>();
		inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.popmenu, null);
		listView = (ListView)view.findViewById(R.id.lv_popupMenu);
		listView.setAdapter(new PopAdapter());
		listView.setOnItemClickListener(this);
		popupWindow = new PopupWindow(view, context.getResources().getDimensionPixelSize(R.dimen.popupmenu_width), LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(onItemClickListener!=null){
			onItemClickListener.onItemClick(position);
		}
		popupWindow.dismiss();
	}
	//������Ӳ˵���
	public void addItems(int[] items){
		for(int item : items){
			list.add(item);
		}
	}
	//������Ӳ˵���
	public void addItem(int item){
		list.add(item);
	}
	//����ʽ�����˵���view���·�
	public void showAsDropDown(View view){
		//��֤�ߴ��Ǹ�����Ļ�����ܶ�����
		popupWindow.showAsDropDown(view, 100, context.getResources().getDimensionPixelSize(R.dimen.popupmenu_yoff));
		popupWindow.setFocusable(true);//�۽�
		popupWindow.setOutsideTouchable(true);//������������ʧ
		popupWindow.update();//����
	}
	class PopAdapter extends BaseAdapter{
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
				vh = new ViewHolder();
				convertView = inflater.inflate(R.layout.popmenu_lv_item, null);
				vh.tvItem = (TextView) convertView.findViewById(R.id.tv_popmenu_item);
				convertView.setTag(vh);
			}
			vh = (ViewHolder) convertView.getTag();
			vh.tvItem.setText(list.get(position));
			return convertView;
		}
		private class ViewHolder{
			TextView tvItem;
		}
	}
}
