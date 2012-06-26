package com.mobilis.util;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MobilisPreferences {

	public int selectedPost = -1;
	public int selectedCourse = -1;
	public int selectedClass = -1;
	public int selectedDiscussion = -1;
	public ArrayList<Integer> ids = null;
	public boolean forumClosed;
	private static SharedPreferences prefs;
	private static MobilisPreferences instance = null;

	private MobilisPreferences() {

	}

	public static MobilisPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new MobilisPreferences();
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return instance;
	}

	public void setToken(String token) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("token", token);
		commit(editor);

	}

	@SuppressLint("NewApi")
	public void commit(SharedPreferences.Editor editor) {
		if (android.os.Build.VERSION.SDK_INT <= 8)
			editor.commit();
		else
			editor.apply();
	}

	public String getToken() {
		return prefs.getString("token", null);
	}

	public SharedPreferences getPreferences() {
		return prefs;
	}
}
