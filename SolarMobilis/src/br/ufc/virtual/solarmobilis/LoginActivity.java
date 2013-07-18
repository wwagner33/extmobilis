package br.ufc.virtual.solarmobilis;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	Object response_post;

	public UserMessenge userMessenge = new UserMessenge();
	public User user = new User();

	@RestService
	SolarClient solarClient;

	@Background
	void getToken() {

		Log.i("enviando", userMessenge.toString());

		response_post = solarClient.postUser(userMessenge);

		Log.i("resposta", response_post.toString());

	}

	@ViewById(R.id.editTextUser)
	EditText field_login;
	@ViewById(R.id.editTextPassword)
	EditText field_passord;

	@Click(R.id.submit)
	void submit() {
		if (!(field_login.getText().toString().trim().length() == 0 || field_passord
				.getText().toString().trim().length() == 0)) {

			user.setLogin(field_login.getText().toString().trim());
			user.setPassword(field_passord.getText().toString().trim());
			userMessenge.setUser(user);

			getToken();
			

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
