package com.mobilis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ClassDAO extends DBAdapter {

	public ClassDAO(Context context) {
		super(context);
	}

	public void addClasses(ContentValues[] values, int course_id) {
		Log.i("COURSE_ID", "" + course_id);

		if (existClasses(course_id)) {
			clearClasses(course_id);
		}

		getDatabase().beginTransaction();
		for (int i = 0; i < values.length; i++) {
			Log.i("INSERT", "INSERT");
			values[i].put("course_id", course_id);
			getDatabase().insert("classes", null, values[i]);
		}
		getDatabase().setTransactionSuccessful();
		getDatabase().endTransaction();
	}

	public boolean existClasses(int course_id) {

		Cursor cursor = getDatabase().query("classes",
				new String[] { "course_id" }, "course_id=" + course_id, null,
				null, null, "1");

		try {
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			cursor.close();
			return (count != 0) ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	public void clearClasses(int course_id) {
		getDatabase().delete("classes", "course_id=" + course_id, null);
	}

	public Cursor getClasses(int course_id) { // Pegar apenas as colunas
												// necessárias;
		Cursor cursor = getDatabase().query("classes", null,
				"course_id=" + course_id, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
}
