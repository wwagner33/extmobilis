package com.mobilis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ClassDAO extends DBAdapter {

	public ClassDAO(Context context) {
		super(context);
	}

	public void addClasses(ContentValues[] values, int course_id) {
		if (existClasses(course_id)) {
			clearClasses(course_id);
		}

		for (int i = 0; i < values.length; i++) {
			values[i].put("course_id", course_id);
			getDatabase().insert("classes", null, values[i]);
		}
	}

	public boolean existClasses(int course_id) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT count(_id) FROM classes WHERE course_id=" + course_id,
				null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return (count > 0) ? true : false;
	}

	public void clearClasses(int course_id) {
		getDatabase().delete("classes", "course_id=" + course_id, null);
	}

	public Cursor getClasses(int course_id) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM classes WHERE course_id=" + course_id, null);
		cursor.moveToFirst();
		return cursor;
	}
}
