package br.ufc.virtual.solarmobilis;

import org.springframework.web.client.ResourceAccessException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@OptionsMenu(R.menu.course_list)
@EActivity(R.layout.activity_curriculum_units_list)
public class CurriculumUnitsListActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	CurriculumUnitList response;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getCurriculumUnits();
	}

	@OptionsItem(R.id.logout)
	void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(this, LoginActivity_.class);
		startActivity(intent);
		finish();
	}

	@Background
	void getCurriculumUnits() {
		Log.i("TOKEN_DISCIPLINAS, TURMAS", preferences.token().get().toString());

		try {
			response = solarManager.getCurriculumUnits(preferences.token()
					.get().toString());
			listaDeCursos();
		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}
	}

	public void listaDeCursos() {
		Log.i("LISTA", response.getCurriculumuUnits().get(0).getName());
	}

}
