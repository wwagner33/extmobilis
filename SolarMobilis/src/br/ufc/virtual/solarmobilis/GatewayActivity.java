package br.ufc.virtual.solarmobilis;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@NoTitle
@EActivity
public class GatewayActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createFolder();

		if (preferences.token().get().length() == 0) {
			Intent intent = new Intent(this, LoginActivity_.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(this, CurriculumUnitsListActivity_.class);
			Log.i("Estado", "Preenchido - Curso");
			startActivity(intent);
			finish();
		}
	}

	void createFolder() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/TTS/");
		if (!file.exists()) {
			file.mkdirs();
			Log.i("Pasta", "criada");
		}
	}

}
