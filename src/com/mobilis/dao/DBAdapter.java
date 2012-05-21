package com.mobilis.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

	private SQLiteDatabase db;
	private Context context;
	private DatabaseHelper databaseHelper;

	public DBAdapter(Context context) {
		this.context = context;
	}

	public DBAdapter open() {
		databaseHelper = new DatabaseHelper(context);
		db = databaseHelper.getWritableDatabase();
		return this;

	}

	public void close() {
		databaseHelper.close();
	}

	public SQLiteDatabase getDatabase() {
		return db;
	}
}
