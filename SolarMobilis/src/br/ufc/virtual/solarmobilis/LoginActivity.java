package br.ufc.virtual.solarmobilis;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity{
	
	@RestService
	SolarClient solarClient;
	
	
	@Background
	void getDisciplinas(){
		Object disc = solarClient.getCurriculumAndList();
		Log.i("Disciplinas", disc.toString());
	}

	@ViewById(R.id.editTextUser)
	EditText usuario;
	@ViewById(R.id.editTextPassword)
	EditText senha;

	@Click(R.id.submit)
	void submit(){
		if (!(usuario.getText().toString().trim().length() == 0 || senha.getText().toString().trim().length() == 0)){
			getDisciplinas();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
