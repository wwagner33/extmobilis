package br.ufc.virtual.solarmobilis;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_course_list)
public class CourseListActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	Object responseDiscussions;

	@RestService
	SolarClient solarClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getForuns();

	}

	@Background
	void getForuns() {
		responseDiscussions = solarClient.getDiscussions(preferences.token().get()
				.toString());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_list, menu);
		return true;
	}

}
