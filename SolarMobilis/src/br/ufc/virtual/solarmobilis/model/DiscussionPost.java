package br.ufc.virtual.solarmobilis.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

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
	@SerializedName("created_at")
	public String createdAt;
    
	
	public List<DiscussionPostFile> files;
	//public List<DiscussionPostAttachment> attachments;
	@SerializedName("parent_id")
	public Integer parentId;
	public Bitmap userImage;
	public transient boolean isMarked = false;
	private String userImageURL;

	public DiscussionPost() {
		super();
	}

	public DiscussionPost(Integer id, Integer profileId, Integer discussionId,
			Integer userId, String userNick, Integer level, String content,
			String updatedAt, List<DiscussionPostFile> files) {
		super();
		this.id = id;
		this.profileId = profileId;
		this.discussionId = discussionId;
		this.userId = userId;
		this.userNick = userNick;
		this.level = level;
		this.content = content;
		this.createdAt = updatedAt;
		this.files = files;
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
		// content = Html.fromHtml(content).toString()/*.trim()*/;
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdatedAt() {
		return createdAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.createdAt = updatedAt;
	}

	/*
	 * public String getDateToString() { SimpleDateFormat simpleDateFormat = new
	 * SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS",
	 * java.util.Locale.getDefault()); String formattedDate = null;
	 * 
	 * try { Date convertedDate = simpleDateFormat.parse(updatedAt);
	 * SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS",
	 * java.util.Locale.getDefault()); formattedDate =
	 * formatter.format(convertedDate); } catch (ParseException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return formattedDate; }
	 */

	public String getDateToString() {

		String formattedString = createdAt.substring(0, 25).replace("-", "").replace(":", "")
				.replace(".", "").replace("T", "");

		Log.i("# post sendo enviado", formattedString);
		
		return formattedString;

		/*
		 * SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
		 * "yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.getDefault()); String
		 * formattedDate = null;
		 * 
		 * try { Log.i("DATA ERRADA", createdAt);
		 * 
		 * Date convertedDate =
		 * simpleDateFormat.parse("2014-03-13T09:02:26.68374-03:00");
		 * SimpleDateFormat formatter = new
		 * SimpleDateFormat("yyyyMMddHHmmssSSS", java.util.Locale.getDefault());
		 * formattedDate = formatter.format(convertedDate); } catch
		 * (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	public String getDateToPost() {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());

		String formattedDate = null;

		try {
			Date date = simpleFormat.parse(createdAt);
			SimpleDateFormat postDate = new SimpleDateFormat(
					"'Dia' dd 'de' MMMM", java.util.Locale.getDefault());
			formattedDate = postDate.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedDate;

	}

	public List<DiscussionPostFile> getFiles() {
		return files;
	}

	public void setAttachments(List<DiscussionPostFile> files) {
		this.files = files;
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

	public boolean isMarked() {
		return isMarked;
	}

	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}

	public String getUserImageURL() {
		return userImageURL;
	}

	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}

}
