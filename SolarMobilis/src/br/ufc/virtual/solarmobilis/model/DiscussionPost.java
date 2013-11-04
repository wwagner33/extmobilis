package br.ufc.virtual.solarmobilis.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.text.Html;

import com.google.gson.annotations.SerializedName;

public class DiscussionPost {

	public Integer id;
	@SerializedName("profile_id")
	public Integer profileId;
	@SerializedName("discussion_id")
	public Integer discussionId;
	@SerializedName("user_id")
	public Integer userId;
	@SerializedName("user_nick")
	public String userNick;
	public Integer level;
	public String content;
	@SerializedName("updated_at")
	public String updatedAt;
	public List<Object> attachments;
	@SerializedName("parent_id")
	public Integer parentId;
	public Bitmap userImage;
	private String userImageURL;

	public DiscussionPost() {
		super();
	}

	public DiscussionPost(Integer id, Integer profileId, Integer discussionId,
			Integer userId, String userNick, Integer level, String content,
			String updatedAt, List<Object> attachments) {
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public Integer getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(Integer discussionId) {
		this.discussionId = discussionId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getContent() {
		content = Html.fromHtml(content).toString().trim();
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

	public String getDateToString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
		String formattedDate = null;

		try {
			Date convertedDate = simpleDateFormat.parse(updatedAt);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",
					java.util.Locale.getDefault());
			formattedDate = formatter.format(convertedDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedDate;
	}

	public String getDateToPost() {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());

		String formattedDate = null;

		try {
			Date date = simpleFormat.parse(updatedAt);
			SimpleDateFormat postDate = new SimpleDateFormat(
					"'Dia' dd 'de' MMMM", java.util.Locale.getDefault());
			formattedDate = postDate.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedDate;

	}

	public List<Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Object> attachments) {
		this.attachments = attachments;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Bitmap getUserImage() {
		return userImage;
	}

	public void setUserImage(Bitmap userImage) {
		this.userImage = userImage;
	}

	public String getUserImageURL() {
		return userImageURL;
	}

	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}

}
