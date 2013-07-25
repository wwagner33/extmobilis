package br.ufc.virtual.solarmobilis;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	Object response_post;
	public static final String PREF_TOKEN = "Prefences";
	public JSONObject jsonobject;
	public UserMessage userMessage = new UserMessage();
	public User user = new User();
	private Intent intent;

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

			intent = new Intent(this, CourseListActivity.class);
			startActivity(intent);
			/* finish(); */

		}
	}

	@Background
	void getToken() {

		SharedPreferences sharedpreferences = getSharedPreferences(PREF_TOKEN,
				0);
		SharedPreferences.Editor editor = sharedpreferences.edit();

		response_post = solarClient.doLogin(userMessage);

		Log.i("RESPOSTA", response_post.toString());

		try {
			jsonobject = new JSONObject(response_post.toString());
		} catch (JSONException e) {

			e.printStackTrace();
		}

		try {
			editor.putString("token", jsonobject.getJSONObject("session")
					.getString("auth_token"));
		} catch (JSONException e) {

			e.printStackTrace();
		}

		editor.commit();

		/*Log.i("msg", sharedpreferences.getString("token", ""));*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent intent = new Intent(this, GatewayActivity_.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("FinishActivity", "YES");

		startActivity(intent);
	}

}
