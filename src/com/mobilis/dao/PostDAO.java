package com.mobilis.dao;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.mobilis.model.DiscussionPost;
import com.mobilis.util.Constants;
import com.mobilis.util.DateUtils;

public class PostDAO extends DBAdapter {

	public PostDAO(Context context) {
		super(context);
	}

	public void addPosts(ArrayList<ContentValues> values, int topic_id) {

		clearPostsFromTopic(topic_id);
		getDatabase().beginTransaction();
		for (ContentValues i : values) {
			getDatabase().insert("posts", null, i);
		}
		getDatabase().setTransactionSuccessful();
		getDatabase().endTransaction();
	}

	public boolean postExistsOnTopic(int topic_id) {
		Cursor cursor = getDatabase().query("posts", new String[] { "_id" },
				"discussion_id=" + topic_id, null, null, null, null, "1");

		cursor.moveToFirst();
		try {
			int count = cursor.getInt(0);
			cursor.close();
			return (count > 0) ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	public void clearPostsFromTopic(int topic_id) {
		int status = getDatabase().delete("posts", "discussion_id=" + topic_id,
				null);
		Log.w("Delete status", String.valueOf(status));
	}

	public Cursor getPostsFromTopic(int topic_id) { // Pegar apenas o necessário
		Log.i("TOPIC_ID", String.valueOf(topic_id));

		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM posts WHERE discussion_id =" + topic_id
						+ " ORDER BY updated_at ASC", null);
		cursor.moveToFirst();
		Log.i("CURSOR_SIZE", String.valueOf(cursor.getCount()));
		return cursor;
	}

	public ArrayList<ContentValues> cursorToContentValues(Cursor cursor) {

		cursor.moveToLast();

		ArrayList<ContentValues> values = new ArrayList<ContentValues>();

		do {

			ContentValues content = new ContentValues();
			content.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			content.put("profile_id",
					cursor.getInt(cursor.getColumnIndex("profile_id")));
			content.put("content",
					cursor.getString(cursor.getColumnIndex("content")));
			content.put("updated_at",
					cursor.getString(cursor.getColumnIndex("updated_at")));
			content.put("user_id",
					cursor.getInt(cursor.getColumnIndex("user_id")));
			content.put("discussion_id",
					cursor.getInt(cursor.getColumnIndex("discussion_id")));
			content.put("parent_id",
					cursor.getInt(cursor.getColumnIndex("parent_id")));
			content.put("user_nick",
					cursor.getString(cursor.getColumnIndex("user_nick")));
			values.add(content);

		} while (cursor.moveToPrevious());

		cursor.close();
		return values;
	}

	public ArrayList<Integer> getAllUserIds() {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT DISTINCT user_id FROM posts", null);
		cursor.moveToFirst();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		do {
			ids.add(cursor.getInt(cursor.getColumnIndex("user_id")));
		} while

		(cursor.moveToNext());
		cursor.close();
		return ids;
	}

	public ArrayList<Integer> getUserIdsAbsentImage(int discussion_id) {
		File imageDirectory = new File(Constants.PATH_IMAGES);
		File[] images = imageDirectory.listFiles();
		Cursor cursor = getDatabase().rawQuery(
				"SELECT DISTINCT user_id FROM posts WHERE discussion_id="
						+ discussion_id, null);
		cursor.moveToFirst();
		Log.i("CURSOR SIZE", "" + cursor.getCount());
		Log.i("IMAGE NUMBER", "" + images.length);

		ArrayList<Integer> list = new ArrayList<Integer>();

		do {

			for (int y = 0; y < images.length; y++) {
				Log.i("USER_ID",
						"" + cursor.getInt(cursor.getColumnIndex("user_id")));
				Log.i("IMAGE_NAME",
						FilenameUtils.removeExtension(images[y].getName()));

				if (cursor.getInt(cursor.getColumnIndex("user_id")) != Integer
						.parseInt(FilenameUtils.removeExtension(images[y]
								.getName()))) {
					list.add(cursor.getInt(cursor.getColumnIndex("user_id")));
				}
			}

		} while (cursor.moveToNext());

		cursor.close();
		return list;
	}

	public boolean postedToday(int topicId, int postId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT updated from posts WHERE discussion_id=" + topicId
						+ " AND date(updated)=date(\'now\') AND _id=" + postId,
				null);
		cursor.moveToFirst();
		int count = cursor.getCount();
		cursor.close();
		return (count > 0) ? true : false;
	}

