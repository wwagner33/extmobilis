package com.mobilis.controller;

import com.mobilis.util.MobilisPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Config extends PreferenceActivity implements
		OnPreferenceChangeListener {

	private CheckBoxPreference autoLogin;
	private MobilisPreferences preferences;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = MobilisPreferences.getInstance(this);
		addPreferencesFromResource(R.layout.config);
		autoLogin = (CheckBoxPreference) findPreference("checkbox_preference");
		autoLogin.setOnPreferenceChangeListener(this);

		boolean initialState = (preferences.getPreferences().getBoolean(
				"AutoLogin", true)) ? true : false;
		autoLogin.setChecked(initialState);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
		boolean newValue = ((Boolean) value) ? true : false;

		if (newValue == true) {

			SharedPreferences.Editor editor = preferences.getPreferences()
					.edit();
			editor.putBoolean("AutoLogin", newValue);
			editor.commit();
			autoLogin.setChecked(true);

		} else {
			SharedPreferences.Editor editor = preferences.getPreferences()
					.edit();
			editor.putBoolean("AutoLogin", newValue);
			editor.commit();
			autoLogin.setChecked(false);
		}

		return false;
	}
}
