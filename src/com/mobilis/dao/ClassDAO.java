package com.mobilis.dao;

import java.sql.SQLException;

import android.database.Cursor;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.mobilis.model.Class;

public class ClassDAO {

	private DatabaseHelper helper = null;
	private RuntimeExceptionDao<Class, Integer> dao;

	public ClassDAO(DatabaseHelper helper) {
		this.helper = helper;
		dao = helper.getRuntimeExceptionDao(Class.class);
	}

	public void clearClassesFromCourse(int courseId) {
		DeleteBuilder<Class, Integer> db = dao.deleteBuilder();
		try {
			db.where().eq(Class.COURSE_ID_FIELD_NAME, courseId);
			PreparedDelete<Class> pd;
			pd = db.prepare();
			dao.delete(pd);

		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public Cursor getClassesAsCursor(int courseId) {

		QueryBuilder<Class, Integer> queryBuilder = dao.queryBuilder();
		PreparedQuery<Class> query;
		try {
			queryBuilder.where().eq(Class.COURSE_ID_FIELD_NAME, courseId);
			query = queryBuilder.prepare();
			AndroidCompiledStatement statement = (AndroidCompiledStatement) query
					.compile(helper.getConnectionSource()
							.getReadOnlyConnection(), StatementType.SELECT);
			Cursor cursor = statement.getCursor();
			return cursor;
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public void addClass(Class mClass, int courseId) {
		mClass.setCourseId(courseId);
		dao.create(mClass);
	}

	public boolean existClassesOnCourse(int courseId) {
		QueryBuilder<Class, Integer> qb = dao.queryBuilder();
		try {
			qb.setCountOf(true);
			qb.where().eq(Class.COURSE_ID_FIELD_NAME, courseId);
			PreparedQuery<Class> pq = qb.prepare();
			return (dao.countOf(pq) > 0) ? true : false;
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public void addClass(Class[] mClasses, int courseId) {
		clearClassesFromCourse(courseId);
		for (Class c : mClasses) {
			addClass(c, courseId);
		}
	}
}
