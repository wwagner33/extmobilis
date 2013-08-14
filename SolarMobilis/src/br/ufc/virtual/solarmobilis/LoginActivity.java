package br.ufc.virtual.solarmobilis;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.model.User;
import br.ufc.virtual.solarmobilis.model.UserMessage;
import br.ufc.virtual.solarmobilis.webservice.SolarClient;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	@Bean
	SolarManager solarManager;

	@Pref
	SolarMobilisPreferences_ preferences;

	@RestService
	SolarClient solarClient;

	Object response_post;
	public JSONObject jsonobject;
	public UserMessage userMessage = new UserMessage();
	public User user = new User();
	private ProgressDialog dialog;
	SolarManager solarmanager;

	@ViewById(R.id.editTextUser)
	EditText field_login;

	@ViewById(R.id.editTextPassword)
	EditText field_passord;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Click(R.id.submit)
	void submit() {
		if (!(field_login.getText().toString().trim().length() == 0 || field_passord
				.getText().toString().trim().length() == 0)) {

			user.setLogin(field_login.getText().toString().trim());
			user.setPassword(field_passord.getText().toString().trim());
			userMessage.setUser(user);

			dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
					true);

			getToken();

		} else {
			Toast.makeText(this, R.string.EMPTY_FIELD, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Background
	void getToken() {
		try {
			response_post = solarManager.doLogin(user);
			saveToken();
		} catch (HttpClientErrorException e) {
			Log.i("ERRO", e.getStatusCode().toString());
			dialog.dismiss();
			solarManager.errorHandler(e.getStatusCode());

		} catch (ResourceAccessException e) {
			dialog.dismiss();
			solarManager.alertTimeout();
		}

		Log.i("MENSSAGEM", "PODE SIM");
	}

	public void saveToken() {
		Log.i("resposta", response_post.toString());

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

		Log.i("Token_na_login", preferences.token().get().toString());

		if (preferences.token().get().length() != 0) {
			Intent intent = new Intent(this, CourseListActivity_.class);

			dialog.dismiss();

			startActivity(intent);
			finish();
		}
	}

}
