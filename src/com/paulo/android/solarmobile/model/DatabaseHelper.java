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

import com.paulo.android.solarmobile.controller.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	String outFileName = Constants.DATABASE_PATH + Constants.DATABASE_NAME;

	private final Context context;

	public DatabaseHelper(Context context) {

		super(context, Constants.DATABASE_NAME, null, 1);
		this.context = context;
	}

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

		database = this.getReadableDatabase();
		try {

			myInput = context.getAssets().open(Constants.DATABASE_NAME);

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

			checkDB = SQLiteDatabase.openDatabase(outFileName, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
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
