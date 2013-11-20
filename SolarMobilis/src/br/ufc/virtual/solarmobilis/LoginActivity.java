package br.ufc.virtual.solarmobilis;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.HttpStatusCodeException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.model.User;
import br.ufc.virtual.solarmobilis.model.UserMessage;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@NoTitle
@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	@Bean
	SolarManager solarManager;

	@Pref
	SolarMobilisPreferences_ preferences;

	Object response_post;
	public JSONObject jsonObject;
	public UserMessage userMessage = new UserMessage();
	public User user = new User();
	private ProgressDialog dialog;
	SolarManager solarmanager;

	@ViewById(R.id.editTextUser)
	EditText fieldLogin;

	@ViewById(R.id.editTextPassword)
	EditText fieldPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Click(R.id.submit)
	void submit() {
		if (!(fieldLogin.getText().toString().trim().length() == 0 || fieldPassword
				.getText().toString().trim().length() == 0)) {

			user.setLogin(fieldLogin.getText().toString().trim());
			user.setPassword(fieldPassword.getText().toString().trim());
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
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
		}
		Log.i("MENSSAGEM", "PODE SIM");
	}

	public void saveToken() {
		Log.i("resposta", response_post.toString());

		try {
			jsonObject = new JSONObject(response_post.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			preferences.token()
					.put(jsonObject.getJSONObject("session").getString(
							"auth_token"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.i("Token_na_login", preferences.token().get().toString());

		if (preferences.token().get().length() != 0) {
			Intent intent = new Intent(this, CurriculumUnitsListActivity_.class);

			dialog.dismiss();

			startActivity(intent);
		}
	}

}
