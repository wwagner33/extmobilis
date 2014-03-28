package br.ufc.virtual.solarmobilis.model;

import com.google.gson.annotations.SerializedName;

public class DiscussionPostFile {

	public DiscussionPostFile(int id, String name, String contentType,
			String updatedAt, int size, String url) {
		super();
		this.id = id;
		this.name = name;
		this.contentType = contentType;
		this.updatedAt = updatedAt;
		this.size = size;
		this.url = url;
	}

	private int id;
	private String name;
	
	@SerializedName("content_type")
	private String contentType;
	
	@SerializedName("updated_at")
	private String updatedAt;
	
	private int size;
	
	private String url;
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}

/* "files": [
                {
                    "id": 1,
                    "name": "filetest.jpg",
                    "content_type": "image/jpeg",
                    "updated_at": "2014-03-27T10:57:46-03:00",
                    "size": 2000,
                    "url": "http://localhost:3000/posts/7/post_files/1/download"
                }*/