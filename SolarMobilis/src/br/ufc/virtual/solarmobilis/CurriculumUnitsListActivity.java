package br.ufc.virtual.solarmobilis;

import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.HttpStatusCodeException;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.model.UserData;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;
import br.virtual.solarmobilis.view.CurriculumUnitGroupsAdapter;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@OptionsMenu(R.menu.options_menu)
@EActivity
public class CurriculumUnitsListActivity extends ActionBarActivity{

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	@Bean
	CurriculumUnitGroupsAdapter curriculumUnitGroupsAdapter;

	List<CurriculumUnit> curriculumUnits;
	
	UserData userData = new UserData();
	
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
		getUser();
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
	void getUser() {
		try {
			userData = solarManager.getUserData();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
		}
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
		setGroupIndicatorToRight();
	}
	
	private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
        	listViewCurriculumUnits.setIndicatorBounds(width-getDipsFromPixel(40), width-getDipsFromPixel(50));
        } else {
        	listViewCurriculumUnits.setIndicatorBoundsRelative(width-getDipsFromPixel(40), width-getDipsFromPixel(50));
        }
    }
	
    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
	
	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			preferences.groupSelected().put(
					curriculumUnits.get(groupPosition).getGroups()
							.get(childPosition).getId());
			preferences.userId().put(userData.getId());
			Intent intent = new Intent(CurriculumUnitsListActivity.this,
					DiscussionListActivity_.class);

			startActivity(intent);

			return false;
		}
	};
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(preferences.remainLoggedIn().get() == false){
			preferences.authToken().put(null);
		}
	}
	
}
