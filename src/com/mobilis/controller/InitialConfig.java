package com.mobilis.controller;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.mobilis.dao.CourseDAO;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.util.Constants;
import com.mobilis.util.MobilisPreferences;

public class InitialConfig extends Activity {
	private Intent intent;
	private Cursor cursor;
	private Bundle extras;
	private MobilisPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = MobilisPreferences.getInstance(this);
		File file = new File(Constants.PATH_MAIN_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
		DatabaseHelper helper = new DatabaseHelper(this);

		if (getIntent().getExtras() != null) {
			extras = getIntent().getExtras();
			if (extras
					.getString(Constants.REQUEST_FINISH_ACTIVITY_ON_RETURN_TEXT) != null) {
				finish();
			}
		}

		else {

			if (helper.checkDataBaseExistence()) {

				if (prefs.getPreferences().getString("token", null) != null) {

					CourseDAO courseDAO = new CourseDAO(this);
					courseDAO.open();
					boolean existCourses = courseDAO.existCourses();
					courseDAO.close();

					if (prefs.getPreferences().getBoolean("AutoLogin", true)
							&& existCourses) {
						intent = new Intent(this, CourseListController.class);
						startActivity(intent);
					} else {
						intent = new Intent(this, Login.class);
						startActivity(intent);

					}
				} else {
					setConfigurations();
					Intent intent = new Intent(this, Login.class);
					startActivity(intent);
				}
			}

			else {
				helper.copyDatabaseFile();
				intent = new Intent(this, Login.class);
				startActivity(intent);
			}
		}
	}

	public void setConfigurations() {
		// Preferências padrão
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
}
