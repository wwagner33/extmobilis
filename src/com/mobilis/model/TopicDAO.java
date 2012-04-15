package com.mobilis.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TopicDAO extends DBAdapter {

	public TopicDAO(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void addTopics(ContentValues[] values, int class_id) {
		if (existsTopic(class_id)) {
			clearTopics(class_id);
		}
		for (int i = 0; i < values.length; i++) {
			values[i].put("class_id", class_id);
			getDatabase().insert("topics", null, values[i]);
		}
	}

	public boolean existsTopic(int class_id) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT count(_id) from topics WHERE class_id=" + class_id,
				null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return (count > 0) ? true : false;
	}

	public void clearTopics(int class_id) {
		getDatabase().delete("topics", "class_id=" + class_id, null);
	}

	public Cursor getTopicsFromClass(int class_id) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM topics WHERE class_id=" + class_id, null);
		cursor.moveToFirst();
		return cursor;
	}

}
