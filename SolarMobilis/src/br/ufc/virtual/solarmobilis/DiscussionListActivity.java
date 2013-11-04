package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.model.DiscussionAdapter;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
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
		solarManager.logout();
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
			response = solarManager.getDiscussions(preferences.groupSelected().get());
			updateList();

		} catch (HttpClientErrorException e) {
			Log.i("ERRO", e.getStatusCode().toString());
			dialog.dismiss();
			solarManager.errorHandler(e.getStatusCode());

		} catch (ResourceAccessException e) {
			dialog.dismiss();
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

		DiscussionAdapter adapter = new DiscussionAdapter(this,
				R.layout.discussion_list, R.id.topic_name, discussions,
				response);
		listViewDiscussions.setAdapter(adapter);

	}

	@ItemClick
	void listViewDiscussions(int position) {

		Intent intent = new Intent(this, DiscussionsPostsActivity_.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("discussionId", response.getDiscussions().get(position)
				.getId());
		intent.putExtra("discussionName",
				response.getDiscussions().get(position).getName());
		intent.putExtra("discussionLastPostDate", response.getDiscussions()
				.get(position).getLastPostDate());
		intent.putExtra("startDate", response.getDiscussions().get(position)
				.getStartDate());
		intent.putExtra("endDate", response.getDiscussions().get(position)
				.getEndDate());
		startActivity(intent);

	}
}
