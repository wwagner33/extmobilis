package com.mobilis.interfaces;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MobilisPreferenceActivity extends PreferenceActivity {

	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public SharedPreferences getPreferences() {
		return settings;
	}

	public void commit(SharedPreferences.Editor editor) {
		if (android.os.Build.VERSION.SDK_INT <= 8)
			editor.commit();
		else
			editor.apply();
	}
}
