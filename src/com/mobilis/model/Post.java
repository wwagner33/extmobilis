package com.mobilis.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mobilis.util.Constants;
import com.mobilis.util.DateUtils;

@DatabaseTable(tableName = "posts")
public class Post {

	public static final String ID_FIELD_NAME = "_id";
	public static final String PARENT_ID_FIELD_NAME = "parent_id";
	public static final String USER_ID_FIELD_NAME = "user_id";
	public static final String DISCUSSION_ID_FIELD_NAME = "discussion_id";
	public static final String PROFILE_ID_FIELD_NAME = "profile_id";
	public static final String USER_NICK_FIELD_NAME = "user_nick";
	public static final String CONTENT_FIELD_NAME = "content";
	public static final String DATE_FIELD_NAME = "date";
	public static final String IS_MARKED_FIELD_NAME = "is_marked";

	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private int _id;

	@DatabaseField(columnName = PARENT_ID_FIELD_NAME)
	private int parentId;

	@DatabaseField(columnName = USER_ID_FIELD_NAME)
	private int userId;

	@DatabaseField(columnName = DISCUSSION_ID_FIELD_NAME)
	private int discussionId;

	@DatabaseField(columnName = PROFILE_ID_FIELD_NAME)
	private int profileId;

	private boolean isMarked, isJustLoaded, isPlaying, isExpanded = false;

	@DatabaseField(columnName = USER_NICK_FIELD_NAME)
	private String userNick;

	@DatabaseField(columnName = CONTENT_FIELD_NAME)
	private String content;

	@DatabaseField(columnName = DATE_FIELD_NAME)
	private Date date;

	public Post() {
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(int discussionId) {
		this.discussionId = discussionId;
	}

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				Constants.DATABASE_DATE_FORMAT);
		return simpleDateFormat.format(date.getTime());
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(String dateString) {
		Date date = new Date();
		try {
			date = DateUtils.getDbFormat().parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		this.date = date;
	}

	public String getDateToString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				Constants.WSCALL_DATE_FORMAT);
		return simpleDateFormat.format(date.getTime());
	}

	public boolean isMarked() {
		return isMarked;
	}

	public void setMarked(boolean marked) {
		this.isMarked = marked;
	}

	public boolean isJustLoaded() {
		return isJustLoaded;
	}

	public void setJustLoaded(boolean isJustLoaded) {
		this.isJustLoaded = isJustLoaded;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}
}
