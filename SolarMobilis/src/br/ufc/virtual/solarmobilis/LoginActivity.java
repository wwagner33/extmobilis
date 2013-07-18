package br.ufc.virtual.solarmobilis;

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
public class LoginActivity extends Activity {

	Object response_post;

	public UserMessage userMessage = new UserMessage();
	public User user = new User();

	@RestService
	SolarClient solarClient;

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
			userMessage.setUser(user);

			getToken();
		}
	}
	
	@Background
	void getToken() {
		Log.i("enviando", userMessage.toString());
		response_post = solarClient.doLogin(userMessage);
		Log.i("resposta", response_post.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
