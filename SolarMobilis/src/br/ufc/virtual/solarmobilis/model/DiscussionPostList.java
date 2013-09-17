package br.ufc.virtual.solarmobilis.model;

import java.util.List;

public class DiscussionPostList {
	public int before;
	public int after;
	public List<DiscussionPosts> posts;

	public int getBefore() {
		return before;
	}

	public void setBefore(int before) {
		this.before = before;
	}

	public int getAfter() {
		return after;
	}

	public void setAfter(int after) {
		this.after = after;
	}

	public List<DiscussionPosts> getPosts() {
		return posts;
	}

	public void setPosts(List<DiscussionPosts> posts) {
		this.posts = posts;
	}

}
