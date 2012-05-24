package com.mobilis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class TopicDAO extends DBAdapter {

	public TopicDAO(Context context) {
		super(context);
	}

	public void addTopics(ContentValues[] values, int classId) {
		if (existsTopic(classId)) {
			clearTopics(classId);
		}
		getDatabase().beginTransaction();
		for (int i = 0; i < values.length; i++) {
			values[i].put("class_id", classId);
			getDatabase().insert("topics", null, values[i]);
		}
		getDatabase().setTransactionSuccessful();
		getDatabase().endTransaction();
	}

	public boolean existsTopic(int classId) {
		Cursor cursor = getDatabase().query("topics", new String[] { "_id" },
				"class_id=" + classId, null, null, null, null, "1");
		cursor.moveToFirst();
		try {
			int count = cursor.getInt(0);
			cursor.close();
			return (count > 0) ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	public void clearTopics(int classId) {
		getDatabase().delete("topics", "class_id=" + classId, null);
	}

	public Cursor getTopicsFromClass(int classId) { // Pegar apenas o necessário
		Cursor cursor = getDatabase().query("topics", null, null, null, null,
				null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public boolean hasNewPosts(int topicId, String date) {

		if (date == null) {
			return false;
		}

		// Log.w("Last Date", date);
		// String sqlQuery = "SELECT COUNT(updated) FROM posts WHERE updated=\'"
		// + date + "\' AND discussion_id=" + topicId;
		// Log.i("SQLQuery", sqlQuery);
		// Cursor cursor = getDatabase().rawQuery(sqlQuery, null);

		Cursor cursor = getDatabase().query("posts",
				new String[] { "count(updated)" },
				"updated=\'" + date + "\' AND discussion_id=" + topicId, null,
				null, null, null);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		Log.i("COUNT", String.valueOf(count));
		return (count == 0) ? true : false;
	}

	public boolean hasNewPostsFlag(int topicId) {
		// Cursor cursor = getDatabase().rawQuery(
		// "SELECT has_new_posts from topics WHERE _id =" + topicId, null);
		Cursor cursor = getDatabase().query("topics",
				new String[] { "has_new_posts" }, "_id=" + topicId, null, null,
				null, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return (result == 1) ? true : false;

	}

	public void updateFlag(ContentValues newValue, int topic_id) {
		getDatabase().update("topics", newValue, "_id=" + topic_id, null);
	}
}
