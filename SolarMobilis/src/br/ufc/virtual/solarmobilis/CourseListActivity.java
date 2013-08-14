package br.ufc.virtual.solarmobilis;

import org.springframework.web.client.ResourceAccessException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import br.ufc.virtual.model.CurriculumUnitList;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@OptionsMenu(R.menu.course_list)
@EActivity(R.layout.activity_course_list)
public class CourseListActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	CurriculumUnitList response;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		chamadas();
	}

	@OptionsItem(R.id.logout)
	void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(this, LoginActivity_.class);
		startActivity(intent);
		finish();
	}

	@Background
	void chamadas() {

		Log.i("TOKEN_DISCIPLINAS, TURMAS", preferences.token().get().toString());

		try {
			response = solarManager.getCurriculumUnits(preferences.token()
					.get().toString());
		} catch (ResourceAccessException e) {

			solarManager.alertTimeout();
		}

		Log.i("LISTA", response.getCurriculumuUnits().get(0).getName());

	}

}
