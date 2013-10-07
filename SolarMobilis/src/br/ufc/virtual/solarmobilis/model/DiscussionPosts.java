package br.ufc.virtual.solarmobilis.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class DiscussionPosts {

	public int id;
	@SerializedName("profile_id")
	public int profileId;
	@SerializedName("discussion_id")
	public int discussionId;
	@SerializedName("user_id")
	public int userId;
	@SerializedName("user_nick")
	public String userNick;
	public int level;
	public String content;
	@SerializedName("updated_at")
	public String updatedAt;
	public List<Object> attachments;

	public DiscussionPosts() {
		super();
	}

	public DiscussionPosts(int id, int profileId, int discussionId, int userId,
			String userNick, int level, String content, String updatedAt,
			List<Object> attachments) {
		super();
		this.id = id;
		this.profileId = profileId;
		this.discussionId = discussionId;
		this.userId = userId;
		this.userNick = userNick;
		this.level = level;
		this.content = content;
		this.updatedAt = updatedAt;
		this.attachments = attachments;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public int getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(int discussionId) {
		this.discussionId = discussionId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	//

	public String getDateToString() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		String formattedDate = null;

		try {
			Date convertedDate = simpleDateFormat.parse(updatedAt);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

			formattedDate = formatter.format(convertedDate);

			

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return formattedDate;

	}

	//
	public List<Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Object> attachments) {
		this.attachments = attachments;
	}

}
