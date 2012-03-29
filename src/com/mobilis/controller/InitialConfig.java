package com.mobilis.controller;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mobilis.model.DBAdapter;
import com.mobilis.model.DatabaseHelper;

public class InitialConfig extends Activity {
	private Intent intent;
	private Cursor cursor;
	private DBAdapter adapter;
	private Bundle extras;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		File file = new File(Constants.PATH_MAIN_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
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

					if (settings.getBoolean("AutoLogin", true)) {
						Log.w("TOKEN TESTE", "TOKEN OK");
						intent = new Intent(this, CourseListController.class);
						startActivity(intent);
					} else {
						Log.w("AutoLogin", "FALSE");
						intent = new Intent(this, Login.class);
						startActivity(intent);

					}
				} else {

					setConfigurations();
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

	public void setConfigurations() {
		// setting default preferences
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("AutoLogin", true);
		editor.commit();

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
