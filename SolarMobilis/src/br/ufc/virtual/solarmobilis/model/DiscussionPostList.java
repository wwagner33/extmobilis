package br.ufc.virtual.solarmobilis.model;

import java.util.List;

public class DiscussionPostList {
	public int newer;
	public int older;
	public List<DiscussionPost> posts;

	public int getNewer() {
		return newer;
	}

	public void setNewer(int newer) {
		this.newer = newer;
	}

	public int getOlder() {
		return older;
	}

	public void setOlder(int older) {
		this.older = older;
	}

	public List<DiscussionPost> getPosts() {
		return posts;
	}

	public void setPosts(List<DiscussionPost> posts) {
		this.posts = posts;
	}

}
