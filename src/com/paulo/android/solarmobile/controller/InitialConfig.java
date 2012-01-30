package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.model.DatabaseHelper;

public class InitialConfig extends Activity {
	Intent intent;
	Cursor cursor;
	DBAdapter adapter;
	public static final int CALLED_FROM_ROOT = 10;
	Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	

		DatabaseHelper helper = new DatabaseHelper(this);
		adapter = new DBAdapter(this);
		// Log.w("TOKEN TESTE", "TESTE2");
		
		
		if 	(getIntent().getExtras()!=null) {
			extras = getIntent().getExtras();
			if (extras.getString("FinishActivity") != null) {
				Log.w("onFinish", "YES");
				finish();
			}
		}
		
		else {

		if (helper.checkDataBaseExistence()) {
			adapter.open();

			if (adapter.tokenExists()) {
				Log.w("TOKEN TESTE", "TOKEN OK");
				intent = new Intent(this, CourseListController.class);
				startActivityForResult(intent, CALLED_FROM_ROOT);
			} else {
				Log.w("TOKEN TESTE", "NO TOKEN");
				Intent intent = new Intent(this, Login.class);

				startActivityForResult(intent, CALLED_FROM_ROOT);
			}

		}

		else {
			Log.w("teste", "db null");
			helper.copyDatabaseFile();
			intent = new Intent(this, Login.class);
			startActivityForResult(intent, CALLED_FROM_ROOT);
		}
	  }
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (cursor != null) {
			cursor.close();
		}
		if (adapter != null) {
			adapter.close();
		}
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { // TODO Auto-generated method stub
	 * super.onActivityResult(requestCode, resultCode, data); finish(); }
	 */
}
