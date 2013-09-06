package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;

import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
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

@OptionsMenu(R.menu.options_menu)
@EActivity
public class DiscussionListActivity extends SherlockFragmentActivity {
	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	DiscussionList response;

	@ViewById
	ListView listViewDiscussions;

	ArrayList<String> discussions = new ArrayList<String>();
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussion_list);

		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);

		getDiscussions();

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
		discussions.clear();
		getDiscussions();
	}

	@Background
	void getDiscussions() {

		Log.i("TOKEN TURMAS", preferences.token().get().toString());

		try {
			response = solarManager.getDiscussions(preferences.token().get()
					.toString(), preferences.groupSelected().get());
			updateList();
		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}
		Log.i("DISCUSSIONLIST", response.getDiscussions().get(0).getName());

	}

	@UiThread
	void updateList() {
		dialog.dismiss();

		for (int i = 0; i < response.getDiscussions().size(); i++) {
			discussions.add(response.getDiscussions().get(i).getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_list, R.id.item, discussions);
		listViewDiscussions.setAdapter(adapter);

	}

}
