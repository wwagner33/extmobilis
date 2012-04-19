package com.mobilis.model;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.mobilis.controller.Constants;

public class PostDAO extends DBAdapter {

	public PostDAO(Context context) {
		super(context);
	}

	public void addPosts(ArrayList<ContentValues> values, int topic_id) {

		clearPostsFromTopic(topic_id);

		for (int i = 0; i < values.size(); i++) {
			ContentValues newPost = values.get(i);
			getDatabase().insert("posts", null, newPost);
		}
	}

	public int getPostCount(int topic_id) {
		Cursor cursor = getDatabase()
				.rawQuery(
						"SELECT count(_id) FROM posts WHERE discussion_id= "
								+ topic_id, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public boolean postExistsOnTopic(int topic_id) {
		return (getPostCount(topic_id) > 0) ? true : false;
	}

	public void clearPostsFromTopic(int topic_id) {
		getDatabase().delete("posts", "discussion_id=" + topic_id, null);
	}

	public Cursor getPostsFromTopic(int topic_id) {
		Log.i("TOPIC_ID", String.valueOf(topic_id));

		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM posts WHERE discussion_id =" + topic_id, null);
		cursor.moveToFirst();
		Log.i("CURSOR_SIZE", String.valueOf(cursor.getCount()));
		return cursor;
	}

	public String getNewestPostDate() {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT max(updated) FROM posts", null);
		cursor.moveToFirst();
		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

	public ArrayList<ContentValues> cursorToContentValues(Cursor cursor) {

		cursor.moveToLast();

		ArrayList<ContentValues> values = new ArrayList<ContentValues>();

		do {

			ContentValues content = new ContentValues();
			content.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			content.put("profile_id",
					cursor.getInt(cursor.getColumnIndex("profile_id")));
			content.put("content_first",
					cursor.getString(cursor.getColumnIndex("content_first")));
			content.put("content_last",
					cursor.getString(cursor.getColumnIndex("content_last")));
			content.put("updated",
					cursor.getString(cursor.getColumnIndex("updated")));
			content.put("user_id",
					cursor.getInt(cursor.getColumnIndex("user_id")));
			content.put("discussion_id",
					cursor.getInt(cursor.getColumnIndex("discussion_id")));
			content.put("parent_id",
					cursor.getInt(cursor.getColumnIndex("parent_id")));
			content.put("user_nick",
					cursor.getString(cursor.getColumnIndex("user_nick")));
			content.put("post_year",
					cursor.getInt(cursor.getColumnIndex("post_year")));
			content.put("post_month",
					cursor.getInt(cursor.getColumnIndex("post_month")));
			content.put("post_day",
					cursor.getInt(cursor.getColumnIndex("post_day")));
			content.put("post_hour",
					cursor.getInt(cursor.getColumnIndex("post_hour")));
			content.put("post_minute",
					cursor.getInt(cursor.getColumnIndex("post_minute")));
			content.put("post_second",
					cursor.getInt(cursor.getColumnIndex("post_second")));
			values.add(content);

		} while (cursor.moveToPrevious());

		cursor.close();
		return values;
	}

	public String getAllUserIds() {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT DISTINCT user_id FROM posts", null);
		StringBuilder builder = new StringBuilder();
		cursor.moveToFirst();

		do {
			builder.append(cursor.getInt(cursor.getColumnIndex("user_id")));
			if (!cursor.isLast()) {
				builder.append(",");
			}
		} while

		(cursor.moveToNext());

		String result = builder.toString();
		cursor.close();
		return result;
	}

	public String getUserIdsAbsentImage(int discussion_id) {
		StringBuilder builder = new StringBuilder();
		File imageDirectory = new File(Constants.PATH_IMAGES);
		File[] images = imageDirectory.listFiles(); // Joga NullPointerException
													// se o diretório não
													// existir
		Cursor cursor = getDatabase().rawQuery(
				"SELECT DISTINCT user_id FROM posts WHERE discussion_id="
						+ discussion_id, null);
		cursor.moveToFirst();

		do {

			boolean append = true;

			for (int y = 0; y < images.length; y++) {

				if (cursor.getInt(cursor.getColumnIndex("user_id")) == Integer
						.parseInt(FilenameUtils.removeExtension(images[y]
								.getName()))) {
					append = false;
				}
			}

			if (append == true) {
				builder.append(cursor.getInt(cursor.getColumnIndex("user_id")));
				if (!cursor.isLast()) {
					builder.append(",");
				}

			}

		} while (cursor.moveToNext());

		cursor.close();
		String string = builder.toString();

		if (string.charAt(string.length() - 1) == ',') {
			// Joga StringIndexOutOfBoundsException se a lista for vazia
			string = string.substring(0, string.length() - 1);
		}
		Log.i("Builder", string);
		return string;
	}
}
