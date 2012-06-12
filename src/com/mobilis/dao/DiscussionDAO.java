package com.mobilis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mobilis.model.Discussion;

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

		Cursor cursor = getDatabase().query("posts",
				new String[] { "count(date)" },
				"date=\'" + date + "\' AND discussion_id=" + discussionId,
				null, null, null, null);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return (count == 0) ? true : false;
	}

	public boolean hasNewPostsFlag(int discussionId) {
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

	public void updateBeforeAndAfter(int discussionId, int[] beforeAfter) {
		ContentValues newValues = new ContentValues();
		newValues.put("previous_posts", beforeAfter[0]);
		newValues.put("next_posts", beforeAfter[1]);
		getDatabase().update("discussions", newValues, "_id=" + discussionId,
				null);
	}

	// TTS

	public void addDiscussion(ContentValues[] values, int classId) {
		clearDiscussion(classId);
		for (int i = 0; i < values.length; i++) {
			values[i].put("class_id", classId);
			getDatabase().insert("discussions", null, values[i]);
		}
	}

	public boolean existsDiscussison(int classId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT count(_id) from discussions WHERE class_id=" + classId,
				null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return (count > 0) ? true : false;
	}

	public Discussion[] getClassDiscussions(int classId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM discussions WHERE class_id = " + classId, null);
		Discussion[] discussions = new Discussion[cursor.getCount()];

		while (cursor.moveToNext()) {
			int i = cursor.getPosition();
			discussions[i] = cursoToDiscussion(cursor);
		}

		cursor.close();
		return discussions;
	}

	public void clearDiscussion(int classId) {
		getDatabase().delete("discussions", "class_id=" + classId, null);
	}

	public void setNextPosts(int discussionId, int value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("next_posts", value);
		getDatabase().update("discussions", contentValues,
				"_id=" + discussionId, null);
	}

	public void setPreviousPosts(int discussionId, int value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("previous_posts", value);
		getDatabase().update("discussions", contentValues,
				"_id=" + discussionId, null);
	}

	public int getPreviousPosts(int discussionId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM discussions WHERE _id=" + discussionId, null);
		int result = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			result = cursor.getInt(cursor.getColumnIndex("previous_posts"));
		}
		cursor.close();
		return result;
	}

	public Discussion getDiscussion(int discussionId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM discussions WHERE _id=" + discussionId, null);

		Discussion discussion = new Discussion();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			discussion = cursoToDiscussion(cursor);
		}

		cursor.close();
		return discussion;
	}

	private Discussion cursoToDiscussion(Cursor cursor) {
		final Discussion discussion = new Discussion();
		discussion.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		discussion.setName(cursor.getString(cursor.getColumnIndex("name")));
		discussion.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
		discussion.setLastPostDate(cursor.getString(cursor
				.getColumnIndex("last_post_date")));
		discussion.setClassId(cursor.getInt(cursor.getColumnIndex("class_id")));
		discussion.setDescription(cursor.getString(cursor
				.getColumnIndex("description")));
		discussion.setNextPosts(cursor.getInt(cursor
				.getColumnIndex("next_posts")));
		discussion.setPreviousPosts(cursor.getInt(cursor
				.getColumnIndex("previous_posts")));
		return discussion;
	}

	public Discussion getDiscussion(String discussionName) {
		Cursor cursor = getDatabase()
				.rawQuery(
						"SELECT * FROM discussions WHERE name='"
								+ discussionName + "'", null);

		Discussion discussion = new Discussion();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			discussion = cursoToDiscussion(cursor);
		}

		cursor.close();
		return discussion;
	}
}
