package com.paulo.android.solarmobile.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	Context context;
	SQLiteDatabase db;
	DatabaseHelper helper;
	
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
	
	public Cursor getOneRow(String tableName,int id) {
	//	Cursor cursor = db.query(tableName, null, "_id="+id, null, null, null, null);
		Cursor cursor = db.rawQuery("SELECT * FROM config WHERE _id=1",null);
		cursor.moveToFirst();
		return cursor;
	}
	public Cursor getAllRows(String tableName) {
		Cursor cursor = db.query(tableName, null, null, null, null, null, null);
		return cursor;
	}
	public Cursor customQuery(String SQLQuery) {
		Cursor cursor = db.rawQuery(SQLQuery, null);
		return cursor;
	}
	public void updateTable(String tableName,int id,ContentValues valores) {
		db.update(tableName, valores, "_id="+id, null);
	}
}
