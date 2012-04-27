package com.mobilis.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Config extends PreferenceActivity implements
		OnPreferenceChangeListener {

	private CheckBoxPreference autoLogin;
	private SharedPreferences settings;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.layout.config);
		autoLogin = (CheckBoxPreference) findPreference("checkbox_preference");
		autoLogin.setOnPreferenceChangeListener(this);

		boolean initialState = (settings.getBoolean("AutoLogin", true)) ? true
				: false;
		autoLogin.setChecked(initialState);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
		boolean newValue = ((Boolean) value) ? true : false;

		if (newValue == true) {

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("AutoLogin", newValue);
			editor.commit();
			autoLogin.setChecked(true);

		} else {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("AutoLogin", newValue);
			editor.commit();
			autoLogin.setChecked(false);
		}

		return false;
	}
}
