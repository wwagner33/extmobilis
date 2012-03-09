package com.mobilis.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.mobilis.model.DBAdapter;
import com.mobilis.model.DatabaseHelper;

public class InitialConfig extends Activity {
	private Intent intent;
	private Cursor cursor;
	private DBAdapter adapter;
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// DEBUG
	     StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
		 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
		
		
		DatabaseHelper helper = new DatabaseHelper(this);
		adapter = new DBAdapter(this);

		if (getIntent().getExtras() != null) {
			extras = getIntent().getExtras();
			if (extras
					.getString(Constants.REQUEST_FINISH_ACTIVITY_ON_RETURN_TEXT) != null) {
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
					startActivity(intent);
				} else {
					Log.w("TOKEN TESTE", "NO TOKEN");
					Intent intent = new Intent(this, Login.class);

					startActivity(intent);
				}

			}

			else {
				Log.w("IsDBNULL", "YES");
				helper.copyDatabaseFile();
				intent = new Intent(this, Login.class);
				startActivity(intent);
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
}
