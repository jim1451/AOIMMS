package com.cmcid.view;

import android.app.Activity;
import android.os.Bundle;

import com.cmcid.TApplication;

public class BaseActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TApplication.listActivity.add(this);
	}
}
