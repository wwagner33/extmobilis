package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;

import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.model.GroupList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@OptionsMenu(R.menu.course_list)
@EActivity
public class GroupsListActivity extends SherlockFragmentActivity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	GroupList response;

	@ViewById
	ListView listView1;

	ArrayList<String> groups = new ArrayList<String>();
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_list);
		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);

		getGroups();

	}

	@OptionsItem(R.id.menu_logout)
	void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(this, LoginActivity_.class);
		startActivity(intent);
		finish();
	}

	@OptionsItem(R.id.menu_refresh)
	void refresh() {
		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);
		groups.clear();
		getGroups();
	}

	@Background
	void getGroups() {

		Log.i("TOKEN TURMAS", preferences.token().get().toString());

		try {
			response = solarManager.getGroups(preferences.token().get()
					.toString(), preferences.curriculumUnitSelected().get());
			updateList();
		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}
		Log.i("GROUPSLIST", response.getGroups().get(0).getCode());
	}

	@UiThread
	void updateList() {
		dialog.dismiss();

		for (int i = 0; i < response.getGroups().size(); i++) {
			groups.add(response.getGroups().get(i).getCode());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_list, R.id.item, groups);
		listView1.setAdapter(adapter);

	}

}
