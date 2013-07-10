package com.mobilis.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import android.database.Cursor;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.table.TableUtils;
import com.mobilis.model.Course;

public class CourseDAO {

	private DatabaseHelper helper = null;
	private RuntimeExceptionDao<Course, Integer> dao;

	public CourseDAO(DatabaseHelper helper) {
		this.helper = helper;
		dao = helper.getRuntimeExceptionDao(Course.class);

	}

	public RuntimeExceptionDao<Course, Integer> getDao() {
		return dao;
	}

	public boolean existCourses() {
		return (dao.countOf() > 0) ? true : false;
	}

	public void addCourse(Course course) {
		dao.create(course);
	}

	public void addCourse(Course[] courses) {
		for (Course course : courses) {
			addCourse(course);
		}
	}

	public ArrayList<Course> getCourses() {
		return new ArrayList<Course>(dao.queryForAll());
	}

	public Cursor getCoursesAsCursor() {

		QueryBuilder<Course, Integer> queryBuilder = dao.queryBuilder();
		PreparedQuery<Course> query;

		try {
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

	public void clearCourses() {
		try {
			TableUtils.clearTable(helper.getConnectionSource(), Course.class);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
}
