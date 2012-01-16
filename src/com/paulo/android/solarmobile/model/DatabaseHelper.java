/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package com.paulo.android.solarmobile.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <p>
 * This class copies a SQLite database from your application's assets directory
 * to /data/data/<your_application_package>/databases/ so you can access it
 * using the SQLite APIs provided by the Android SDK. Note that
 * {@link DatabaseHelper#copyDatabaseFile()} checks for the existence of the
 * database and only copies it if needed.
 * </p>
 * <p>
 * {@link DatabaseHelper#copyDatabaseFile()} calls
 * {@link SQLiteOpenHelper#getReadableDatabase()}, which in turn calls
 * {@link SQLiteOpenHelper#onCreate(SQLiteDatabase)}. Be aware that the
 * implementation of the overridden
 * {@link SQLiteOpenHelper#onCreate(SQLiteDatabase)} must remain empty in order
 * for the copy operation to work correctly.
 * </p>
 * <p>
 * This class includes a constructor
 * {@link DatabaseHelper#DatabaseHelper(Context, boolean)} which allows you to
 * control whether the database file should be copied when the class is
 * instantiated.
 * </p>
 * 
 * @see SQLiteOpenHelper
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// Android's default system path for your application's database.
	private static String DB_PATH = "/data/data/com.paulo.android.solarmobile.controller/databases/";

	private static String DB_NAME = "MobilisDB.sqlite";

	private final Context context;

	/**
	 * Constructor Keeps a reference to the passed context in order to access
	 * the application's assets.
	 * 
	 * @param context
	 *            Context to be used
	 */
	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	/**
	 * This constructor copies the database file if the copyDatabase argument is
	 * <code>true</code>. It keeps a reference to the passed context in order to
	 * access the application's assets.
	 * 
	 * @param context
	 *            Context to be used
	 * @param copyDatabase
	 *            If <code>true</code>, the database file is copied (if it does
	 *            not already exist)
	 */
	public DatabaseHelper(Context context, boolean copyDatabase) {

		this(context);
		if (copyDatabase) {
			copyDatabaseFile();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Leave this method empty
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// fill in your code here
	}

	public void copyDatabaseFile() {

		InputStream myInput = null;
		OutputStream myOutput = null;
		SQLiteDatabase database = null;

		// only proceed in case the database does not exist

		// get the database
		database = this.getReadableDatabase();
		try {

			myInput = context.getAssets().open(DB_NAME);

			String outFileName = DB_PATH + DB_NAME;

			myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			// handle your exception here
		} catch (IOException e) {
			// handle your exception here
		} finally {
			try {

				myOutput.flush();
				myOutput.close();
				myInput.close();
				if (database != null && database.isOpen()) {

					database.close();
				}
			} catch (Exception e) {
				// handle your exception here
			}
		}

	}

	public boolean checkDataBaseExistence() {

		SQLiteDatabase checkDB = null;
		try {

			checkDB = SQLiteDatabase
					.openDatabase(
							"/data/data/com.paulo.android.solarmobile.controller/databases/MobilisDB.sqlite",
							null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			checkDB.close();
		} catch (SQLiteException e) {

		}

		if (checkDB != null) {
			return true;
		} else {
			checkDB = this.getReadableDatabase();
			checkDB.close();
			return false;
		}

	}
}
