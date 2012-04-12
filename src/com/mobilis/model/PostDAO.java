package com.mobilis.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PostDAO extends DBAdapter2 {

	public PostDAO(Context context) {
		super(context);
	}

	public void addPosts(ContentValues[] values) {

	}

	public int getPostCount(int topic_id) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT count(id) FROM posts WHERE topic_id = " + topic_id,
				null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public void clearPostsFromTopic(int topic_id) {
		getDatabase().delete("posts", "topic_id=" + topic_id, null);
	}

}
