package com.mobilis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DiscussionDAO extends DBAdapter {

	public DiscussionDAO(Context context) {
		super(context);
	}

	public void addDiscussions(ContentValues[] values, int classId) {
		if (existsDiscussion(classId)) {
			clearDiscussions(classId);
		}
		getDatabase().beginTransaction();
		for (int i = 0; i < values.length; i++) {
			values[i].put("class_id", classId);
			getDatabase().insert("discussions", null, values[i]);
		}
		getDatabase().setTransactionSuccessful();
		getDatabase().endTransaction();
	}

	public boolean existsDiscussion(int classId) {
		Cursor cursor = getDatabase().query("discussions",
				new String[] { "_id" }, "class_id=" + classId, null, null,
				null, null, "1");
		cursor.moveToFirst();
		try {
			int count = cursor.getInt(0);
			cursor.close();
			return (count > 0) ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	public void clearDiscussions(int classId) {
		getDatabase().delete("discussions", "class_id=" + classId, null);
	}

	public Cursor getDiscussionsFromClass(int classId) { // Pegar apenas o
															// necessário
		Cursor cursor = getDatabase().query("discussions", null, null, null,
				null, null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public boolean hasNewPosts(int discussionId, String date) {

		if (date == null) {
			return false;
		}

		// Log.w("Last Date", date);
		// String sqlQuery = "SELECT COUNT(updated) FROM posts WHERE updated=\'"
		// + date + "\' AND discussion_id=" + topicId;
		// Log.i("SQLQuery", sqlQuery);
		// Cursor cursor = getDatabase().rawQuery(sqlQuery, null);

		Cursor cursor = getDatabase().query("posts",
				new String[] { "count(updated_at)" },
				"updated_at=\'" + date + "\' AND discussion_id=" + discussionId,
				null, null, null, null);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		Log.i("COUNT", String.valueOf(count));
		return (count == 0) ? true : false;
	}

	public boolean hasNewPostsFlag(int discussionId) {
		// Cursor cursor = getDatabase().rawQuery(
		// "SELECT has_new_posts from topics WHERE _id =" + topicId, null);
		Cursor cursor = getDatabase().query("discussions",
				new String[] { "has_new_posts" }, "_id=" + discussionId, null,
				null, null, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return (result == 1) ? true : false;

	}

	public void updateFlag(ContentValues newValue, int discussionId) {
		getDatabase().update("discussions", newValue, "_id=" + discussionId,
				null);
	}
}
