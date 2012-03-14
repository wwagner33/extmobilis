package com.mobilis.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {
	private Context context;
	private SQLiteDatabase db;
	private DatabaseHelper helper;
	Thread openBank;
	Runnable runnable;

	public DBAdapter(Context c) {
		context = c;
		helper = new DatabaseHelper(c);
	}

	public DBAdapter open() {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
		return this;

	}

	public void close() {
		helper.close();
	}

	public String getTopics() {
		Cursor teste = db.rawQuery("SELECT * FROM config WHERE _id=1", null);
		teste.moveToFirst();
		String token = teste.getString(teste.getColumnIndex("valor"));
		teste.close();
		return token;
	}

	public boolean TopicsExists() {
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=1", null);
		cursor.moveToFirst();
		String token = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		if (token == null) {
			return false;
		} else
			return true;
	}

	public void updateTopics(String newTopics) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newTopics);

		db.update("config", valores, "_id=1", null);
	}

	public String getToken() {
		Cursor teste = db.rawQuery("SELECT * FROM config WHERE _id=2", null);
		teste.moveToFirst();
		String token = teste.getString(teste.getColumnIndex("valor"));
		teste.close();
		return token;
	}

	public boolean tokenExists() {
		Log.w("onTokenExists", "onTokenExists");
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=2", null);
		cursor.moveToFirst();
		String token = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		if (token == null) {
			return false;
		} else
			return true;
	}

	public void updateToken(String authToken) {
		ContentValues valores = new ContentValues();
		valores.put("valor", authToken);

		db.update("config", valores, "_id=2", null);
	}

	// Token Queries END

	// Cursos Queries

	public boolean coursesExist() {
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=3", null);
		cursor.moveToFirst();
		String token = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		if (token == null) {
			return false;
		} else
			return true;
	}

	public void updateCourses(String newValues) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newValues);
		db.update("config", valores, "_id=3", null);
	}

	public String getCourseList() {
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=3", null);
		cursor.moveToFirst();
		String result = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		return result;
	}

	public boolean groupsExist() {

		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=4", null);
		cursor.moveToFirst();
		String token = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		if (token == null) {
			return false;
		} else
			return true;
	}

	public void updateGroups(String newValues) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newValues);
		db.update("config", valores, "_id=4", null);
	}

	public String getGroups() {

		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=4", null);
		cursor.moveToFirst();
		String result = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		return result;
	}

	/*
	 * public boolean postsStringExists() { // lembrar de deletar Cursor cursor
	 * = db.rawQuery("SELECT * FROM config WHERE _id=5", null);
	 * cursor.moveToFirst(); String token =
	 * cursor.getString(cursor.getColumnIndex("valor")); cursor.close(); if
	 * (token == null) { return false; } else return true; }
	 */

	public String getPosts() {
		// lembrar de deletar
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=5", null);
		cursor.moveToFirst();
		String result = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		return result;
	}

	public boolean postExistsOnTopic(long topicId) {

		Cursor cursor = db.rawQuery("SELECT posts from topics WHERE topic_id="
				+ topicId, null);
		cursor.moveToFirst();

		if (cursor.getString(cursor.getColumnIndex("posts"))!=null) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	public String getPostsFromTopic(long topicId) {
		Cursor cursor = db.rawQuery("SELECT posts from topics WHERE topic_id="
				+ topicId, null);
		cursor.moveToFirst();
		String posts = cursor.getString(cursor.getColumnIndex("posts"));
		cursor.close();
		return posts;

	}

	public void updatePosts(String newPosts, long topicId) {

		ContentValues teste = new ContentValues();
		teste.put("posts", newPosts);
		db.update("topics", teste, "topic_id =" + topicId, null);
	}

	public void insertNewTopic(ContentValues newTopic) {
		db.insert("topics", null, newTopic);
	}

	public boolean topicExists(long topicId) {

		Cursor cursor = db.rawQuery("SELECT * FROM topics WHERE topic_id="
				+ topicId, null);

		if (cursor.getCount() >= 1) {
			Log.w("TOPICEXISTS", "TRUE");
			cursor.close();
			return true;
		} else {
			cursor.close();
			Log.w("TOPICEXISTS", "FALSE");
			return false;
		}
	}

	public void updatePostsString(String newPosts) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newPosts);
		db.update("config", valores, "_id=5", null);
	}
}
