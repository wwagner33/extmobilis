package com.mobilis.controller;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.CourseDAO;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.util.Constants;
import com.mobilis.util.MobilisPreferences;

public class GatewayActivity extends Activity {
	private Intent intent;
	private Cursor cursor;
	private Bundle extras;
	private MobilisPreferences prefs;
	private DatabaseHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}

		prefs = MobilisPreferences.getInstance(this);
		File file = new File(Constants.PATH_MAIN_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}

		if (getIntent().getExtras() != null) {
			extras = getIntent().getExtras();
			if (extras
					.getString(Constants.REQUEST_FINISH_ACTIVITY_ON_RETURN_TEXT) != null) {
				finish();
			}
		}

		else {
			if (prefs.getPreferences().getString("token", null) != null) {

				CourseDAO courseDAO = new CourseDAO(helper);
				boolean existCourses = courseDAO.existCourses();

				if (prefs.getPreferences().getBoolean("AutoLogin", true)
						&& existCourses) {
					intent = new Intent(this, CourseListActivity.class);
					startActivity(intent);
				} else {
					intent = new Intent(this, LoginActivity.class);
					startActivity(intent);

				}
			} else {
				setConfigurations();
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
		}
	}

	public void setConfigurations() {
		SharedPreferences.Editor editor = prefs.getPreferences().edit();
		editor.putBoolean("AutoLogin", true);
		editor.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}
}
