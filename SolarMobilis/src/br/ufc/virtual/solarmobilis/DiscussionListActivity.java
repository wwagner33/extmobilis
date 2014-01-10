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
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.model.DiscussionAdapter;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

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
			response = solarManager.getDiscussions(preferences.groupSelected()
					.get());
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
		// Log.i("DISCUSSIONLIST", response.getDiscussions().get(0).getName());
	}

	@UiThread
	void updateList() {
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
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
