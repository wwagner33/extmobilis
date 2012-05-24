package com.mobilis.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.mobilis.interfaces.MobilisPreferenceActivity;

public class Config extends MobilisPreferenceActivity implements
		OnPreferenceChangeListener {

	private CheckBoxPreference autoLogin;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.config);
		autoLogin = (CheckBoxPreference) findPreference("checkbox_preference");
		autoLogin.setOnPreferenceChangeListener(this);

		boolean initialState = (getPreferences().getBoolean("AutoLogin", true)) ? true
				: false;
		autoLogin.setChecked(initialState);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
		boolean newValue = ((Boolean) value) ? true : false;

		if (newValue == true) {

			SharedPreferences.Editor editor = getPreferences().edit();
			editor.putBoolean("AutoLogin", newValue);
			commit(editor);
			autoLogin.setChecked(true);

		} else {
			SharedPreferences.Editor editor = getPreferences().edit();
			editor.putBoolean("AutoLogin", newValue);
			commit(editor);
			autoLogin.setChecked(false);
		}

		return false;
	}
}
