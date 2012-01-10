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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseHelper helper = new DatabaseHelper(this);
		adapter = new DBAdapter(this);
			
		if (helper.checkDataBaseExistence()) {
			adapter.open();
			cursor = adapter.getOneRow("config", 1);
			Log.w("Cursor size", String.valueOf(cursor.getColumnCount()));
			Log.w("Cursor Count", String.valueOf(cursor.getCount()));
		//	Log.w("Value",cursor.getString(1));
			
		
			if (cursor.getString(cursor.getColumnIndex("valor"))!=null) {
				intent = new Intent(this,ListaCursos.class);
				startActivity(intent);
			}
			else {
				Intent intent = new Intent(this,Login.class);
				startActivity(intent);
			}
			
		}
		
		else {
			helper.copyDatabaseFile();
			intent = new Intent(this,Login.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (cursor!=null) {
		cursor.close();
		}
		if (adapter!=null) {
		adapter.close();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		moveTaskToBack(true);
	}
}
