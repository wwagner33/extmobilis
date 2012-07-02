package com.mobilis.dao;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mobilis.controller.R;
import com.mobilis.model.Class;
import com.mobilis.model.Course;
import com.mobilis.model.Discussion;
import com.mobilis.model.Post;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	public static final String TAG = "DatabaseHelper";
	public static final String DATABASE_NAME = "mobilis.db";
	public static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION,
				R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {

			TableUtils.createTable(connectionSource, Course.class);
			TableUtils.createTable(connectionSource, Class.class);
			TableUtils.createTable(connectionSource, Discussion.class);
			TableUtils.createTable(connectionSource, Post.class);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	private Dao<Course, Integer> courseDAO;

	public Dao<Course, Integer> getDao() throws SQLException {
		if (courseDAO == null) {
			courseDAO = getDao(Course.class);
		}
		return courseDAO;
	}
}
