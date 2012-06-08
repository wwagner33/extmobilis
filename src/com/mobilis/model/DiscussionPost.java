package com.mobilis.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mobilis.util.Constants;
import com.mobilis.util.DateUtils;

//import br.iuvi.util.Constants;
//import br.iuvi.util.DateUtils;

public class DiscussionPost {
	private long _id;
	private long parentId;
	private long userId;
	private long discussionId;
	private long profileId;
	private boolean isMarked, isPlaying;
	private String userNick;
	private String content;
	private Calendar date;

	public DiscussionPost() {

	}

	public DiscussionPost(long _id, long parentId, long userId,
			long discussionId, long profileId, boolean isMarked,
			String userNick, String content, Calendar date) {
		this._id = _id;
		this.parentId = parentId;
		this.userId = userId;
		this.discussionId = discussionId;
		this.profileId = profileId;
		this.isMarked = isMarked;
		this.userNick = userNick;
		this.content = content;
		this.date = date;
	}

	public long getId() {
		return _id;
	}

	public void setId(long id) {
		this._id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(long discussionId) {
		this.discussionId = discussionId;
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public boolean isMarked() {
		return isMarked;
	}

	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
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

	public void setDate(Calendar date) {
		this.date = date;
	}

	public void setDate(String dateString) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		try {
			date = DateUtils.getDbFormat().parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		cal.setTime(date);
		this.date = cal;
	}

	public String getDateToString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				Constants.WSCALL_DATE_FORMAT);
		return simpleDateFormat.format(date.getTime());
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
}
