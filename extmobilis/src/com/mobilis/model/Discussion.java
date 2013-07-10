package com.mobilis.model;

import java.text.ParseException;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mobilis.util.DateUtils;

@DatabaseTable(tableName = "discussions")
public class Discussion {

	public static final String ID_FIELD_NAME = "_id";
	public static final String NAME_FIELD_NAME = "name";
	public static final String LAST_POST_DATE_FIELD_NAME = "last_post_date";
	public static final String STATUS_FIELD_NAME = "status";
	public static final String CLASS_ID_FIELD_NAME = "class_id";
	public static final String DESCRIPTION_FIELD_NAME = "description";
	public static final String NEXT_POSTS_FIELD_NAME = "next_posts";
	public static final String PREVIOUS_POSTS_FIELD_NAME = "previous_posts";
	public static final String HAS_NEW_POSTS_FIELD_NAME = "has_new_posts";
	public static final String START_DATE_FIELD_NAME = "start_date";
	public static final String END_DATE_FIELD_NAME = "end_date";

	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private int _id;

	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String name;

	@DatabaseField(columnName = LAST_POST_DATE_FIELD_NAME)
	private Date lastPostDate;

	@DatabaseField(columnName = STATUS_FIELD_NAME)
	private int status;

	@DatabaseField(columnName = CLASS_ID_FIELD_NAME)
	private int classId;

	@DatabaseField(columnName = DESCRIPTION_FIELD_NAME)
	private String description;

	@DatabaseField(columnName = NEXT_POSTS_FIELD_NAME)
	private int nextPosts;

	@DatabaseField(columnName = PREVIOUS_POSTS_FIELD_NAME)
	private int previousPosts;

	@DatabaseField(columnName = HAS_NEW_POSTS_FIELD_NAME)
	private boolean hasNewPosts = false;

	@DatabaseField(columnName = START_DATE_FIELD_NAME)
	private Date startDate;

	@DatabaseField(columnName = END_DATE_FIELD_NAME)
	private Date endDate;

	public Discussion() {

	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(Date lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public void setLastPostDate(String lastPostDateString) {
		Date date = new Date();
		try {
			date = DateUtils.getDbFormat().parse(lastPostDateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		this.lastPostDate = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNextPosts() {
		return nextPosts;
	}

	public void setNextPosts(int nextPosts) {
		this.nextPosts = nextPosts;
	}

	public int getPreviousPosts() {
		return previousPosts;
	}

	public void setPreviousPosts(int previousPosts) {
		this.previousPosts = previousPosts;
	}

	public boolean HasNewPosts() {
		return hasNewPosts;
	}

	public void setHasNewPosts(boolean hasNewPosts) {
		this.hasNewPosts = hasNewPosts;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
