package com.mobilis.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter2 {

	private SQLiteDatabase db;
	private Context context;
	private DatabaseHelper helper;

	public DBAdapter2(Context context) {
		this.context = context;
	}

	public DBAdapter2 open() {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
		return this;

	}

	public void close() {
		helper.close();
	}

	public SQLiteDatabase getDatabase() {
		return db;
	}

}
