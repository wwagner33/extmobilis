package com.mobilis.util;

import java.util.ArrayList;

public class MobilisStatus {

	public int selectedPost = -1;
	public int selectedCourse = -1;
	public int selectedClass = -1;
	public int selectedDiscussion = -1;
	public ArrayList<Integer> ids = null;
	public String token = null;
	public boolean forumClosed;

	private static MobilisStatus instance = null;

	private MobilisStatus() {

	}

	public static MobilisStatus getInstance() {
		if (instance == null) {
			instance = new MobilisStatus();
		}
		return instance;
	}
}
