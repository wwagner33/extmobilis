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
		if (authToken == null) {
			valores.putNull("valor");
		} else {
			valores.put("valor", authToken);
		}
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

	// public void updateCourses(String newValues) {
	// ContentValues valores = new ContentValues();
	// valores.put("valor", newValues);
	// db.update("config", valores, "_id=3", null);
	// }

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

	public void updatePostsString(String newPosts) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newPosts);
		db.update("config", valores, "_id=5", null);
	}

	public String getPosts() {
		// lembrar de deletar
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=5", null);
		cursor.moveToFirst();
		String result = cursor.getString(cursor.getColumnIndex("valor"));
		cursor.close();
		return result;
	}

	public String getPostsFromTopic(long topicId) {
		Cursor cursor = db.rawQuery("SELECT posts from topics WHERE topic_id="
				+ topicId, null);
		cursor.moveToFirst();
		String posts = cursor.getString(cursor.getColumnIndex("posts"));
		cursor.close();
		return posts;

	}

	// CLASSES OPERATIONS

	public boolean existsTopicsOnClass(long classId) {

		Cursor cursor = db.rawQuery(
				"SELECT topics from classes WHERE class_id=" + classId, null);
		cursor.moveToFirst();

		if (cursor.getString(cursor.getColumnIndex("topics")) != null) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}

	}

	public String getTopicsFromClasses(long classId) {

		Cursor cursor = db.rawQuery(
				"SELECT topics from classes WHERE class_id=" + classId, null);
		cursor.moveToFirst();
		String courses = cursor.getString(cursor.getColumnIndex("topics"));
		cursor.close();
		return courses;
	}

	public boolean existsClassWithId(long classId) {

		Cursor cursor = db.rawQuery("SELECT * from classes WHERE class_id="
				+ classId, null);

		if (cursor.getCount() >= 1) {
			Log.w("CLASSEXISTS", "TRUE");
			cursor.close();
			return true;
		} else {
			cursor.close();
			Log.w("CLASSEXISTS", "FALSE");
			return false;
		}
	}

	public void insertNewClass(ContentValues newClass) {
		db.insert("classes", null, newClass);
	}

	public void updateTopicsFromClasses(String topics, long classId) {
		ContentValues newValues = new ContentValues();
		newValues.put("topics", topics);
		db.update("classes", newValues, "class_id =" + classId, null);
	}

	// COURSES OPERATIONS

	public boolean existsClassesOnCourse(long courseId) {
		Cursor cursor = db
				.rawQuery("SELECT classes from courses WHERE course_id="
						+ courseId, null);
		cursor.moveToFirst();

		if (cursor.getString(cursor.getColumnIndex("classes")) != null) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	public String getClassesFromCourse(long courseId) {

		Cursor cursor = db
				.rawQuery("SELECT classes from courses WHERE course_id="
						+ courseId, null);
		cursor.moveToFirst();
		String courses = cursor.getString(cursor.getColumnIndex("classes"));
		cursor.close();
		return courses;
	}

	public boolean existsCourseWithId(long courseId) {

		Cursor cursor = db.rawQuery("SELECT * from courses WHERE course_id="
				+ courseId, null);

		if (cursor.getCount() >= 1) {
			Log.w("COURSEEXISTS", "TRUE");
			cursor.close();
			return true;
		} else {
			cursor.close();
			Log.w("COURSEEXISTS", "FALSE");
			return false;
		}
	}

	public void updateClassesFromCourse(String classes, long courseId) {
		ContentValues teste = new ContentValues();
		teste.put("classes", classes);
		db.update("courses", teste, "course_id =" + courseId, null);
	}

	public void updateCourses(String newValues) {
		ContentValues valores = new ContentValues();
		valores.put("valor", newValues);
		db.update("config", valores, "_id=3", null);
	}

	public void insertNewCourse(ContentValues newCourse) {
		db.insert("courses", null, newCourse);
	}

	// TOPICS OPERATIONS

	public void updatePostsFromTopic(String newPosts, long topicId) {

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

	public boolean postExistsOnTopic(long topicId) {

		Cursor cursor = db.rawQuery("SELECT posts from topics WHERE topic_id="
				+ topicId, null);
		cursor.moveToFirst();

		if (cursor.getString(cursor.getColumnIndex("posts")) != null) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}
}
