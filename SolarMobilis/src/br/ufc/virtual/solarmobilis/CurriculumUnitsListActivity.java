package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.HttpStatusCodeException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@OptionsMenu(R.menu.options_menu)
@EActivity
public class CurriculumUnitsListActivity extends SherlockFragmentActivity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	CurriculumUnitList response;

	@ViewById
	ListView listViewCurriculumUnits;

	ArrayList<String> courses = new ArrayList<String>();
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curriculum_units_list);

		dialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
				getString(R.string.dialog_message), true);

		getCurriculumUnits();
	}

	@OptionsItem(R.id.menu_logout)
	void logout() {
		solarManager.logout();
	}

	@OptionsItem(R.id.menu_refresh)
	void refresh() {
		dialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
				getString(R.string.dialog_message), true);
		courses.clear();
		getCurriculumUnits();
	}

	@Background
	void getCurriculumUnits() {
		try {
			response = solarManager.getCurriculumUnits();
			updateList();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
		}
	}

	@UiThread
	void updateList() {
		for (int i = 0; i < response.getCurriculumuUnits().size(); i++) {
			courses.add(response.getCurriculumuUnits().get(i).getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_list, R.id.item, courses);
		listViewCurriculumUnits.setAdapter(adapter);
	}

	@ItemClick
	void listViewCurriculumUnits(int position) {
		preferences.curriculumUnitSelected().put(
				response.getCurriculumuUnits().get(position).getid());

		Intent intent = new Intent(this, GroupsListActivity_.class);
		startActivity(intent);
	}
}
