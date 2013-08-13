package br.ufc.virtual.solarmobilis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import br.ufc.virtual.model.CurriculumUnitList;

import com.googlecode.androidannotations.annotations.Background;
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

	@RestService
	SolarClient solarClient;

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

		response = solarClient.getDiscipinas(preferences.token().get()
				.toString());

		Log.i("LISTA", response.getList().get(0).getName());

	}

}