	public String getPostedTodayDateFormat(int topicId, int postId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT strftime(\'%H:%M\',updated) FROM posts WHERE discussion_id="
						+ topicId + " AND _id=" + postId, null);
		cursor.moveToFirst();
		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

	public String getPostedBeforeTodayDateFormat(int topicId, int postId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT strftime(\'%d\',updated) FROM posts WHERE discussion_id="
						+ topicId + " AND _id=" + postId, null);
		cursor.moveToFirst();
		String dateDay = cursor.getString(0);
		// cursor.close();
		cursor = getDatabase().rawQuery(
				"SELECT strftime(\'%m\',updated) FROM posts WHERE discussion_id="
						+ topicId + " AND _id=" + postId, null);
		cursor.moveToFirst();
		String dateMonth = cursor.getString(0);
		cursor.close();
		dateMonth = DateUtils.getMonthAsText((Integer.parseInt(dateMonth) - 1));

		return dateDay + " " + dateMonth;
	}

	public String getOldestPost(int topicId) {
		Cursor cursor = getDatabase()
				.rawQuery(
						"SELECT MIN(strftime(\'%Y%m%d%H%M%S\',updated_at)) FROM posts WHERE discussion_id="
								+ topicId, null);
		cursor.moveToFirst();
		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

	// TTS

	public void clearPostsTable() {
		getDatabase().delete("posts", null, null);
	}

	public void clearPostsFromDiscussion(long discussionId) {
		getDatabase().delete("posts", "posts.discussion_id = ?",
				new String[] { Long.toString(discussionId) });
	}

	public void insertPostsToDB(DiscussionPost[] posts) {
		for (DiscussionPost post : posts) {
			insertSinglePostToDB(post);
		}

		// int discussionId = (int) posts[0].getDiscussionId();
		// int totalPosts = totalDiscussionPosts(discussionId);
		// int totalPostsToDelete = totalPosts - 20;
		//
		// if (totalPosts > 20) {
		// // deleta os posts excedentes
		// deleteNPosts(discussionId, totalPostsToDelete);
		// // atualiza o valor de previous_posts
		// DiscussionDAO discussionDAO = new DiscussionDAO(getContext());
		// discussionDAO.open();
		// discussionDAO.setPreviousPosts(discussionId,
		// discussionDAO.getPreviousPosts(discussionId)
		// + totalPostsToDelete);
		// discussionDAO.close();
		// }
	}

	public void insertPostsToDB(ArrayList<DiscussionPost> posts,
			int discussionId) {
		DiscussionPost[] discussionPosts = posts
				.toArray(new DiscussionPost[posts.size()]);
		clearPostsFromDiscussion(discussionId);
		insertPostsToDB(discussionPosts);
	}

	public void deleteNPosts(int discussionId, int n) {
		Cursor cursor = getDatabase().query("posts",
				new String[] { "_id", "date" },
				"discussion_id = " + discussionId, null, null, null, "date",
				"" + n);
		cursor.moveToFirst();
		do {
			int colIndex = cursor.getColumnIndex("_id");
			Log.d("cursor", "id >> " + colIndex);
			int _id = cursor.getInt(colIndex);
			Log.d("cursor", "id >> " + _id);
			deleteSinglePostFromDB(_id);
		} while (cursor.moveToNext());
		cursor.close();
	}

	public void deleteSinglePostFromDB(int id) {
		getDatabase().delete("posts", "_id=" + id, null);
	}

	public void insertSinglePostToDB(DiscussionPost post) {
		if (post == null)
			return;
		ContentValues values = getValues(post);
		getDatabase().insert("posts", null, values);
	}

	public boolean discussionHasPosts(long discussionId) {

		int count = totalDiscussionPosts(discussionId);

		if (count > 0)
			return true;
		else
			return false;
	}

	public int totalDiscussionPosts(long discussionId) {
		Cursor cursor = getDatabase().query("posts",
				new String[] { "count(_id)" },
				"discussion_id = " + discussionId, null, null, null, null);
		cursor.moveToFirst();
		int total = cursor.getInt(0);
		cursor.close();
		return total;
	}

	public boolean postExists(long postId) {
		Cursor cursor = getDatabase().query("posts",
				new String[] { "count(_id)" }, "_id = " + postId, null, null,
				null, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		if (count > 0)
			return true;
		else
			return false;
	}

	public DiscussionPost[] getAllPosts(int discussionId) {
		Cursor cursor = getDatabase().rawQuery(
				"SELECT * FROM posts WHERE discussion_id = " + discussionId
						+ " ORDER BY date ASC", null);
		DiscussionPost[] discussionPosts = new DiscussionPost[cursor.getCount()];

		while (cursor.moveToNext()) {
			int i = cursor.getPosition();
			discussionPosts[i] = cursorToPost(cursor);
		}

		cursor.close();
		return discussionPosts;
	}

	private DiscussionPost cursorToPost(Cursor cursor) {
		final DiscussionPost post = new DiscussionPost();
		post.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		post.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));
		post.setDiscussionId(cursor.getLong(cursor
				.getColumnIndex("discussion_id")));
		post.setDate(cursor.getString(cursor.getColumnIndex("date")));
		post.setParentId(cursor.getLong(cursor.getColumnIndex("parent_id")));
		post.setProfileId(cursor.getLong(cursor.getColumnIndex("profile_id")));
		post.setMarked(cursor.getInt(cursor.getColumnIndex("is_marked")) == 1);
		post.setUserNick(cursor.getString(cursor.getColumnIndex("user_nick")));
		post.setContent(cursor.getString(cursor.getColumnIndex("content")));
		// post.setContentLast(cursor.getString(cursor
		// .getColumnIndex("content_last")));
		return post;
	}

	private ContentValues getValues(DiscussionPost post) {
		if (post == null)
			return null;

		ContentValues values = new ContentValues();
		values.put("_id", post.getId());
		values.put("user_id", post.getUserId());
		values.put("discussion_id", post.getDiscussionId());
		values.put("date", post.getDate());
		values.put("parent_id", post.getParentId());
		values.put("profile_id", post.getProfileId());
		values.put("is_marked", post.isMarked());
		values.put("user_nick", post.getUserNick());
		values.put("content", post.getContent());
		// values.put("content_last", post.getContentLast());

		return values;
	}

	public void setMarked(int postId, boolean isMarked) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("is_marked", isMarked ? 1 : 0);
		getDatabase().update("posts", contentValues, "_id=" + postId, null);
	}

	public void deletePost(DiscussionPost post) {
		getDatabase().delete("post", "_id =" + post.getId(), null);
	}

	public void close() {
		getDatabase().close();
	}

}
