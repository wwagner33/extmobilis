package br.ufc.virtual.solarmobilis;

import com.google.gson.annotations.SerializedName;

import br.ufc.virtual.solarmobilis.model.DiscussionPost;

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
