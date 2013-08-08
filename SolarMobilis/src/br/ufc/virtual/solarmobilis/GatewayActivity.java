package br.ufc.virtual.solarmobilis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity()
public class GatewayActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (preferences.token().get().length() == 0) {
			Intent intent = new Intent(this, LoginActivity_.class);

			startActivity(intent);
			finish();

		} else {
			Intent intent = new Intent(this, CourseListActivity_.class);
			Log.i("Estado", "Preenchido - Curso");
			startActivity(intent);
			finish();
		}

	}

}