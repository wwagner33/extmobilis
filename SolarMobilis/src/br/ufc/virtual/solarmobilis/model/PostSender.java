package br.ufc.virtual.solarmobilis.model;

import com.google.gson.annotations.SerializedName;

public class PostSender {

	@SerializedName("discussion_post")
	DiscussionPost discussionPost;

	public DiscussionPost getDiscussionPost() {
		return discussionPost;
	}

	public void setDiscussionPost(DiscussionPost discussionPost) {
		this.discussionPost = discussionPost;
	}

}
