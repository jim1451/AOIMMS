package com.cmcid.view;

import java.util.ArrayList;

import com.cmcid.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class InventoryActivityQurey extends Activity {
	private Spinner spinnerRef, spinnerRef2, spinnerRef3;
	private String tiaojian, guanxi;
	private Button btn_query_Inventory;
	private EditText et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_query);

		et = (EditText) findViewById(R.id.et_show_chaxu);
		spinnerRef = (Spinner) this.findViewById(R.id.spinner1);
		btn_query_Inventory = (Button) findViewById(R.id.btn_query_Inventory);
		// spinnerRef3 = (Spinner) this.findViewById(R.id.spinner3);
		ArrayList dropListData = new ArrayList();
		dropListData.add("���ʱ���");//CCDCID
		dropListData.add("����");//StockNum
		dropListData.add("�۸�");//UnitPrice
		dropListData.add("����ͺ�");//Type
		dropListData.add("��������");//XiYuName
		
		
		
		ArrayAdapter spinnerAdapterRefRef = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, dropListData);
		spinnerAdapterRefRef
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRef.setAdapter(spinnerAdapterRefRef);

		spinnerRef2 = (Spinner) this.findViewById(R.id.spinner2);
		ArrayList dropListData2 = new ArrayList();
		dropListData2.add("����");
		dropListData2.add("����");
		dropListData2.add("���ڵ���");
		dropListData2.add("С��");
		dropListData2.add("С�ڵ���");
		dropListData2.add("������");
		ArrayAdapter spinnerAdapterRefRef2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, dropListData2);
		spinnerAdapterRefRef2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRef2.setAdapter(spinnerAdapterRefRef2);

		
		btn_query_Inventory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(InventoryActivityQurey.this,
						InventoryFormActivityQueryResult.class);

				if (spinnerRef2.getSelectedItem().toString().equals("����")) {
					tiaojian = "like";
				}
				if (spinnerRef2.getSelectedItem().toString().equals("����")) {
					tiaojian = ">";
				}
				if (spinnerRef2.getSelectedItem().toString().equals("С��")) {
					tiaojian = "<";
				}
				if (spinnerRef2.getSelectedItem().toString().equals("����")) {
					tiaojian = "=";
				}
				if (spinnerRef2.getSelectedItem().toString().equals("���ڵ���")) {
					tiaojian = ">=";
				}
				if (spinnerRef2.getSelectedItem().toString().equals("С�ڵ���")) {
					tiaojian = "<=";
				}

				intent.putExtra("ziduan", spinnerRef.getSelectedItem()
						.toString());
				intent.putExtra("tiaojian", tiaojian);
				// intent.putExtra("zhi",
				// spinnerRef3.getSelectedItem().toString());
				intent.putExtra("chaxuzhi", et.getText().toString().trim());
				startActivity(intent);

			}

		});

	}

}
