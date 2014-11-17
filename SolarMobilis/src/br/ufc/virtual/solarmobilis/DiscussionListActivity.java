package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;
import java.util.List;

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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.model.Discussion;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;
import br.virtual.solarmobilis.view.DiscussionAdapter;

@OptionsMenu(R.menu.options_menu)
@EActivity
public class DiscussionListActivity extends ActionBarActivity {
	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	@ViewById
	ListView listViewDiscussions;

	List<Discussion> discussions = new ArrayList<Discussion>();
	private ProgressDialog dialog;

	@Bean
	DiscussionAdapter discussionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussion_list);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		dialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
				getString(R.string.dialog_message), true);
		getDiscussions();
	}

	@OptionsItem(R.id.menu_logout)
	void logout() {
		solarManager.logout();
	}

	@OptionsItem(R.id.menu_refresh)
	void refresh() {
		dialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
				getString(R.string.dialog_message), true);
		discussions.clear();
		getDiscussions();
	}

	@Background
	void getDiscussions() {
		try {
			discussions = solarManager.getDiscussions(preferences
					.groupSelected().get());
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
		discussionAdapter.setDiscussions(discussions);
		listViewDiscussions.setAdapter(discussionAdapter);
	}

	@ItemClick
	void listViewDiscussions(int position) {

		Intent intent = new Intent(this, DiscussionPostsActivity_.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("discussionId", discussions.get(position).getId());
		intent.putExtra("discussionName", discussions.get(position).getName());
		intent.putExtra("discussionLastPostDate", discussions.get(position)
				.getLastPostDate());
		intent.putExtra("startDate", discussions.get(position).getStartDate());
		intent.putExtra("endDate", discussions.get(position).getEndDate());
		intent.putExtra("status", discussions.get(position).getStatus());
		startActivity(intent);
	}

	@OptionsItem(android.R.id.home)
	void homeSelected() {
		onBackPressed();
	}
}
