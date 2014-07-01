package br.ufc.virtual.solarmobilis;

import java.io.File;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

@EActivity
public class GatewayActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createFolder();

		if (preferences.authToken().get().length() == 0) {
			Intent intent = new Intent(this, LoginActivity_.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(this, CurriculumUnitsListActivity_.class);
			startActivity(intent);
			finish();
		}
	}

	void createFolder() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/TTS/");

		File fileRec = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/Recordings/");

		if (!file.exists() || !fileRec.exists()) {
			file.mkdirs();
			fileRec.mkdirs();
		}
	}

}
