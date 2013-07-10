package com.mobilis.dao;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;

public class PostDAO {

	private RuntimeExceptionDao<Post, Integer> dao;

	public PostDAO(DatabaseHelper helper) {
		dao = helper.getRuntimeExceptionDao(Post.class);
	}

	public boolean hasNewPosts(int discussionId, Date date) {

		if (date == null) {
			return false;
		}

		QueryBuilder<Post, Integer> qb = dao.queryBuilder();
		qb.setCountOf(true);
		qb.selectColumns(Post.DATE_FIELD_NAME);
		try {
			qb.where().eq(Post.DISCUSSION_ID_FIELD_NAME, discussionId).and()
					.eq(Post.DATE_FIELD_NAME, date);
			PreparedQuery<Post> pq = qb.prepare();
			return (dao.countOf(pq) == 0) ? true : false;
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public boolean postsExistOnDiscusison(int discussionId) {
		QueryBuilder<Post, Integer> qb = dao.queryBuilder();
		qb.setCountOf(true);
		try {
			qb.where().eq(Post.DISCUSSION_ID_FIELD_NAME, discussionId);
			PreparedQuery<Post> pq = qb.prepare();
			return (dao.countOf(pq) > 0) ? true : false;
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public void insertPost(Post post, int discussionId) {
		post.setDiscussionId(discussionId);
		dao.create(post);
	}

	public void insertPosts(Post[] posts, int discussionId) {
		clearPostsFromDiscussion(discussionId);
		for (Post post : posts) {
			insertPost(post, discussionId);
		}
	}

	public ArrayList<Integer> getIdsOfPostsWithoutImage(int discussionId) {
		ArrayList<Integer> ids = null;
		File imageDirectory = new File(Constants.PATH_IMAGES);
		if (imageDirectory.exists()) {
			int numberOfImages = imageDirectory.listFiles().length;
			if (numberOfImages > 0) {
				ids = getUserIdsAbsentImage(discussionId);
			} else {
				ids = getAllUserIds();
			}
		}

		else {
			imageDirectory.mkdir();
			ids = getAllUserIds();
		}
		return ids;
	}

	private ArrayList<Integer> getAllUserIds() {

		ArrayList<Integer> ids = new ArrayList<Integer>();
		QueryBuilder<Post, Integer> qb = dao.queryBuilder();
		qb.selectColumns(Post.ID_FIELD_NAME);
		qb.distinct();

		try {
			PreparedQuery<Post> pq = qb.prepare();
			List<Post> result = dao.query(pq);

			for (Post post : result) {
				ids.add(post.getId());
			}
			return ids;

		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public void updatePost(Post post) {
		dao.update(post);
	}

	public ArrayList<Integer> getUserIdsAbsentImage(int discussionId) {
		File imageDirectory = new File(Constants.PATH_IMAGES);
		File[] images = imageDirectory.listFiles();
		ArrayList<Integer> list = new ArrayList<Integer>();

		QueryBuilder<Post, Integer> qb = dao.queryBuilder();
		try {
			qb.where().eq(Post.DISCUSSION_ID_FIELD_NAME, discussionId);
			qb.distinct();
			PreparedQuery<Post> pq = qb.prepare();
			List<Post> queryResult = dao.query(pq);

			for (Post post : queryResult) {

				for (int y = 0; y < images.length; y++) {

					if (post.getId() != Integer.parseInt(FilenameUtils
							.removeExtension(images[y].getName()))) {
						list.add(post.getId());
					}
				}
				return list;
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		return list;
	}

	public ArrayList<Post> getAllPostsFromDiscussion(int discussionId) {
		QueryBuilder<Post, Integer> qb = dao.queryBuilder();
		try {
			qb.where().eq(Post.DISCUSSION_ID_FIELD_NAME, discussionId);
			qb.orderBy(Post.DATE_FIELD_NAME, true);
			PreparedQuery<Post> pq = qb.prepare();
			return new ArrayList<Post>(dao.query(pq));
		} catch (SQLException e) {
			throw new RuntimeException();
		}

	}

	public Post getPost(int postId) {
		return dao.queryForId(postId);
	}

	public void clearPostsFromDiscussion(int discussionId) {
		DeleteBuilder<Post, Integer> qb = dao.deleteBuilder();
		try {
			qb.where().eq(Post.DISCUSSION_ID_FIELD_NAME, discussionId);
			PreparedDelete<Post> pd = qb.prepare();
			dao.delete(pd);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
}
