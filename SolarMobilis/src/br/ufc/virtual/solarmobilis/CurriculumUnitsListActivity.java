package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;

import org.springframework.web.client.ResourceAccessException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@OptionsMenu(R.menu.course_list)
@EActivity
public class CurriculumUnitsListActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	CurriculumUnitList response;

	@ViewById
	ListView listView;

	ArrayList<String> courses = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curriculum_units_list);

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
			updateList();
		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}
	}

	@UiThread
	void updateList() {
		Log.i("LISTA", response.getCurriculumuUnits().get(0).getName());

		for (int i = 0; i < response.getCurriculumuUnits().size(); i++) {
			courses.add(response.getCurriculumuUnits().get(i).getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_liste, R.id.item, courses);
		listView.setAdapter(adapter);

	}

	@ItemClick
	void listView(String items) {
		Toast.makeText(this, "clicado: " + items, Toast.LENGTH_SHORT).show();

	}

}
