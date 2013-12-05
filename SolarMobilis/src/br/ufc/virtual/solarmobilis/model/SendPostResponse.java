package br.ufc.virtual.solarmobilis.model;

import com.google.gson.annotations.SerializedName;

public class SendPostResponse {
	public Integer result;

	@SerializedName("post_id")
	public Integer postId;

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

}
