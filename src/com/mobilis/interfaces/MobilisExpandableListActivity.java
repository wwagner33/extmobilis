package com.mobilis.interfaces;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public abstract class MobilisExpandableListActivity extends
		ExpandableListActivity {

	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

	public void closeDialog(Dialog dialog) {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
}
