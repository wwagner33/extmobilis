package com.mobilis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CourseDAO extends DBAdapter {

	public CourseDAO(Context context) {
		super(context);
	}

	public void addCourses(ContentValues[] values) {

		if (existCourses()) {
			clearCourses();
		}

		getDatabase().beginTransaction();
		for (int i = 0; i < values.length; i++) {
			getDatabase().insert("courses", null, values[i]);
		}
		getDatabase().setTransactionSuccessful();
		getDatabase().endTransaction();
	}

	public boolean existCourses() {

		Cursor cursor = getDatabase().query("courses", new String[] { "_id" },
				null, null, null, null, null, "1");
		cursor.moveToFirst();
		try {
			int count = cursor.getInt(0);
			cursor.close();
			return (count != 0) ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	public void clearCourses() {
		getDatabase().delete("courses", null, null);
	}

	public Cursor getAllCourses() { // Pegar só as colunas necessárias

		Cursor cursor = getDatabase().query("courses", null, null, null, null,
				null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
}
