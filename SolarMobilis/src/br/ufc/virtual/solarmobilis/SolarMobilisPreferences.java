package br.ufc.virtual.solarmobilis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SolarMobilisPreferences {

	private static SharedPreferences prefs;
	private static SolarMobilisPreferences instance = null;

	public static SolarMobilisPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new SolarMobilisPreferences();
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return instance;
	}

	public void setToken(String token) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("token", token);
		editor.commit();

	}

	public String getToken() {
		return prefs.getString("token", null);
	}

	public SharedPreferences getPreferences() {
		return prefs;
	}

}
