package com.mobilis.dao;

import java.sql.SQLException;

import android.database.Cursor;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.mobilis.model.Discussion;

public class DiscussionDAO {

	private DatabaseHelper helper = null;
	private RuntimeExceptionDao<Discussion, Integer> dao;

	public DiscussionDAO(DatabaseHelper helper) {
		this.helper = helper;
		dao = helper.getRuntimeExceptionDao(Discussion.class);
	}

	public boolean existsDiscussionOnClass(int classId) {

		QueryBuilder<Discussion, Integer> qb = dao.queryBuilder();
		qb.selectColumns(Discussion.ID_FIELD_NAME);
		qb.setCountOf(true);
		try {
			qb.where().eq(Discussion.CLASS_ID_FIELD_NAME, classId);
			PreparedQuery<Discussion> pq = qb.prepare();
			return (dao.countOf(pq) > 0) ? true : false;

		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public Cursor getDiscussionsFromClassAsCursor(int classId) {

		QueryBuilder<Discussion, Integer> queryBuilder = dao.queryBuilder();
		PreparedQuery<Discussion> query;

		try {
			queryBuilder.where().eq(Discussion.CLASS_ID_FIELD_NAME, classId);
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

	public Discussion getDiscussion(int discussionId) {
		return dao.queryForId(discussionId);
	}

	public void updateDiscussion(Discussion discussion) {
		dao.update(discussion);
	}

	public void addDiscussion(Discussion discussion, int classId) {
		discussion.setClassId(classId);
		dao.create(discussion);
	}

	public void addDiscussions(Discussion[] discussions, int classId) {
		clearDiscussionsFromClass(classId);
		for (Discussion d : discussions) {
			addDiscussion(d, classId);
		}
	}

	public void clearDiscussionsFromClass(int classId) {
		DeleteBuilder<Discussion, Integer> db = dao.deleteBuilder();
		try {
			db.where().eq(Discussion.CLASS_ID_FIELD_NAME, classId);
			dao.delete(db.prepare());
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
}