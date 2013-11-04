package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.model.GroupList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@OptionsMenu(R.menu.options_menu)
@EActivity
public class GroupsListActivity extends SherlockFragmentActivity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	GroupList response;

	@ViewById
	ListView listViewGroups;

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
		solarManager.logout();
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
			response = solarManager.getGroups(preferences.curriculumUnitSelected().get());
			updateList();
			
		} catch (HttpClientErrorException e) {
			Log.i("ERRO", e.getStatusCode().toString());
			dialog.dismiss();
			solarManager.errorHandler(e.getStatusCode());

		} catch (ResourceAccessException e) {
			dialog.dismiss();
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
		listViewGroups.setAdapter(adapter);

	}

	@ItemClick
	void listViewGroups(int position) {

		Toast.makeText(this,
				"clicado: " + response.getGroups().get(position).getCode(),
				Toast.LENGTH_SHORT).show();

		preferences.groupSelected().put(
				response.getGroups().get(position).getId());

		Intent intent = new Intent(this, DiscussionListActivity_.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}

}
