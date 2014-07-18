package br.ufc.virtual.solarmobilis;

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
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;
import br.virtual.solarmobilis.view.CurriculumUnitGroupsAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@OptionsMenu(R.menu.options_menu)
@EActivity
public class CurriculumUnitsListActivity extends SherlockFragmentActivity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	@Bean
	CurriculumUnitGroupsAdapter curriculumUnitGroupsAdapter;

	List<CurriculumUnit> curriculumUnits;

	@ViewById
	ExpandableListView listViewCurriculumUnits;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curriculum_units_list);

		listViewCurriculumUnits.setOnChildClickListener(onChildClickListener);

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
		getCurriculumUnits();
	}

	@Background
	void getCurriculumUnits() {
		try {
			curriculumUnits = solarManager.getCurriculumUnitGroups();
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
		curriculumUnitGroupsAdapter.setCurriculumUnits(curriculumUnits);
		bindAdapter();
	}

	void bindAdapter() {

		listViewCurriculumUnits.setAdapter(curriculumUnitGroupsAdapter);

	}

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			preferences.groupSelected().put(
					curriculumUnits.get(groupPosition).getGroups()
							.get(childPosition).getId());

			Intent intent = new Intent(CurriculumUnitsListActivity.this,
					DiscussionListActivity_.class);

			startActivity(intent);

			return false;
		}
	};

}
