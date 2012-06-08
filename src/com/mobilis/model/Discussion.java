package com.mobilis.model;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.mobilis.util.DateUtils;

public class Discussion {
	private int _id;
	private String name;
	private Calendar lastPostDate;
	private int status;
	private int classId;
	private String description;
	private int nextPosts;
	private int previousPosts;

	public Discussion() {

	}

	public Discussion(int _id, String name, Calendar lastPostDate, int status,
			int classId, String description, int nextPosts, int previousPosts) {
		this._id = _id;
		this.name = name;
		this.lastPostDate = lastPostDate;
		this.status = status;
		this.classId = classId;
		this.description = description;
		this.nextPosts = nextPosts;
		this.previousPosts = previousPosts;
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

	public Calendar getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(Calendar lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public void setLastPostDate(String lastPostDateString) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		try {
			date = DateUtils.getDbFormat().parse(lastPostDateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		cal.setTime(date);
		this.lastPostDate = cal;
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

}
