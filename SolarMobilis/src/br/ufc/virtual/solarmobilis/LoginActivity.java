package br.ufc.virtual.solarmobilis;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.HttpStatusCodeException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.model.LoginResponse;
import br.ufc.virtual.solarmobilis.model.User;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	@Bean
	SolarManager solarManager;

	@Pref
	SolarMobilisPreferences_ preferences;

	LoginResponse loginResponse;

	public User user = new User();
	private ProgressDialog dialog;

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
		if (!(fieldLogin.getText().toString().trim().isEmpty() || fieldPassword
				.getText().toString().trim().isEmpty())) {

			user.setLogin(fieldLogin.getText().toString().trim());
			user.setPassword(fieldPassword.getText().toString().trim());

			dialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
					getString(R.string.dialog_message), true);
			getToken();

		} else {
			Toast.makeText(this, R.string.EMPTY_FIELD, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Background
	void getToken() {

		try {
			loginResponse = solarManager.doLogin(user);
			saveToken();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
		}
	}

	public void saveToken() {
		preferences.authToken().put(loginResponse.getAccessToken());

		if (preferences.authToken().get().length() != 0) {
			Intent intent = new Intent(this, CurriculumUnitsListActivity_.class);
			startActivity(intent);
			finish();
		}
	}

}
