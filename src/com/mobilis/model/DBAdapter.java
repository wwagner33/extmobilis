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

	/*
	 * public synchronized void openAsync() {
	 * 
	 * runnable = new Runnable() { public void run() { helper = new
	 * DatabaseHelper(context); db = helper.getWritableDatabase(); } };
	 * 
	 * if (openBank != null) openBank.run(); else { openBank = new
	 * Thread(runnable); openBank.run(); } }
	 */
	public void close() {
		helper.close();
	}

	// Queries genéricas
	/*
	 * public Cursor getOneRow(String tableName, int id) { Cursor cursor =
	 * db.rawQuery("SELECT * FROM config WHERE _id=1", null);
	 * cursor.moveToFirst(); return cursor; }
	 * 
	 * public Cursor getAllRows(String tableName) { Cursor cursor =
	 * db.query(tableName, null, null, null, null, null, null); return cursor; }
	 * 
	 * public Cursor customQuery(String SQLQuery) { Cursor cursor =
	 * db.rawQuery(SQLQuery, null); return cursor; }
	 * 
	 * public void updateTable(String tableName, int id, ContentValues valores)
	 * { db.update(tableName, valores, "_id=" + id, null);
	 * 
	 * }
	 */
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

	public boolean postsStringExists() {

		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=5", null);
		cursor.moveToFirst();
		String token = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		if (token == null) {
			return false;
		} else
			return true;
	}

	public String getPosts() {
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=5", null);
		cursor.moveToFirst();
		String result = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		return result;
	}

	public void updatePostsString(String newPosts) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newPosts);
		db.update("config", valores, "_id=5", null);
	}
}
