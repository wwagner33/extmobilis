package com.mobilis.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CourseDAO extends DBAdapter {

	public CourseDAO(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void addCourses(ContentValues[] values) {

		if (existCourses()) {
			clearCourses();
		}

		for (int i = 0; i < values.length; i++) {
			getDatabase().insert("courses", null, values[i]);
		}
	}

	public boolean existCourses() {

		Cursor cursor = getDatabase().rawQuery(
				"SELECT count(_id) FROM courses", null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();

		return (count > 0) ? true : false;
	}

	public void clearCourses() {
		getDatabase().delete("courses", null, null);
	}

	public Cursor getAllCourses() {
		Cursor cursor = getDatabase().rawQuery("SELECT * FROM courses", null);
		cursor.moveToFirst();
		return cursor;
	}
}
