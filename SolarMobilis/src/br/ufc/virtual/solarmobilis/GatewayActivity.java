package br.ufc.virtual.solarmobilis;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

@EActivity
public class GatewayActivity extends Activity {

	private static final String PREF_TOKEN = "Prefences";
	private Intent intent;
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		SharedPreferences sharedpreferences = getSharedPreferences(PREF_TOKEN,
				0);
		SharedPreferences.Editor editor = sharedpreferences.edit();

		if (getIntent().getExtras() != null) {
			extras = getIntent().getExtras();
			if (extras.getString("FinishActivity") != null) {
				finish();
			}
		} else {

			if (sharedpreferences.getString("token", null) != null) {

				intent = new Intent(this, CourseListActivity.class);
				startActivity(intent);

				/* finish(); */

			} else if ((sharedpreferences.getString("token", null) == null)) {

				intent = new Intent(this, LoginActivity_.class);
				startActivity(intent);

				/* finish(); */

			}

		}

	}

}
