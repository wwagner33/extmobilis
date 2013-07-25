package br.ufc.virtual.solarmobilis;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	Object response_post;
	public JSONObject jsonobject;
	public UserMessage userMessage = new UserMessage();
	public User user = new User();

	@RestService
	SolarClient solarClient;

	@ViewById(R.id.editTextUser)
	EditText field_login;

	@ViewById(R.id.editTextPassword)
	EditText field_passord;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Click(R.id.submit)
	void submit() {
		if (!(field_login.getText().toString().trim().length() == 0 || field_passord
				.getText().toString().trim().length() == 0)) {

			user.setLogin(field_login.getText().toString().trim());
			user.setPassword(field_passord.getText().toString().trim());
			userMessage.setUser(user);

			getToken();

		} else {
			Toast.makeText(this, "O campo não pode estar vazio",
					Toast.LENGTH_SHORT).show();

		}
	}

	@Background
	void getToken() {
		response_post = solarClient.doLogin(userMessage);

		try {
			jsonobject = new JSONObject(response_post.toString());
		} catch (JSONException e) {

			e.printStackTrace();
		}

		try {
			preferences.token()
					.put(jsonobject.getJSONObject("session").getString(
							"auth_token"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (preferences.token().get().length() != 0) {
			Intent intent = new Intent(this, CourseListActivity_.class);

			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
