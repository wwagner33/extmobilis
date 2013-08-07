package br.ufc.virtual.solarmobilis;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_course_list)
public class CourseListActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;
	@RestService
	SolarClient solarClient;
	
	Object response;
	Object response2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		chamadas();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_list, menu);
		return true;
	}
	
	
	@Background
	void chamadas(){
		
		Log.i("TOKEN_DISCIPLINAS, TURMAS", preferences.token().get().toString());
		
		response = solarClient.getDisciplinas(preferences.token().get().toString());

	     Log.i("RESPOSNSE_CURSO",response.toString());
		
		response2 = solarClient.getTurmas(preferences.token().get().toString());
		
		Log.i("RESPOSNSE_TURMAS",response2.toString());	
		
		
		
		
		
	}

}
